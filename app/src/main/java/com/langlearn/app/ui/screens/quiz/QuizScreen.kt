package com.langlearn.app.ui.screens.quiz

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
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
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
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.langlearn.app.ui.viewmodel.QuizViewModel

private enum class QuizScreenState {
    CONFIG, PLAYING, FINISHED
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuizScreen(
    languageId: Long,
    quizViewModel: QuizViewModel = viewModel(factory = QuizViewModel.Factory),
    onBack: () -> Unit
) {
    val currentQuestion by quizViewModel.currentQuestion.collectAsStateWithLifecycle()
    val options by quizViewModel.options.collectAsStateWithLifecycle()
    val score by quizViewModel.score.collectAsStateWithLifecycle()
    val totalQuestions by quizViewModel.totalQuestions.collectAsStateWithLifecycle()
    val questionIndex by quizViewModel.questionIndex.collectAsStateWithLifecycle()
    val quizType by quizViewModel.quizType.collectAsStateWithLifecycle()
    val isFinished by quizViewModel.isFinished.collectAsStateWithLifecycle()
    val selectedAnswer by quizViewModel.selectedAnswer.collectAsStateWithLifecycle()
    val isAnswerCorrect by quizViewModel.isAnswerCorrect.collectAsStateWithLifecycle()

    var screenState by remember { mutableStateOf(QuizScreenState.CONFIG) }
    var selectedQuizType by remember { mutableStateOf("multiple_choice") }
    var selectedCount by remember { mutableStateOf(10) }

    val correctAnswer = currentQuestion?.translation ?: ""

    val showResult = isAnswerCorrect != null

    LaunchedEffect(isFinished) {
        if (isFinished && screenState == QuizScreenState.PLAYING) {
            screenState = QuizScreenState.FINISHED
        }
    }

    Scaffold(
        topBar = {
            if (screenState == QuizScreenState.PLAYING) {
                TopAppBar(
                    title = {
                        Text("Question ${questionIndex + 1}/$totalQuestions")
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
                        Text(
                            text = "Score: $score",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onPrimary,
                            modifier = Modifier.padding(end = 16.dp)
                        )
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        titleContentColor = MaterialTheme.colorScheme.onPrimary,
                        navigationIconContentColor = MaterialTheme.colorScheme.onPrimary
                    )
                )
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            AnimatedContent(
                targetState = screenState,
                transitionSpec = {
                    fadeIn() togetherWith fadeOut()
                },
                label = "quiz_state"
            ) { state ->
                when (state) {
                    QuizScreenState.CONFIG -> QuizConfigScreen(
                        selectedQuizType = selectedQuizType,
                        onQuizTypeSelected = { selectedQuizType = it },
                        selectedCount = selectedCount,
                        onCountSelected = { selectedCount = it },
                        onStartQuiz = {
                            quizViewModel.startQuiz(languageId, selectedQuizType, selectedCount)
                            screenState = QuizScreenState.PLAYING
                        },
                        onBack = onBack
                    )

                    QuizScreenState.PLAYING -> {
                        if (currentQuestion != null) {
                            QuizPlayingContent(
                                question = currentQuestion!!,
                                options = options,
                                quizType = quizType,
                                showResult = showResult,
                                isAnswerCorrect = isAnswerCorrect,
                                correctAnswer = correctAnswer,
                                selectedAnswer = selectedAnswer,
                                onSelectAnswer = { answer -> quizViewModel.submitAnswer(answer) },
                                onSubmitTyped = { answer -> quizViewModel.submitAnswer(answer) },
                                onNext = { quizViewModel.nextQuestion() }
                            )
                        }
                    }

                    QuizScreenState.FINISHED -> QuizFinishedScreen(
                        score = score,
                        totalQuestions = totalQuestions,
                        onPlayAgain = {
                            selectedQuizType = "multiple_choice"
                            selectedCount = 10
                            screenState = QuizScreenState.CONFIG
                        },
                        onBack = onBack
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun QuizConfigScreen(
    selectedQuizType: String,
    onQuizTypeSelected: (String) -> Unit,
    selectedCount: Int,
    onCountSelected: (Int) -> Unit,
    onStartQuiz: () -> Unit,
    onBack: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        TopAppBar(
            title = { Text("Quiz Setup") },
            navigationIcon = {
                IconButton(onClick = onBack) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back"
                    )
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = MaterialTheme.colorScheme.primary,
                titleContentColor = MaterialTheme.colorScheme.onPrimary,
                navigationIconContentColor = MaterialTheme.colorScheme.onPrimary
            )
        )

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Choose Quiz Type",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurface
        )

        Spacer(modifier = Modifier.height(12.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            QuizTypeCard(
                title = "Multiple Choice",
                description = "Pick the correct translation",
                isSelected = selectedQuizType == "multiple_choice",
                onClick = { onQuizTypeSelected("multiple_choice") },
                modifier = Modifier.weight(1f)
            )
            QuizTypeCard(
                title = "Typing",
                description = "Type the translation",
                isSelected = selectedQuizType == "typing",
                onClick = { onQuizTypeSelected("typing") },
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Number of Questions",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurface
        )

        Spacer(modifier = Modifier.height(12.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            listOf(5, 10, 20).forEach { count ->
                OutlinedButton(
                    onClick = { onCountSelected(count) },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        containerColor = if (selectedCount == count) {
                            MaterialTheme.colorScheme.primaryContainer
                        } else {
                            MaterialTheme.colorScheme.surface
                        },
                        contentColor = if (selectedCount == count) {
                            MaterialTheme.colorScheme.onPrimaryContainer
                        } else {
                            MaterialTheme.colorScheme.onSurface
                        }
                    )
                ) {
                    Text(
                        text = "$count",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        Button(
            onClick = onStartQuiz,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(16.dp)
        ) {
            Text(
                text = "Start Quiz",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun QuizTypeCard(
    title: String,
    description: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    ElevatedCard(
        onClick = onClick,
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.elevatedCardElevation(
            defaultElevation = if (isSelected) 6.dp else 2.dp
        ),
        colors = CardDefaults.elevatedCardColors(
            containerColor = if (isSelected) {
                MaterialTheme.colorScheme.primaryContainer
            } else {
                MaterialTheme.colorScheme.surface
            }
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = if (isSelected) {
                    MaterialTheme.colorScheme.onPrimaryContainer
                } else {
                    MaterialTheme.colorScheme.onSurface
                },
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = description,
                style = MaterialTheme.typography.bodySmall,
                color = if (isSelected) {
                    MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
                } else {
                    MaterialTheme.colorScheme.onSurfaceVariant
                },
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun QuizPlayingContent(
    question: com.langlearn.app.data.database.entity.VocabularyEntity,
    options: List<String>,
    quizType: String,
    showResult: Boolean,
    isAnswerCorrect: Boolean?,
    correctAnswer: String,
    selectedAnswer: String?,
    onSelectAnswer: (String) -> Unit,
    onSubmitTyped: (String) -> Unit,
    onNext: () -> Unit
) {
    val focusManager = LocalFocusManager.current
    var typedAnswer by remember { mutableStateOf("") }

    LaunchedEffect(question.id) {
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
            targetState = question.id,
            transitionSpec = {
                (slideInHorizontally { it } + fadeIn()) togetherWith
                        (slideOutHorizontally { -it } + fadeOut())
            },
            label = "question_content"
        ) { _ ->
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = question.word,
                    style = MaterialTheme.typography.displaySmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    textAlign = TextAlign.Center
                )

                if (question.romanization.isNotBlank()) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = question.romanization,
                        style = MaterialTheme.typography.titleMedium,
                        fontStyle = FontStyle.Italic,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        if (showResult) {
            ResultFeedback(
                isCorrect = isAnswerCorrect ?: false,
                correctAnswer = correctAnswer,
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
            if (quizType == "multiple_choice") {
                MultipleChoiceOptions(
                    options = options,
                    onSelectAnswer = onSelectAnswer
                )
            } else {
                TypingInput(
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
        options.forEach { option ->
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
private fun TypingInput(
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
            label = { Text("Type your answer") },
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
private fun ResultFeedback(
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun QuizFinishedScreen(
    score: Int,
    totalQuestions: Int,
    onPlayAgain: () -> Unit,
    onBack: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        TopAppBar(
            title = { Text("Quiz Complete") },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = MaterialTheme.colorScheme.primary,
                titleContentColor = MaterialTheme.colorScheme.onPrimary
            )
        )

        Spacer(modifier = Modifier.height(16.dp))

        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            val percentage = if (totalQuestions > 0) {
                (score.toFloat() / totalQuestions * 100).toInt()
            } else {
                0
            }

            Text(
                text = "Your Score",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "$score / $totalQuestions",
                style = MaterialTheme.typography.displayMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

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

        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            Button(
                onClick = onPlayAgain,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = "Play Again",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            FilledTonalButton(
                onClick = onBack,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = "Back to Review",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}
