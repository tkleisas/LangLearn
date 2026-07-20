package com.langlearn.app.data.repository

import com.langlearn.app.data.database.AppDatabase
import com.langlearn.app.data.database.entity.GrammarExerciseEntity
import com.langlearn.app.data.database.entity.GrammarRuleEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

class GrammarRepository(private val db: AppDatabase) {
    private val dao get() = db.grammarDao()

    fun getRulesForLevel(languageId: Long, level: Int): Flow<List<GrammarRuleEntity>> =
        dao.getRulesByLevel(languageId, level)

    fun getExercisesForRule(ruleId: Long): Flow<List<GrammarExerciseEntity>> =
        dao.getExercisesForRule(ruleId)

    suspend fun getMaxLevel(languageId: Long): Int = withContext(Dispatchers.IO) {
        val cursor = db.openHelper.readableDatabase.query(
            "SELECT MAX(lessonLevel) FROM grammar_rules WHERE languageId = ?",
            arrayOf(languageId.toString())
        )
        cursor.use { c ->
            if (c.moveToFirst()) c.getInt(0) else 0
        }
    }
}
