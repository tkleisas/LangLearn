package com.langlearn.app.data.database.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "alphabet",
    foreignKeys = [ForeignKey(entity = LanguageEntity::class, parentColumns = ["id"], childColumns = ["languageId"], onDelete = ForeignKey.CASCADE)],
    indices = [Index("languageId")]
)
data class AlphabetEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val languageId: Long,
    val character: String,
    val romanization: String,
    val pronunciationHint: String,
    val isTone: Boolean = false
)
