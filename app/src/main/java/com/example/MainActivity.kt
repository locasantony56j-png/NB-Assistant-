package com.example

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.NbAssistantViewModel
import com.example.ui.components.*
import com.example.ui.theme.NbAssistantTheme
import com.example.ui.theme.NbBlackDark

class MainActivity : ComponentActivity() {

    private val viewModel: NbAssistantViewModel by viewModels()

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            NbAssistantTheme(darkTheme = true) {
                val uiState by viewModel.uiState.collectAsStateWithLifecycle()
                var showDiagnosticsModal by remember { mutableStateOf(false) }

                // Runtime Permissions Launcher
                val permissionsLauncher = rememberLauncherForActivityResult(
                    contract = ActivityResultContracts.RequestMultiplePermissions()
                ) { perms ->
                    val micGranted = perms[Manifest.permission.RECORD_AUDIO] ?: false
                    viewModel.updateMicPermission(micGranted)
                }

                LaunchedEffect(Unit) {
                    val permissionsToAsk = mutableListOf<String>()
                    if (ContextCompat.checkSelfPermission(this@MainActivity, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
                        permissionsToAsk.add(Manifest.permission.RECORD_AUDIO)
                    }
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
                        ContextCompat.checkSelfPermission(this@MainActivity, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                        permissionsToAsk.add(Manifest.permission.POST_NOTIFICATIONS)
                    }
                    if (permissionsToAsk.isNotEmpty()) {
                        permissionsLauncher.launch(permissionsToAsk.toTypedArray())
                    } else {
                        viewModel.updateMicPermission(true)
                    }
                }

                Scaffold(
                    modifier = Modifier
                        .fillMaxSize()
                        .testTag("main_screen"),
                    containerColor = NbBlackDark,
                    contentWindowInsets = WindowInsets.safeDrawing,
                    topBar = {
                        LogoHeader(
                            voiceMode = uiState.voiceMode,
                            isContinuousModeActive = uiState.isContinuousModeEnabled,
                            onEmergencyStop = { viewModel.emergencyStop() },
                            onOpenDiagnostics = { showDiagnosticsModal = true },
                            modifier = Modifier.testTag("logo_header")
                        )
                    }
                ) { innerPadding ->
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                            .verticalScroll(rememberScrollState())
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        // 1. Voice Control & Continuous Mode Hero Card
                        VoiceControlCard(
                            voiceMode = uiState.voiceMode,
                            isContinuousModeEnabled = uiState.isContinuousModeEnabled,
                            interimText = uiState.interimSpeech,
                            lastReply = uiState.lastReply,
                            latencyMetrics = uiState.latencyMetrics,
                            onToggleListening = { viewModel.toggleListening() },
                            onToggleContinuousMode = { viewModel.toggleContinuousMode(it) },
                            onSampleCommandClick = { viewModel.handleSpokenCommand(it) },
                            modifier = Modifier.testTag("voice_control_card")
                        )

                        // 2. WhatsApp & Messaging Intelligence
                        WhatsAppIntelligenceCard(
                            recentNotifications = uiState.recentNotifications,
                            isNotificationAccessEnabled = uiState.isNotificationAccessEnabled,
                            onReadNotificationsAloud = { viewModel.readRecentNotifications() },
                            onOpenWhatsApp = {
                                val wa = com.example.model.AppRegistry.findApp("whatsapp")
                                if (wa != null) viewModel.executeQuickAppAction(wa, "open")
                            },
                            onGrantNotificationAccess = { viewModel.openNotificationSettings() },
                            modifier = Modifier.testTag("whatsapp_card")
                        )

                        // 3. Autonomous Self-Healing & Error Protection
                        SelfHealingPanel(
                            healthState = uiState.healthState,
                            onRunDiagnostics = { viewModel.runDiagnostics() },
                            onOpenAccessibilitySettings = { viewModel.openAccessibilitySettings() },
                            onOpenNotificationSettings = { viewModel.openNotificationSettings() },
                            modifier = Modifier.testTag("self_healing_panel")
                        )

                        // 4. Universal App Launcher Grid (36+ apps)
                        AppDashboardGrid(
                            onAppAction = { app, action ->
                                viewModel.executeQuickAppAction(app, action)
                            },
                            modifier = Modifier.testTag("app_dashboard_grid")
                        )

                        // 5. Action History Log
                        ActionHistoryView(
                            actionLogs = uiState.actionLogs,
                            modifier = Modifier.testTag("action_history_view")
                        )

                        Spacer(modifier = Modifier.height(20.dp))
                    }
                }

                // Diagnostics BottomSheet Dialog
                if (showDiagnosticsModal) {
                    ModalBottomSheet(
                        onDismissRequest = { showDiagnosticsModal = false },
                        containerColor = NbBlackDark
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(20.dp)
                        ) {
                            SelfHealingPanel(
                                healthState = uiState.healthState,
                                onRunDiagnostics = { viewModel.runDiagnostics() },
                                onOpenAccessibilitySettings = { viewModel.openAccessibilitySettings() },
                                onOpenNotificationSettings = { viewModel.openNotificationSettings() }
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Button(
                                onClick = { showDiagnosticsModal = false },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text("Close Diagnostics")
                            }
                        }
                    }
                }
            }
        }
    }
}
