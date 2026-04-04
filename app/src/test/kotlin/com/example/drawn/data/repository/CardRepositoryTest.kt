package com.example.drawn.data.repository

import app.cash.turbine.test
import com.example.drawn.data.database.dao.CardDao
import com.example.drawn.data.database.dao.DeckDao
import com.example.drawn.data.database.entity.CardEntity
import com.example.drawn.data.database.entity.toDomain
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

@ExtendWith(io.mockk.junit5.MockKExtension::class)
class CardRepositoryTest {

    private val cardDao: CardDao = mockk()
    private val deckDao: DeckDao = mockk()

    private lateinit var repository: CardRepository

    private val testCardEntity = CardEntity(
        id = 1L, deckId = 1L, name = "The Fool", number = 0,
        arcanaType = "MAJOR", imageResId = 0, keywordsJson = "[]",
        meaningUpright = "New beginnings", meaningReversed = "Recklessness"
    )

    @BeforeEach
    fun setUp() {
        repository = CardRepository(cardDao, deckDao)
    }

    @Nested
    inner class ObserveAllCards {

        @Test
        fun `observeAllCards maps entities to domain models`() = runTest {
            every { cardDao.observeAllCards() } returns flowOf(listOf(testCardEntity))

            repository.observeAllCards().test {
                val cards = awaitItem()
                assertEquals(1, cards.size)
                assertEquals(testCardEntity.toDomain(), cards[0])
                cancelAndIgnoreRemainingEvents()
            }
        }
    }

    @Nested
    inner class ObserveCardById {

        @Test
        fun `observeCardById maps entity to domain model`() = runTest {
            every { cardDao.observeCardById(1L) } returns flowOf(testCardEntity)

            repository.observeCardById(1L).test {
                val card = awaitItem()
                assertEquals(testCardEntity.toDomain(), card)
                cancelAndIgnoreRemainingEvents()
            }
        }

        @Test
        fun `observeCardById returns null when entity is null`() = runTest {
            every { cardDao.observeCardById(99L) } returns flowOf(null)

            repository.observeCardById(99L).test {
                val card = awaitItem()
                assertEquals(null, card)
                cancelAndIgnoreRemainingEvents()
            }
        }
    }

    @Nested
    inner class ObserveCardsByDeck {

        @Test
        fun `observeCardsByDeck passes deckId to DAO and maps results`() = runTest {
            every { cardDao.observeCardsByDeck(1L) } returns flowOf(listOf(testCardEntity))

            repository.observeCardsByDeck(1L).test {
                val cards = awaitItem()
                assertEquals(1, cards.size)
                assertEquals(testCardEntity.toDomain(), cards[0])
                cancelAndIgnoreRemainingEvents()
            }
        }
    }

    @Nested
    inner class InsertAllCards {

        @Test
        fun `insertAllCards maps domain cards to entities and calls DAO insertAll`() = runTest {
            coEvery { cardDao.insertAll(any()) } returns Unit

            val cards = listOf(testCardEntity.toDomain())
            repository.insertAllCards(cards)

            coVerify { cardDao.insertAll(listOf(testCardEntity)) }
        }
    }
}
