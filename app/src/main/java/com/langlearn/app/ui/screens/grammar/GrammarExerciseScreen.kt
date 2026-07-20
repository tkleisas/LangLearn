package com.langlearn.app.ui.screens.grammar

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.langlearn.app.ui.viewmodel.GrammarExerciseViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GrammarExerciseScreen(
    ruleId: Long,
    grammarExerciseViewModel: GrammarExerciseViewModel = viewModel(factory = GrammarExerciseViewModel.Factory),
    onBack: () -> Unit
) {
    val currentExercise by grammarExerciseViewModel.currentExercise.collectAsStateWithLifecycle()
    val score by grammarExerciseViewModel.score.collectAsStateWithLifecycle()
    val exercises by grammarExerciseViewModel.exercises.collectAsStateWithLifecycle()
    val currentIndex by grammarExerciseViewModel.currentIndex.collectAsStateWithLifecycle()
    val isFinished by grammarExerciseViewModel.isFinished.collectAsStateWithLifecycle()
    val selectedAnswer by grammarExerciseViewModel.selectedAnswer.collectAsStateWithLifecycle()
    val showResult by grammarExerciseViewModel.showResult.collectAsStateWithLifecycle()

    LaunchedEffect(ruleId) {
        grammarExerciseViewModel.loadExercises(ruleId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text("Grammar Exercise")
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                actions = {
                    if (!isFinished && exercises.isNotEmpty()) {
                        Row(
                            modifier = Modifier.padding(end = 16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "${currentIndex + 1}/${exercises.size}",
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.onPrimary
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                text = "Score: $score",
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.onPrimary
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            if (isFinished) {
                ExerciseFinishedScreen(
                    score = score,
                    totalExercises = exercises.size,
                    onBack = onBack
                )
            } else if (currentExercise != null) {
                ExerciseContent(
                    exercise = currentExercise!!,
                    options = grammarExerciseViewModel.getOptions(currentExercise!!),
                    showResult = showResult,
                    selectedAnswer = selectedAnswer,
                    onSelectAnswer = { answer ->
                        grammarExerciseViewModel.submitAnswer(answer)
                    },
                    onSubmitTyped = { answer ->
                        grammarExerciseViewModel.submitAnswer(answer)
                    },
                    onNext = {
                        grammarExerciseViewModel.nextExercise()
                    }
                )
            } else {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No exercises available",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Composable
private fun ExerciseContent(
    exercise: com.langlearn.app.data.database.entity.GrammarExerciseEntity,
    options: List<String>,
    showResult: Boolean,
    selectedAnswer: String?,
    onSelectAnswer: (String) -> Unit,
    onSubmitTyped: (String) -> Unit,
    onNext: () -> Unit
) {
    val focusManager = LocalFocusManager.current
    var typedAnswer by remember { mutableStateOf("") }

    LaunchedEffect(exercise.id) {
        typedAnswer = ""
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        AnimatedContent(
            targetState = exercise.id,
            transitionSpec = {
                (slideInHorizontally { it } + fadeIn()) togetherWith
                        (slideOutHorizontally { -it } + fadeOut())
            },
            label = "exercise_content"
        ) { _ ->
            ElevatedCard(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.elevatedCardElevation(defaultElevation = 4.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp)
                ) {
                    Text(
                        text = "Type: ${exercise.type.replaceFirstChar { it.uppercase() }.replace("_", " ")}",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.SemiBold
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = exercise.question,
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        if (showResult) {
            val isCorrect = selectedAnswer?.trim()?.equals(
                exercise.correctAnswer.trim(), ignoreCase = true
            ) ?: false

            ExerciseResultFeedback(
                isCorrect = isCorrect,
                correctAnswer = exercise.correctAnswer,
                selectedAnswer = selectedAnswer
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = onNext,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = "Next",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.SemiBold
                )
            }
        } else {
            if (exercise.type == "multiple_choice") {
                MultipleChoiceOptions(
                    options = options,
                    onSelectAnswer = onSelectAnswer
                )
            } else {
                FillBlankInput(
                    typedAnswer = typedAnswer,
                    onTypedAnswerChange = { typedAnswer = it },
                    onSubmit = {
                        if (typedAnswer.isNotBlank()) {
                            onSubmitTyped(typedAnswer.trim())
                            focusManager.clearFocus()
                        }
                    }
                )
            }
        }

        Spacer(modifier = Modifier.weight(1f))
    }
}

@Composable
private fun MultipleChoiceOptions(
    options: List<String>,
    onSelectAnswer: (String) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        options.forEachIndexed { index, option ->
            OutlinedButton(
                onClick = { onSelectAnswer(option) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.outlinedButtonColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            ) {
                Text(
                    text = option,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurface,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Composable
private fun FillBlankInput(
    typedAnswer: String,
    onTypedAnswerChange: (String) -> Unit,
    onSubmit: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        OutlinedTextField(
            value = typedAnswer,
            onValueChange = onTypedAnswerChange,
            label = { Text("Your answer") },
            singleLine = true,
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
            keyboardActions = KeyboardActions(onDone = { onSubmit() }),
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = onSubmit,
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            shape = RoundedCornerShape(12.dp),
            enabled = typedAnswer.isNotBlank()
        ) {
            Text(
                text = "Submit",
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

@Composable
private fun ExerciseResultFeedback(
    isCorrect: Boolean,
    correctAnswer: String,
    selectedAnswer: String?
) {
    ElevatedCard(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.elevatedCardColors(
            containerColor = if (isCorrect) {
                Color(0xFFE8F5E9)
            } else {
                Color(0xFFFFEBEE)
            }
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = if (isCorrect) Icons.Filled.CheckCircle else Icons.Filled.Cancel,
                contentDescription = null,
                modifier = Modifier.size(40.dp),
                tint = if (isCorrect) Color(0xFF4CAF50) else Color(0xFFEF5350)
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = if (isCorrect) "Correct!" else "Incorrect",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = if (isCorrect) Color(0xFF2E7D32) else Color(0xFFC62828)
            )

            if (!isCorrect) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Your answer: ${selectedAnswer ?: "—"}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color(0xFFC62828).copy(alpha = 0.8f)
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Correct answer: $correctAnswer",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium,
                    color = Color(0xFF2E7D32)
                )
            }
        }
    }
}

@Composable
private fun ExerciseFinishedScreen(
    score: Int,
    totalExercises: Int,
    onBack: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Exercises Complete!",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )

        Spacer(modifier = Modifier.height(32.dp))

        ElevatedCard(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            elevation = CardDefaults.elevatedCardElevation(defaultElevation = 4.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Your Score",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "$score / $totalExercises",
                    style = MaterialTheme.typography.displayMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )

                val percentage = if (totalExercises > 0) {
                    (score.toFloat() / totalExercises * 100).toInt()
                } else {
                    0
                }

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "$percentage%",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = if (percentage >= 80) Color(0xFF4CAF50)
                    else if (percentage >= 50) Color(0xFFFF9800)
                    else Color(0xFFEF5350)
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        FilledTonalButton(
            onClick = onBack,
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text(
                text = "Back",
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}
