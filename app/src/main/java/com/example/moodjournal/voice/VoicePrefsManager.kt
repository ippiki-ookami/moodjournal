package com.example.moodjournal.voice

import androidx.datastore.core.DataStore
import com.example.moodjournal.VoicePrefs
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

data class VoiceSettings(
    val promptTtsEnabled: Boolean = true,
    val voiceInputEnabled: Boolean = true
)

@Singleton
class VoicePrefsManager @Inject constructor(
    private val dataStore: DataStore<VoicePrefs>
) {
    val settings: Flow<VoiceSettings> = dataStore.data
        .map { proto ->
            VoiceSettings(
                promptTtsEnabled = proto.promptTtsEnabled,
                voiceInputEnabled = proto.voiceInputEnabled
            )
        }
    
    val isVoiceInputEnabled: Flow<Boolean> = dataStore.data.map { prefs ->
        prefs.voiceInputEnabled
    }

    suspend fun setPromptTts(enabled: Boolean) =
        dataStore.updateData { it.toBuilder().setPromptTtsEnabled(enabled).build() }

    suspend fun setVoiceInput(enabled: Boolean) =
        dataStore.updateData { it.toBuilder().setVoiceInputEnabled(enabled).build() }
}