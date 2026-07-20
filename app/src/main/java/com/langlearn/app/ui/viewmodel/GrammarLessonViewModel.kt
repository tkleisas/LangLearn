package com.langlearn.app.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.langlearn.app.LangLearnApp
import com.langlearn.app.data.database.entity.GrammarRuleEntity
import com.langlearn.app.data.repository.GrammarRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn

class GrammarLessonViewModel(
    private val grammarRepo: GrammarRepository
) : ViewModel() {

    private val _languageId = MutableStateFlow(0L)
    private val _level = MutableStateFlow(0)

    @OptIn(ExperimentalCoroutinesApi::class)
    val rules: StateFlow<List<GrammarRuleEntity>> = combine(_languageId, _level) { langId, level ->
        Pair(langId, level)
    }.flatMapLatest { (langId, level) ->
        if (langId > 0L && level > 0) {
            grammarRepo.getRulesForLevel(langId, level)
        } else {
            flowOf(emptyList())
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun loadRules(languageId: Long, level: Int) {
        _languageId.value = languageId
        _level.value = level
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val db = LangLearnApp.instance.database
                GrammarLessonViewModel(
                    grammarRepo = GrammarRepository(db)
                )
            }
        }
    }
}
