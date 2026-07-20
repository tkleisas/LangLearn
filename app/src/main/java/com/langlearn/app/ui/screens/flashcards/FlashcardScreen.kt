package com.langlearn.app.ui.screens.flashcards

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Error
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.langlearn.app.ui.viewmodel.FlashcardSessionStats
import com.langlearn.app.ui.viewmodel.FlashcardViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FlashcardScreen(
    languageId: Long,
    flashcardViewModel: FlashcardViewModel = viewModel(factory = FlashcardViewModel.Factory),
    onBack: () -> Unit
) {
    val currentCard by flashcardViewModel.currentCard.collectAsStateWithLifecycle()
    val dueCount by flashcardViewModel.dueCount.collectAsStateWithLifecycle()
    val sessionStats by flashcardViewModel.sessionStats.collectAsStateWithLifecycle()
    val isSessionComplete by flashcardViewModel.isSessionComplete.collectAsStateWithLifecycle()

    var isFlipped by remember { mutableStateOf(false) }

    LaunchedEffect(languageId) {
        flashcardViewModel.startReview(languageId)
    }

    LaunchedEffect(currentCard) {
        isFlipped = false
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text("Flashcards")
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
                    if (!isSessionComplete && dueCount > 0) {
                        Text(
                            text = "${sessionStats.reviewed + 1}/$dueCount",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onPrimary,
                            modifier = Modifier.padding(end = 16.dp)
                        )
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
        AnimatedContent(
            targetState = isSessionComplete,
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            transitionSpec = {
                fadeIn() togetherWith fadeOut()
            },
            label = "session_state"
        ) { complete ->
            if (complete) {
                CompletionSummary(
                    stats = sessionStats,
                    onBack = onBack
                )
            } else if (currentCard != null) {
                FlashcardContent(
                    card = currentCard!!,
                    isFlipped = isFlipped,
                    onFlip = { isFlipped = !isFlipped },
                    sessionStats = sessionStats,
                    onRateCard = { quality ->
                        flashcardViewModel.rateCard(quality)
                    }
                )
            } else {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No cards to review",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Composable
private fun FlashcardContent(
    card: com.langlearn.app.data.database.entity.VocabularyEntity,
    isFlipped: Boolean,
    onFlip: () -> Unit,
    sessionStats: FlashcardSessionStats,
    onRateCard: (Int) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(24.dp))

        AnimatedContent(
            targetState = card.id,
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            transitionSpec = {
                (slideInHorizontally { it } + fadeIn()) togetherWith
                        (slideOutHorizontally { -it } + fadeOut())
            },
            label = "card_content"
        ) { _ ->
            CardFace(
                card = card,
                isFlipped = isFlipped,
                onFlip = onFlip
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        RatingButtons(onRateCard = onRateCard)

        Spacer(modifier = Modifier.height(16.dp))

        SessionStatsRow(stats = sessionStats)
    }
}

@Composable
private fun CardFace(
    card: com.langlearn.app.data.database.entity.VocabularyEntity,
    isFlipped: Boolean,
    onFlip: () -> Unit
) {
    val key = "${card.id}_$isFlipped"

    AnimatedContent(
        targetState = key,
        transitionSpec = {
            fadeIn() togetherWith fadeOut()
        },
        label = "card_face"
    ) { _ ->
        ElevatedCard(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onFlip() },
            shape = RoundedCornerShape(16.dp),
            elevation = CardDefaults.elevatedCardElevation(defaultElevation = 6.dp),
            colors = CardDefaults.elevatedCardColors(
                containerColor = if (isFlipped) {
                    MaterialTheme.colorScheme.secondaryContainer
                } else {
                    MaterialTheme.colorScheme.surface
                }
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                if (!isFlipped) {
                    Text(
                        text = card.word,
                        style = MaterialTheme.typography.displaySmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface,
                        textAlign = TextAlign.Center
                    )

                    if (card.romanization.isNotBlank()) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = card.romanization,
                            style = MaterialTheme.typography.titleMedium,
                            fontStyle = FontStyle.Italic,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center
                        )
                    }
                } else {
                    Text(
                        text = card.translation,
                        style = MaterialTheme.typography.displaySmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSecondaryContainer,
                        textAlign = TextAlign.Center
                    )

                    if (card.word.isNotBlank()) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = card.word,
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.7f),
                            textAlign = TextAlign.Center
                        )
                    }
                }

                if (card.partOfSpeech.isNotBlank() && !isFlipped) {
                    Spacer(modifier = Modifier.height(16.dp))
                    PartOfSpeechBadge(label = card.partOfSpeech)
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = if (isFlipped) "Tap to show word" else "Tap to reveal translation",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                )
            }
        }
    }
}

@Composable
private fun RatingButtons(onRateCard: (Int) -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        RatingButton(
            label = "Again",
            quality = 1,
            containerColor = Color(0xFFEF5350),
            contentColor = Color.White,
            onClick = onRateCard,
            modifier = Modifier.weight(1f)
        )
        RatingButton(
            label = "Hard",
            quality = 3,
            containerColor = Color(0xFFFF9800),
            contentColor = Color.White,
            onClick = onRateCard,
            modifier = Modifier.weight(1f)
        )
        RatingButton(
            label = "Good",
            quality = 4,
            containerColor = Color(0xFF4CAF50),
            contentColor = Color.White,
            onClick = onRateCard,
            modifier = Modifier.weight(1f)
        )
        RatingButton(
            label = "Easy",
            quality = 5,
            containerColor = Color(0xFF2196F3),
            contentColor = Color.White,
            onClick = onRateCard,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun RatingButton(
    label: String,
    quality: Int,
    containerColor: Color,
    contentColor: Color,
    onClick: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    OutlinedButton(
        onClick = { onClick(quality) },
        modifier = modifier.height(48.dp),
        shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.outlinedButtonColors(
            containerColor = containerColor,
            contentColor = contentColor
        )
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.SemiBold
        )
    }
}

@Composable
private fun SessionStatsRow(stats: FlashcardSessionStats) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Filled.CheckCircle,
            contentDescription = null,
            modifier = Modifier.size(16.dp),
            tint = Color(0xFF4CAF50)
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = "${stats.correct}",
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.SemiBold,
            color = Color(0xFF4CAF50)
        )
        Spacer(modifier = Modifier.width(16.dp))
        Icon(
            imageVector = Icons.Filled.Error,
            contentDescription = null,
            modifier = Modifier.size(16.dp),
            tint = Color(0xFFEF5350)
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = "${stats.incorrect}",
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.SemiBold,
            color = Color(0xFFEF5350)
        )
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            text = "Reviewed: ${stats.reviewed}",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun CompletionSummary(
    stats: FlashcardSessionStats,
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
            text = "Session Complete!",
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
                SummaryStatItem(
                    label = "Reviewed",
                    value = "${stats.reviewed}"
                )
                Spacer(modifier = Modifier.height(12.dp))
                SummaryStatItem(
                    label = "Correct",
                    value = "${stats.correct}",
                    valueColor = Color(0xFF4CAF50)
                )
                Spacer(modifier = Modifier.height(12.dp))
                SummaryStatItem(
                    label = "Incorrect",
                    value = "${stats.incorrect}",
                    valueColor = Color(0xFFEF5350)
                )
                Spacer(modifier = Modifier.height(12.dp))

                val accuracy = if (stats.reviewed > 0) {
                    (stats.correct.toFloat() / stats.reviewed * 100).toInt()
                } else {
                    0
                }

                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Accuracy",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "${accuracy}%",
                    style = MaterialTheme.typography.headlineLarge,
                    fontWeight = FontWeight.Bold,
                    color = if (accuracy >= 80) Color(0xFF4CAF50)
                    else if (accuracy >= 50) Color(0xFFFF9800)
                    else Color(0xFFEF5350)
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        FilledTonalButton(
            onClick = onBack,
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
        ) {
            Text(
                text = "Back to Review",
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

@Composable
private fun SummaryStatItem(
    label: String,
    value: String,
    valueColor: Color = MaterialTheme.colorScheme.onSurface
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = valueColor
        )
    }
}

@Composable
private fun PartOfSpeechBadge(label: String) {
    val containerColor = when (label.lowercase()) {
        "noun" -> MaterialTheme.colorScheme.primaryContainer
        "verb" -> MaterialTheme.colorScheme.secondaryContainer
        "adjective" -> MaterialTheme.colorScheme.tertiaryContainer
        "adverb" -> MaterialTheme.colorScheme.surfaceVariant
        else -> MaterialTheme.colorScheme.primaryContainer
    }
    val contentColor = when (label.lowercase()) {
        "noun" -> MaterialTheme.colorScheme.onPrimaryContainer
        "verb" -> MaterialTheme.colorScheme.onSecondaryContainer
        "adjective" -> MaterialTheme.colorScheme.onTertiaryContainer
        "adverb" -> MaterialTheme.colorScheme.onSurfaceVariant
        else -> MaterialTheme.colorScheme.onPrimaryContainer
    }

    androidx.compose.material3.Surface(
        color = containerColor,
        shape = RoundedCornerShape(6.dp)
    ) {
        Text(
            text = label.replaceFirstChar { it.uppercase() },
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.SemiBold,
            color = contentColor
        )
    }
}
