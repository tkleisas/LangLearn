package com.langlearn.app.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.langlearn.app.data.database.dao.FlashcardDao
import com.langlearn.app.data.database.dao.GrammarDao
import com.langlearn.app.data.database.dao.PhraseDao
import com.langlearn.app.data.database.dao.QuizResultDao
import com.langlearn.app.data.database.dao.UserProgressDao
import com.langlearn.app.data.database.dao.VocabularyDao
import com.langlearn.app.data.database.entity.AlphabetEntity
import com.langlearn.app.data.database.entity.FlashcardReviewEntity
import com.langlearn.app.data.database.entity.GrammarExerciseEntity
import com.langlearn.app.data.database.entity.GrammarRuleEntity
import com.langlearn.app.data.database.entity.LanguageEntity
import com.langlearn.app.data.database.entity.LessonCompletedEntity
import com.langlearn.app.data.database.entity.PhraseCategoryEntity
import com.langlearn.app.data.database.entity.PhraseEntity
import com.langlearn.app.data.database.entity.QuizResultEntity
import com.langlearn.app.data.database.entity.UserProgressEntity
import com.langlearn.app.data.database.entity.VocabularyEntity

@Database(
    entities = [
        LanguageEntity::class,
        VocabularyEntity::class,
        PhraseCategoryEntity::class,
        PhraseEntity::class,
        GrammarRuleEntity::class,
        GrammarExerciseEntity::class,
        AlphabetEntity::class,
        FlashcardReviewEntity::class,
        QuizResultEntity::class,
        UserProgressEntity::class,
        LessonCompletedEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun vocabularyDao(): VocabularyDao
    abstract fun phraseDao(): PhraseDao
    abstract fun grammarDao(): GrammarDao
    abstract fun flashcardDao(): FlashcardDao
    abstract fun quizResultDao(): QuizResultDao
    abstract fun userProgressDao(): UserProgressDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "langlearn_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
