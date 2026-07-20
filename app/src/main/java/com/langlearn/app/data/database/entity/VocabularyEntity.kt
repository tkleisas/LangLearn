package com.langlearn.app.data.database.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "vocabulary",
    foreignKeys = [ForeignKey(
        entity = LanguageEntity::class,
        parentColumns = ["id"],
        childColumns = ["languageId"],
        onDelete = ForeignKey.CASCADE
    )],
    indices = [Index("languageId"), Index("lessonLevel")]
)
data class VocabularyEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val languageId: Long,
    val word: String,
    val translation: String,
    val romanization: String,
    val partOfSpeech: String,
    val lessonLevel: Int,
    val audioFile: String? = null
)
