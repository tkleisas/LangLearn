package com.langlearn.app.data.repository

import com.langlearn.app.data.database.AppDatabase
import com.langlearn.app.data.database.entity.QuizResultEntity
import kotlinx.coroutines.flow.Flow

class QuizRepository(private val db: AppDatabase) {
    private val dao get() = db.quizResultDao()

    fun getRecentResults(languageId: Long, limit: Int = 10): Flow<List<QuizResultEntity>> =
        dao.getRecentResults(languageId, limit)

    fun getTotalCorrectAnswers(languageId: Long): Flow<Int> =
        dao.getTotalCorrectAnswers(languageId)

    suspend fun saveResult(languageId: Long, quizType: String, totalQuestions: Int, correctAnswers: Int) {
        dao.insert(
            QuizResultEntity(
                languageId = languageId,
                quizType = quizType,
                totalQuestions = totalQuestions,
                correctAnswers = correctAnswers
            )
        )
    }
}
