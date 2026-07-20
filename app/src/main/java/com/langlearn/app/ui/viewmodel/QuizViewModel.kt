package com.langlearn.app.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.langlearn.app.LangLearnApp
import com.langlearn.app.data.database.entity.VocabularyEntity
import com.langlearn.app.data.repository.QuizRepository
import com.langlearn.app.data.repository.VocabularyRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class QuizViewModel(
    private val vocabularyRepo: VocabularyRepository,
    private val quizRepo: QuizRepository
) : ViewModel() {

    private val _currentQuestion = MutableStateFlow<VocabularyEntity?>(null)
    val currentQuestion: StateFlow<VocabularyEntity?> = _currentQuestion.asStateFlow()

    private val _options = MutableStateFlow<List<String>>(emptyList())
    val options: StateFlow<List<String>> = _options.asStateFlow()

    private val _score = MutableStateFlow(0)
    val score: StateFlow<Int> = _score.asStateFlow()

    private val _totalQuestions = MutableStateFlow(0)
    val totalQuestions: StateFlow<Int> = _totalQuestions.asStateFlow()

    private val _questionIndex = MutableStateFlow(0)
    val questionIndex: StateFlow<Int> = _questionIndex.asStateFlow()

    private val _quizType = MutableStateFlow("multiple_choice")
    val quizType: StateFlow<String> = _quizType.asStateFlow()

    private val _isFinished = MutableStateFlow(false)
    val isFinished: StateFlow<Boolean> = _isFinished.asStateFlow()

    private val _selectedAnswer = MutableStateFlow<String?>(null)
    val selectedAnswer: StateFlow<String?> = _selectedAnswer.asStateFlow()

    private val _isAnswerCorrect = MutableStateFlow<Boolean?>(null)
    val isAnswerCorrect: StateFlow<Boolean?> = _isAnswerCorrect.asStateFlow()

    private var languageId = 0L
    private val questionList = mutableListOf<VocabularyEntity>()

    fun startQuiz(langId: Long, type: String, questionCount: Int) {
        languageId = langId
        _quizType.value = type
        _totalQuestions.value = questionCount
        _score.value = 0
        _questionIndex.value = 0
        _isFinished.value = false
        _selectedAnswer.value = null
        _isAnswerCorrect.value = null

        viewModelScope.launch {
            val allWords = vocabularyRepo.getAllWords(langId).first()
            if (allWords.isEmpty()) {
                _isFinished.value = true
                return@launch
            }
            questionList.clear()
            questionList.addAll(allWords.shuffled().take(questionCount))
            _totalQuestions.value = questionList.size
            loadQuestion()
        }
    }

    fun submitAnswer(selected: String) {
        val question = _currentQuestion.value ?: return
        _selectedAnswer.value = selected
        val correct = selected == question.translation
        _isAnswerCorrect.value = correct

        if (correct) {
            _score.value = _score.value + 1
        }
    }

    fun nextQuestion() {
        _selectedAnswer.value = null
        _isAnswerCorrect.value = null
        val nextIndex = _questionIndex.value + 1
        _questionIndex.value = nextIndex

        if (nextIndex >= questionList.size) {
            viewModelScope.launch {
                quizRepo.saveResult(languageId, _quizType.value, _totalQuestions.value, _score.value)
                _isFinished.value = true
                _currentQuestion.value = null
            }
        } else {
            loadQuestion()
        }
    }

    private fun loadQuestion() {
        val question = questionList.getOrNull(_questionIndex.value)
        _currentQuestion.value = question
        if (question != null) {
            _options.value = generateOptions(question)
        }
    }

    private fun generateOptions(question: VocabularyEntity): List<String> {
        val correct = question.translation
        val wrongOptions = questionList
            .filter { it.id != question.id && it.translation != correct }
            .map { it.translation }
            .distinct()
            .shuffled()
            .take(3)

        val result = (wrongOptions + correct).shuffled()
        if (result.size < 4) {
            return result
        }
        return result
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val db = LangLearnApp.instance.database
                QuizViewModel(
                    vocabularyRepo = VocabularyRepository(db),
                    quizRepo = QuizRepository(db)
                )
            }
        }
    }
}
