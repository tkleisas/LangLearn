package com.langlearn.app.data.repository

import com.langlearn.app.data.database.AppDatabase
import com.langlearn.app.data.database.entity.PhraseCategoryEntity
import com.langlearn.app.data.database.entity.PhraseEntity
import kotlinx.coroutines.flow.Flow

class PhraseRepository(private val db: AppDatabase) {
    private val dao get() = db.phraseDao()

    fun getCategories(languageId: Long): Flow<List<PhraseCategoryEntity>> =
        dao.getCategories(languageId)

    fun getPhrasesByCategory(categoryId: Long): Flow<List<PhraseEntity>> =
        dao.getPhrasesByCategory(categoryId)

    fun searchPhrases(languageId: Long, query: String): Flow<List<PhraseEntity>> =
        dao.searchPhrases(languageId, query)
}
