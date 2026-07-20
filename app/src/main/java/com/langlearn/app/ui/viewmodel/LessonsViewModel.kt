package com.langlearn.app.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.langlearn.app.LangLearnApp
import com.langlearn.app.data.database.entity.LanguageEntity
import com.langlearn.app.data.repository.GrammarRepository
import com.langlearn.app.data.repository.ProgressRepository
import com.langlearn.app.data.repository.VocabularyRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class LessonItem(
    val level: Int,
    val type: String,
    val title: String,
    val completed: Boolean,
    val locked: Boolean
)

class LessonsViewModel(
    private val progressRepo: ProgressRepository,
    private val vocabularyRepo: VocabularyRepository,
    private val grammarRepo: GrammarRepository
) : ViewModel() {

    private val _selectedLanguage = MutableStateFlow(LanguageEntity())
    val selectedLanguage: StateFlow<LanguageEntity> = _selectedLanguage.asStateFlow()

    private val _lessons = MutableStateFlow<List<LessonItem>>(emptyList())
    val lessons: StateFlow<List<LessonItem>> = _lessons.asStateFlow()

    fun selectLanguage(language: LanguageEntity) {
        _selectedLanguage.value = language
        loadLessons()
    }

    fun loadLessons() {
        val langId = _selectedLanguage.value.id
        if (langId <= 0L) return

        viewModelScope.launch {
            val maxVocabLevel = vocabularyRepo.getMaxLessonLevel(langId)
            val maxGrammarLevel = grammarRepo.getMaxLevel(langId)
            val maxLevel = maxOf(maxVocabLevel, maxGrammarLevel)

            if (maxLevel == 0) {
                _lessons.value = emptyList()
                return@launch
            }

            val items = mutableListOf<LessonItem>()
            for (level in 1..maxLevel) {
                if (level <= maxVocabLevel) {
                    val vocabCompleted = progressRepo.isLessonCompleted(langId, "vocabulary", level)
                    val prevVocabCompleted = if (level == 1) true
                    else progressRepo.isLessonCompleted(langId, "vocabulary", level - 1)
                    val prevGrammarCompleted = if (level == 1 || level > maxGrammarLevel) true
                    else progressRepo.isLessonCompleted(langId, "grammar", level - 1)
                    val vocabLocked = !prevVocabCompleted && !prevGrammarCompleted
                    items.add(
                        LessonItem(
                            level = level,
                            type = "vocabulary",
                            title = "Lesson $level - Vocabulary",
                            completed = vocabCompleted,
                            locked = vocabLocked
                        )
                    )
                }
                if (level <= maxGrammarLevel) {
                    val grammarCompleted = progressRepo.isLessonCompleted(langId, "grammar", level)
                    val prevGrammarCompleted = if (level == 1) true
                    else progressRepo.isLessonCompleted(langId, "grammar", level - 1)
                    val prevVocabCompleted = if (level == 1 || level > maxVocabLevel) true
                    else progressRepo.isLessonCompleted(langId, "vocabulary", level - 1)
                    val grammarLocked = !prevGrammarCompleted && !prevVocabCompleted
                    items.add(
                        LessonItem(
                            level = level,
                            type = "grammar",
                            title = "Lesson $level - Grammar",
                            completed = grammarCompleted,
                            locked = grammarLocked
                        )
                    )
                }
            }
            _lessons.value = items
        }
    }

    fun completeLesson(lessonLevel: Int, lessonType: String) {
        val langId = _selectedLanguage.value.id
        if (langId <= 0L) return
        viewModelScope.launch {
            progressRepo.markLessonCompleted(langId, lessonType, lessonLevel)
            loadLessons()
        }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val db = LangLearnApp.instance.database
                LessonsViewModel(
                    progressRepo = ProgressRepository(db),
                    vocabularyRepo = VocabularyRepository(db),
                    grammarRepo = GrammarRepository(db)
                )
            }
        }
    }
}
