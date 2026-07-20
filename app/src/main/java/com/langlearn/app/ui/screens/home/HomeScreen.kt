package com.langlearn.app.ui.screens.home

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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.langlearn.app.data.database.entity.LanguageEntity
import com.langlearn.app.ui.viewmodel.HomeViewModel

private data class LanguageOption(
    val entity: LanguageEntity,
    val displayName: String
)

private val availableLanguages = listOf(
    LanguageOption(
        entity = LanguageEntity(id = 1, code = "zh", name = "Chinese", nativeName = "中文"),
        displayName = "中文 Chinese"
    ),
    LanguageOption(
        entity = LanguageEntity(id = 2, code = "es", name = "Spanish", nativeName = "Español"),
        displayName = "Español Spanish"
    )
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    homeViewModel: HomeViewModel = viewModel(factory = HomeViewModel.Factory),
    onNavigateToLessons: () -> Unit,
    onNavigateToFlashcards: (languageId: Long) -> Unit,
    onNavigateToPhrasebook: (languageId: Long) -> Unit
) {
    val selectedLanguage by homeViewModel.selectedLanguage.collectAsStateWithLifecycle()
    val todayProgress by homeViewModel.todayProgress.collectAsStateWithLifecycle()
    val dueReviewCount by homeViewModel.dueReviewCount.collectAsStateWithLifecycle()
    val completedLessons by homeViewModel.completedLessons.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        Text(
            text = "Welcome back!",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )

        Spacer(modifier = Modifier.height(12.dp))

        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            availableLanguages.forEach { lang ->
                FilterChip(
                    selected = selectedLanguage.id == lang.entity.id,
                    onClick = { homeViewModel.selectLanguage(lang.entity) },
                    label = { Text(lang.displayName) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = MaterialTheme.colorScheme.primary,
                        selectedLabelColor = MaterialTheme.colorScheme.onPrimary
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        StatsCardsRow(
            dueReviewCount = dueReviewCount,
            minutesPracticed = todayProgress?.minutesPracticed ?: 0,
            streak = todayProgress?.streak ?: 0,
            completedLessons = completedLessons
        )

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Quick Actions",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onBackground
        )

        Spacer(modifier = Modifier.height(12.dp))

        QuickActionButton(
            label = "Start Review",
            subtitle = if (dueReviewCount > 0) "$dueReviewCount cards due" else "No cards due",
            icon = Icons.Filled.FlashOn,
            badgeCount = dueReviewCount,
            onClick = { onNavigateToFlashcards(selectedLanguage.id) }
        )

        Spacer(modifier = Modifier.height(8.dp))

        QuickActionButton(
            label = "Continue Lessons",
            subtitle = "Pick up where you left off",
            icon = Icons.Filled.MenuBook,
            onClick = onNavigateToLessons
        )

        Spacer(modifier = Modifier.height(8.dp))

        QuickActionButton(
            label = "Browse Phrases",
            subtitle = "Explore useful expressions",
            icon = Icons.Filled.Book,
            onClick = { onNavigateToPhrasebook(selectedLanguage.id) }
        )

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Progress",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onBackground
        )

        Spacer(modifier = Modifier.height(12.dp))

        ProgressSection(
            completedLessons = completedLessons,
            totalLessons = 20
        )
    }
}

@Composable
private fun StatsCardsRow(
    dueReviewCount: Int,
    minutesPracticed: Int,
    streak: Int,
    completedLessons: Int
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        StatCard(
            modifier = Modifier.weight(1f),
            value = dueReviewCount.toString(),
            label = "Due Cards",
            icon = Icons.Filled.FlashOn,
            iconTint = MaterialTheme.colorScheme.secondary
        )
        StatCard(
            modifier = Modifier.weight(1f),
            value = "${minutesPracticed}m",
            label = "Today",
            icon = Icons.Filled.Timer,
            iconTint = MaterialTheme.colorScheme.primary
        )
        StatCard(
            modifier = Modifier.weight(1f),
            value = streak.toString(),
            label = "Streak",
            icon = Icons.Filled.Star,
            iconTint = MaterialTheme.colorScheme.tertiary
        )
        StatCard(
            modifier = Modifier.weight(1f),
            value = completedLessons.toString(),
            label = "Lessons",
            icon = Icons.Filled.Flag,
            iconTint = MaterialTheme.colorScheme.secondary
        )
    }
}

@Composable
private fun StatCard(
    modifier: Modifier = Modifier,
    value: String,
    label: String,
    icon: ImageVector,
    iconTint: androidx.compose.ui.graphics.Color
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(24.dp),
                tint = iconTint
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun QuickActionButton(
    label: String,
    subtitle: String,
    icon: ImageVector,
    modifier: Modifier = Modifier,
    badgeCount: Int = 0,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        modifier = modifier.fillMaxWidth().height(64.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.onSurface
        ),
        elevation = ButtonDefaults.buttonElevation(
            defaultElevation = 2.dp,
            pressedElevation = 4.dp
        ),
        shape = MaterialTheme.shapes.medium
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(28.dp),
            tint = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = label,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        if (badgeCount > 0) {
            val errorColor = MaterialTheme.colorScheme.error
            Box(
                modifier = Modifier
                    .size(28.dp),
                contentAlignment = Alignment.Center
            ) {
                androidx.compose.foundation.Canvas(modifier = Modifier.fillMaxSize()) {
                    drawCircle(color = errorColor)
                }
                Text(
                    text = badgeCount.toString(),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onError,
                    fontWeight = FontWeight.Bold
                )
            }
        } else {
            Icon(
                imageVector = Icons.Filled.ChevronRight,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun ProgressSection(
    completedLessons: Int,
    totalLessons: Int
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Lessons Completed",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "$completedLessons / $totalLessons",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            LinearProgressIndicator(
                progress = (completedLessons.toFloat() / totalLessons).coerceIn(0f, 1f),
                modifier = Modifier.fillMaxWidth().height(8.dp),
                color = MaterialTheme.colorScheme.primary,
                trackColor = MaterialTheme.colorScheme.surfaceVariant,
            )
        }
    }
}
