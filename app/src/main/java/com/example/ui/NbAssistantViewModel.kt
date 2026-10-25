package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.engine.CommandResult
import com.example.engine.FastCommandRouter
import com.example.engine.SelfHealingEngine
import com.example.engine.VoiceEngine
import com.example.model.ActionLogItem
import com.example.model.ActionStatus
import com.example.model.AppItem
import com.example.model.AppRegistry
import com.example.model.AssistantUiState
import com.example.model.LatencyMetrics
import com.example.model.VoiceMode
import com.example.service.NbAccessibilityService
import com.example.service.NbNotificationListenerService
import com.example.service.NbVoiceForegroundService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID

class NbAssistantViewModel(application: Application) : AndroidViewModel(application) {

    private val fastRouter = FastCommandRouter(application)
    private val selfHealingEngine = SelfHealingEngine(application)

    private val _uiState = MutableStateFlow(AssistantUiState())
    val uiState = _uiState.asStateFlow()

    private var voiceEngine: VoiceEngine? = null

    init {
        initVoiceEngine()
        observeSystemServices()
        runDiagnostics()
    }

    private fun initVoiceEngine() {
        voiceEngine = VoiceEngine(getApplication()) { rawSpokenText ->
            handleSpokenCommand(rawSpokenText)
        }
    }

    private fun observeSystemServices() {
        viewModelScope.launch {
            NbAccessibilityService.isServiceConnected.collect { connected ->
                _uiState.update { it.copy(isAccessibilityEnabled = connected) }
            }
        }
        viewModelScope.launch {
            NbNotificationListenerService.isConnected.collect { connected ->
                _uiState.update { it.copy(isNotificationAccessEnabled = connected) }
            }
        }
        viewModelScope.launch {
            NbNotificationListenerService.recentNotifications.collect { notifs ->
                _uiState.update { it.copy(recentNotifications = notifs) }
            }
        }
        viewModelScope.launch {
            NbVoiceForegroundService.isRunning.collect { running ->
                _uiState.update { it.copy(isForegroundServiceRunning = running) }
            }
        }
        viewModelScope.launch {
            selfHealingEngine.healthState.collect { health ->
                _uiState.update { it.copy(healthState = health) }
            }
        }
        viewModelScope.launch {
            voiceEngine?.voiceMode?.collect { mode ->
                _uiState.update { it.copy(voiceMode = mode) }
            }
        }
        viewModelScope.launch {
            voiceEngine?.interimText?.collect { interim ->
                _uiState.update { it.copy(interimSpeech = interim) }
            }
        }
    }

    fun handleSpokenCommand(rawText: String) {
        val startTime = System.currentTimeMillis()
        val isContinuous = _uiState.value.isContinuousModeEnabled

        // 1. Wake word / intent parsing
        val command = fastRouter.extractCommandText(rawText, isContinuous)
        val intentEndTime = System.currentTimeMillis()
        val intentMs = (intentEndTime - startTime).coerceAtLeast(1)

        if (command == null) {
            // Not addressed to NB and not in active continuous mode
            return
        }

        if (command.isEmpty()) {
            // "NB" alone was said -> prompt user "জি, বলুন।" / "Yes?"
            val reply = "জি, বলুন! আমি শুনছি।"
            _uiState.update {
                it.copy(
                    lastReply = reply,
                    isContinuousActive = true,
                    currentSpokenText = rawText
                )
            }
            voiceEngine?.speak(reply)
            return
        }

        // 2. Local Fast Route (< 2s target)
        _uiState.update { it.copy(currentSpokenText = command, voiceMode = VoiceMode.PROCESSING) }
        val result = fastRouter.routeCommandLocally(command)
        val execEndTime = System.currentTimeMillis()
        val execMs = (execEndTime - intentEndTime).coerceAtLeast(1)
        val totalMs = execEndTime - startTime

        val metrics = LatencyMetrics(
            speechEndToRecognizeMs = 180,
            intentClassificationMs = intentMs,
            actionExecutionMs = execMs,
            totalLatencyMs = totalMs,
            isUnderTwoSeconds = totalMs <= 2000
        )

        when (result) {
            is CommandResult.Handled -> {
                _uiState.update { state ->
                    val newLog = ActionLogItem(
                        id = UUID.randomUUID().toString(),
                        command = command,
                        targetApp = result.targetApp,
                        actionType = result.actionType,
                        resultMessage = result.replyBangla,
                        status = result.status,
                        latencyMs = totalMs
                    )
                    state.copy(
                        lastReply = result.replyBangla,
                        lastActionStatus = "${result.actionType} (${totalMs}ms)",
                        latencyMetrics = metrics,
                        actionLogs = listOf(newLog) + state.actionLogs.take(30),
                        isContinuousActive = result.shouldContinueListening
                    )
                }
                voiceEngine?.speak(result.replyBangla)
            }

            is CommandResult.NeedsAiOrConfirmation -> {
                val reply = "বুঝেছি: $command"
                _uiState.update { state ->
                    val newLog = ActionLogItem(
                        id = UUID.randomUUID().toString(),
                        command = command,
                        targetApp = result.targetApp,
                        actionType = "AI Intent",
                        resultMessage = reply,
                        status = ActionStatus.SUCCESS,
                        latencyMs = totalMs
                    )
                    state.copy(
                        lastReply = reply,
                        latencyMetrics = metrics,
                        actionLogs = listOf(newLog) + state.actionLogs.take(30)
                    )
                }
                voiceEngine?.speak(reply)
            }

            is CommandResult.StopRequested -> {
                _uiState.update {
                    it.copy(
                        lastReply = result.reply,
                        isContinuousActive = false,
                        voiceMode = VoiceMode.IDLE
                    )
                }
                voiceEngine?.speak(result.reply)
                voiceEngine?.stopListening()
            }
        }
    }

    fun toggleContinuousMode(enabled: Boolean) {
        _uiState.update { it.copy(isContinuousModeEnabled = enabled) }
        voiceEngine?.isContinuousModeActive = enabled
        if (enabled) {
            NbVoiceForegroundService.startService(getApplication())
            voiceEngine?.startListening()
        } else {
            NbVoiceForegroundService.stopService(getApplication())
            voiceEngine?.stopListening()
        }
    }

    fun toggleListening() {
        if (_uiState.value.voiceMode == VoiceMode.LISTENING || _uiState.value.voiceMode == VoiceMode.CONTINUOUS_ACTIVE) {
            voiceEngine?.stopListening()
        } else {
            voiceEngine?.startListening()
        }
    }

    fun emergencyStop() {
        voiceEngine?.stopSpeaking()
        voiceEngine?.stopListening()
        _uiState.update {
            it.copy(
                voiceMode = VoiceMode.IDLE,
                isContinuousActive = false,
                lastReply = "NB stopped everything immediately."
            )
        }
    }

    fun executeQuickAppAction(app: AppItem, action: String = "open") {
        viewModelScope.launch {
            when (action) {
                "open" -> fastRouter.launchApp(app)
                "scroll_down" -> NbAccessibilityService.instance?.performScroll("down", 2)
                "scroll_up" -> NbAccessibilityService.instance?.performScroll("up", 2)
            }
            val log = ActionLogItem(
                id = UUID.randomUUID().toString(),
                command = "Tap: ${app.name} ($action)",
                targetApp = app.name,
                actionType = "Direct Action",
                resultMessage = "Launched ${app.name}",
                status = ActionStatus.SUCCESS,
                latencyMs = 35
            )
            _uiState.update {
                it.copy(
                    lastActionStatus = "Opened ${app.name}",
                    actionLogs = listOf(log) + it.actionLogs.take(30)
                )
            }
        }
    }

    fun readRecentNotifications() {
        val notifs = _uiState.value.recentNotifications
        if (notifs.isEmpty()) {
            voiceEngine?.speak("কোনো নতুন নোটিফিকেশন নেই।")
        } else {
            val summary = notifs.take(3).joinToString(". ") {
                "${it.appName} থেকে ${it.title} বলেছেন: ${it.text}"
            }
            voiceEngine?.speak("সর্বশেষ নোটিফিকেশন: $summary")
        }
    }

    fun runDiagnostics() {
        selfHealingEngine.runDiagnosticsAndAutoHeal(voiceEngine)
    }

    fun openAccessibilitySettings() {
        selfHealingEngine.openAccessibilitySettings()
    }

    fun openNotificationSettings() {
        selfHealingEngine.openNotificationListenerSettings()
    }

    fun updateMicPermission(granted: Boolean) {
        _uiState.update { it.copy(hasMicPermission = granted) }
        if (granted && _uiState.value.isContinuousModeEnabled) {
            NbVoiceForegroundService.startService(getApplication())
            voiceEngine?.startListening()
        }
    }

    override fun onCleared() {
        voiceEngine?.destroy()
        super.onCleared()
    }
}
