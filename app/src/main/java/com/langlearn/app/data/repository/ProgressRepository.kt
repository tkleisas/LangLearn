package com.langlearn.app.data.repository

import com.langlearn.app.data.database.AppDatabase
import com.langlearn.app.data.database.entity.LessonCompletedEntity
import com.langlearn.app.data.database.entity.UserProgressEntity
import kotlinx.coroutines.flow.Flow
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class ProgressRepository(private val db: AppDatabase) {
    private val dao get() = db.userProgressDao()

    private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.US)

    suspend fun recordPractice(languageId: Long, minutes: Int, wordsLearned: Int = 0) {
        val today = dateFormat.format(Date())
        val yesterday = dateFormat.format(Date(System.currentTimeMillis() - 86_400_000L))

        val existing = dao.getProgressForDate(today, languageId)
        val yesterdayProgress = dao.getProgressForDate(yesterday, languageId)

        val newStreak = if (yesterdayProgress != null && yesterdayProgress.streak > 0) {
            yesterdayProgress.streak + 1
        } else {
            1
        }

        val progress = existing?.copy(
            minutesPracticed = existing.minutesPracticed + minutes,
            wordsLearned = existing.wordsLearned + wordsLearned,
            streak = newStreak
        ) ?: UserProgressEntity(
            date = today,
            languageId = languageId,
            minutesPracticed = minutes,
            wordsLearned = wordsLearned,
            streak = newStreak
        )

        dao.upsert(progress)
    }

    suspend fun getTodayProgress(languageId: Long): UserProgressEntity? {
        val today = dateFormat.format(Date())
        return dao.getProgressForDate(today, languageId)
    }

    fun getWeekProgress(languageId: Long): Flow<List<UserProgressEntity>> {
        val cal = Calendar.getInstance()
        val dayOfWeek = cal.get(Calendar.DAY_OF_WEEK)
        val diffToMonday = if (dayOfWeek == Calendar.SUNDAY) -6 else Calendar.MONDAY - dayOfWeek
        cal.add(Calendar.DAY_OF_MONTH, diffToMonday)
        val startDate = dateFormat.format(cal.time)
        cal.add(Calendar.DAY_OF_MONTH, 6)
        val endDate = dateFormat.format(cal.time)
        return dao.getProgressForWeek(startDate, endDate, languageId)
    }

    suspend fun markLessonCompleted(languageId: Long, lessonType: String, lessonLevel: Int) {
        dao.insertLessonCompletion(
            LessonCompletedEntity(
                languageId = languageId,
                lessonType = lessonType,
                lessonLevel = lessonLevel
            )
        )
    }

    suspend fun isLessonCompleted(languageId: Long, lessonType: String, lessonLevel: Int): Boolean =
        dao.isLessonCompleted(languageId, lessonType, lessonLevel)

    fun getCompletedLessonCount(languageId: Long, lessonType: String): Flow<Int> =
        dao.getCompletedLessonsCount(languageId, lessonType)

    suspend fun getCurrentStreak(languageId: Long): Int {
        return dao.getCurrentStreak(languageId)
    }
}
