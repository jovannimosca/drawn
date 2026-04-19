package com.example.drawn.data.repository

import app.cash.turbine.test
import com.example.drawn.data.database.dao.CustomCardDao
import com.example.drawn.data.database.entity.CustomCardEntity
import com.example.drawn.domain.model.CustomCard
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
import io.mockk.junit5.MockKExtension

@ExtendWith(io.mockk.junit5.MockKExtension::class)
class CustomCardRepositoryTest {

    private val customCardDao: CustomCardDao = mockk()
    private lateinit var repository: CustomCardRepository

    private val testCardEntity = CustomCardEntity(
        id = 1L,
        deckId = 1L,
        name = "Custom Fool",
        imagePath = null,
        keywords = "",
        uprightMeaning = "New beginnings",
        reversedMeaning = "Recklessness"
    )

    private val testCard = CustomCard(
        id = 1L,
        deckId = 1L,
        name = "Custom Fool",
        imagePath = null,
        keywords = "",
        uprightMeaning = "New beginnings",
        reversedMeaning = "Recklessness"
    )

    @BeforeEach
    fun setUp() {
        repository = CustomCardRepository(customCardDao)
    }

    @Nested
    inner class ObserveCardsForDeck {

        @Test
        fun `observeCardsForDeck returns flow of cards for deck`() = runTest {
            every { customCardDao.observeCardsForDeck(1L) } returns flowOf(listOf(testCardEntity))

            repository.observeCardsForDeck(1L).test {
                val result = awaitItem()
                assertEquals(1, result.size)
                assertEquals("Custom Fool", result[0].name)
                cancelAndIgnoreRemainingEvents()
            }
        }

        @Test
        fun `observeCardsForDeck emits empty list when no cards`() = runTest {
            every { customCardDao.observeCardsForDeck(1L) } returns flowOf(emptyList())

            repository.observeCardsForDeck(1L).test {
                val result = awaitItem()
                assertEquals(0, result.size)
                cancelAndIgnoreRemainingEvents()
            }
        }
    }

    @Nested
    inner class InsertCard {

        @Test
        fun `insert maps domain to entity and calls DAO`() = runTest {
            coEvery { customCardDao.insert(any()) } returns 1L

            val result = repository.insert(testCard)

            assertEquals(1L, result)
            coVerify { customCardDao.insert(any()) }
        }

        @Test
        fun `insert returns generated id`() = runTest {
            coEvery { customCardDao.insert(any()) } returns 5L

            val result = repository.insert(testCard.copy(id = 0))

            assertEquals(5L, result)
        }
    }

    @Nested
    inner class UpdateCard {

        @Test
        fun `update calls DAO update`() = runTest {
            coEvery { customCardDao.update(any()) } returns Unit

            repository.update(testCard)

            coVerify { customCardDao.update(any()) }
        }
    }

    @Nested
    inner class DeleteCard {

        @Test
        fun `delete calls DAO delete with entity`() = runTest {
            coEvery { customCardDao.delete(any()) } returns Unit

            repository.delete(testCard)

            coVerify { customCardDao.delete(any()) }
        }
    }
}