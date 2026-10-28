package com.nb.assistant

import android.content.Context
import android.speech.tts.TextToSpeech
import android.speech.tts.Voice
import java.util.Locale

/** Uses installed Android TTS voices. Voice gender and language availability vary by device. */
class VoiceEngine(context: Context) : TextToSpeech.OnInitListener {
    private var tts: TextToSpeech? = TextToSpeech(context.applicationContext, this)
    private var ready = false

    override fun onInit(status: Int) {
        ready = status == TextToSpeech.SUCCESS
    }

    fun availableVoices(): List<String> = tts?.voices
        ?.mapNotNull { it.name }
        ?.sorted()
        ?: emptyList()

    fun setLanguage(languageTag: String): Boolean {
        val locale = Locale.forLanguageTag(languageTag)
        val result = tts?.setLanguage(locale) ?: TextToSpeech.ERROR
        return result >= TextToSpeech.LANG_AVAILABLE
    }

    /** Select an exact installed voice name after showing the user available choices. */
    fun selectInstalledVoice(voiceName: String): Boolean {
        val voice: Voice = tts?.voices?.firstOrNull { it.name == voiceName } ?: return false
        tts?.voice = voice
        return true
    }

    fun speak(text: String) {
        if (!ready || text.isBlank()) return
        tts?.speak(text, TextToSpeech.QUEUE_FLUSH, null, "nb-${System.currentTimeMillis()}")
    }

    fun stop() { tts?.stop() }
    fun close() { tts?.stop(); tts?.shutdown(); tts = null }
}
