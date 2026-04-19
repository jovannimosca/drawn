package com.example.drawn.ui.readinglist

import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithContentDescription
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.semantics.ProgressBarRangeInfo
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.drawn.data.database.entity.ReadingWithSpread
import com.example.drawn.ui.theme.DrawnTheme
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.time.Instant

@RunWith(AndroidJUnit4::class)
class ReadingListScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private lateinit var viewModel: ReadingListViewModel
    private lateinit var uiStateFlow: MutableStateFlow<ReadingListUiState>
    private lateinit var searchQueryFlow: MutableStateFlow<String>

    private val testReading1 = ReadingWithSpread(
        id = 1L,
        title = "Morning Reading",
        spreadId = 1L,
        spreadName = "Three Card",
        createdAt = Instant.now().toEpochMilli(),
        notes = "Test notes"
    )

    private val testReading2 = ReadingWithSpread(
        id = 2L,
        title = "Evening Reading",
        spreadId = 2L,
        spreadName = "Celtic Cross",
        createdAt = Instant.now().toEpochMilli(),
        notes = null
    )

    private val isProgressBar = SemanticsMatcher("isProgressBar") {
        it.config.contains(SemanticsProperties.ProgressBarRangeInfo)
    }

    @Before
    fun setUp() {
        viewModel = mockk(relaxed = true)
        uiStateFlow = MutableStateFlow(ReadingListUiState.Loading)
        searchQueryFlow = MutableStateFlow("")
        every { viewModel.uiState } returns uiStateFlow
        every { viewModel.searchQuery } returns searchQueryFlow
    }

    @Test
    fun loadingState_showsLoadingIndicator() {
        composeTestRule.setContent {
            DrawnTheme {
                ReadingListScreen(
                    viewModel = viewModel,
                    onAddReading = {},
                    onReadingSelected = {}
                )
            }
        }
        composeTestRule.waitForIdle()
        composeTestRule.onNode(isProgressBar).assertIsDisplayed()
    }

    @Test
    fun successState_withReadings_displaysReadingList() {
        uiStateFlow.value = ReadingListUiState.Success(listOf(testReading1, testReading2))
        composeTestRule.setContent {
            DrawnTheme {
                ReadingListScreen(
                    viewModel = viewModel,
                    onAddReading = {},
                    onReadingSelected = {}
                )
            }
        }
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithText("Morning Reading").assertIsDisplayed()
        composeTestRule.onNodeWithText("Test notes").assertIsDisplayed()
        composeTestRule.onNodeWithText("Evening Reading").assertIsDisplayed()
    }

    @Test
    fun successState_emptyList_showsEmptyState() {
        uiStateFlow.value = ReadingListUiState.Success(emptyList())
        composeTestRule.setContent {
            DrawnTheme {
                ReadingListScreen(
                    viewModel = viewModel,
                    onAddReading = {},
                    onReadingSelected = {}
                )
            }
        }
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithText("No readings yet").assertIsDisplayed()
        composeTestRule.onNodeWithText("Tap + to record your first reading").assertIsDisplayed()
    }

    @Test
    fun successState_emptyListWithSearch_showsNoMatchMessage() {
        searchQueryFlow.value = "nonexistent"
        uiStateFlow.value = ReadingListUiState.Success(emptyList())
        composeTestRule.setContent {
            DrawnTheme {
                ReadingListScreen(
                    viewModel = viewModel,
                    onAddReading = {},
                    onReadingSelected = {}
                )
            }
        }
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithText("No readings match your search").assertIsDisplayed()
    }

    @Test
    fun errorState_showsErrorMessage() {
        uiStateFlow.value = ReadingListUiState.Error("Network error")
        composeTestRule.setContent {
            DrawnTheme {
                ReadingListScreen(
                    viewModel = viewModel,
                    onAddReading = {},
                    onReadingSelected = {}
                )
            }
        }
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithText("Couldn't load readings").assertIsDisplayed()
        composeTestRule.onNodeWithText("Network error").assertIsDisplayed()
    }

    @Test
    fun fabClick_triggersOnAddReading() {
        var addReadingClicked = false
        composeTestRule.setContent {
            DrawnTheme {
                ReadingListScreen(
                    viewModel = viewModel,
                    onAddReading = { addReadingClicked = true },
                    onReadingSelected = {}
                )
            }
        }
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithContentDescription("Add reading").performClick()
        assert(addReadingClicked)
    }

    @Test
    fun readingItemClick_triggersOnReadingSelected() {
        uiStateFlow.value = ReadingListUiState.Success(listOf(testReading1))
        var selectedId: Long? = null
        composeTestRule.setContent {
            DrawnTheme {
                ReadingListScreen(
                    viewModel = viewModel,
                    onAddReading = {},
                    onReadingSelected = { selectedId = it }
                )
            }
        }
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithText("Morning Reading").performClick()
        assert(selectedId == 1L)
    }

    @Test
    fun searchQuery_hidesFabAndShowsClearButton() {
        uiStateFlow.value = ReadingListUiState.Success(listOf(testReading1))
        composeTestRule.setContent {
            DrawnTheme {
                ReadingListScreen(
                    viewModel = viewModel,
                    onAddReading = {},
                    onReadingSelected = {}
                )
            }
        }
        composeTestRule.waitForIdle()
        // FAB visible when not searching
        composeTestRule.onNodeWithContentDescription("Add reading").assertIsDisplayed()

        // Simulate search query
        searchQueryFlow.value = "test"
        composeTestRule.waitForIdle()

        // FAB hidden, clear button visible
        composeTestRule.onAllNodesWithContentDescription("Add reading").assertCountEquals(0)
        composeTestRule.onNodeWithContentDescription("Clear search").assertIsDisplayed()
    }

    @Test
    fun clearSearchButton_callsOnSearchQueryChange() {
        searchQueryFlow.value = "test"
        uiStateFlow.value = ReadingListUiState.Success(listOf(testReading1))
        composeTestRule.setContent {
            DrawnTheme {
                ReadingListScreen(
                    viewModel = viewModel,
                    onAddReading = {},
                    onReadingSelected = {}
                )
            }
        }
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithContentDescription("Clear search").performClick()
        verify { viewModel.onSearchQueryChange("") }
    }

    @Test
    fun searchFiltering_updatesDisplayedReadings() {
        // Both readings displayed initially
        uiStateFlow.value = ReadingListUiState.Success(listOf(testReading1, testReading2))
        composeTestRule.setContent {
            DrawnTheme {
                ReadingListScreen(
                    viewModel = viewModel,
                    onAddReading = {},
                    onReadingSelected = {}
                )
            }
        }
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithText("Morning Reading").assertIsDisplayed()
        composeTestRule.onNodeWithText("Evening Reading").assertIsDisplayed()

        // Simulate filtered results (only one reading matches)
        uiStateFlow.value = ReadingListUiState.Success(listOf(testReading1))
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithText("Morning Reading").assertIsDisplayed()
        composeTestRule.onAllNodesWithText("Evening Reading").assertCountEquals(0)
    }
}
