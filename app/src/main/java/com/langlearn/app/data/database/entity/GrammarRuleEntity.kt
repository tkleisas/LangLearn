package com.langlearn.app.data.database.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "grammar_rules",
    foreignKeys = [ForeignKey(entity = LanguageEntity::class, parentColumns = ["id"], childColumns = ["languageId"], onDelete = ForeignKey.CASCADE)],
    indices = [Index("languageId"), Index("lessonLevel")]
)
data class GrammarRuleEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val languageId: Long,
    val title: String,
    val explanation: String,
    val example: String,
    val exampleTranslation: String,
    val lessonLevel: Int
)
