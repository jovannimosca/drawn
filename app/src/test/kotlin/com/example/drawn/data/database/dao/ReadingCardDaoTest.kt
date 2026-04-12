package com.example.drawn.data.database.dao

import app.cash.turbine.test
import com.example.drawn.data.database.DatabaseTest
import com.example.drawn.data.database.entity.CardEntity
import com.example.drawn.data.database.entity.DeckEntity
import com.example.drawn.data.database.entity.ReadingCardEntity
import com.example.drawn.data.database.entity.ReadingEntity
import com.example.drawn.data.database.entity.SpreadEntity
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.robolectric.annotation.Config
import tech.apter.junit.jupiter.robolectric.RobolectricExtension

/**
 * ReadingCardDao tests using Room in-memory database.
 */
@ExtendWith(RobolectricExtension::class)
@Config(sdk = [33])
class ReadingCardDaoTest : DatabaseTest() {

    private val readingCardDao by lazy { db.readingCardDao() }
    private val readingDao by lazy { db.readingDao() }
    private val cardDao by lazy { db.cardDao() }
    private val spreadDao by lazy { db.spreadDao() }
    private val deckDao by lazy { db.deckDao() }

    @BeforeEach
    fun insertParentEntities() {
        kotlinx.coroutines.runBlocking {
            val spread = SpreadEntity(id = 1L, name = "Test Spread", description = "", positionsJson = "[]")
            spreadDao.insertAll(listOf(spread))

            val deck = DeckEntity(id = 1L, name = "RWS", description = "", createdAt = 1000L)
            deckDao.insert(deck)

            val card = CardEntity(
                id = 1L, deckId = 1L, name = "The Fool", number = 0, arcanaType = "MAJOR",
                imageResId = 0, keywordsJson = "[]", meaningUpright = "", meaningReversed = ""
            )
            cardDao.insertAll(listOf(card))
        }
    }

    private suspend fun insertTestReading(id: Long = 1L) {
        val reading = ReadingEntity(id = id, title = "Test Reading", spreadId = 1L, createdAt = 1000L)
        readingDao.insert(reading)
    }

    private fun createReadingCardEntity(
        id: Long = 0,
        readingId: Long = 1L,
        cardId: Long = 1L,
        positionName: String = "Past",
        positionOrder: Int = 0,
        interpretation: String? = null,
        isReversed: Boolean = false
    ) = ReadingCardEntity(
        id = id,
        readingId = readingId,
        cardId = cardId,
        positionName = positionName,
        positionOrder = positionOrder,
        interpretation = interpretation,
        isReversed = isReversed
    )

    @Test
    fun `insert reading card and observeCardsForReading emits it`() = runTest {
        insertTestReading()
        val card = createReadingCardEntity(readingId = 1L, cardId = 1L, positionName = "Past")

        readingCardDao.observeCardsForReading(1L).test {
            val initial = awaitItem()
            assertTrue(initial.isEmpty())

            readingCardDao.insertAll(listOf(card))

            val afterInsert = awaitItem()
            assertEquals(1, afterInsert.size)
            assertEquals("Past", afterInsert[0].positionName)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `insert multiple cards for same reading ordered by positionOrder`() = runTest {
        insertTestReading()
        val card1 = createReadingCardEntity(readingId = 1L, cardId = 1L, positionName = "Present", positionOrder = 1)
        val card2 = createReadingCardEntity(readingId = 1L, cardId = 1L, positionName = "Past", positionOrder = 0)
        val card3 = createReadingCardEntity(readingId = 1L, cardId = 1L, positionName = "Future", positionOrder = 2)

        readingCardDao.insertAll(listOf(card1, card2, card3))

        readingCardDao.observeCardsForReading(1L).test {
            val items = awaitItem()
            assertEquals(3, items.size)
            assertEquals("Past", items[0].positionName)
            assertEquals("Present", items[1].positionName)
            assertEquals("Future", items[2].positionName)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `getCardsForReading returns correct list`() = runTest {
        insertTestReading()
        val card1 = createReadingCardEntity(readingId = 1L, cardId = 1L, positionOrder = 0)
        val card2 = createReadingCardEntity(readingId = 1L, cardId = 1L, positionOrder = 1)
        readingCardDao.insertAll(listOf(card1, card2))

        val result = readingCardDao.getCardsForReading(1L)

        assertEquals(2, result.size)
        assertEquals(0, result[0].positionOrder)
        assertEquals(1, result[1].positionOrder)
    }

    @Test
    fun `delete card and observeCardsForReading no longer emits it`() = runTest {
        insertTestReading()
        val card = createReadingCardEntity(readingId = 1L, cardId = 1L, positionOrder = 0)
        readingCardDao.insertAll(listOf(card))

        readingCardDao.observeCardsForReading(1L).test {
            val beforeDelete = awaitItem()
            assertEquals(1, beforeDelete.size)

            val entity = readingCardDao.getCardsForReading(1L)[0]
            readingCardDao.delete(entity)

            val afterDelete = awaitItem()
            assertTrue(afterDelete.isEmpty())
            cancelAndIgnoreRemainingEvents()
        }
    }
}
