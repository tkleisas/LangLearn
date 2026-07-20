package com.langlearn.app.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.langlearn.app.LangLearnApp
import com.langlearn.app.data.database.entity.AlphabetEntity
import com.langlearn.app.data.database.entity.LanguageEntity
import com.langlearn.app.data.repository.LanguageRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

data class NumberEntry(val number: Int, val word: String = "")
data class DateEntry(val category: String, val entries: List<Pair<String, String>>)

class ReferenceViewModel(
    private val languageRepo: LanguageRepository
) : ViewModel() {

    private val _alphabetList = MutableStateFlow<List<AlphabetEntity>>(emptyList())
    val alphabetList: StateFlow<List<AlphabetEntity>> = _alphabetList.asStateFlow()

    private val _selectedCategory = MutableStateFlow("alphabet")
    val selectedCategory: StateFlow<String> = _selectedCategory.asStateFlow()

    private val _currentLanguage = MutableStateFlow(LanguageEntity())
    val currentLanguage: StateFlow<LanguageEntity> = _currentLanguage.asStateFlow()

    private val _numbers = MutableStateFlow<List<NumberEntry>>(emptyList())
    val numbers: StateFlow<List<NumberEntry>> = _numbers.asStateFlow()

    private val _datesInfo = MutableStateFlow<List<DateEntry>>(emptyList())
    val datesInfo: StateFlow<List<DateEntry>> = _datesInfo.asStateFlow()

    fun loadAlphabet(languageId: Long) {
        viewModelScope.launch {
            val lang = languageRepo.getLanguageById(languageId)
            if (lang != null) {
                _currentLanguage.value = lang
            }
            _selectedCategory.value = "alphabet"
            _alphabetList.value = queryAlphabet(languageId)
        }
    }

    fun loadNumbers(languageId: Long) {
        viewModelScope.launch {
            val lang = languageRepo.getLanguageById(languageId)
            if (lang != null) {
                _currentLanguage.value = lang
            }
            _selectedCategory.value = "numbers"
            _numbers.value = generateNumbers()
        }
    }

    fun loadDatesInfo(languageId: Long) {
        viewModelScope.launch {
            val lang = languageRepo.getLanguageById(languageId)
            if (lang != null) {
                _currentLanguage.value = lang
            }
            _selectedCategory.value = "dates"
            _datesInfo.value = generateDatesInfo()
        }
    }

    private suspend fun queryAlphabet(languageId: Long): List<AlphabetEntity> = withContext(Dispatchers.IO) {
        val db = LangLearnApp.instance.database
        val cursor = db.openHelper.readableDatabase.query(
            "SELECT * FROM alphabet WHERE languageId = ? ORDER BY id",
            arrayOf(languageId.toString())
        )
        cursor.use { c ->
            val list = mutableListOf<AlphabetEntity>()
            while (c.moveToNext()) {
                list.add(
                    AlphabetEntity(
                        id = c.getLong(c.getColumnIndexOrThrow("id")),
                        languageId = c.getLong(c.getColumnIndexOrThrow("languageId")),
                        character = c.getString(c.getColumnIndexOrThrow("character")),
                        romanization = c.getString(c.getColumnIndexOrThrow("romanization")),
                        pronunciationHint = c.getString(c.getColumnIndexOrThrow("pronunciationHint")),
                        isTone = c.getInt(c.getColumnIndexOrThrow("isTone")) != 0
                    )
                )
            }
            list
        }
    }

    private fun generateNumbers(): List<NumberEntry> {
        val specialNumbers = listOf(
            0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10,
            11, 12, 13, 14, 15, 16, 17, 18, 19, 20,
            30, 40, 50, 60, 70, 80, 90,
            100, 1000, 10000
        )
        return specialNumbers.map { NumberEntry(it) }
    }

    private fun generateDatesInfo(): List<DateEntry> {
        val daysOfWeek = listOf(
            "monday" to "Monday",
            "tuesday" to "Tuesday",
            "wednesday" to "Wednesday",
            "thursday" to "Thursday",
            "friday" to "Friday",
            "saturday" to "Saturday",
            "sunday" to "Sunday"
        )
        val months = listOf(
            "january" to "January", "february" to "February", "march" to "March",
            "april" to "April", "may" to "May", "june" to "June",
            "july" to "July", "august" to "August", "september" to "September",
            "october" to "October", "november" to "November", "december" to "December"
        )
        val dateVocab = listOf(
            "today" to "Today",
            "tomorrow" to "Tomorrow",
            "yesterday" to "Yesterday",
            "day" to "Day",
            "week" to "Week",
            "month" to "Month",
            "year" to "Year"
        )
        return listOf(
            DateEntry("days_of_week", daysOfWeek),
            DateEntry("months", months),
            DateEntry("date_vocabulary", dateVocab)
        )
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val db = LangLearnApp.instance.database
                ReferenceViewModel(
                    languageRepo = LanguageRepository(db)
                )
            }
        }
    }
}
