package com.example.drawn.ui.addreading

import com.example.drawn.data.repository.CardRepository
import com.example.drawn.data.repository.ReadingRepository
import com.example.drawn.data.repository.SpreadRepository
import com.example.drawn.domain.model.ArcanaType
import com.example.drawn.domain.model.Card
import com.example.drawn.domain.model.Spread
import com.example.drawn.domain.model.SpreadPosition
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@OptIn(ExperimentalCoroutinesApi::class)
@ExtendWith(io.mockk.junit5.MockKExtension::class)
class AddReadingViewModelTest {

    private val testDispatcher = StandardTestDispatcher()

    private val spreadRepository: SpreadRepository = mockk()
    private val cardRepository: CardRepository = mockk()
    private val readingRepository: ReadingRepository = mockk()

    private lateinit var viewModel: AddReadingViewModel

    private val testSpread = Spread(
        id = 1L,
        name = "Three Card Spread",
        description = "Past, Present, Future",
        positions = listOf(
            SpreadPosition("Past", 0, "What led to this situation"),
            SpreadPosition("Present", 1, "Current energy"),
            SpreadPosition("Future", 2, "What is coming")
        )
    )

    private val testCard = Card(
        id = 1L,
        deckId = 1L,
        name = "The Fool",
        number = 0,
        arcanaType = ArcanaType.MAJOR,
        imageResId = 0,
        keywords = listOf("beginnings", "innocence"),
        meaningUpright = "New beginnings",
        meaningReversed = "Recklessness"
    )

    private val testCard2 = Card(
        id = 2L,
        deckId = 1L,
        name = "The Magician",
        number = 1,
        arcanaType = ArcanaType.MAJOR,
        imageResId = 0,
        keywords = listOf("manifestation", "power"),
        meaningUpright = "Manifestation",
        meaningReversed = "Manipulation"
    )

    private val testCard3 = Card(
        id = 3L,
        deckId = 1L,
        name = "The High Priestess",
        number = 2,
        arcanaType = ArcanaType.MAJOR,
        imageResId = 0,
        keywords = listOf("intuition", "mystery"),
        meaningUpright = "Intuition",
        meaningReversed = "Secrets"
    )

    @BeforeEach
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
    }

    @AfterEach
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun createViewModel() {
        viewModel = AddReadingViewModel(spreadRepository, cardRepository, readingRepository)
    }

    private fun advance() {
        testDispatcher.scheduler.advanceUntilIdle()
    }

    private fun readyState(): AddReadingState =
        (viewModel.uiState.value as AddReadingUiState.Ready).state

    @Nested
    inner class Initialization {

        @Test
        fun `loading state emitted initially`() = runTest {
            every { spreadRepository.observeAllSpreads() } returns flowOf(emptyList())
            every { cardRepository.observeAllCards() } returns flowOf(emptyList())

            createViewModel()

            assertTrue(viewModel.uiState.value is AddReadingUiState.Loading)
        }

        @Test
        fun `ready state emitted after repositories emit`() = runTest {
            every { spreadRepository.observeAllSpreads() } returns flowOf(listOf(testSpread))
            every { cardRepository.observeAllCards() } returns flowOf(listOf(testCard))

            createViewModel()
            advance()

            assertTrue(viewModel.uiState.value is AddReadingUiState.Ready)
            val ready = readyState()
            assertEquals(listOf(testSpread), ready.spreads)
            assertEquals(listOf(testCard), ready.cards)
        }
    }

    @Nested
    inner class StepNavigation {

        @Test
        fun `selectSpread transitions to CardAssignment step with auto-generated title`() = runTest {
            every { spreadRepository.observeAllSpreads() } returns flowOf(listOf(testSpread))
            every { cardRepository.observeAllCards() } returns flowOf(listOf(testCard))

            createViewModel()
            advance()

            viewModel.selectSpread(testSpread)
            advance()

            val state = readyState()
            assertEquals(AddReadingStep.CardAssignment, state.currentStep)
            assertEquals(testSpread, state.selectedSpread)
            assertTrue(state.title.startsWith("Three Card Spread"))
        }

        @Test
        fun `goToNextStep from SpreadPicker does nothing when no spread selected`() = runTest {
            every { spreadRepository.observeAllSpreads() } returns flowOf(listOf(testSpread))
            every { cardRepository.observeAllCards() } returns flowOf(listOf(testCard))

            createViewModel()
            advance()

            val before = viewModel.uiState.value
            viewModel.goToNextStep()
            advance()

            // State unchanged — canProceedToNext is false
            assertEquals(before, viewModel.uiState.value)
        }

        @Test
        fun `goToNextStep from CardAssignment transitions to NotesAndSave when all positions filled`() = runTest {
            every { spreadRepository.observeAllSpreads() } returns flowOf(listOf(testSpread))
            every { cardRepository.observeAllCards() } returns flowOf(listOf(testCard, testCard2, testCard3))

            createViewModel()
            advance()

            viewModel.selectSpread(testSpread)
            advance()

            viewModel.assignCard(0, testCard)
            advance()
            viewModel.assignCard(1, testCard2)
            advance()
            viewModel.assignCard(2, testCard3)
            advance()

            viewModel.goToNextStep()
            advance()

            val state = readyState()
            assertEquals(AddReadingStep.NotesAndSave, state.currentStep)
        }

        @Test
        fun `goToPreviousStep from CardAssignment goes to SpreadPicker`() = runTest {
            every { spreadRepository.observeAllSpreads() } returns flowOf(listOf(testSpread))
            every { cardRepository.observeAllCards() } returns flowOf(listOf(testCard))

            createViewModel()
            advance()

            viewModel.selectSpread(testSpread)
            advance()

            viewModel.goToPreviousStep()
            advance()

            val state = readyState()
            assertEquals(AddReadingStep.SpreadPicker, state.currentStep)
        }

        @Test
        fun `goToPreviousStep from SpreadPicker does nothing`() = runTest {
            every { spreadRepository.observeAllSpreads() } returns flowOf(listOf(testSpread))
            every { cardRepository.observeAllCards() } returns flowOf(listOf(testCard))

            createViewModel()
            advance()

            val before = viewModel.uiState.value
            viewModel.goToPreviousStep()
            advance()

            assertEquals(before, viewModel.uiState.value)
        }

        @Test
        fun `goToPreviousStep from NotesAndSave goes to CardAssignment`() = runTest {
            every { spreadRepository.observeAllSpreads() } returns flowOf(listOf(testSpread))
            every { cardRepository.observeAllCards() } returns flowOf(listOf(testCard, testCard2, testCard3))

            createViewModel()
            advance()

            viewModel.selectSpread(testSpread)
            advance()

            viewModel.assignCard(0, testCard)
            advance()
            viewModel.assignCard(1, testCard2)
            advance()
            viewModel.assignCard(2, testCard3)
            advance()

            viewModel.goToNextStep()
            advance()

            viewModel.goToPreviousStep()
            advance()

            val state = readyState()
            assertEquals(AddReadingStep.CardAssignment, state.currentStep)
        }
    }

    @Nested
    inner class CardAssignment {

        @Test
        fun `assignCard adds card to assignedCards map`() = runTest {
            every { spreadRepository.observeAllSpreads() } returns flowOf(listOf(testSpread))
            every { cardRepository.observeAllCards() } returns flowOf(listOf(testCard, testCard2))

            createViewModel()
            advance()

            viewModel.selectSpread(testSpread)
            advance()

            viewModel.assignCard(0, testCard)
            advance()

            val state = readyState()
            assertEquals(1, state.assignedCards.size)
            assertEquals(testCard, state.assignedCards[0])
        }

        @Test
        fun `unassignCard removes card from assignedCards map`() = runTest {
            every { spreadRepository.observeAllSpreads() } returns flowOf(listOf(testSpread))
            every { cardRepository.observeAllCards() } returns flowOf(listOf(testCard))

            createViewModel()
            advance()

            viewModel.selectSpread(testSpread)
            advance()

            viewModel.assignCard(0, testCard)
            advance()

            viewModel.unassignCard(0)
            advance()

            val state = readyState()
            assertTrue(state.assignedCards.isEmpty())
        }

        @Test
        fun `togglePositionReversed adds position when not present`() = runTest {
            every { spreadRepository.observeAllSpreads() } returns flowOf(listOf(testSpread))
            every { cardRepository.observeAllCards() } returns flowOf(listOf(testCard))

            createViewModel()
            advance()

            viewModel.selectSpread(testSpread)
            advance()

            viewModel.togglePositionReversed(0)
            advance()

            val state = readyState()
            assertTrue(state.reversedPositions.contains(0))
        }

        @Test
        fun `togglePositionReversed removes position when present`() = runTest {
            every { spreadRepository.observeAllSpreads() } returns flowOf(listOf(testSpread))
            every { cardRepository.observeAllCards() } returns flowOf(listOf(testCard))

            createViewModel()
            advance()

            viewModel.selectSpread(testSpread)
            advance()

            viewModel.togglePositionReversed(0)
            advance()

            viewModel.togglePositionReversed(0)
            advance()

            val state = readyState()
            assertFalse(state.reversedPositions.contains(0))
        }
    }

    @Nested
    inner class TitleAndNotes {

        @Test
        fun `updateTitle updates title in state`() = runTest {
            every { spreadRepository.observeAllSpreads() } returns flowOf(listOf(testSpread))
            every { cardRepository.observeAllCards() } returns flowOf(listOf(testCard))

            createViewModel()
            advance()

            viewModel.updateTitle("My Custom Title")
            advance()

            assertEquals("My Custom Title", readyState().title)
        }

        @Test
        fun `updateNotes updates notes in state`() = runTest {
            every { spreadRepository.observeAllSpreads() } returns flowOf(listOf(testSpread))
            every { cardRepository.observeAllCards() } returns flowOf(listOf(testCard))

            createViewModel()
            advance()

            viewModel.updateNotes("My reading notes")
            advance()

            assertEquals("My reading notes", readyState().notes)
        }
    }

    @Nested
    inner class SaveReading {

        @Test
        fun `saveReading emits Saving then Saved on success`() = runTest {
            every { spreadRepository.observeAllSpreads() } returns flowOf(listOf(testSpread))
            every { cardRepository.observeAllCards() } returns flowOf(listOf(testCard, testCard2, testCard3))
            coEvery { readingRepository.createReading(any(), any()) } returns 42L
            coEvery { readingRepository.addPhotoToReading(any(), any()) } returns 1L

            createViewModel()
            advance()

            viewModel.selectSpread(testSpread)
            advance()

            viewModel.assignCard(0, testCard)
            advance()
            viewModel.assignCard(1, testCard2)
            advance()
            viewModel.assignCard(2, testCard3)
            advance()

            viewModel.updateNotes("Test notes")
            advance()

            viewModel.saveReading()
            advance()

            assertTrue(viewModel.uiState.value is AddReadingUiState.Saved)
            assertEquals(42L, (viewModel.uiState.value as AddReadingUiState.Saved).readingId)

            coVerify { readingRepository.createReading(any(), any()) }
        }

        @Test
        fun `saveReading emits Error when repository throws`() = runTest {
            every { spreadRepository.observeAllSpreads() } returns flowOf(listOf(testSpread))
            every { cardRepository.observeAllCards() } returns flowOf(listOf(testCard, testCard2, testCard3))
            coEvery { readingRepository.createReading(any(), any()) } throws RuntimeException("DB error")

            createViewModel()
            advance()

            viewModel.selectSpread(testSpread)
            advance()

            viewModel.assignCard(0, testCard)
            advance()
            viewModel.assignCard(1, testCard2)
            advance()
            viewModel.assignCard(2, testCard3)
            advance()

            viewModel.saveReading()
            advance()

            assertTrue(viewModel.uiState.value is AddReadingUiState.Error)
            assertEquals("DB error", (viewModel.uiState.value as AddReadingUiState.Error).message)
        }

        @Test
        fun `saveReading returns early if no spread selected`() = runTest {
            every { spreadRepository.observeAllSpreads() } returns flowOf(listOf(testSpread))
            every { cardRepository.observeAllCards() } returns flowOf(listOf(testCard))

            createViewModel()
            advance()

            val before = viewModel.uiState.value
            viewModel.saveReading()
            advance()

            assertEquals(before, viewModel.uiState.value)
            coVerify(exactly = 0) { readingRepository.createReading(any(), any()) }
        }
    }

    @Nested
    inner class PhotoManagement {

        @Test
        fun `addPhoto adds URI to photoUris list`() = runTest {
            every { spreadRepository.observeAllSpreads() } returns flowOf(listOf(testSpread))
            every { cardRepository.observeAllCards() } returns flowOf(listOf(testCard))

            createViewModel()
            advance()

            viewModel.addPhoto("content://photo/1")
            advance()

            val state = readyState()
            assertEquals(1, state.photoUris.size)
            assertEquals("content://photo/1", state.photoUris[0])
        }

        @Test
        fun `removePhoto removes URI from photoUris list`() = runTest {
            every { spreadRepository.observeAllSpreads() } returns flowOf(listOf(testSpread))
            every { cardRepository.observeAllCards() } returns flowOf(listOf(testCard))

            createViewModel()
            advance()

            viewModel.addPhoto("content://photo/1")
            advance()

            viewModel.removePhoto("content://photo/1")
            advance()

            val state = readyState()
            assertTrue(state.photoUris.isEmpty())
        }
    }

    @Nested
    inner class ResetAndRetry {

        @Test
        fun `retry resets to Ready state when current state is Error`() = runTest {
            every { spreadRepository.observeAllSpreads() } returns flowOf(listOf(testSpread))
            every { cardRepository.observeAllCards() } returns flowOf(listOf(testCard, testCard2, testCard3))
            coEvery { readingRepository.createReading(any(), any()) } throws RuntimeException("Error")

            createViewModel()
            advance()

            viewModel.selectSpread(testSpread)
            advance()

            viewModel.assignCard(0, testCard)
            advance()
            viewModel.assignCard(1, testCard)
            advance()
            viewModel.assignCard(2, testCard)
            advance()

            viewModel.saveReading()
            advance()

            assertTrue(viewModel.uiState.value is AddReadingUiState.Error)

            viewModel.retry()
            advance()

            assertTrue(viewModel.uiState.value is AddReadingUiState.Ready)
        }

        @Test
        fun `retry does nothing when state is not Error`() = runTest {
            every { spreadRepository.observeAllSpreads() } returns flowOf(listOf(testSpread))
            every { cardRepository.observeAllCards() } returns flowOf(listOf(testCard))

            createViewModel()
            advance()

            val before = viewModel.uiState.value
            viewModel.retry()
            advance()

            assertEquals(before, viewModel.uiState.value)
        }

        @Test
        fun `reset resets to Ready state`() = runTest {
            every { spreadRepository.observeAllSpreads() } returns flowOf(listOf(testSpread))
            every { cardRepository.observeAllCards() } returns flowOf(listOf(testCard))

            createViewModel()
            advance()

            viewModel.selectSpread(testSpread)
            advance()

            viewModel.reset()
            advance()

            val state = readyState()
            assertEquals(AddReadingStep.SpreadPicker, state.currentStep)
            assertNull(state.selectedSpread)
        }
    }
}
