package com.langlearn.app.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.langlearn.app.LangLearnApp
import com.langlearn.app.data.database.entity.LanguageEntity
import com.langlearn.app.data.database.entity.UserProgressEntity
import com.langlearn.app.data.repository.FlashcardRepository
import com.langlearn.app.data.repository.ProgressRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class HomeViewModel(
    private val progressRepo: ProgressRepository,
    private val flashcardRepo: FlashcardRepository
) : ViewModel() {

    private val _selectedLanguage = MutableStateFlow(LanguageEntity())
    val selectedLanguage: StateFlow<LanguageEntity> = _selectedLanguage.asStateFlow()

    private val _todayProgress = MutableStateFlow<UserProgressEntity?>(null)
    val todayProgress: StateFlow<UserProgressEntity?> = _todayProgress.asStateFlow()

    @OptIn(ExperimentalCoroutinesApi::class)
    val dueReviewCount: StateFlow<Int> = _selectedLanguage
        .filter { it.id > 0L }
        .flatMapLatest { lang -> flashcardRepo.getDueReviewCount(lang.id) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    @OptIn(ExperimentalCoroutinesApi::class)
    val completedLessons: StateFlow<Int> = _selectedLanguage
        .filter { it.id > 0L }
        .flatMapLatest { lang ->
            combine(
                progressRepo.getCompletedLessonCount(lang.id, "vocabulary"),
                progressRepo.getCompletedLessonCount(lang.id, "grammar")
            ) { vocab, grammar -> vocab + grammar }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    fun selectLanguage(language: LanguageEntity) {
        _selectedLanguage.value = language
        loadStats()
    }

    fun loadStats() {
        val langId = _selectedLanguage.value.id
        if (langId <= 0L) return
        viewModelScope.launch {
            _todayProgress.value = progressRepo.getTodayProgress(langId)
        }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val db = LangLearnApp.instance.database
                HomeViewModel(
                    progressRepo = ProgressRepository(db),
                    flashcardRepo = FlashcardRepository(db)
                )
            }
        }
    }
}
