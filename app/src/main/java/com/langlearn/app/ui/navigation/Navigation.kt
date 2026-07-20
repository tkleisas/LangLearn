package com.langlearn.app.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LibraryBooks
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.RateReview
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.LibraryBooks
import androidx.compose.material.icons.outlined.MenuBook
import androidx.compose.material.icons.outlined.RateReview
import androidx.compose.ui.graphics.vector.ImageVector

sealed class Screen(
    val route: String,
    val title: String,
    val icon: ImageVector,
    val selectedIcon: ImageVector
) {
    object Home : Screen("home", "Home", Icons.Outlined.Home, Icons.Filled.Home)
    object Lessons : Screen("lessons", "Lessons", Icons.Outlined.MenuBook, Icons.Filled.MenuBook)
    object Review : Screen("review", "Review", Icons.Outlined.RateReview, Icons.Filled.RateReview)
    object Reference : Screen("reference", "Reference", Icons.Outlined.LibraryBooks, Icons.Filled.LibraryBooks)
}

val bottomNavItems = listOf(Screen.Home, Screen.Lessons, Screen.Review, Screen.Reference)

object Routes {
    const val VOCABULARY_LESSON = "vocabulary_lesson/{languageId}/{level}"
    const val GRAMMAR_LESSON = "grammar_lesson/{languageId}/{level}"
    const val FLASHCARDS = "flashcards/{languageId}"
    const val QUIZ = "quiz/{languageId}"
    const val PHRASEBOOK = "phrasebook/{languageId}"
    const val GRAMMAR_EXERCISE = "grammar_exercise/{ruleId}"
    const val PRONUNCIATION = "pronunciation/{languageId}"
    const val ALPHABET = "alphabet/{languageId}"
    const val NUMBERS = "numbers/{languageId}"
    const val DATES = "dates/{languageId}"

    fun vocabLesson(langId: Long, level: Int) = "vocabulary_lesson/$langId/$level"
    fun grammarLesson(langId: Long, level: Int) = "grammar_lesson/$langId/$level"
    fun flashcards(langId: Long) = "flashcards/$langId"
    fun quiz(langId: Long) = "quiz/$langId"
    fun phrasebook(langId: Long) = "phrasebook/$langId"
    fun grammarExercise(ruleId: Long) = "grammar_exercise/$ruleId"
    fun pronunciation(langId: Long) = "pronunciation/$langId"
    fun alphabet(langId: Long) = "alphabet/$langId"
    fun numbers(langId: Long) = "numbers/$langId"
    fun dates(langId: Long) = "dates/$langId"
}
