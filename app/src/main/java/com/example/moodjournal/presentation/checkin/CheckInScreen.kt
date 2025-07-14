package com.example.moodjournal.presentation.checkin

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.moodjournal.data.Prompt
import com.example.moodjournal.ui.theme.MoodJournalTheme
import java.time.LocalDate

@Composable
fun CheckInScreen(
    onEntrySaved: (LocalDate) -> Unit,
    viewModel: CheckInViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(errorMessage) {
        errorMessage?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearError()
        }
    }

    LaunchedEffect(uiState) {
        if (uiState is CheckInUiState.Saved) {
            onEntrySaved((uiState as CheckInUiState.Saved).entryId)
            viewModel.onEvent(CheckInEvent.OnNavigateConsumed)
        }
    }

    CheckInContent(
        uiState = uiState,
        onEvent = viewModel::onEvent,
        snackbarHostState = snackbarHostState
    )
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
private fun CheckInContent(
    uiState: CheckInUiState,
    onEvent: (CheckInEvent) -> Unit,
    snackbarHostState: SnackbarHostState
) {
    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        floatingActionButton = {
            if (uiState is CheckInUiState.Ready && uiState.mood != null) {
                ExtendedFloatingActionButton(
                    onClick = { onEvent(CheckInEvent.OnSaveTapped) },
                    text = { Text("Save Entry") },
                    icon = {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = "Save"
                        )
                    },
                    modifier = Modifier.testTag("save_button")
                )
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentAlignment = Alignment.Center
        ) {
            when (uiState) {
                is CheckInUiState.Loading -> {
                    CircularProgressIndicator()
                }
                is CheckInUiState.Ready -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState())
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(24.dp)
                    ) {
                        // Prompt
                        Text(
                            text = "Today's Prompt",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = uiState.prompt.text,
                            style = MaterialTheme.typography.titleLarge,
                            textAlign = TextAlign.Center,
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("prompt_text")
                        )

                        // Mood selector
                        Column {
                            Text(
                                text = "How are you feeling?",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            MoodSelector(
                                selectedMood = uiState.mood,
                                onMoodSelected = { mood ->
                                    onEvent(CheckInEvent.OnMoodSelected(mood))
                                }
                            )
                        }

                        // Note input
                        OutlinedTextField(
                            value = uiState.note,
                            onValueChange = { text ->
                                onEvent(CheckInEvent.OnNoteChanged(text))
                            },
                            label = { Text("Add a note (optional)") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .heightIn(min = 120.dp)
                                .testTag("note_input"),
                            maxLines = 5
                        )

                        // Tags
                        Column {
                            Text(
                                text = "Tags",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            TagSelector(
                                selectedTags = uiState.tags,
                                onTagsChanged = { tags ->
                                    onEvent(CheckInEvent.OnTagsChanged(tags))
                                }
                            )
                        }

                        // Spacer for FAB
                        Spacer(modifier = Modifier.height(80.dp))
                    }
                }
                is CheckInUiState.Saving -> {
                    CircularProgressIndicator()
                }
                is CheckInUiState.Saved -> {
                    // Navigation happens via side effect
                }
            }
        }
    }
}

@Composable
private fun MoodSelector(
    selectedMood: Int?,
    onMoodSelected: (Int) -> Unit
) {
    val moods = listOf(
        1 to "😫",
        2 to "😟",
        3 to "😐",
        4 to "😊",
        5 to "😄"
    )

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        moods.forEach { (value, emoji) ->
            MoodButton(
                emoji = emoji,
                isSelected = selectedMood == value,
                onClick = { onMoodSelected(value) },
                moodValue = value
            )
        }
    }
}

@Composable
private fun MoodButton(
    emoji: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    moodValue: Int
) {
    val backgroundColor by animateColorAsState(
        targetValue = if (isSelected) {
            MaterialTheme.colorScheme.primaryContainer
        } else {
            Color.Transparent
        },
        label = "mood_background"
    )

    Surface(
        modifier = Modifier
            .size(64.dp)
            .clip(CircleShape)
            .clickable { onClick() }
            .testTag("mood_button_$moodValue"),
        color = backgroundColor,
        border = if (isSelected) {
            BorderStroke(2.dp, MaterialTheme.colorScheme.primary)
        } else {
            null
        }
    ) {
        Box(
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = emoji,
                fontSize = 28.sp
            )
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun TagSelector(
    selectedTags: List<String>,
    onTagsChanged: (List<String>) -> Unit
) {
    val availableTags = listOf(
        "productive",
        "anxious",
        "tired",
        "grateful",
        "stressed",
        "happy",
        "calm",
        "motivated"
    )

    FlowRow(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        availableTags.forEach { tag ->
            FilterChip(
                selected = tag in selectedTags,
                onClick = {
                    if (tag in selectedTags) {
                        onTagsChanged(selectedTags - tag)
                    } else {
                        onTagsChanged(selectedTags + tag)
                    }
                },
                label = { Text(tag) },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = MaterialTheme.colorScheme.primaryContainer
                )
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun CheckInScreenPreview() {
    MoodJournalTheme {
        CheckInContent(
            uiState = CheckInUiState.Ready(
                prompt = Prompt(
                    id = "001",
                    text = "What made you smile today?",
                    pack = "base"
                ),
                mood = 4,
                note = "Had a great day!",
                tags = listOf("grateful", "happy")
            ),
            onEvent = {},
            snackbarHostState = remember { SnackbarHostState() }
        )
    }
}

@Preview(showBackground = true, uiMode = android.content.res.Configuration.UI_MODE_NIGHT_YES)
@Composable
fun CheckInScreenDarkPreview() {
    MoodJournalTheme {
        CheckInContent(
            uiState = CheckInUiState.Ready(
                prompt = Prompt(
                    id = "001",
                    text = "What made you smile today?",
                    pack = "base"
                ),
                mood = null,
                note = "",
                tags = emptyList()
            ),
            onEvent = {},
            snackbarHostState = remember { SnackbarHostState() }
        )
    }
}
