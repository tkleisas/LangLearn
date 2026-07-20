package com.langlearn.app.ui.screens.lessons

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.langlearn.app.data.database.entity.LanguageEntity
import com.langlearn.app.ui.viewmodel.LessonItem
import com.langlearn.app.ui.viewmodel.LessonsViewModel

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

@OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)
@Composable
fun LessonsScreen(
    lessonsViewModel: LessonsViewModel = viewModel(factory = LessonsViewModel.Factory),
    onVocabularyLesson: (languageId: Long, level: Int) -> Unit,
    onGrammarLesson: (languageId: Long, level: Int) -> Unit
) {
    val selectedLanguage by lessonsViewModel.selectedLanguage.collectAsStateWithLifecycle()
    val lessons by lessonsViewModel.lessons.collectAsStateWithLifecycle()

    Column(modifier = Modifier.fillMaxSize()) {
        Text(
            text = "Lessons",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 16.dp)
        )

        Spacer(modifier = Modifier.height(12.dp))

        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.padding(horizontal = 16.dp)
        ) {
            availableLanguages.forEach { lang ->
                FilterChip(
                    selected = selectedLanguage.id == lang.entity.id,
                    onClick = { lessonsViewModel.selectLanguage(lang.entity) },
                    label = { Text(lang.displayName) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = MaterialTheme.colorScheme.primary,
                        selectedLabelColor = MaterialTheme.colorScheme.onPrimary
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = androidx.compose.foundation.layout.PaddingValues(
                start = 16.dp,
                end = 16.dp,
                bottom = 16.dp
            ),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(lessons, key = { "${it.type}_${it.level}" }) { lesson ->
                LessonCard(
                    lesson = lesson,
                    selectedLanguageId = selectedLanguage.id,
                    onClick = {
                        if (lesson.type == "vocabulary") {
                            onVocabularyLesson(selectedLanguage.id, lesson.level)
                        } else {
                            onGrammarLesson(selectedLanguage.id, lesson.level)
                        }
                    }
                )
            }
        }
    }
}

@Composable
private fun LessonCard(
    lesson: LessonItem,
    selectedLanguageId: Long,
    onClick: () -> Unit
) {
    val isClickable = !lesson.locked && !lesson.completed

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .then(
                if (isClickable) Modifier.clickable(onClick = onClick)
                else Modifier
            ),
        colors = CardDefaults.cardColors(
            containerColor = when {
                lesson.locked -> MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                lesson.completed -> MaterialTheme.colorScheme.surfaceVariant
                else -> MaterialTheme.colorScheme.surface
            }
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (!lesson.locked && !lesson.completed) 2.dp else 0.dp
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            val typeIcon: ImageVector = if (lesson.type == "vocabulary") {
                Icons.Filled.MenuBook
            } else {
                Icons.Filled.Edit
            }

            Icon(
                imageVector = typeIcon,
                contentDescription = lesson.type,
                modifier = Modifier.size(32.dp),
                tint = when {
                    lesson.locked -> MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f)
                    lesson.completed -> Color(0xFF4CAF50)
                    else -> MaterialTheme.colorScheme.primary
                }
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Level ${lesson.level}",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = lesson.title,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium,
                    color = if (lesson.locked) {
                        MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f)
                    } else {
                        MaterialTheme.colorScheme.onSurface
                    }
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            when {
                lesson.completed -> Icon(
                    imageVector = Icons.Filled.CheckCircle,
                    contentDescription = "Completed",
                    modifier = Modifier.size(28.dp),
                    tint = Color(0xFF4CAF50)
                )
                lesson.locked -> Icon(
                    imageVector = Icons.Filled.Lock,
                    contentDescription = "Locked",
                    modifier = Modifier.size(24.dp),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f)
                )
            }
        }
    }
}
