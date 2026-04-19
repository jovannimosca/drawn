package com.example.drawn.data.repository

import app.cash.turbine.test
import com.example.drawn.data.database.dao.CustomCardDao
import com.example.drawn.data.database.dao.ReadingTagDao
import com.example.drawn.data.database.dao.TagDao
import com.example.drawn.data.database.entity.CustomCardEntity
import com.example.drawn.data.database.entity.DeckEntity
import com.example.drawn.data.database.entity.ReadingEntity
import com.example.drawn.data.database.entity.ReadingTagCrossRef
import com.example.drawn.data.database.entity.TagEntity
import com.example.drawn.domain.model.CustomCard
import com.example.drawn.domain.model.Deck
import com.example.drawn.domain.model.Reading
import com.example.drawn.domain.model.Tag
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import java.time.Instant

/**
 * UAT Verification Tests for Phase 09 Repository Layer
 * Tests all specified features from the verification requirements:
 * 1. CustomCardRepository.observeCardsForDeck(deckId) returns Flow
 * 2. TagRepository.observeAllTags() returns Flow
 * 3. ReadingTagRepository.observeTagsForReading(readingId) returns Flow
 * 4. ReadingTagRepository.addTag/removeTag/replaceTags work
 * 5. ReadingRepository.observeFavorites() and toggleFavorite() work
 * 6. ReadingRepository.observeReadingsByTags(tagIds) returns Flow with ANY logic
 * 7. DeckRepository.observeCustomDecks() returns Flow
 */
@ExtendWith(io.mockk.junit5.MockKExtension::class)
class UatRepositoryVerification {

    // DAOs
    private val customCardDao: CustomCardDao = mockk()
    private val tagDao: TagDao = mockk()
    private val readingTagDao: ReadingTagDao = mockk()
    private val readingDao: com.example.drawn.data.database.dao.ReadingDao = mockk()
    private val deckDao: com.example.drawn.data.database.dao.DeckDao = mockk()

    // Repositories
    private lateinit var customCardRepository: CustomCardRepository
    private lateinit var tagRepository: TagRepository
    private lateinit var readingTagRepository: ReadingTagRepository
    private lateinit var readingRepository: ReadingRepository
    private lateinit var deckRepository: DeckRepository

    // Test entities
    private val testCustomCardEntity = CustomCardEntity(
        id = 1L, deckId = 1L, name = "Custom Fool",
        keywords = "adventure,new beginnings", uprightMeaning = "New beginnings", reversedMeaning = "Hold on"
    )

    private val testCustomCard = CustomCard(
        id = 1L, deckId = 1L, name = "Custom Fool",
        keywords = "adventure,new beginnings", uprightMeaning = "New beginnings", reversedMeaning = "Hold on"
    )

    private val testTagEntity = TagEntity(
        id = 1L, name = "Love", color = "#FF69B4"
    )

private val testTag = Tag(
        id = 1L, name = "Love", color = "#FF69B4"
    )

    private val testReadingEntity = ReadingEntity(
        id = 1L, title = "Test Reading", spreadId = 1L,
        createdAt = 1000L, notes = "Notes", isFavorite = false
    )

    private val testCustomDeckEntity = DeckEntity(
        id = 2L, name = "My Custom Deck", description = "Custom",
        isCustom = true, createdAt = 1000L
    )

    private val testCustomDeck = Deck(
        id = 2L, name = "My Custom Deck", description = "Custom",
        isCustom = true, createdAt = Instant.ofEpochMilli(1000L)
    )

    // Unused but kept for reference
    private val unusedCustomCard = testCustomCardEntity.copy(id = 99L)

    @BeforeEach
    fun setUp() {
        customCardRepository = CustomCardRepository(customCardDao)
        tagRepository = TagRepository(tagDao)
        readingTagRepository = ReadingTagRepository(tagDao, readingTagDao)
        readingRepository = ReadingRepository(
            readingDao,
            mockk(),
            mockk(),
            mockk(),
            mockk(),
            readingTagDao
        )
        deckRepository = DeckRepository(deckDao)
    }

    @Nested
    inner class Feature1_CustomCardRepository_ObserveCardsForDeck {
        /** Feature 1: CustomCardRepository.observeCardsForDeck(deckId) returns Flow */
        @Test
        fun `observeCardsForDeck returns Flow of custom cards for deck`() = runTest {
            // Given: custom cards for deck 1 exist in DAO
            every { customCardDao.observeCardsForDeck(1L) } returns flowOf(listOf(testCustomCardEntity))

            // When: repository observes cards for deck 1
            customCardRepository.observeCardsForDeck(1L).test {
                // Then: returns Flow of mapped custom cards
                val cards = awaitItem()
                assertEquals(1, cards.size)
                assertEquals("Custom Fool", cards[0].name)
                cancelAndIgnoreRemainingEvents()
            }
        }

        @Test
        fun `observeCardsForDeck returns empty Flow when no cards exist`() = runTest {
            // Given: no custom cards for deck
            every { customCardDao.observeCardsForDeck(1L) } returns flowOf(emptyList())

            // When: repository observes cards for deck 1
            customCardRepository.observeCardsForDeck(1L).test {
                // Then: returns empty Flow
                val cards = awaitItem()
                assertTrue(cards.isEmpty())
                cancelAndIgnoreRemainingEvents()
            }
        }
    }

    @Nested
    inner class Feature2_TagRepository_ObserveAllTags {
        /** Feature 2: TagRepository.observeAllTags() returns Flow */
        @Test
        fun `observeAllTags returns Flow of all tags`() = runTest {
            // Given: tags exist in DAO
            every { tagDao.observeAllTags() } returns flowOf(listOf(testTagEntity))

            // When: repository observes all tags
            tagRepository.observeAllTags().test {
                // Then: returns Flow of mapped tags
                val tags = awaitItem()
                assertEquals(1, tags.size)
                assertEquals("Love", tags[0].name)
                cancelAndIgnoreRemainingEvents()
            }
        }

        @Test
        fun `observeAllTags returns empty Flow when no tags`() = runTest {
            // Given: no tags in DAO
            every { tagDao.observeAllTags() } returns flowOf(emptyList())

            // When: repository observes all tags
            tagRepository.observeAllTags().test {
                // Then: returns empty Flow
                val tags = awaitItem()
                assertTrue(tags.isEmpty())
                cancelAndIgnoreRemainingEvents()
            }
        }
    }

    @Nested
    inner class Feature3_ReadingTagRepository_ObserveTagsForReading {
        /** Feature 3: ReadingTagRepository.observeTagsForReading(readingId) returns Flow */
        @Test
        fun `observeTagsForReading returns Flow of tags for reading`() = runTest {
            // Given: reading has tag IDs
            every { readingTagDao.observeTagIdsForReading(1L) } returns flowOf(listOf(1L))
            // Given: tag exists
            coEvery { tagDao.getTagById(1L) } returns testTagEntity

            // When: repository observes tags for reading 1
            readingTagRepository.observeTagsForReading(1L).test {
                // Then: returns Flow of mapped tags
                val tags = awaitItem()
                assertEquals(1, tags.size)
                assertEquals("Love", tags[0].name)
                cancelAndIgnoreRemainingEvents()
            }
        }

        @Test
        fun `observeTagsForReading returns empty Flow when no tags`() = runTest {
            // Given: reading has no tags
            every { readingTagDao.observeTagIdsForReading(1L) } returns flowOf(emptyList())

            // When: repository observes tags for reading 1
            readingTagRepository.observeTagsForReading(1L).test {
                // Then: returns empty Flow
                val tags = awaitItem()
                assertTrue(tags.isEmpty())
                cancelAndIgnoreRemainingEvents()
            }
        }
    }

    @Nested
    inner class Feature4_ReadingTagRepository_AddRemoveReplaceTags {
        /** Feature 4: ReadingTagRepository.addTag/removeTag/replaceTags work */
        @Test
        fun `addTag inserts reading-tag cross ref`() = runTest {
            // Given
            coEvery { readingTagDao.insert(any()) } returns Unit

            // When
            readingTagRepository.addTag(1L, 1L)

            // Then
            coVerify { readingTagDao.insert(ReadingTagCrossRef(1L, 1L)) }
        }

        @Test
        fun `removeTag deletes reading-tag cross ref`() = runTest {
            // Given
            coEvery { readingTagDao.delete(any()) } returns Unit

            // When
            readingTagRepository.removeTag(1L, 1L)

            // Then
            coVerify { readingTagDao.delete(ReadingTagCrossRef(1L, 1L)) }
        }

        @Test
        fun `replaceTags deletes all and inserts new tags`() = runTest {
            // Given
            coEvery { readingTagDao.deleteAllForReading(1L) } returns Unit
            coEvery { readingTagDao.insert(any()) } returns Unit

            // When
            readingTagRepository.replaceTags(1L, listOf(1L, 2L, 3L))

            // Then
            coVerify { readingTagDao.deleteAllForReading(1L) }
            coVerify { readingTagDao.insert(ReadingTagCrossRef(1L, 1L)) }
            coVerify { readingTagDao.insert(ReadingTagCrossRef(1L, 2L)) }
            coVerify { readingTagDao.insert(ReadingTagCrossRef(1L, 3L)) }
        }
    }

    @Nested
    inner class Feature5_ReadingRepository_Favorites {
        /** Feature 5: ReadingRepository.observeFavorites() and toggleFavorite() work */
        @Test
        fun `toggleFavorite flips favorite status`() = runTest {
            // Given: reading exists with isFavorite = false
            coEvery { readingDao.getReadingById(1L) } returns testReadingEntity
            coEvery { readingDao.update(any()) } returns Unit
            val favoritedEntity = testReadingEntity.copy(isFavorite = true)

            // When
            readingRepository.toggleFavorite(1L)

            // Then: updates to isFavorite = true
            coVerify { readingDao.update(favoritedEntity) }
        }

        @Test
        fun `observeFavorites returns Flow of favorite readings`() = runTest {
            // Given: favorites exist
            val favoriteEntity = testReadingEntity.copy(isFavorite = true)
            every { readingDao.observeFavorites() } returns flowOf(listOf(favoriteEntity))

            // When
            readingRepository.observeFavorites().test {
                // Then: returns favorited readings
                val readings = awaitItem()
                assertEquals(1, readings.size)
                assertTrue(readings[0].isFavorite)
                cancelAndIgnoreRemainingEvents()
            }
        }

        @Test
        fun `observeFavorites returns empty when no favorites`() = runTest {
            // Given: no favorites
            every { readingDao.observeFavorites() } returns flowOf(emptyList())

            // When
            readingRepository.observeFavorites().test {
                val readings = awaitItem()
                assertTrue(readings.isEmpty())
                cancelAndIgnoreRemainingEvents()
            }
        }
    }

    @Nested
    inner class Feature6_ReadingRepository_ObserveReadingsByTags {
        /** Feature 6: ReadingRepository.observeReadingsByTags(tagIds) returns Flow with ANY logic */
        @Test
        fun `observeReadingsByTags returns readings matching ANY tag`() = runTest {
            // Given: reading IDs 1, 2 have tag 1 (OR logic)
            every { readingTagDao.observeReadingIdsByTags(listOf(1L)) } returns flowOf(listOf(1L, 2L))
            // Given: reading DAO returns those readings
            coEvery { readingDao.getReadingById(1L) } returns testReadingEntity
            coEvery { readingDao.getReadingById(2L) } returns testReadingEntity.copy(id = 2L)

            // When
            readingRepository.observeReadingsByTags(listOf(1L)).test {
                // Then: returns both readings (ANY logic - either tag matches)
                val readings = awaitItem()
                assertEquals(2, readings.size)
                cancelAndIgnoreRemainingEvents()
            }
        }

        @Test
        fun `observeReadingsByTags returns empty when no matches`() = runTest {
            // Given: no readings matchTags.empty
            every { readingTagDao.observeReadingIdsByTags(listOf(1L)) } returns flowOf(emptyList())

            // When
            readingRepository.observeReadingsByTags(listOf(1L)).test {
                val readings = awaitItem()
                assertTrue(readings.isEmpty())
                cancelAndIgnoreRemainingEvents()
            }
        }
    }

    @Nested
    inner class Feature7_DeckRepository_ObserveCustomDecks {
        /** Feature 7: DeckRepository.observeCustomDecks() returns Flow */
        @Test
        fun `observeCustomDecks returns Flow of custom decks only`() = runTest {
            // Given: custom decks exist
            every { deckDao.observeCustomDecks() } returns flowOf(listOf(testCustomDeckEntity))

            // When
            deckRepository.observeCustomDecks().test {
                // Then: returns custom (isCustom=true) decks only
                val decks = awaitItem()
                assertEquals(1, decks.size)
                assertTrue(decks[0].isCustom)
                assertEquals("My Custom Deck", decks[0].name)
                cancelAndIgnoreRemainingEvents()
            }
        }

        @Test
        fun `observeCustomDecks returns empty when no custom decks`() = runTest {
            // Given: no custom decks
            every { deckDao.observeCustomDecks() } returns flowOf(emptyList())

            // When
            deckRepository.observeCustomDecks().test {
                val decks = awaitItem()
                assertTrue(decks.isEmpty())
                cancelAndIgnoreRemainingEvents()
            }
        }
    }
}