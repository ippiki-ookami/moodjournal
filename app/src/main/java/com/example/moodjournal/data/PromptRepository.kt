package com.example.moodjournal.data

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import java.time.LocalDate
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

@Serializable
data class PromptsJson(
    val prompts: List<PromptJson>
)

@Serializable
data class PromptJson(
    val id: String,
    val text: String,
    val pack: String
)

@Singleton
class PromptRepository @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val json = Json { ignoreUnknownKeys = true }
    private var cachedPrompts: List<Prompt>? = null

    suspend fun getAllPrompts(): List<Prompt> {
        if (cachedPrompts == null) {
            val jsonString = context.assets.open("prompts.json")
                .bufferedReader()
                .use { it.readText() }
            val promptsJson = json.decodeFromString<PromptsJson>(jsonString)
            cachedPrompts = promptsJson.prompts.map {
                Prompt(id = it.id, text = it.text, pack = it.pack)
            }
        }
        return cachedPrompts!!
    }

    suspend fun getTodayPrompt(): Prompt {
        val prompts = getAllPrompts()
        val dayOfYear = LocalDate.now().dayOfYear
        val index = (dayOfYear - 1) % prompts.size
        return prompts[index]
    }

    suspend fun getPromptById(id: String): Prompt? {
        return getAllPrompts().find { it.id == id }
    }
}
