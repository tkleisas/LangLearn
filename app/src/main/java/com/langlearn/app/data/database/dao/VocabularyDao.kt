package com.langlearn.app.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.langlearn.app.data.database.entity.VocabularyEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface VocabularyDao {

    @Query("SELECT * FROM vocabulary WHERE languageId = :languageId")
    fun getAllForLanguage(languageId: Long): Flow<List<VocabularyEntity>>

    @Query("SELECT * FROM vocabulary WHERE languageId = :languageId AND lessonLevel = :level")
    fun getByLessonLevel(languageId: Long, level: Int): Flow<List<VocabularyEntity>>

    @Query("SELECT * FROM vocabulary WHERE id = :id")
    suspend fun getById(id: Long): VocabularyEntity?

    @Query("SELECT * FROM vocabulary WHERE languageId = :languageId AND lessonLevel = :level")
    suspend fun getByLessonLevelSync(languageId: Long, level: Int): List<VocabularyEntity>

    @Query(
        """
        SELECT v.* FROM vocabulary v
        LEFT JOIN flashcard_reviews fr ON v.id = fr.vocabularyId
        WHERE v.languageId = :languageId
        AND (fr.id IS NULL OR fr.nextReviewDate <= :currentTime)
        """
    )
    fun getUnreviewedWords(languageId: Long, currentTime: Long): Flow<List<VocabularyEntity>>

    @Query("SELECT * FROM vocabulary WHERE languageId = :languageId AND id NOT IN (:excludeIds) ORDER BY RANDOM() LIMIT :limit")
    fun getWordsForQuiz(languageId: Long, excludeIds: List<Long>, limit: Int): Flow<List<VocabularyEntity>>

    @Query("SELECT COUNT(*) FROM vocabulary WHERE languageId = :languageId")
    fun getCount(languageId: Long): Flow<Int>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(words: List<VocabularyEntity>)

    @Query("SELECT * FROM vocabulary WHERE languageId = :languageId AND id NOT IN (:excludingIds) LIMIT :limit")
    suspend fun getNewWords(languageId: Long, excludingIds: List<Long>, limit: Int): List<VocabularyEntity>
}
