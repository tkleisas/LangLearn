package com.langlearn.app.util.tts

import android.content.Context
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
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
        }
    }

    fun speak(text: String, languageCode: String, onComplete: (() -> Unit)? = null) {
        if (!isInitialized || tts == null) {
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
                onComplete?.invoke()
                return
            }
            this.onComplete = onComplete
            tts?.speak(text, TextToSpeech.QUEUE_FLUSH, null, "langlearn_tts")
        } catch (e: Exception) {
            e.printStackTrace()
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
