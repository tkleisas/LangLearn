package com.langlearn.app.data.repository

import com.langlearn.app.data.database.AppDatabase
import com.langlearn.app.data.database.entity.VocabularyEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

class VocabularyRepository(private val db: AppDatabase) {
    private val dao get() = db.vocabularyDao()

    fun getWordsForLevel(languageId: Long, level: Int): Flow<List<VocabularyEntity>> =
        dao.getByLessonLevel(languageId, level)

    fun getUnreviewedWords(languageId: Long): Flow<List<VocabularyEntity>> =
        dao.getUnreviewedWords(languageId, System.currentTimeMillis())

    fun getWordsForQuiz(languageId: Long, excludeIds: List<Long>, count: Int): Flow<List<VocabularyEntity>> =
        dao.getWordsForQuiz(languageId, excludeIds, count)

    fun getWordCount(languageId: Long): Flow<Int> =
        dao.getCount(languageId)

    fun getAllWords(languageId: Long): Flow<List<VocabularyEntity>> =
        dao.getAllForLanguage(languageId)

    suspend fun getNewWords(languageId: Long, seenIds: List<Long>, count: Int): List<VocabularyEntity> =
        dao.getNewWords(languageId, seenIds, count)

    suspend fun insertWords(words: List<VocabularyEntity>) =
        dao.insertAll(words)

    suspend fun getMaxLessonLevel(languageId: Long): Int = withContext(Dispatchers.IO) {
        val cursor = db.openHelper.readableDatabase.query(
            "SELECT MAX(lessonLevel) FROM vocabulary WHERE languageId = ?",
            arrayOf(languageId.toString())
        )
        cursor.use { c ->
            if (c.moveToFirst()) c.getInt(0) else 0
        }
    }
}
