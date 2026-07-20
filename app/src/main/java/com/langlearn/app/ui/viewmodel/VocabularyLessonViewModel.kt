package com.langlearn.app.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.langlearn.app.LangLearnApp
import com.langlearn.app.data.database.entity.VocabularyEntity
import com.langlearn.app.data.repository.LanguageRepository
import com.langlearn.app.data.repository.VocabularyRepository
import com.langlearn.app.util.tts.TtsManager
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class VocabularyLessonViewModel(
    private val vocabularyRepo: VocabularyRepository,
    private val languageRepo: LanguageRepository,
    private val ttsManager: TtsManager
) : ViewModel() {

    private val _languageId = MutableStateFlow(0L)
    private val _level = MutableStateFlow(0)
    private val _isSpeaking = MutableStateFlow(false)
    val isSpeaking: StateFlow<Boolean> = _isSpeaking

    @OptIn(ExperimentalCoroutinesApi::class)
    val words: StateFlow<List<VocabularyEntity>> = combine(_languageId, _level) { langId, level ->
        Pair(langId, level)
    }.flatMapLatest { (langId, level) ->
        if (langId > 0L && level > 0) {
            vocabularyRepo.getWordsForLevel(langId, level)
        } else {
            flowOf(emptyList())
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun loadWords(languageId: Long, level: Int) {
        _languageId.value = languageId
        _level.value = level
    }

    fun speakWord(word: String) {
        if (word.isBlank()) return
        viewModelScope.launch {
            val language = languageRepo.getLanguageById(_languageId.value)
            val langCode = language?.code ?: return@launch
            _isSpeaking.value = true
            ttsManager.speak(word, langCode) {
                _isSpeaking.value = false
            }
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
                VocabularyLessonViewModel(
                    vocabularyRepo = VocabularyRepository(app.database),
                    languageRepo = LanguageRepository(app.database),
                    ttsManager = TtsManager(app)
                )
            }
        }
    }
}
