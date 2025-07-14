package com.example.moodjournal.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDate

@Entity(tableName = "entries")
data class Entry(
    @PrimaryKey
    val date: LocalDate,
    val moodScore: Int, // 1-5 scale
    val note: String,
    val promptId: String,
    val tags: List<String> = emptyList(),
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)
