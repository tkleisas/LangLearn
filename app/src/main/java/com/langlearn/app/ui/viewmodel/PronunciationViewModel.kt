package com.langlearn.app.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.langlearn.app.LangLearnApp
import com.langlearn.app.util.tts.TtsManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class PronunciationViewModel(
    private val ttsManager: TtsManager
) : ViewModel() {

    private val _wordToPronounce = MutableStateFlow("")
    val wordToPronounce: StateFlow<String> = _wordToPronounce.asStateFlow()

    private val _isSpeaking = MutableStateFlow(false)
    val isSpeaking: StateFlow<Boolean> = _isSpeaking.asStateFlow()

    private val _languageCode = MutableStateFlow("")
    val languageCode: StateFlow<String> = _languageCode.asStateFlow()

    fun speak(word: String, romanization: String, langCode: String) {
        _wordToPronounce.value = word
        _languageCode.value = langCode
        _isSpeaking.value = true
        ttsManager.speak(word, langCode) {
            _isSpeaking.value = false
        }
    }

    override fun onCleared() {
        super.onCleared()
        ttsManager.shutdown()
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val app = LangLearnApp.instance
                PronunciationViewModel(
                    ttsManager = TtsManager(app)
                )
            }
        }
    }
}
