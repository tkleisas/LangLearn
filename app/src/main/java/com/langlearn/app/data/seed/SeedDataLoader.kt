package com.langlearn.app.data.seed

import android.content.ContentValues
import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.langlearn.app.data.database.AppDatabase
import com.langlearn.app.data.database.entity.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

data class SeedData(
    val language: LanguageEntry,
    val vocabulary: List<VocabEntry>,
    val phraseCategories: List<CatEntry>,
    val phrases: List<PhraseEntry>,
    val grammarRules: List<GrammarEntry>,
    val grammarExercises: List<ExerciseEntry>,
    val alphabet: List<AlphaEntry>
)

data class LanguageEntry(val code: String, val name: String, val nativeName: String)
data class VocabEntry(val word: String, val translation: String, val romanization: String, val partOfSpeech: String, val lessonLevel: Int)
data class CatEntry(val name: String)
data class PhraseEntry(val categoryIndex: Int, val phrase: String, val translation: String, val romanization: String)
data class GrammarEntry(val title: String, val explanation: String, val example: String, val exampleTranslation: String, val lessonLevel: Int)
data class ExerciseEntry(val grammarRuleIndex: Int, val question: String, val correctAnswer: String, val options: String, val type: String)
data class AlphaEntry(val character: String, val romanization: String, val pronunciationHint: String, val isTone: Boolean)

class SeedDataLoader {

    private val gson = Gson()

    suspend fun loadSeedData(context: Context, database: AppDatabase) {
        withContext(Dispatchers.IO) {
            val db = database.openHelper.writableDatabase
            val cursor = db.rawQuery("SELECT COUNT(*) FROM languages", null)
            var existingCount = 0
            if (cursor.moveToFirst()) {
                existingCount = cursor.getInt(0)
            }
            cursor.close()

            if (existingCount > 0) return@withContext

            val seedFiles = try {
                context.assets.list("data")
            } catch (e: Exception) {
                null
            } ?: return@withContext

            for (fileName in seedFiles) {
                if (fileName.endsWith("_seed.json")) {
                    loadSeedFile(context, database, fileName)
                }
            }
        }
    }

    private fun loadSeedFile(context: Context, database: AppDatabase, fileName: String) {
        val json = context.assets.open("data/$fileName")
            .bufferedReader()
            .use { it.readText() }
        val seed: SeedData = gson.fromJson(json, object : TypeToken<SeedData>() {}.type)

        val db = database.openHelper.writableDatabase

        db.beginTransaction()
        try {
            val langValues = ContentValues().apply {
                put("code", seed.language.code)
                put("name", seed.language.name)
                put("nativeName", seed.language.nativeName)
            }
            val languageId = db.insert("languages", null, langValues)

            seed.vocabulary.forEach { v ->
                val values = ContentValues().apply {
                    put("languageId", languageId)
                    put("word", v.word)
                    put("translation", v.translation)
                    put("romanization", v.romanization)
                    put("partOfSpeech", v.partOfSpeech)
                    put("lessonLevel", v.lessonLevel)
                }
                db.insert("vocabulary", null, values)
            }

            val categoryIds = mutableMapOf<Int, Long>()
            seed.phraseCategories.forEachIndexed { index, c ->
                val values = ContentValues().apply {
                    put("name", c.name)
                    put("languageId", languageId)
                }
                categoryIds[index] = db.insert("phrase_categories", null, values)
            }

            seed.phrases.forEach { p ->
                val catId = categoryIds[p.categoryIndex] ?: 0L
                val values = ContentValues().apply {
                    put("languageId", languageId)
                    put("categoryId", catId)
                    put("phrase", p.phrase)
                    put("translation", p.translation)
                    put("romanization", p.romanization)
                }
                db.insert("phrases", null, values)
            }

            val ruleIds = mutableMapOf<Int, Long>()
            seed.grammarRules.forEachIndexed { index, r ->
                val values = ContentValues().apply {
                    put("languageId", languageId)
                    put("title", r.title)
                    put("explanation", r.explanation)
                    put("example", r.example)
                    put("exampleTranslation", r.exampleTranslation)
                    put("lessonLevel", r.lessonLevel)
                }
                ruleIds[index] = db.insert("grammar_rules", null, values)
            }

            seed.grammarExercises.forEach { e ->
                val ruleId = ruleIds[e.grammarRuleIndex] ?: 0L
                val values = ContentValues().apply {
                    put("grammarRuleId", ruleId)
                    put("question", e.question)
                    put("correctAnswer", e.correctAnswer)
                    put("options", e.options)
                    put("type", e.type)
                }
                db.insert("grammar_exercises", null, values)
            }

            seed.alphabet.forEach { a ->
                val values = ContentValues().apply {
                    put("languageId", languageId)
                    put("character", a.character)
                    put("romanization", a.romanization)
                    put("pronunciationHint", a.pronunciationHint)
                    put("isTone", if (a.isTone) 1 else 0)
                }
                db.insert("alphabet", null, values)
            }

            db.setTransactionSuccessful()
        } finally {
            db.endTransaction()
        }
    }
}
