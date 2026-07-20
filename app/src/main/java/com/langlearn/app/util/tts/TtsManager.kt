package com.langlearn.app.util.tts

import android.content.Context
import android.media.AudioAttributes
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import android.util.Log
import java.util.Locale

class TtsManager(private val context: Context) : TextToSpeech.OnInitListener {

    private var tts: TextToSpeech? = null
    private var isInitialized = false
    private var onComplete: (() -> Unit)? = null

    init {
        tts = TextToSpeech(context, this)
    }

    override fun onInit(status: Int) {
        isInitialized = (status == TextToSpeech.SUCCESS)
        if (isInitialized) {
            val audioAttributes = AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_MEDIA)
                .setContentType(AudioAttributes.CONTENT_TYPE_SPEECH)
                .build()
            tts?.setAudioAttributes(audioAttributes)

            tts?.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
                override fun onStart(utteranceId: String?) {}
                override fun onDone(utteranceId: String?) {
                    onComplete?.invoke()
                }
                @Deprecated("Deprecated in Java")
                override fun onError(utteranceId: String?) {
                    onComplete?.invoke()
                }
                override fun onError(utteranceId: String?, errorCode: Int) {
                    onComplete?.invoke()
                }
                override fun onStop(utteranceId: String?, interrupted: Boolean) {
                    if (interrupted) onComplete?.invoke()
                }
            })
        } else {
            Log.w("TtsManager", "TTS initialization failed with status: $status")
        }
    }

    fun speak(text: String, languageCode: String, onComplete: (() -> Unit)? = null) {
        if (text.isBlank()) {
            onComplete?.invoke()
            return
        }
        if (!isInitialized || tts == null) {
            Log.w("TtsManager", "TTS not initialized, cannot speak: $text")
            onComplete?.invoke()
            return
        }
        try {
            val locale = when (languageCode) {
                "zh" -> Locale.SIMPLIFIED_CHINESE
                else -> Locale.forLanguageTag(languageCode)
            }
            val result = tts?.setLanguage(locale)
            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Log.w("TtsManager", "Language not supported or data missing for: $languageCode")
                onComplete?.invoke()
                return
            }
            this.onComplete = onComplete
            tts?.speak(text, TextToSpeech.QUEUE_FLUSH, null, "langlearn_tts")
        } catch (e: Exception) {
            Log.e("TtsManager", "Error speaking text", e)
            onComplete?.invoke()
        }
    }

    fun stop() {
        tts?.stop()
    }

    fun shutdown() {
        tts?.stop()
        tts?.shutdown()
    }
}
