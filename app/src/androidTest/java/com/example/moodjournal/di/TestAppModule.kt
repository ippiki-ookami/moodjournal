package com.example.moodjournal.di

import android.content.Context
import androidx.room.Room
import com.example.moodjournal.data.MoodDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import dagger.hilt.testing.TestInstallIn
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
}