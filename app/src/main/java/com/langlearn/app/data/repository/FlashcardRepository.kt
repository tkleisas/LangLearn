package com.langlearn.app.data.repository

import com.langlearn.app.data.database.AppDatabase
import com.langlearn.app.data.database.entity.FlashcardReviewEntity
import com.langlearn.app.data.database.entity.VocabularyEntity
import com.langlearn.app.util.spacedrepetition.Sm2Algorithm
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

class FlashcardRepository(private val db: AppDatabase) {
    private val dao get() = db.flashcardDao()
    private val vocabDao get() = db.vocabularyDao()

    fun getDueReviews(languageId: Long): Flow<List<FlashcardReviewEntity>> =
        dao.getDueReviews(languageId, System.currentTimeMillis())

    fun getDueReviewCount(languageId: Long): Flow<Int> =
        dao.getDueReviewCount(languageId, System.currentTimeMillis())

    suspend fun recordReview(vocabularyId: Long, quality: Int) {
        val existing = dao.getReviewForVocab(vocabularyId)
        val current = existing ?: FlashcardReviewEntity(vocabularyId = vocabularyId)
        val updated = Sm2Algorithm.calculate(quality, current)
        dao.insertOrUpdate(updated)
    }

    suspend fun getVocabForReview(reviewId: Long): VocabularyEntity? = withContext(Dispatchers.IO) {
        val cursor = db.openHelper.readableDatabase.query(
            """
                SELECT v.* FROM flashcard_reviews fr
                INNER JOIN vocabulary v ON fr.vocabularyId = v.id
                WHERE fr.id = ?
            """.trimIndent(),
            arrayOf(reviewId.toString())
        )
        cursor.use { c ->
            if (c.moveToFirst()) {
                VocabularyEntity(
                    id = c.getLong(c.getColumnIndexOrThrow("id")),
                    languageId = c.getLong(c.getColumnIndexOrThrow("languageId")),
                    word = c.getString(c.getColumnIndexOrThrow("word")),
                    translation = c.getString(c.getColumnIndexOrThrow("translation")),
                    romanization = c.getString(c.getColumnIndexOrThrow("romanization")),
                    partOfSpeech = c.getString(c.getColumnIndexOrThrow("partOfSpeech")),
                    lessonLevel = c.getInt(c.getColumnIndexOrThrow("lessonLevel")),
                    audioFile = c.getString(c.getColumnIndexOrThrow("audioFile"))
                )
            } else {
                null
            }
        }
    }

    suspend fun initializeNewWords(languageId: Long, count: Int): List<VocabularyEntity> {
        val db = this.db.openHelper.writableDatabase
        val cursor = db.query(
            """
                SELECT v.* FROM vocabulary v
                LEFT JOIN flashcard_reviews fr ON v.id = fr.vocabularyId
                WHERE v.languageId = ? AND fr.id IS NULL
                ORDER BY v.lessonLevel ASC, RANDOM()
                LIMIT ?
            """.trimIndent(),
            arrayOf(languageId.toString(), count.toString())
        )
        val words = mutableListOf<VocabularyEntity>()
        cursor.use { c ->
            while (c.moveToNext()) {
                val word = VocabularyEntity(
                    id = c.getLong(c.getColumnIndexOrThrow("id")),
                    languageId = c.getLong(c.getColumnIndexOrThrow("languageId")),
                    word = c.getString(c.getColumnIndexOrThrow("word")),
                    translation = c.getString(c.getColumnIndexOrThrow("translation")),
                    romanization = c.getString(c.getColumnIndexOrThrow("romanization")),
                    partOfSpeech = c.getString(c.getColumnIndexOrThrow("partOfSpeech")),
                    lessonLevel = c.getInt(c.getColumnIndexOrThrow("lessonLevel")),
                    audioFile = c.getString(c.getColumnIndexOrThrow("audioFile"))
                )
                words.add(word)
                val review = FlashcardReviewEntity(vocabularyId = word.id)
                dao.insertOrUpdate(review)
            }
        }
        return words
    }
}
