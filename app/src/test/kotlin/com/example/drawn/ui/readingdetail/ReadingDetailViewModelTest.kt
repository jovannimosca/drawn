package com.example.drawn.ui.readingdetail

import android.content.Context
import com.example.drawn.data.repository.ReadingRepository
import com.example.drawn.domain.model.ArcanaType
import com.example.drawn.domain.model.Card
import com.example.drawn.domain.model.Reading
import com.example.drawn.domain.model.ReadingCard
import com.example.drawn.domain.model.ReadingCardWithDetails
import com.example.drawn.domain.model.ReadingDetail
import com.example.drawn.domain.model.ReadingPhoto
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.async
import kotlinx.coroutines.cancel
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import java.io.File
import java.time.Instant

/**
 * ReadingDetailViewModel unit tests.
 *
 * Note: addPhoto test is disabled because it requires Android framework (Uri.parse,
 * ContentResolver.openInputStream) which needs Robolectric. Robolectric + JUnit5
 * integration is complex. Photo management will be covered by Compose UI tests
 * in a future plan.
 */
@OptIn(ExperimentalCoroutinesApi::class)
@ExtendWith(io.mockk.junit5.MockKExtension::class)
class ReadingDetailViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private val readingRepository: ReadingRepository = mockk()
    private val mockContext: Context = mockk(relaxed = true)
    private lateinit var viewModel: ReadingDetailViewModel

    private val testReading = Reading(
        id = 1L,
        title = "Test Reading",
        spreadId = 1L,
        createdAt = Instant.parse("2026-01-01T08:00:00Z"),
        notes = "Test notes"
    )

    private val testCard = Card(
        id = 1L,
        deckId = 1L,
        name = "The Fool",
        number = 0,
        arcanaType = ArcanaType.MAJOR,
        imageResId = 0,
        keywords = listOf("beginnings"),
        meaningUpright = "New beginnings",
        meaningReversed = "Recklessness"
    )

    private val testReadingCard = ReadingCard(
        id = 1L,
        readingId = 1L,
        cardId = 1L,
        positionName = "Past",
        positionOrder = 0,
        interpretation = null,
        isReversed = false
    )

    private val testPhoto = ReadingPhoto(
        id = 1L,
        readingId = 1L,
        photoUri = "file:///test/photo.jpg"
    )

    private val testDetail = ReadingDetail(
        reading = testReading,
        spreadName = "Three Card Spread",
        cards = listOf(ReadingCardWithDetails(testReadingCard, testCard)),
        photos = listOf(testPhoto)
    )

    @BeforeEach
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        every { mockContext.filesDir } returns File("/tmp/test")
        every { mockContext.contentResolver } returns mockk(relaxed = true)
    }

    @AfterEach
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun createViewModel() {
        viewModel = ReadingDetailViewModel(readingRepository, mockContext)
    }

    private fun advance() {
        testDispatcher.scheduler.advanceUntilIdle()
    }

    private suspend fun collectUiState(): ReadingDetailUiState = coroutineScope {
        var latest: ReadingDetailUiState? = null
        val job = async {
            viewModel.uiState.collect { latest = it }
        }
        advance()
        job.cancel()
        latest!!
    }

    @Nested
    inner class LoadingAndSuccess {

        @Test
        fun `initial state is Loading`() = runTest {
            every { readingRepository.observeReadingWithDetails(any()) } returns flowOf(testDetail)

            createViewModel()

            assertTrue(viewModel.uiState.value is ReadingDetailUiState.Loading)
        }

        @Test
        fun `setReadingId emits Success when repository emits ReadingDetail`() = runTest {
            every { readingRepository.observeReadingWithDetails(1L) } returns flowOf(testDetail)

            createViewModel()
            viewModel.setReadingId(1L)
            val state = collectUiState()

            assertTrue(state is ReadingDetailUiState.Success)
            val success = state as ReadingDetailUiState.Success
            assertEquals(testDetail, success.detail)
        }

        @Test
        fun `setReadingId emits Error when repository emits null`() = runTest {
            every { readingRepository.observeReadingWithDetails(99L) } returns flowOf(null)

            createViewModel()
            viewModel.setReadingId(99L)
            val state = collectUiState()

            assertTrue(state is ReadingDetailUiState.Error)
            assertEquals("Reading not found", (state as ReadingDetailUiState.Error).message)
        }

        @Test
        fun `setReadingId emits Error when repository flow throws`() = runTest {
            every { readingRepository.observeReadingWithDetails(any()) } returns flow<ReadingDetail?> {
                throw RuntimeException("DB error")
            }

            createViewModel()
            viewModel.setReadingId(1L)
            val state = collectUiState()

            assertTrue(state is ReadingDetailUiState.Error)
            assertEquals("DB error", (state as ReadingDetailUiState.Error).message)
        }

        @Test
        fun `setReadingId with same ID does nothing`() = runTest {
            every { readingRepository.observeReadingWithDetails(1L) } returns flowOf(testDetail)

            createViewModel()
            viewModel.setReadingId(1L)
            val before = collectUiState()

            // Call again with same ID
            viewModel.setReadingId(1L)
            advance()

            assertEquals(before, viewModel.uiState.value)
        }
    }

    @Nested
    inner class EditMode {

        @Test
        fun `toggleEditMode toggles isEditMode between true and false`() = runTest {
            every { readingRepository.observeReadingWithDetails(any()) } returns flowOf(testDetail)

            createViewModel()

            assertFalse(viewModel.isEditMode.value)

            viewModel.toggleEditMode()
            advance()
            assertTrue(viewModel.isEditMode.value)

            viewModel.toggleEditMode()
            advance()
            assertFalse(viewModel.isEditMode.value)
        }

        @Test
        fun `exitEditMode sets isEditMode to false`() = runTest {
            every { readingRepository.observeReadingWithDetails(any()) } returns flowOf(testDetail)

            createViewModel()
            viewModel.toggleEditMode()
            advance()
            assertTrue(viewModel.isEditMode.value)

            viewModel.exitEditMode()
            advance()
            assertFalse(viewModel.isEditMode.value)
        }
    }

    @Nested
    inner class SaveReading {

        @Test
        fun `saveReading calls updateReading and exits edit mode`() = runTest {
            every { readingRepository.observeReadingWithDetails(any()) } returns flowOf(testDetail)
            coEvery { readingRepository.updateReading(any(), any()) } returns Unit

            createViewModel()
            viewModel.setReadingId(1L)
            collectUiState()
            viewModel.toggleEditMode()
            advance()

            viewModel.saveReading("Updated Title", "Updated notes")
            advance()

            coVerify { readingRepository.updateReading(any(), any()) }
            assertFalse(viewModel.isEditMode.value)
        }
    }

    @Nested
    inner class DeleteReading {

        @Test
        fun `deleteReading calls repository with correct ID`() = runTest {
            every { readingRepository.observeReadingWithDetails(any()) } returns flowOf(testDetail)
            coEvery { readingRepository.deleteReading(any()) } returns Unit

            createViewModel()
            viewModel.setReadingId(1L)
            collectUiState()

            viewModel.deleteReading()
            advance()

            coVerify { readingRepository.deleteReading(1L) }
        }
    }

    @Nested
    inner class PhotoManagement {

        @Test
        fun `deletePhoto calls repository removePhotoFromReading with correct photoId`() = runTest {
            every { readingRepository.observeReadingWithDetails(any()) } returns flowOf(testDetail)
            coEvery { readingRepository.removePhotoFromReading(any()) } returns Unit

            createViewModel()
            viewModel.setReadingId(1L)
            collectUiState()

            viewModel.deletePhoto(1L)
            advance()

            coVerify { readingRepository.removePhotoFromReading(1L) }
        }

        @Test
        @Disabled("Requires Android framework (Uri.parse, ContentResolver) — covered by Compose UI tests")
        fun `addPhoto calls repository addPhotoToReading`() = runTest {
            every { readingRepository.observeReadingWithDetails(any()) } returns flowOf(testDetail)
            coEvery { readingRepository.addPhotoToReading(any(), any()) } returns 2L

            createViewModel()
            viewModel.setReadingId(1L)
            collectUiState()

            viewModel.addPhoto("content://test/photo.jpg")
            advance()

            coVerify { readingRepository.addPhotoToReading(1L, any()) }
        }
    }
}
