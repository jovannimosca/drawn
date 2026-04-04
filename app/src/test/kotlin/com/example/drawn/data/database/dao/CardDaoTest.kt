package com.example.drawn.data.database.dao

import app.cash.turbine.test
import com.example.drawn.data.database.DatabaseTest
import com.example.drawn.data.database.entity.CardEntity
import com.example.drawn.data.database.entity.DeckEntity
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.robolectric.annotation.Config
import tech.apter.junit.jupiter.robolectric.RobolectricExtension

/**
 * CardDao tests using Room in-memory database.
 * Tests CRUD operations and Flow-based observation.
 */
@ExtendWith(RobolectricExtension::class)
@Config(sdk = [33])
class CardDaoTest : DatabaseTest() {

    private val cardDao by lazy { db.cardDao() }
    private val deckDao by lazy { db.deckDao() }

    @BeforeEach
    fun insertTestDecks() {
        val deck1 = DeckEntity(id = 1L, name = "RWS", description = "Rider-Waite-Smith", createdAt = 1000L)
        val deck2 = DeckEntity(id = 2L, name = "Oracle", description = "Oracle Deck", createdAt = 2000L)
        kotlinx.coroutines.runBlocking {
            deckDao.insert(deck1)
            deckDao.insert(deck2)
        }
    }

    private fun createCardEntity(
        id: Long,
        deckId: Long = 1L,
        name: String = "Test Card",
        number: Int = 0,
        arcanaType: String = "MAJOR"
    ) = CardEntity(
        id = id,
        deckId = deckId,
        name = name,
        number = number,
        arcanaType = arcanaType,
        imageResId = 0,
        keywordsJson = "[]",
        meaningUpright = "Upright meaning",
        meaningReversed = "Reversed meaning"
    )

    @Test
    fun `insert card and observeAllCards emits it`() = runTest {
        val card = createCardEntity(id = 1L, name = "The Fool")

        cardDao.observeAllCards().test {
            val initial = awaitItem()
            assertTrue(initial.isEmpty())

            cardDao.insertAll(listOf(card))

            val afterInsert = awaitItem()
            assertEquals(1, afterInsert.size)
            assertEquals("The Fool", afterInsert[0].name)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `insert multiple cards ordered by arcanaType, number`() = runTest {
        val major2 = createCardEntity(id = 2L, name = "Magician", number = 1, arcanaType = "MAJOR")
        val major1 = createCardEntity(id = 1L, name = "Fool", number = 0, arcanaType = "MAJOR")
        val minor1 = createCardEntity(id = 3L, name = "Ace of Cups", number = 1, arcanaType = "MINOR")

        cardDao.insertAll(listOf(major2, major1, minor1))

        cardDao.observeAllCards().test {
            val items = awaitItem()
            assertEquals(3, items.size)
            // Ordered by arcanaType (MAJOR < MINOR alphabetically), then number
            assertEquals("Fool", items[0].name)
            assertEquals("Magician", items[1].name)
            assertEquals("Ace of Cups", items[2].name)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `observeCardById emits correct card`() = runTest {
        val card = createCardEntity(id = 1L, name = "The Empress")
        cardDao.insertAll(listOf(card))

        cardDao.observeCardById(1L).test {
            val entity = awaitItem()
            assertEquals("The Empress", entity!!.name)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `getCardsByIds returns matching cards`() = runTest {
        val card1 = createCardEntity(id = 1L, name = "Card One")
        val card2 = createCardEntity(id = 2L, name = "Card Two")
        val card3 = createCardEntity(id = 3L, name = "Card Three")
        cardDao.insertAll(listOf(card1, card2, card3))

        val result = cardDao.getCardsByIds(listOf(1L, 3L))

        assertEquals(2, result.size)
        val names = result.map { it.name }.toSet()
        assertTrue(names.contains("Card One"))
        assertTrue(names.contains("Card Three"))
    }

    @Test
    fun `observeCardsByDeck filters by deckId correctly`() = runTest {
        val deck1Card1 = createCardEntity(id = 1L, deckId = 1L, name = "Deck1 Card1")
        val deck1Card2 = createCardEntity(id = 2L, deckId = 1L, name = "Deck1 Card2")
        val deck2Card = createCardEntity(id = 3L, deckId = 2L, name = "Deck2 Card")

        cardDao.insertAll(listOf(deck1Card1, deck1Card2, deck2Card))

        cardDao.observeCardsByDeck(1L).test {
            val items = awaitItem()
            assertEquals(2, items.size)
            assertTrue(items.all { it.deckId == 1L })
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `insertAll with OnConflictStrategy REPLACE updates existing cards`() = runTest {
        val original = createCardEntity(id = 1L, name = "Original Name")
        cardDao.insertAll(listOf(original))

        val updated = createCardEntity(id = 1L, name = "Updated Name")
        cardDao.insertAll(listOf(updated))

        cardDao.observeCardById(1L).test {
            val entity = awaitItem()
            assertEquals("Updated Name", entity!!.name)
            cancelAndIgnoreRemainingEvents()
        }
    }
}
