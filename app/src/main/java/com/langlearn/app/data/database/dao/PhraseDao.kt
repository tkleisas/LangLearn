package com.langlearn.app.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.langlearn.app.data.database.entity.PhraseCategoryEntity
import com.langlearn.app.data.database.entity.PhraseEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PhraseDao {

    @Query("SELECT * FROM phrase_categories WHERE languageId = :languageId")
    fun getCategories(languageId: Long): Flow<List<PhraseCategoryEntity>>

    @Query("SELECT * FROM phrases WHERE categoryId = :categoryId")
    fun getPhrasesByCategory(categoryId: Long): Flow<List<PhraseEntity>>

    @Query(
        """
        SELECT * FROM phrases
        WHERE languageId = :languageId
        AND (phrase LIKE '%' || :query || '%' OR translation LIKE '%' || :query || '%')
        """
    )
    fun searchPhrases(languageId: Long, query: String): Flow<List<PhraseEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCategories(categories: List<PhraseCategoryEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPhrases(phrases: List<PhraseEntity>)
}
