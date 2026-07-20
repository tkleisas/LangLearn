package com.langlearn.app.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.langlearn.app.data.database.entity.QuizResultEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface QuizResultDao {

    @Insert
    suspend fun insert(result: QuizResultEntity)

    @Query("SELECT * FROM quiz_results WHERE languageId = :languageId ORDER BY timestamp DESC LIMIT :limit")
    fun getRecentResults(languageId: Long, limit: Int): Flow<List<QuizResultEntity>>

    @Query("SELECT COALESCE(SUM(correctAnswers), 0) FROM quiz_results WHERE languageId = :languageId")
    fun getTotalCorrectAnswers(languageId: Long): Flow<Int>
}
