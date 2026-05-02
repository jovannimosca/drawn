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

        @Test
        fun `observeCardsForDeck handles multiple cards`() = runTest {
            val card2 = testCardEntity.copy(id = 2L, name = "Card 2")
            val card3 = testCardEntity.copy(id = 3L, name = "Card 3")
            every { customCardDao.observeCardsForDeck(1L) } returns flowOf(listOf(testCardEntity, card2, card3))

            repository.observeCardsForDeck(1L).test {
                val result = awaitItem()
                assertEquals(3, result.size)
                cancelAndIgnoreRemainingEvents()
            }
        }

        @Test
        fun `observeCardsForDeck maps all entity fields to domain`() = runTest {
            val cardWithAllFields = testCardEntity.copy(
                name = "Oracle Card",
                imagePath = "/images/oracle.png",
                keywords = "wisdom,guidance",
                uprightMeaning = "Inner wisdom",
                reversedMeaning = "Blocked guidance"
            )
            every { customCardDao.observeCardsForDeck(1L) } returns flowOf(listOf(cardWithAllFields))

            repository.observeCardsForDeck(1L).test {
                val result = awaitItem()
                assertEquals("Oracle Card", result[0].name)
                assertEquals("/images/oracle.png", result[0].imagePath)
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

        @Test
        fun `insert handles card with all fields`() = runTest {
            coEvery { customCardDao.insert(any()) } returns 1L

            val cardWithAllFields = testCard.copy(
                name = "Oracle Card",
                imagePath = "/path/to/image.png",
                keywords = "wisdom,intuition",
                uprightMeaning = "Inner guidance",
                reversedMeaning = "Blocked intuition"
            )
            repository.insert(cardWithAllFields)

            coVerify { customCardDao.insert(match { it.name == "Oracle Card" }) }
        }

        @Test
        fun `insert preserves deck reference`() = runTest {
            coEvery { customCardDao.insert(any()) } returns 1L

            repository.insert(testCard.copy(deckId = 99L))

            coVerify { customCardDao.insert(match { it.deckId == 99L }) }
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

        @Test
        fun `update modifies card name`() = runTest {
            coEvery { customCardDao.update(any()) } returns Unit

            val updatedCard = testCard.copy(name = "Updated Name")
            repository.update(updatedCard)

            coVerify { customCardDao.update(match { it.name == "Updated Name" }) }
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

        @Test
        fun `delete handles different card`() = runTest {
            val differentCard = testCard.copy(id = 99L, name = "Different")
            coEvery { customCardDao.delete(any()) } returns Unit

            repository.delete(differentCard)

            coVerify { customCardDao.delete(match { it.id == 99L }) }
        }

        @Test
        fun `delete handles card with multiple fields`() = runTest {
            val complexCard = testCard.copy(
                id = 5L,
                name = "Complex Card",
                imagePath = "/complex.png",
                keywords = "a,b,c",
                uprightMeaning = "Meaning 1",
                reversedMeaning = "Meaning 2"
            )
            coEvery { customCardDao.delete(any()) } returns Unit

            repository.delete(complexCard)

            coVerify { customCardDao.delete(match { it.name == "Complex Card" }) }
        }
    }
}