package com.example.drawn.di

import com.example.drawn.data.database.dao.CardDao
import com.example.drawn.data.database.dao.DeckDao
import com.example.drawn.data.database.dao.ReadingCardDao
import com.example.drawn.data.database.dao.ReadingDao
import com.example.drawn.data.database.dao.ReadingPhotoDao
import com.example.drawn.data.database.dao.SpreadDao
import com.example.drawn.data.repository.CardRepository
import com.example.drawn.data.repository.DeckRepository
import com.example.drawn.data.repository.ReadingRepository
import com.example.drawn.data.repository.SpreadRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Provides
    fun provideCardRepository(
        cardDao: CardDao,
        deckDao: DeckDao
    ): CardRepository = CardRepository(cardDao, deckDao)

    @Provides
    fun provideReadingRepository(
        readingDao: ReadingDao,
        readingCardDao: ReadingCardDao,
        readingPhotoDao: ReadingPhotoDao
    ): ReadingRepository = ReadingRepository(readingDao, readingCardDao, readingPhotoDao)

    @Provides
    fun provideSpreadRepository(
        spreadDao: SpreadDao
    ): SpreadRepository = SpreadRepository(spreadDao)

    @Provides
    fun provideDeckRepository(
        deckDao: DeckDao
    ): DeckRepository = DeckRepository(deckDao)
}
