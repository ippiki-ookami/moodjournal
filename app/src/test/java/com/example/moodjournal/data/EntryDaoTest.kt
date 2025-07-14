package com.example.moodjournal.data

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import app.cash.turbine.test
import java.time.LocalDate
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.Config

@RunWith(AndroidJUnit4::class)
@Config(sdk = [33])
class EntryDaoTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    private lateinit var database: MoodJournalDatabase
    private lateinit var entryDao: EntryDao

    @Before
    fun setup() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            MoodJournalDatabase::class.java
        ).allowMainThreadQueries().build()
        entryDao = database.entryDao()
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun `upsert replaces duplicates by date`() = runTest {
        // Given
        val date = LocalDate.now()
        val entry1 = Entry(
            date = date,
            moodScore = 3,
            note = "First note",
            promptId = "base_001",
            tags = listOf("work", "tired")
        )
        val entry2 = entry1.copy(
            moodScore = 5,
            note = "Updated note",
            tags = listOf("happy", "productive")
        )

        // When
        entryDao.upsert(entry1)
        val firstResult = entryDao.getEntryByDate(date)

        entryDao.upsert(entry2)
        val secondResult = entryDao.getEntryByDate(date)

        // Then
        assertNotNull(firstResult)
        assertEquals(3, firstResult?.moodScore)
        assertEquals("First note", firstResult?.note)

        assertNotNull(secondResult)
        assertEquals(5, secondResult?.moodScore)
        assertEquals("Updated note", secondResult?.note)
        assertEquals(listOf("happy", "productive"), secondResult?.tags)
    }

    @Test
    fun `observeRange emits when inserting new entry in range`() = runTest {
        // Given
        val today = LocalDate.now()
        val yesterday = today.minusDays(1)
        val tomorrow = today.plusDays(1)
        val lastWeek = today.minusDays(7)

        val entryToday = Entry(
            date = today,
            moodScore = 4,
            note = "Today's note",
            promptId = "base_001"
        )
        val entryYesterday = Entry(
            date = yesterday,
            moodScore = 3,
            note = "Yesterday's note",
            promptId = "base_002"
        )
        val entryLastWeek = Entry(
            date = lastWeek,
            moodScore = 5,
            note = "Last week's note",
            promptId = "base_003"
        )

        // When & Then
        entryDao.observeRange(yesterday, tomorrow).test {
            // Initially empty
            assertEquals(emptyList<Entry>(), awaitItem())

            // Insert entry within range
            entryDao.upsert(entryToday)
            val firstEmission = awaitItem()
            assertEquals(1, firstEmission.size)
            assertEquals(entryToday, firstEmission[0])

            // Insert another entry within range
            entryDao.upsert(entryYesterday)
            val secondEmission = awaitItem()
            assertEquals(2, secondEmission.size)
            // Should be ordered by date DESC
            assertEquals(entryToday, secondEmission[0])
            assertEquals(entryYesterday, secondEmission[1])

            // Insert entry outside range - should not emit
            entryDao.upsert(entryLastWeek)
            expectNoEvents()

            cancel()
        }
    }

    @Test
    fun `getLatestEntry returns most recent entry`() = runTest {
        // Given
        val today = LocalDate.now()
        val yesterday = today.minusDays(1)
        val lastWeek = today.minusDays(7)

        // When no entries
        assertNull(entryDao.getLatestEntry())

        // When entries exist
        entryDao.upsert(Entry(lastWeek, 3, "Old", "base_001"))
        entryDao.upsert(Entry(yesterday, 4, "Yesterday", "base_002"))
        entryDao.upsert(Entry(today, 5, "Today", "base_003"))

        val latest = entryDao.getLatestEntry()

        // Then
        assertNotNull(latest)
        assertEquals(today, latest?.date)
        assertEquals(5, latest?.moodScore)
        assertEquals("Today", latest?.note)
    }

    @Test
    fun `entry count works correctly`() = runTest {
        // Given
        assertEquals(0, entryDao.getEntryCount())

        // When
        val dates = (0..4).map { LocalDate.now().minusDays(it.toLong()) }
        dates.forEach { date ->
            entryDao.upsert(Entry(date, 3, "Note", "base_001"))
        }

        // Then
        assertEquals(5, entryDao.getEntryCount())

        // When deleting
        entryDao.deleteEntry(dates[0])
        assertEquals(4, entryDao.getEntryCount())
    }

    @Test
    fun `tags are properly serialized and deserialized`() = runTest {
        // Given
        val date = LocalDate.now()
        val tags = listOf("happy", "productive", "social")
        val entry = Entry(
            date = date,
            moodScore = 5,
            note = "Great day!",
            promptId = "base_001",
            tags = tags
        )

        // When
        entryDao.upsert(entry)
        val retrieved = entryDao.getEntryByDate(date)

        // Then
        assertNotNull(retrieved)
        assertEquals(tags, retrieved?.tags)
    }
}
