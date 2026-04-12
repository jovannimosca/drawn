package com.example.drawn.ui.readinglist

import com.example.drawn.data.repository.ReadingRepository
import com.example.drawn.data.database.entity.ReadingWithSpread
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
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import java.time.Instant

@OptIn(ExperimentalCoroutinesApi::class)
@ExtendWith(io.mockk.junit5.MockKExtension::class)
class ReadingListViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private val readingRepository: ReadingRepository = mockk()
    private lateinit var viewModel: ReadingListViewModel

    private val testReading1 = ReadingWithSpread(
        id = 1L,
        title = "Morning Reading",
        spreadId = 1L,
        spreadName = "Three Card",
        createdAt = Instant.parse("2026-01-01T08:00:00Z").toEpochMilli(),
        notes = "Feeling optimistic"
    )

    private val testReading2 = ReadingWithSpread(
        id = 2L,
        title = "Evening Reflection",
        spreadId = 2L,
        spreadName = "Past Present Future",
        createdAt = Instant.parse("2026-01-01T20:00:00Z").toEpochMilli(),
        notes = "Need to focus on career"
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
        viewModel = ReadingListViewModel(readingRepository)
    }

    private fun advance() {
        testDispatcher.scheduler.advanceUntilIdle()
    }

    /**
     * Collect the uiState flow to trigger the WhileSubscribed sharing,
     * then advance the scheduler and return the final state.
     */
    private suspend fun collectUiState(): ReadingListUiState = coroutineScope {
        var latest: ReadingListUiState? = null
        val job = async {
            viewModel.uiState.collect { latest = it }
        }
        advance()
        job.cancel()
        latest!!
    }

    @Nested
    inner class Initialization {

        @Test
        fun `initial state is Loading`() = runTest {
            every { readingRepository.observeAllReadingsWithSpread() } returns flowOf(emptyList())

            createViewModel()

            // uiState starts with initialValue = Loading
            assertTrue(viewModel.uiState.value is ReadingListUiState.Loading)
        }

        @Test
        fun `emits Success when repository emits readings`() = runTest {
            every { readingRepository.observeAllReadingsWithSpread() } returns flowOf(listOf(testReading1, testReading2))

            createViewModel()
            val state = collectUiState()

            assertTrue(state is ReadingListUiState.Success)
            val success = state as ReadingListUiState.Success
            assertEquals(2, success.readings.size)
        }

        @Test
        fun `emits Error when repository flow throws`() = runTest {
            every { readingRepository.observeAllReadingsWithSpread() } returns flow<List<ReadingWithSpread>> {
                throw RuntimeException("Database error")
            }

            createViewModel()
            val state = collectUiState()

            assertTrue(state is ReadingListUiState.Error)
            assertEquals("Database error", (state as ReadingListUiState.Error).message)
        }
    }

    @Nested
    inner class SearchFiltering {

        @Test
        fun `blank query returns all readings`() = runTest {
            every { readingRepository.observeAllReadingsWithSpread() } returns flowOf(listOf(testReading1, testReading2))

            createViewModel()
            val state = collectUiState()

            assertTrue(state is ReadingListUiState.Success)
            val success = state as ReadingListUiState.Success
            assertEquals(2, success.readings.size)
        }

        @Test
        fun `non-blank query filters by title case-insensitive`() = runTest {
            every { readingRepository.observeAllReadingsWithSpread() } returns flowOf(listOf(testReading1, testReading2))

            createViewModel()
            // First collect to trigger the flow
            collectUiState()

            viewModel.onSearchQueryChange("morning")
            val state = collectUiState()

            assertTrue(state is ReadingListUiState.Success)
            val success = state as ReadingListUiState.Success
            assertEquals(1, success.readings.size)
            assertEquals("Morning Reading", success.readings[0].title)
        }

        @Test
        fun `non-blank query filters by notes case-insensitive`() = runTest {
            every { readingRepository.observeAllReadingsWithSpread() } returns flowOf(listOf(testReading1, testReading2))

            createViewModel()
            collectUiState()

            viewModel.onSearchQueryChange("career")
            val state = collectUiState()

            assertTrue(state is ReadingListUiState.Success)
            val success = state as ReadingListUiState.Success
            assertEquals(1, success.readings.size)
            assertEquals("Evening Reflection", success.readings[0].title)
        }

        @Test
        fun `query with no matches returns empty list`() = runTest {
            every { readingRepository.observeAllReadingsWithSpread() } returns flowOf(listOf(testReading1, testReading2))

            createViewModel()
            collectUiState()

            viewModel.onSearchQueryChange("nonexistent")
            val state = collectUiState()

            assertTrue(state is ReadingListUiState.Success)
            val success = state as ReadingListUiState.Success
            assertEquals(0, success.readings.size)
        }
    }

    @Nested
    inner class SearchQueryState {

        @Test
        fun `onSearchQueryChange updates searchQuery StateFlow`() = runTest {
            every { readingRepository.observeAllReadingsWithSpread() } returns flowOf(emptyList())

            createViewModel()

            assertEquals("", viewModel.searchQuery.value)

            viewModel.onSearchQueryChange("test query")
            advance()

            assertEquals("test query", viewModel.searchQuery.value)
        }
    }
}
