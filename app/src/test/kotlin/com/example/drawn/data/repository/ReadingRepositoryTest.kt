package com.example.drawn.data.repository

import app.cash.turbine.test
import com.example.drawn.data.database.dao.CardDao
import com.example.drawn.data.database.dao.ReadingCardDao
import com.example.drawn.data.database.dao.ReadingDao
import com.example.drawn.data.database.dao.ReadingPhotoDao
import com.example.drawn.data.database.dao.ReadingTagDao
import com.example.drawn.data.database.dao.SpreadDao
import com.example.drawn.data.database.entity.CardEntity
import com.example.drawn.data.database.entity.ReadingCardEntity
import com.example.drawn.data.database.entity.ReadingEntity
import com.example.drawn.data.database.entity.ReadingPhotoEntity
import com.example.drawn.data.database.entity.ReadingWithSpread
import com.example.drawn.data.database.entity.SpreadEntity
import com.example.drawn.domain.model.ArcanaType
import com.example.drawn.domain.model.Card
import com.example.drawn.domain.model.Reading
import com.example.drawn.domain.model.ReadingCard
import com.example.drawn.domain.model.ReadingPhoto
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.coVerifyOrder
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

@ExtendWith(io.mockk.junit5.MockKExtension::class)
class ReadingRepositoryTest {

    private val readingDao: ReadingDao = mockk()
    private val readingCardDao: ReadingCardDao = mockk()
    private val readingPhotoDao: ReadingPhotoDao = mockk()
    private val cardDao: CardDao = mockk()
    private val spreadDao: SpreadDao = mockk()
    private val readingTagDao: ReadingTagDao = mockk()

    private lateinit var repository: ReadingRepository

    private val testReadingEntity = ReadingEntity(
        id = 1L, title = "Test Reading", spreadId = 1L, createdAt = 1000L, notes = "Notes"
    )

    private val testReading = Reading(
        id = 1L, title = "Test Reading", spreadId = 1L,
        createdAt = Instant.ofEpochMilli(1000L), notes = "Notes"
    )

    private val testSpreadEntity = SpreadEntity(
        id = 1L, name = "Three Card Spread", description = "", positionsJson = "[]"
    )

    private val testCardEntity = CardEntity(
        id = 1L, deckId = 1L, name = "The Fool", number = 0,
        arcanaType = "MAJOR", imageResId = 0, keywordsJson = "[]",
        meaningUpright = "New beginnings", meaningReversed = "Recklessness"
    )

    private val testReadingCardEntity = ReadingCardEntity(
        id = 1L, readingId = 1L, cardId = 1L, positionName = "Past",
        positionOrder = 0, interpretation = null, isReversed = false
    )

    private val testPhotoEntity = ReadingPhotoEntity(
        id = 1L, readingId = 1L, photoUri = "content://test/photo.jpg", caption = null
    )

    private val testReadingWithSpread = ReadingWithSpread(
        id = 1L,
        title = "Test Reading",
        spreadId = 1L,
        spreadName = "Three Card",
        createdAt = 1000L,
        notes = "Notes"
    )

    @BeforeEach
    fun setUp() {
        repository = ReadingRepository(readingDao, readingCardDao, readingPhotoDao, cardDao, spreadDao, readingTagDao)
    }

    @Nested
    inner class ObserveAllReadings {

        @Test
        fun `observeAllReadings maps entities to domain models`() = runTest {
            every { readingDao.observeAllReadings() } returns flowOf(listOf(testReadingEntity))

            repository.observeAllReadings().test {
                val readings = awaitItem()
                assertEquals(1, readings.size)
                assertEquals(testReading, readings[0])
                cancelAndIgnoreRemainingEvents()
            }
        }
    }

    @Nested
    inner class ObserveReadingWithDetails {

        @Test
        fun `observeReadingWithDetails combines reading, cards, photos, spread name, and full card details`() = runTest {
            every { readingDao.observeReadingById(1L) } returns flowOf(testReadingEntity)
            every { readingCardDao.observeCardsForReading(1L) } returns flowOf(listOf(testReadingCardEntity))
            every { readingPhotoDao.observePhotosForReading(1L) } returns flowOf(listOf(testPhotoEntity))
            every { spreadDao.getSpreadById(1L) } returns testSpreadEntity
            coEvery { cardDao.getCardsByIds(listOf(1L)) } returns listOf(testCardEntity)

            repository.observeReadingWithDetails(1L).test {
                val detail = awaitItem()
                assertEquals(testReading, detail!!.reading)
                assertEquals("Three Card Spread", detail.spreadName)
                assertEquals(1, detail.cards.size)
                assertEquals("The Fool", detail.cards[0].card.name)
                assertEquals(1, detail.photos.size)
                assertEquals("content://test/photo.jpg", detail.photos[0].photoUri)
                cancelAndIgnoreRemainingEvents()
            }
        }

        @Test
        fun `observeReadingWithDetails returns null when reading is null`() = runTest {
            every { readingDao.observeReadingById(99L) } returns flowOf(null)
            every { readingCardDao.observeCardsForReading(99L) } returns flowOf(emptyList())
            every { readingPhotoDao.observePhotosForReading(99L) } returns flowOf(emptyList())

            repository.observeReadingWithDetails(99L).test {
                val detail = awaitItem()
                assertEquals(null, detail)
                cancelAndIgnoreRemainingEvents()
            }
        }

        @Test
        fun `observeReadingWithDetails uses Unknown Spread when spread not found`() = runTest {
            every { readingDao.observeReadingById(1L) } returns flowOf(testReadingEntity)
            every { readingCardDao.observeCardsForReading(1L) } returns flowOf(emptyList())
            every { readingPhotoDao.observePhotosForReading(1L) } returns flowOf(emptyList())
            every { spreadDao.getSpreadById(1L) } returns null
            coEvery { cardDao.getCardsByIds(emptyList()) } returns emptyList()

            repository.observeReadingWithDetails(1L).test {
                val detail = awaitItem()
                assertEquals("Unknown Spread", detail!!.spreadName)
                assertTrue(detail.cards.isEmpty())
                cancelAndIgnoreRemainingEvents()
            }
        }
    }

@Nested
    inner class CreateReading {

        @Test
        fun `createReading inserts reading and cards, returns id`() = runTest {
            coEvery { readingDao.insert(any()) } returns 1L
            coEvery { readingCardDao.insertAll(any()) } returns Unit

            val readingId = repository.createReading(testReading, listOf(
                ReadingCard(id = 0L, readingId = 0L, cardId = 1L, positionName = "Past", positionOrder = 0)
            ))

            assertEquals(1L, readingId)
            coVerify { readingDao.insert(match { it.title == "Test Reading" && it.spreadId == 1L }) }
            coVerify { readingCardDao.insertAll(match { it.size == 1 && it[0].readingId == 1L }) }
        }

        @Test
        fun `createReading with empty cards list`() = runTest {
            coEvery { readingDao.insert(any()) } returns 1L
            coEvery { readingCardDao.insertAll(any()) } returns Unit

            val readingId = repository.createReading(testReading, emptyList())

            assertEquals(1L, readingId)
            coVerify { readingDao.insert(any()) }
            coVerify { readingCardDao.insertAll(emptyList()) }
        }

        @Test
        fun `createReading maps card with generated readingId`() = runTest {
            coEvery { readingDao.insert(any()) } returns 5L
            coEvery { readingCardDao.insertAll(any()) } returns Unit

            val readingId = repository.createReading(testReading.copy(id = 0), listOf(
                ReadingCard(id = 0L, readingId = 0L, cardId = 1L, positionName = "Past", positionOrder = 0)
            ))

            assertEquals(5L, readingId)
            coVerify { readingCardDao.insertAll(match { it.size == 1 && it[0].readingId == 5L }) }
        }
    }

    @Nested
    inner class UpdateReading {

        @Test
        fun `updateReading updates reading, deletes existing cards, re-inserts new cards`() = runTest {
            coEvery { readingCardDao.getCardsForReading(1L) } returns listOf(
                ReadingCardEntity(id = 1L, readingId = 1L, cardId = 1L, positionName = "Past", positionOrder = 0)
            )
            coEvery { readingDao.update(any()) } returns Unit
            coEvery { readingCardDao.delete(any()) } returns Unit
            coEvery { readingCardDao.insertAll(any()) } returns Unit

            val updatedCards = listOf(
                ReadingCard(
                    id = 0L, readingId = 1L, cardId = 2L,
                    positionName = "Present", positionOrder = 0
                )
            )

            repository.updateReading(testReading, updatedCards)

            coVerify { readingDao.update(match { it.id == 1L }) }
            coVerifyOrder {
                readingCardDao.getCardsForReading(1L)
                readingCardDao.delete(any())
                readingCardDao.insertAll(any())
            }
        }
    }

    @Nested
    inner class DeleteReading {

        @Test
        fun `deleteReading gets reading then deletes it`() = runTest {
            coEvery { readingDao.getReadingById(1L) } returns testReadingEntity
            coEvery { readingDao.delete(any()) } returns Unit

            repository.deleteReading(1L)

            coVerify { readingDao.getReadingById(1L) }
            coVerify { readingDao.delete(testReadingEntity) }
        }

        @Test
        fun `deleteReading does nothing when reading not found`() = runTest {
            coEvery { readingDao.getReadingById(99L) } returns null

            repository.deleteReading(99L)

            coVerify(exactly = 0) { readingDao.delete(any()) }
        }
    }

    @Nested
    inner class ToggleFavorite {

        @Test
        fun `toggleFavorite flips isFavorite from false to true`() = runTest {
            val unfavorited = testReadingEntity.copy(isFavorite = false)
            coEvery { readingDao.getReadingById(1L) } returns unfavorited
            coEvery { readingDao.update(any()) } returns Unit

            repository.toggleFavorite(1L)

            coVerify { readingDao.update(match { it.isFavorite == true }) }
        }

        @Test
        fun `toggleFavorite flips isFavorite from true to false`() = runTest {
            val favorited = testReadingEntity.copy(isFavorite = true)
            coEvery { readingDao.getReadingById(1L) } returns favorited
            coEvery { readingDao.update(any()) } returns Unit

            repository.toggleFavorite(1L)

            coVerify { readingDao.update(match { it.isFavorite == false }) }
        }

        @Test
        fun `toggleFavorite does nothing when reading not found`() = runTest {
            coEvery { readingDao.getReadingById(99L) } returns null

            repository.toggleFavorite(99L)

            coVerify(exactly = 0) { readingDao.update(any()) }
        }
    }

    @Nested
    inner class PhotoManagement {

        @Test
        fun `addPhotoToReading inserts photo entity with correct readingId and uri`() = runTest {
            coEvery { readingPhotoDao.insert(any()) } returns 2L

            val photoId = repository.addPhotoToReading(1L, "content://test/new.jpg")

            assertEquals(2L, photoId)
            coVerify {
                readingPhotoDao.insert(match {
                    it.readingId == 1L && it.photoUri == "content://test/new.jpg"
                })
            }
        }

        @Test
        fun `removePhotoFromReading gets photo then deletes it`() = runTest {
            val photoEntity = ReadingPhotoEntity(
                id = 1L, readingId = 1L, photoUri = "content://test/photo.jpg"
            )
            coEvery { readingPhotoDao.getPhotoById(1L) } returns photoEntity
            coEvery { readingPhotoDao.delete(any()) } returns Unit

            repository.removePhotoFromReading(1L)

            coVerify { readingPhotoDao.getPhotoById(1L) }
            coVerify { readingPhotoDao.delete(photoEntity) }
        }

        @Test
        fun `removePhotoFromReading does nothing when photo not found`() = runTest {
            coEvery { readingPhotoDao.getPhotoById(99L) } returns null

            repository.removePhotoFromReading(99L)

            coVerify(exactly = 0) { readingPhotoDao.delete(any()) }
        }
    }

    @Nested
    inner class ObserveAllReadingsWithSpread {

        @Test
        fun `observeAllReadingsWithSpread returns flow with spread info`() = runTest {
            every { readingDao.observeAllReadingsWithSpread() } returns flowOf(listOf(testReadingWithSpread))

            repository.observeAllReadingsWithSpread().test {
                val result = awaitItem()
                assertEquals(1, result.size)
                cancelAndIgnoreRemainingEvents()
            }
        }

        @Test
        fun `observeAllReadingsWithSpread emits empty when no readings`() = runTest {
            every { readingDao.observeAllReadingsWithSpread() } returns flowOf(emptyList())

            repository.observeAllReadingsWithSpread().test {
                val result = awaitItem()
                assertEquals(0, result.size)
                cancelAndIgnoreRemainingEvents()
            }
        }
    }

    @Nested
    inner class FavoritesObserving {

        @Test
        fun `observeFavorites returns flow of favorite readings`() = runTest {
            every { readingDao.observeFavorites() } returns flowOf(listOf(testReadingEntity))

            repository.observeFavorites().test {
                val result = awaitItem()
                assertEquals(1, result.size)
                cancelAndIgnoreRemainingEvents()
            }
        }

        @Test
        fun `observeFavoritesWithSpread returns flow with spread info`() = runTest {
            every { readingDao.observeFavoritesWithSpread() } returns flowOf(listOf(testReadingWithSpread))

            repository.observeFavoritesWithSpread().test {
                val result = awaitItem()
                assertEquals(1, result.size)
                cancelAndIgnoreRemainingEvents()
            }
        }
    }

    @Nested
    inner class ObserveReadingsByTags {

        @Test
        fun `observeReadingsByTags returns readings with any of the tags`() = runTest {
            every { readingTagDao.observeReadingIdsByTags(listOf(1L, 2L)) } returns flowOf(listOf(1L))
            coEvery { readingDao.getReadingById(1L) } returns testReadingEntity

            repository.observeReadingsByTags(listOf(1L, 2L)).test {
                val result = awaitItem()
                assertEquals(1, result.size)
                cancelAndIgnoreRemainingEvents()
            }
        }

        @Test
        fun `observeReadingsByTags returns empty when no readings have tags`() = runTest {
            every { readingTagDao.observeReadingIdsByTags(listOf(99L)) } returns flowOf(emptyList())

            repository.observeReadingsByTags(listOf(99L)).test {
                val result = awaitItem()
                assertEquals(0, result.size)
                cancelAndIgnoreRemainingEvents()
            }
        }

        @Test
        fun `observeReadingsByTags handles readingNotFound in mapNotNull`() = runTest {
            // First ID returns a reading, second ID returns null (simulates deleted reading)
            every { readingTagDao.observeReadingIdsByTags(listOf(1L, 2L)) } returns flowOf(listOf(1L, 2L))
            coEvery { readingDao.getReadingById(1L) } returns testReadingEntity
            coEvery { readingDao.getReadingById(2L) } returns null

            repository.observeReadingsByTags(listOf(1L, 2L)).test {
                val result = awaitItem()
                // Should only have 1 reading since second was null (filtered out by mapNotNull)
                assertEquals(1, result.size)
                cancelAndIgnoreRemainingEvents()
            }
        }
    }
}
