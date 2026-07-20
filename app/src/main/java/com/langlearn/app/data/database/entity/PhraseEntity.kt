package com.langlearn.app.data.database.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "phrases",
    foreignKeys = [
        ForeignKey(entity = LanguageEntity::class, parentColumns = ["id"], childColumns = ["languageId"], onDelete = ForeignKey.CASCADE),
        ForeignKey(entity = PhraseCategoryEntity::class, parentColumns = ["id"], childColumns = ["categoryId"], onDelete = ForeignKey.CASCADE)
    ],
    indices = [Index("languageId"), Index("categoryId")]
)
data class PhraseEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val languageId: Long,
    val categoryId: Long,
    val phrase: String,
    val translation: String,
    val romanization: String,
    val audioFile: String? = null
)
