package com.example.moodjournal.presentation.timeline

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.moodjournal.data.Entry
import com.example.moodjournal.ui.theme.MoodJournalTheme
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimelineScreen(
    navigateBack: () -> Unit,
    viewModel: TimelineViewModel = hiltViewModel()
) {
    val entries by viewModel.entries.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Timeline") },
                navigationIcon = {
                    IconButton(onClick = navigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        PullToRefreshBox(
            isRefreshing = false,
            onRefresh = { /* Entries auto-update via Flow */ },
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            if (entries.isEmpty()) {
                EmptyState()
            } else {
                EntryList(entries = entries)
            }
        }
    }
}

@Composable
private fun EmptyState() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "No entries yet — check in to begin!",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun EntryList(entries: List<Entry>) {
    LazyColumn(
        modifier = Modifier.testTag("timeline_list"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(
            items = entries,
            key = { it.date.toEpochDay() }
        ) { entry ->
            EntryRow(entry = entry)
        }
    }
}

@Composable
private fun EntryRow(entry: Entry) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Mood emoji
            Text(
                text = getMoodEmoji(entry.moodScore),
                fontSize = 28.sp
            )

            Spacer(modifier = Modifier.width(16.dp))

            // Content
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = if (entry.note.isBlank()) "No note" else entry.note.take(40),
                    style = MaterialTheme.typography.bodyLarge,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.testTag("entry_note_${entry.date}")
                )
                Text(
                    text = getRelativeDate(entry.date),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // Tags indicator
            if (entry.tags.isNotEmpty()) {
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "${entry.tags.size}",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier
                        .size(24.dp)
                        .padding(4.dp)
                )
            }
        }
    }
}

private fun getMoodEmoji(moodScore: Int): String {
    return when (moodScore) {
        1 -> "😫"
        2 -> "😟"
        3 -> "😐"
        4 -> "😊"
        5 -> "😄"
        else -> "😐"
    }
}

private fun getRelativeDate(date: LocalDate): String {
    val today = LocalDate.now()
    return when (date) {
        today -> "Today"
        today.minusDays(1) -> "Yesterday"
        in today.minusDays(7)..today -> date.dayOfWeek.getDisplayName(
            TextStyle.SHORT,
            Locale.getDefault()
        )
        else -> date.format(DateTimeFormatter.ofPattern("MMM d"))
    }
}

@Preview(showBackground = true)
@Composable
fun TimelineScreenPreview() {
    MoodJournalTheme {
        val fakeEntries = listOf(
            Entry(
                date = LocalDate.now(),
                moodScore = 5,
                note = "Had an amazing day! Got a lot done and feeling great about the progress.",
                promptId = "001",
                tags = listOf("productive", "happy")
            ),
            Entry(
                date = LocalDate.now().minusDays(1),
                moodScore = 3,
                note = "Regular day, nothing special",
                promptId = "002",
                tags = emptyList()
            ),
            Entry(
                date = LocalDate.now().minusDays(2),
                moodScore = 4,
                note = "",
                promptId = "003",
                tags = listOf("grateful")
            ),
            Entry(
                date = LocalDate.now().minusDays(5),
                moodScore = 2,
                note = "Feeling a bit down today, hopefully tomorrow will be better",
                promptId = "004",
                tags = listOf("tired", "anxious")
            )
        )

        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Timeline") },
                    navigationIcon = {
                        IconButton(onClick = {}) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back"
                            )
                        }
                    }
                )
            }
        ) { paddingValues ->
            Box(modifier = Modifier.padding(paddingValues)) {
                EntryList(entries = fakeEntries)
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun EmptyTimelinePreview() {
    MoodJournalTheme {
        EmptyState()
    }
}
