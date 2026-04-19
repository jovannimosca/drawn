package com.example.drawn.di

import com.example.drawn.data.database.dao.CardDao
import com.example.drawn.data.database.dao.CustomCardDao
import com.example.drawn.data.database.dao.DeckDao
import com.example.drawn.data.database.dao.ReadingCardDao
import com.example.drawn.data.database.dao.ReadingDao
import com.example.drawn.data.database.dao.ReadingPhotoDao
import com.example.drawn.data.database.dao.ReadingTagDao
import com.example.drawn.data.database.dao.SpreadDao
import com.example.drawn.data.database.dao.TagDao
import com.example.drawn.data.repository.CardRepository
import com.example.drawn.data.repository.CustomCardRepository
import com.example.drawn.data.repository.DeckRepository
import com.example.drawn.data.repository.ReadingRepository
import com.example.drawn.data.repository.ReadingTagRepository
import com.example.drawn.data.repository.SpreadRepository
import com.example.drawn.data.repository.TagRepository
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
        readingPhotoDao: ReadingPhotoDao,
        cardDao: CardDao,
        spreadDao: SpreadDao,
        readingTagDao: ReadingTagDao
    ): ReadingRepository = ReadingRepository(readingDao, readingCardDao, readingPhotoDao, cardDao, spreadDao, readingTagDao)

    @Provides
    fun provideSpreadRepository(
        spreadDao: SpreadDao
    ): SpreadRepository = SpreadRepository(spreadDao)

    @Provides
    fun provideDeckRepository(
        deckDao: DeckDao
    ): DeckRepository = DeckRepository(deckDao)

    @Provides
    fun provideCustomCardRepository(
        customCardDao: CustomCardDao
    ): CustomCardRepository = CustomCardRepository(customCardDao)

    @Provides
    fun provideTagRepository(
        tagDao: TagDao
    ): TagRepository = TagRepository(tagDao)

    @Provides
    fun provideReadingTagRepository(
        tagDao: TagDao,
        readingTagDao: ReadingTagDao
    ): ReadingTagRepository = ReadingTagRepository(tagDao, readingTagDao)
}