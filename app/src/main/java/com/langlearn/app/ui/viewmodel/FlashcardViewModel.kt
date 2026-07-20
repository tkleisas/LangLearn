package com.langlearn.app.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.langlearn.app.LangLearnApp
import com.langlearn.app.data.database.entity.VocabularyEntity
import com.langlearn.app.data.repository.FlashcardRepository
import com.langlearn.app.data.repository.VocabularyRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

data class FlashcardSessionStats(
    val reviewed: Int = 0,
    val correct: Int = 0,
    val incorrect: Int = 0
)

class FlashcardViewModel(
    private val flashcardRepo: FlashcardRepository,
    private val vocabularyRepo: VocabularyRepository
) : ViewModel() {

    private val _currentCard = MutableStateFlow<VocabularyEntity?>(null)
    val currentCard: StateFlow<VocabularyEntity?> = _currentCard.asStateFlow()

    private val _dueCount = MutableStateFlow(0)
    val dueCount: StateFlow<Int> = _dueCount.asStateFlow()

    private val _sessionStats = MutableStateFlow(FlashcardSessionStats())
    val sessionStats: StateFlow<FlashcardSessionStats> = _sessionStats.asStateFlow()

    private val _isSessionComplete = MutableStateFlow(false)
    val isSessionComplete: StateFlow<Boolean> = _isSessionComplete.asStateFlow()

    private val reviewQueue = mutableListOf<Long>()
    private var currentQueueIndex = 0

    fun startReview(languageId: Long) {
        viewModelScope.launch {
            val dueReviews = flashcardRepo.getDueReviews(languageId).first()
            reviewQueue.clear()

            if (dueReviews.isEmpty()) {
                val newWords = flashcardRepo.initializeNewWords(languageId, 10)
                val newReviews = flashcardRepo.getDueReviews(languageId).first()
                reviewQueue.addAll(newReviews.map { it.id })
            } else {
                reviewQueue.addAll(dueReviews.map { it.id })
            }

            _dueCount.value = reviewQueue.size
            currentQueueIndex = 0

            _sessionStats.value = FlashcardSessionStats()
            _isSessionComplete.value = false

            if (reviewQueue.isNotEmpty()) {
                loadCurrentCard()
            } else {
                _currentCard.value = null
                _isSessionComplete.value = true
            }
        }
    }

    fun rateCard(quality: Int) {
        val card = _currentCard.value ?: return
        viewModelScope.launch {
            flashcardRepo.recordReview(card.id, quality)

            val stats = _sessionStats.value
            _sessionStats.value = stats.copy(
                reviewed = stats.reviewed + 1,
                correct = if (quality >= 3) stats.correct + 1 else stats.correct,
                incorrect = if (quality < 3) stats.incorrect + 1 else stats.incorrect
            )

            currentQueueIndex++
            if (currentQueueIndex < reviewQueue.size) {
                loadCurrentCard()
            } else {
                _currentCard.value = null
                _isSessionComplete.value = true
            }
        }
    }

    private suspend fun loadCurrentCard() {
        val reviewId = reviewQueue.getOrNull(currentQueueIndex)
        _currentCard.value = if (reviewId != null) {
            flashcardRepo.getVocabForReview(reviewId)
        } else {
            null
        }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val db = LangLearnApp.instance.database
                FlashcardViewModel(
                    flashcardRepo = FlashcardRepository(db),
                    vocabularyRepo = VocabularyRepository(db)
                )
            }
        }
    }
}
