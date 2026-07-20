package com.langlearn.app.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Upsert
import com.langlearn.app.data.database.entity.LessonCompletedEntity
import com.langlearn.app.data.database.entity.UserProgressEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface UserProgressDao {

    @Query("SELECT * FROM user_progress WHERE date = :date AND languageId = :languageId")
    suspend fun getProgressForDate(date: String, languageId: Long): UserProgressEntity?

    @Query("SELECT * FROM user_progress WHERE date BETWEEN :startDate AND :endDate AND languageId = :languageId")
    fun getProgressForWeek(startDate: String, endDate: String, languageId: Long): Flow<List<UserProgressEntity>>

    @Query("SELECT COALESCE(streak, 0) FROM user_progress WHERE languageId = :languageId ORDER BY date DESC LIMIT 1")
    suspend fun getStreakForDate(date: String, languageId: Long): Int

    @Upsert
    suspend fun upsert(progress: UserProgressEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLessonCompletion(lesson: LessonCompletedEntity)

    @Query(
        """
        SELECT EXISTS(
            SELECT 1 FROM lessons_completed
            WHERE languageId = :languageId AND lessonType = :lessonType AND lessonLevel = :lessonLevel
        )
        """
    )
    suspend fun isLessonCompleted(languageId: Long, lessonType: String, lessonLevel: Int): Boolean

    @Query("SELECT COUNT(*) FROM lessons_completed WHERE languageId = :languageId AND lessonType = :lessonType")
    fun getCompletedLessonsCount(languageId: Long, lessonType: String): Flow<Int>
}
