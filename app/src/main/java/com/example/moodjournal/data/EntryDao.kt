package com.example.moodjournal.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import java.time.LocalDate
import kotlinx.coroutines.flow.Flow

@Dao
interface EntryDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(entry: Entry)

    @Query("SELECT * FROM entries WHERE date >= :start AND date <= :end ORDER BY date DESC")
    fun observeRange(start: LocalDate, end: LocalDate): Flow<List<Entry>>

    @Query("SELECT * FROM entries ORDER BY date DESC LIMIT 1")
    suspend fun getLatestEntry(): Entry?

    @Query("SELECT * FROM entries WHERE date = :date")
    suspend fun getEntryByDate(date: LocalDate): Entry?
}
