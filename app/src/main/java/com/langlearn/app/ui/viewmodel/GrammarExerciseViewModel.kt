package com.langlearn.app.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.langlearn.app.LangLearnApp
import com.langlearn.app.data.database.entity.GrammarExerciseEntity
import com.langlearn.app.data.repository.GrammarRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import org.json.JSONArray

class GrammarExerciseViewModel(
    private val grammarRepo: GrammarRepository
) : ViewModel() {

    private val _currentExercise = MutableStateFlow<GrammarExerciseEntity?>(null)
    val currentExercise: StateFlow<GrammarExerciseEntity?> = _currentExercise.asStateFlow()

    private val _score = MutableStateFlow(0)
    val score: StateFlow<Int> = _score.asStateFlow()

    private val _exercises = MutableStateFlow<List<GrammarExerciseEntity>>(emptyList())
    val exercises: StateFlow<List<GrammarExerciseEntity>> = _exercises.asStateFlow()

    private val _currentIndex = MutableStateFlow(0)
    val currentIndex: StateFlow<Int> = _currentIndex.asStateFlow()

    private val _isFinished = MutableStateFlow(false)
    val isFinished: StateFlow<Boolean> = _isFinished.asStateFlow()

    private val _selectedAnswer = MutableStateFlow<String?>(null)
    val selectedAnswer: StateFlow<String?> = _selectedAnswer.asStateFlow()

    private val _showResult = MutableStateFlow(false)
    val showResult: StateFlow<Boolean> = _showResult.asStateFlow()

    fun loadExercises(ruleId: Long) {
        viewModelScope.launch {
            val allExercises = grammarRepo.getExercisesForRule(ruleId).first()
            _exercises.value = allExercises
            _currentIndex.value = 0
            _score.value = 0
            _isFinished.value = false
            _selectedAnswer.value = null
            _showResult.value = false

            if (allExercises.isNotEmpty()) {
                _currentExercise.value = allExercises.first()
            } else {
                _isFinished.value = true
            }
        }
    }

    fun submitAnswer(answer: String) {
        val exercise = _currentExercise.value ?: return
        _selectedAnswer.value = answer
        _showResult.value = true
        if (answer.trim().equals(exercise.correctAnswer.trim(), ignoreCase = true)) {
            _score.value = _score.value + 1
        }
    }

    fun nextExercise() {
        val nextIndex = _currentIndex.value + 1
        val allExercises = _exercises.value

        if (nextIndex >= allExercises.size) {
            _isFinished.value = true
            _currentExercise.value = null
        } else {
            _currentIndex.value = nextIndex
            _currentExercise.value = allExercises[nextIndex]
            _selectedAnswer.value = null
            _showResult.value = false
        }
    }

    fun getOptions(exercise: GrammarExerciseEntity): List<String> {
        return try {
            val jsonArray = JSONArray(exercise.options)
            val list = mutableListOf<String>()
            for (i in 0 until jsonArray.length()) {
                list.add(jsonArray.getString(i))
            }
            list
        } catch (e: Exception) {
            listOf(exercise.correctAnswer)
        }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val db = LangLearnApp.instance.database
                GrammarExerciseViewModel(
                    grammarRepo = GrammarRepository(db)
                )
            }
        }
    }
}
