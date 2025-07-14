package com.example.moodjournal.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.core.DataStoreFactory
import androidx.datastore.dataStoreFile
import androidx.room.Room
import com.example.moodjournal.UserPrefs
import com.example.moodjournal.data.EntryDao
import com.example.moodjournal.data.MoodDatabase
import com.example.moodjournal.datastore.UserPrefsSerializer
import dagger.Module
import dagger.Provides
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import dagger.hilt.testing.TestInstallIn
import javax.inject.Named
import javax.inject.Singleton

@Module
@TestInstallIn(
    components = [SingletonComponent::class],
    replaces = [AppModule::class]
)
object TestAppModule {

    @Provides
    @Singleton
    fun provideTestDatabase(@ApplicationContext context: Context): MoodDatabase {
        return Room.inMemoryDatabaseBuilder(
            context,
            MoodDatabase::class.java
        ).allowMainThreadQueries().build()
    }

    @Provides
    fun provideEntryDao(database: MoodDatabase): EntryDao {
        return database.entryDao()
    }

    @Provides
    @Singleton
    fun provideTestDataStore(@ApplicationContext context: Context): DataStore<UserPrefs> {
        return DataStoreFactory.create(
            serializer = UserPrefsSerializer,
            produceFile = { context.dataStoreFile("test_user_prefs.pb") }
        )
    }

    @Provides
    @Named("splashDelayMs")
    fun provideTestSplashDelay(): Long = 0L
}