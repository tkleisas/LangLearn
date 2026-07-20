package com.langlearn.app.data.repository

import android.database.Cursor
import com.langlearn.app.data.database.AppDatabase
import com.langlearn.app.data.database.entity.LanguageEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class LanguageRepository(private val db: AppDatabase) {

    suspend fun getLanguages(): List<LanguageEntity> = withContext(Dispatchers.IO) {
        val cursor = db.openHelper.readableDatabase.rawQuery("SELECT * FROM languages", null)
        cursor.use { c ->
            val languages = mutableListOf<LanguageEntity>()
            while (c.moveToNext()) {
                languages.add(cursorToEntity(c))
            }
            languages
        }
    }

    suspend fun getLanguageByCode(code: String): LanguageEntity? = withContext(Dispatchers.IO) {
        val cursor = db.openHelper.readableDatabase.rawQuery(
            "SELECT * FROM languages WHERE code = ?",
            arrayOf(code)
        )
        cursor.use { c ->
            if (c.moveToFirst()) cursorToEntity(c) else null
        }
    }

    suspend fun getLanguageById(id: Long): LanguageEntity? = withContext(Dispatchers.IO) {
        val cursor = db.openHelper.readableDatabase.rawQuery(
            "SELECT * FROM languages WHERE id = ?",
            arrayOf(id.toString())
        )
        cursor.use { c ->
            if (c.moveToFirst()) cursorToEntity(c) else null
        }
    }

    private fun cursorToEntity(cursor: Cursor): LanguageEntity {
        return LanguageEntity(
            id = cursor.getLong(cursor.getColumnIndexOrThrow("id")),
            code = cursor.getString(cursor.getColumnIndexOrThrow("code")),
            name = cursor.getString(cursor.getColumnIndexOrThrow("name")),
            nativeName = cursor.getString(cursor.getColumnIndexOrThrow("nativeName"))
        )
    }
}
