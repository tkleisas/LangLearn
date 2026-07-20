package com.langlearn.app.data.database.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "flashcard_reviews",
    foreignKeys = [ForeignKey(entity = VocabularyEntity::class, parentColumns = ["id"], childColumns = ["vocabularyId"], onDelete = ForeignKey.CASCADE)],
    indices = [Index("vocabularyId", unique = true)]
)
data class FlashcardReviewEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val vocabularyId: Long,
    val easeFactor: Double = 2.5,
    val interval: Int = 0,
    val repetitions: Int = 0,
    val nextReviewDate: Long = 0,
    val lastReviewDate: Long = 0
)

@Entity(tableName = "quiz_results")
data class QuizResultEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val languageId: Long,
    val quizType: String,
    val totalQuestions: Int,
    val correctAnswers: Int,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "user_progress")
data class UserProgressEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val date: String, // yyyy-MM-dd
    val languageId: Long,
    val wordsLearned: Int = 0,
    val lessonsCompleted: Int = 0,
    val minutesPracticed: Int = 0,
    val streak: Int = 0
)

@Entity(tableName = "lessons_completed")
data class LessonCompletedEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val languageId: Long,
    val lessonLevel: Int,
    val lessonType: String, // "vocabulary" or "grammar"
    val completedDate: Long = System.currentTimeMillis()
)
