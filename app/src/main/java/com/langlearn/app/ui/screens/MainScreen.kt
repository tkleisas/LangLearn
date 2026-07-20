package com.langlearn.app.ui.screens

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.langlearn.app.ui.navigation.Routes
import com.langlearn.app.ui.navigation.Screen
import com.langlearn.app.ui.navigation.bottomNavItems
import com.langlearn.app.ui.screens.flashcards.FlashcardScreen
import com.langlearn.app.ui.screens.grammar.GrammarExerciseScreen
import com.langlearn.app.ui.screens.home.HomeScreen
import com.langlearn.app.ui.screens.lessons.GrammarLessonScreen
import com.langlearn.app.ui.screens.lessons.LessonsScreen
import com.langlearn.app.ui.screens.lessons.VocabularyLessonScreen
import com.langlearn.app.ui.screens.phrasebook.PhrasebookScreen
import com.langlearn.app.ui.screens.pronunciation.PronunciationScreen
import com.langlearn.app.ui.screens.quiz.QuizScreen
import com.langlearn.app.ui.screens.reference.ReferenceScreen
import com.langlearn.app.ui.screens.review.ReviewScreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen() {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val isMainRoute = currentRoute in listOf(
        Screen.Home.route, Screen.Lessons.route,
        Screen.Review.route, Screen.Reference.route
    )

    Scaffold(
        topBar = {
            if (isMainRoute) {
                TopAppBar(
                    title = { Text("LangLearn") },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        titleContentColor = MaterialTheme.colorScheme.onPrimary
                    )
                )
            }
        },
        bottomBar = {
            if (isMainRoute) {
                NavigationBar {
                    bottomNavItems.forEach { screen ->
                        NavigationBarItem(
                            icon = {
                                Icon(
                                    imageVector = if (currentRoute == screen.route) screen.selectedIcon else screen.icon,
                                    contentDescription = screen.title
                                )
                            },
                            label = { Text(screen.title) },
                            selected = currentRoute == screen.route,
                            onClick = {
                                navController.navigate(screen.route) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Home.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            // Main tabs
            composable(Screen.Home.route) {
                HomeScreen(
                    onNavigateToLessons = {
                        navController.navigate(Screen.Lessons.route) {
                            popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    },
                    onNavigateToFlashcards = { langId ->
                        navController.navigate(Routes.flashcards(langId))
                    },
                    onNavigateToPhrasebook = { langId ->
                        navController.navigate(Routes.phrasebook(langId))
                    }
                )
            }

            composable(Screen.Lessons.route) {
                LessonsScreen(
                    onVocabularyLesson = { langId, level ->
                        navController.navigate(Routes.vocabLesson(langId, level))
                    },
                    onGrammarLesson = { langId, level ->
                        navController.navigate(Routes.grammarLesson(langId, level))
                    }
                )
            }

            composable(Screen.Review.route) {
                ReviewScreen(
                    languageId = 1L,
                    onStartFlashcards = { langId ->
                        navController.navigate(Routes.flashcards(langId))
                    },
                    onStartQuiz = { langId ->
                        navController.navigate(Routes.quiz(langId))
                    }
                )
            }

            composable(Screen.Reference.route) {
                ReferenceScreen(
                    languageId = 1L,
                    onBack = { navController.popBackStack() }
                )
            }

            // Sub-screens
            composable(
                route = Routes.VOCABULARY_LESSON,
                arguments = listOf(
                    navArgument("languageId") { type = NavType.LongType },
                    navArgument("level") { type = NavType.IntType }
                )
            ) { backStackEntry ->
                val langId = backStackEntry.arguments?.getLong("languageId") ?: 1L
                val level = backStackEntry.arguments?.getInt("level") ?: 1
                VocabularyLessonScreen(
                    languageId = langId,
                    level = level,
                    onBack = { navController.popBackStack() }
                )
            }

            composable(
                route = Routes.GRAMMAR_LESSON,
                arguments = listOf(
                    navArgument("languageId") { type = NavType.LongType },
                    navArgument("level") { type = NavType.IntType }
                )
            ) { backStackEntry ->
                val langId = backStackEntry.arguments?.getLong("languageId") ?: 1L
                val level = backStackEntry.arguments?.getInt("level") ?: 1
                GrammarLessonScreen(
                    languageId = langId,
                    level = level,
                    onBack = { navController.popBackStack() },
                    onStartExercises = { ruleId ->
                        navController.navigate(Routes.grammarExercise(ruleId))
                    }
                )
            }

            composable(
                route = Routes.FLASHCARDS,
                arguments = listOf(navArgument("languageId") { type = NavType.LongType })
            ) { backStackEntry ->
                val langId = backStackEntry.arguments?.getLong("languageId") ?: 1L
                FlashcardScreen(
                    languageId = langId,
                    onBack = { navController.popBackStack() }
                )
            }

            composable(
                route = Routes.QUIZ,
                arguments = listOf(navArgument("languageId") { type = NavType.LongType })
            ) { backStackEntry ->
                val langId = backStackEntry.arguments?.getLong("languageId") ?: 1L
                QuizScreen(
                    languageId = langId,
                    onBack = { navController.popBackStack() }
                )
            }

            composable(
                route = Routes.PHRASEBOOK,
                arguments = listOf(navArgument("languageId") { type = NavType.LongType })
            ) { backStackEntry ->
                val langId = backStackEntry.arguments?.getLong("languageId") ?: 1L
                PhrasebookScreen(
                    languageId = langId,
                    onBack = { navController.popBackStack() }
                )
            }

            composable(
                route = Routes.GRAMMAR_EXERCISE,
                arguments = listOf(navArgument("ruleId") { type = NavType.LongType })
            ) { backStackEntry ->
                val ruleId = backStackEntry.arguments?.getLong("ruleId") ?: 1L
                GrammarExerciseScreen(
                    ruleId = ruleId,
                    onBack = { navController.popBackStack() }
                )
            }

            composable(
                route = Routes.PRONUNCIATION,
                arguments = listOf(navArgument("languageId") { type = NavType.LongType })
            ) { backStackEntry ->
                val langId = backStackEntry.arguments?.getLong("languageId") ?: 1L
                PronunciationScreen(
                    languageId = langId,
                    onBack = { navController.popBackStack() }
                )
            }
        }
    }
}
