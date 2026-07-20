package com.langlearn.app.data.database.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "grammar_exercises",
    foreignKeys = [ForeignKey(entity = GrammarRuleEntity::class, parentColumns = ["id"], childColumns = ["grammarRuleId"], onDelete = ForeignKey.CASCADE)],
    indices = [Index("grammarRuleId")]
)
data class GrammarExerciseEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val grammarRuleId: Long,
    val question: String,
    val correctAnswer: String,
    val options: String, // JSON array of options
    val type: String // "multiple_choice" or "fill_blank"
)
