package com.example.moodjournal.presentation.settings

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.moodjournal.ui.theme.MoodJournalTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onBackClick: () -> Unit,
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Settings") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
        ) {
            // Voice Settings Section
            ListItem(
                headlineContent = { Text("Voice", style = MaterialTheme.typography.titleMedium) }
            )
            
            Divider()
            
            ListItem(
                headlineContent = { Text("Read prompt aloud") },
                supportingContent = { Text("Hear each day's prompt via Text-to-Speech") },
                trailingContent = {
                    Switch(
                        checked = uiState.promptTtsEnabled,
                        onCheckedChange = { viewModel.togglePromptTts(it) }
                    )
                }
            )
            
            Divider()
            
            ListItem(
                headlineContent = { Text("Enable voice input") },
                supportingContent = { Text("Dictate notes instead of typing") },
                trailingContent = {
                    Switch(
                        checked = uiState.voiceInputEnabled,
                        onCheckedChange = { viewModel.toggleVoiceInput(it) }
                    )
                }
            )
            
            Divider()
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SettingsScreenPreview() {
    MoodJournalTheme {
        SettingsScreen(
            onBackClick = {}
        )
    }
}

@Preview(showBackground = true, uiMode = android.content.res.Configuration.UI_MODE_NIGHT_YES)
@Composable
fun SettingsScreenDarkPreview() {
    MoodJournalTheme {
        SettingsScreen(
            onBackClick = {}
        )
    }
}