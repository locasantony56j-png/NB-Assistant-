package com.example.engine

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import com.example.model.VoiceMode
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.Locale

class VoiceEngine(
    private val context: Context,
    private val onCommandReceived: (String) -> Unit
) {
    private val mainHandler = Handler(Looper.getMainLooper())
    private var speechRecognizer: SpeechRecognizer? = null
    private var textToSpeech: TextToSpeech? = null
    private var isTtsReady = false
    private var isCurrentlySpeaking = false

    private val _voiceMode = MutableStateFlow(VoiceMode.IDLE)
    val voiceMode = _voiceMode.asStateFlow()

    private val _interimText = MutableStateFlow("")
    val interimText = _interimText.asStateFlow()

    var isContinuousModeActive = true
    var activeLanguageCode: String = "bn-BD"

    init {
        initTts()
    }

    private fun initTts() {
        textToSpeech = TextToSpeech(context) { status ->
            if (status == TextToSpeech.SUCCESS) {
                isTtsReady = true
                val bangla = Locale("bn", "BD")
                val available = textToSpeech?.isLanguageAvailable(bangla)
                if (available == TextToSpeech.LANG_AVAILABLE || available == TextToSpeech.LANG_COUNTRY_AVAILABLE) {
                    textToSpeech?.language = bangla
                } else {
                    textToSpeech?.language = Locale.ENGLISH
                }
            }
        }
        textToSpeech?.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
            override fun onStart(utteranceId: String?) {
                isCurrentlySpeaking = true
                _voiceMode.value = VoiceMode.SPEAKING
            }

            override fun onDone(utteranceId: String?) {
                isCurrentlySpeaking = false
                // Auto-resume continuous listening after speech finishes
                if (isContinuousModeActive) {
                    mainHandler.postDelayed({
                        startListening()
                    }, 250)
                } else {
                    _voiceMode.value = VoiceMode.IDLE
                }
            }

            override fun onError(utteranceId: String?) {
                isCurrentlySpeaking = false
                if (isContinuousModeActive) {
                    mainHandler.postDelayed({ startListening() }, 300)
                } else {
                    _voiceMode.value = VoiceMode.IDLE
                }
            }
        })
    }

    fun startListening() {
        if (isCurrentlySpeaking) return
        mainHandler.post {
            destroyRecognizer()
            if (!SpeechRecognizer.isRecognitionAvailable(context)) {
                _voiceMode.value = VoiceMode.ERROR
                return@post
            }

            try {
                speechRecognizer = SpeechRecognizer.createSpeechRecognizer(context).apply {
                    setRecognitionListener(createListener())
                }
                val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
                    putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
                    putExtra(RecognizerIntent.EXTRA_LANGUAGE, activeLanguageCode)
                    putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true)
                    putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 3)
                    putExtra(RecognizerIntent.EXTRA_PREFER_OFFLINE, true)
                    putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_COMPLETE_SILENCE_LENGTH_MILLIS, 500L)
                    putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_POSSIBLY_COMPLETE_SILENCE_LENGTH_MILLIS, 400L)
                }
                speechRecognizer?.startListening(intent)
                _voiceMode.value = if (isContinuousModeActive) VoiceMode.CONTINUOUS_ACTIVE else VoiceMode.LISTENING
                _interimText.value = ""
            } catch (_: Exception) {
                _voiceMode.value = VoiceMode.ERROR
            }
        }
    }

    fun stopListening() {
        mainHandler.post {
            destroyRecognizer()
            _voiceMode.value = VoiceMode.IDLE
            _interimText.value = ""
        }
    }

    private fun destroyRecognizer() {
        try {
            speechRecognizer?.stopListening()
            speechRecognizer?.cancel()
            speechRecognizer?.destroy()
        } catch (_: Exception) {}
        speechRecognizer = null
    }

    private fun createListener(): RecognitionListener {
        return object : RecognitionListener {
            override fun onReadyForSpeech(params: Bundle?) {}
            override fun onBeginningOfSpeech() {
                _voiceMode.value = VoiceMode.LISTENING
            }
            override fun onRmsChanged(rmsdB: Float) {}
            override fun onBufferReceived(buffer: ByteArray?) {}
            override fun onEndOfSpeech() {
                _voiceMode.value = VoiceMode.PROCESSING
            }

            override fun onError(error: Int) {
                // If it's silence or no-match, immediately resume in continuous mode within < 250ms
                val isNormalSilence = error == SpeechRecognizer.ERROR_NO_MATCH || error == SpeechRecognizer.ERROR_SPEECH_TIMEOUT
                if (isContinuousModeActive && isNormalSilence) {
                    mainHandler.postDelayed({ startListening() }, 150)
                } else if (isContinuousModeActive) {
                    mainHandler.postDelayed({ startListening() }, 800)
                } else {
                    _voiceMode.value = VoiceMode.IDLE
                }
            }

            override fun onResults(results: Bundle?) {
                val matches = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                val best = matches?.firstOrNull()?.trim() ?: ""
                if (best.isNotEmpty()) {
                    _interimText.value = best
                    onCommandReceived(best)
                } else if (isContinuousModeActive) {
                    startListening()
                }
            }

            override fun onPartialResults(partialResults: Bundle?) {
                val matches = partialResults?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                val partial = matches?.firstOrNull() ?: ""
                if (partial.isNotBlank()) {
                    _interimText.value = partial
                }
            }

            override fun onEvent(eventType: Int, params: Bundle?) {}
        }
    }

    fun speak(text: String) {
        if (!isTtsReady || text.isBlank()) return
        stopListening()
        val params = Bundle().apply {
            putFloat(TextToSpeech.Engine.KEY_PARAM_VOLUME, 1.0f)
        }
        val isBangla = text.any { it in '\u0980'..'\u09FF' }
        if (isBangla) {
            val bangla = Locale("bn", "BD")
            if (textToSpeech?.isLanguageAvailable(bangla) == TextToSpeech.LANG_AVAILABLE) {
                textToSpeech?.language = bangla
            }
        }
        textToSpeech?.speak(text, TextToSpeech.QUEUE_FLUSH, params, "nb_utterance_${System.currentTimeMillis()}")
    }

    fun stopSpeaking() {
        textToSpeech?.stop()
        isCurrentlySpeaking = false
    }

    fun destroy() {
        stopListening()
        stopSpeaking()
        textToSpeech?.shutdown()
        textToSpeech = null
    }
}
