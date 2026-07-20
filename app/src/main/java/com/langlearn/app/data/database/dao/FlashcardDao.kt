package com.langlearn.app.data.database.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.langlearn.app.data.database.entity.FlashcardReviewEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface FlashcardDao {

    @Query("SELECT * FROM flashcard_reviews WHERE vocabularyId = :vocabularyId")
    suspend fun getReviewForVocab(vocabularyId: Long): FlashcardReviewEntity?

    @Query(
        """
        SELECT fr.* FROM flashcard_reviews fr
        INNER JOIN vocabulary v ON fr.vocabularyId = v.id
        WHERE v.languageId = :languageId AND fr.nextReviewDate <= :currentTime
        """
    )
    fun getDueReviews(languageId: Long, currentTime: Long): Flow<List<FlashcardReviewEntity>>

    @Query(
        """
        SELECT COUNT(*) FROM flashcard_reviews fr
        INNER JOIN vocabulary v ON fr.vocabularyId = v.id
        WHERE v.languageId = :languageId AND fr.nextReviewDate <= :currentTime
        """
    )
    fun getDueReviewCount(languageId: Long, currentTime: Long): Flow<Int>

    @Upsert
    suspend fun insertOrUpdate(review: FlashcardReviewEntity)

    @Query("DELETE FROM flashcard_reviews")
    suspend fun deleteAll()
}
