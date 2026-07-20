package com.langlearn.app.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.langlearn.app.data.database.entity.GrammarExerciseEntity
import com.langlearn.app.data.database.entity.GrammarRuleEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface GrammarDao {

    @Query("SELECT * FROM grammar_rules WHERE languageId = :languageId AND lessonLevel = :lessonLevel")
    fun getRulesByLevel(languageId: Long, lessonLevel: Int): Flow<List<GrammarRuleEntity>>

    @Query("SELECT * FROM grammar_exercises WHERE grammarRuleId = :grammarRuleId")
    fun getExercisesForRule(grammarRuleId: Long): Flow<List<GrammarExerciseEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRules(rules: List<GrammarRuleEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertExercises(exercises: List<GrammarExerciseEntity>)
}
