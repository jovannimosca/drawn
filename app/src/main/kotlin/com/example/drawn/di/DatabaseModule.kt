package com.example.drawn.di

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.drawn.data.database.AppDatabase
import com.example.drawn.data.database.dao.CardDao
import com.example.drawn.data.database.dao.DeckDao
import com.example.drawn.data.database.dao.ReadingCardDao
import com.example.drawn.data.database.dao.ReadingDao
import com.example.drawn.data.database.dao.ReadingPhotoDao
import com.example.drawn.data.database.dao.SpreadDao
import com.example.drawn.data.database.migration.MIGRATION_1_2
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(
        @ApplicationContext context: Context
    ): AppDatabase = Room.databaseBuilder(
        context,
        AppDatabase::class.java,
        "drawn_database"
    )
        .createFromAsset("database/drawn_prepopulated.db")
        .setJournalMode(RoomDatabase.JournalMode.WRITE_AHEAD_LOGGING)
        .addMigrations(MIGRATION_1_2)
        .build()

    @Provides
    fun provideDeckDao(db: AppDatabase): DeckDao = db.deckDao()

    @Provides
    fun provideCardDao(db: AppDatabase): CardDao = db.cardDao()

    @Provides
    fun provideSpreadDao(db: AppDatabase): SpreadDao = db.spreadDao()

    @Provides
    fun provideReadingDao(db: AppDatabase): ReadingDao = db.readingDao()

    @Provides
    fun provideReadingCardDao(db: AppDatabase): ReadingCardDao = db.readingCardDao()

    @Provides
    fun provideReadingPhotoDao(db: AppDatabase): ReadingPhotoDao = db.readingPhotoDao()
}
