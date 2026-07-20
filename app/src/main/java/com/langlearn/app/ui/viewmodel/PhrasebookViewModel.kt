package com.langlearn.app.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.langlearn.app.LangLearnApp
import com.langlearn.app.data.database.entity.PhraseCategoryEntity
import com.langlearn.app.data.database.entity.PhraseEntity
import com.langlearn.app.data.repository.PhraseRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn

class PhrasebookViewModel(
    private val phraseRepo: PhraseRepository
) : ViewModel() {

    private val _languageId = MutableStateFlow(0L)

    @OptIn(ExperimentalCoroutinesApi::class)
    val categories: StateFlow<List<PhraseCategoryEntity>> = _languageId
        .filter { it > 0L }
        .flatMapLatest { phraseRepo.getCategories(it) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _selectedCategoryId = MutableStateFlow(0L)

    @OptIn(ExperimentalCoroutinesApi::class)
    val phrasesForSelectedCategory: StateFlow<List<PhraseEntity>> = _selectedCategoryId
        .flatMapLatest { categoryId ->
            if (categoryId > 0L) phraseRepo.getPhrasesByCategory(categoryId)
            else flowOf(emptyList())
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _searchQuery = MutableStateFlow("")

    @OptIn(ExperimentalCoroutinesApi::class)
    val searchResults: StateFlow<List<PhraseEntity>> = combine(_languageId, _searchQuery) { langId, query ->
        Pair(langId, query)
    }.flatMapLatest { (langId, query) ->
        if (langId > 0L && query.isNotBlank()) phraseRepo.searchPhrases(langId, query)
        else flowOf(emptyList())
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun loadCategories(languageId: Long) {
        _languageId.value = languageId
    }

    fun selectCategory(categoryId: Long) {
        _selectedCategoryId.value = categoryId
    }

    fun search(query: String) {
        _searchQuery.value = query
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val db = LangLearnApp.instance.database
                PhrasebookViewModel(
                    phraseRepo = PhraseRepository(db)
                )
            }
        }
    }
}
