package com.example.drawn.data.repository

import app.cash.turbine.test
import com.example.drawn.data.database.dao.DeckDao
import com.example.drawn.data.database.entity.DeckEntity
import com.example.drawn.domain.model.Deck
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import java.time.Instant

@ExtendWith(io.mockk.junit5.MockKExtension::class)
class DeckRepositoryTest {

    private val deckDao: DeckDao = mockk()
    private lateinit var repository: DeckRepository

    private val testDeck = Deck(
        id = 1L,
        name = "Rider-Waite Smith",
        description = "The classic tarot deck",
        isCustom = false,
        createdAt = Instant.parse("2026-01-01T00:00:00Z")
    )

    private val testDeckEntity = DeckEntity(
        id = 1L,
        name = "Rider-Waite Smith",
        description = "The classic tarot deck",
        isCustom = false,
        createdAt = Instant.parse("2026-01-01T00:00:00Z").toEpochMilli()
    )

    @BeforeEach
    fun setUp() {
        repository = DeckRepository(deckDao)
    }

    @Nested
    inner class ObserveAllDecks {

        @Test
        fun `observeAllDecks returns flow of decks`() = runTest {
            every { deckDao.observeAllDecks() } returns flowOf(listOf(testDeckEntity))

            repository.observeAllDecks().test {
                val result = awaitItem()
                assertEquals(1, result.size)
                assertEquals("Rider-Waite Smith", result[0].name)
                cancelAndIgnoreRemainingEvents()
            }
        }

        @Test
        fun `observeAllDecks emits empty list when no decks`() = runTest {
            every { deckDao.observeAllDecks() } returns flowOf(emptyList())

            repository.observeAllDecks().test {
                val result = awaitItem()
                assertEquals(0, result.size)
                cancelAndIgnoreRemainingEvents()
            }
        }
    }

    @Nested
    inner class CreateDeck {

        @Test
        fun `createDeck inserts deck and returns id`() = runTest {
            coEvery { deckDao.insert(any()) } returns 1L

            val result = repository.createDeck(testDeck)

            assertEquals(1L, result)
            coVerify { deckDao.insert(any()) }
        }

        @Test
        fun `createDeck converts domain to entity before inserting`() = runTest {
            coEvery { deckDao.insert(any()) } returns 5L

            val result = repository.createDeck(testDeck.copy(id = 0))

            assertEquals(5L, result)
        }
    }

    @Nested
    inner class UpdateDeck {

        @Test
        fun `updateDeck calls dao update`() = runTest {
            coEvery { deckDao.update(any()) } returns Unit

            repository.updateDeck(testDeck)

            coVerify { deckDao.update(any()) }
        }
    }

    @Nested
    inner class DeleteDeck {

        @Test
        fun `deleteDeck deletes deck when it exists`() = runTest {
            coEvery { deckDao.getDeckById(1L) } returns testDeckEntity
            coEvery { deckDao.delete(any()) } returns Unit

            repository.deleteDeck(1L)

            coVerify { deckDao.delete(testDeckEntity) }
        }

        @Test
        fun `deleteDeck does nothing when deck not found`() = runTest {
            coEvery { deckDao.getDeckById(1L) } returns null

            repository.deleteDeck(1L)

            coVerify(exactly = 0) { deckDao.delete(any()) }
        }
    }
}