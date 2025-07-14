package com.example.moodjournal.presentation.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.moodjournal.voice.VoicePrefsManager
import com.example.moodjournal.voice.VoiceSettings
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

data class SettingsUiState(
    val promptTtsEnabled: Boolean = true,
    val voiceInputEnabled: Boolean = true
)

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val voicePrefsManager: VoicePrefsManager
) : ViewModel() {

    val uiState: StateFlow<SettingsUiState> = voicePrefsManager.settings
        .map { voiceSettings ->
            SettingsUiState(
                promptTtsEnabled = voiceSettings.promptTtsEnabled,
                voiceInputEnabled = voiceSettings.voiceInputEnabled
            )
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = SettingsUiState()
        )

    fun togglePromptTts(enabled: Boolean) {
        viewModelScope.launch {
            voicePrefsManager.setPromptTts(enabled)
        }
    }

    fun toggleVoiceInput(enabled: Boolean) {
        viewModelScope.launch {
            voicePrefsManager.setVoiceInput(enabled)
        }
    }
}