package com.example.drawn.ui.deck

import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.drawn.domain.model.Deck
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
class DeckListScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private lateinit var viewModel: DeckViewModel
    private lateinit var uiStateFlow: MutableStateFlow<DeckListUiState>

    private val testDeck = Deck(
        id = 1L,
        name = "Oracle Cards",
        description = "My custom oracle deck",
        isCustom = true,
        createdAt = Instant.now()
    )

    private val testDeck2 = Deck(
        id = 2L,
        name = "Angel Cards",
        description = null,
        isCustom = true,
        createdAt = Instant.now()
    )

    @Before
    fun setUp() {
        viewModel = mockk(relaxed = true)
        uiStateFlow = MutableStateFlow(DeckListUiState.Loading)
        every { viewModel.customDecks } returns uiStateFlow
    }

    @Test
    fun loadingState_showsLoadingText() {
        composeTestRule.setContent {
            DrawnTheme {
                DeckListScreen(
                    viewModel = viewModel,
                    onNavigateToDetail = {}
                )
            }
        }
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithText("Loading...").assertIsDisplayed()
    }

    @Test
    fun successState_withDecks_displaysDeckList() {
        uiStateFlow.value = DeckListUiState.Success(listOf(testDeck, testDeck2))
        composeTestRule.setContent {
            DrawnTheme {
                DeckListScreen(
                    viewModel = viewModel,
                    onNavigateToDetail = {}
                )
            }
        }
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithText("Oracle Cards").assertIsDisplayed()
        composeTestRule.onNodeWithText("My custom oracle deck").assertIsDisplayed()
        composeTestRule.onNodeWithText("Angel Cards").assertIsDisplayed()
    }

    @Test
    fun successState_emptyList_showsEmptyState() {
        uiStateFlow.value = DeckListUiState.Success(emptyList())
        composeTestRule.setContent {
            DrawnTheme {
                DeckListScreen(
                    viewModel = viewModel,
                    onNavigateToDetail = {}
                )
            }
        }
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithText("No custom decks yet").assertIsDisplayed()
        composeTestRule.onNodeWithText("Create your first custom deck to get started").assertIsDisplayed()
    }

    @Test
    fun errorState_showsErrorMessage() {
        uiStateFlow.value = DeckListUiState.Error("Database error")
        composeTestRule.setContent {
            DrawnTheme {
                DeckListScreen(
                    viewModel = viewModel,
                    onNavigateToDetail = {}
                )
            }
        }
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithText("Database error").assertIsDisplayed()
    }

    @Test
    fun fabClick_opensCreateDialog() {
        uiStateFlow.value = DeckListUiState.Success(emptyList())
        composeTestRule.setContent {
            DrawnTheme {
                DeckListScreen(
                    viewModel = viewModel,
                    onNavigateToDetail = {}
                )
            }
        }
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithContentDescription("Create Deck").performClick()
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithText("Create").assertIsDisplayed()
        composeTestRule.onNodeWithText("Cancel").assertIsDisplayed()
    }

    @Test
    fun deckItemClick_triggersOnNavigateToDetail() {
        uiStateFlow.value = DeckListUiState.Success(listOf(testDeck))
        var selectedDeckId: Long? = null
        composeTestRule.setContent {
            DrawnTheme {
                DeckListScreen(
                    viewModel = viewModel,
                    onNavigateToDetail = { selectedDeckId = it }
                )
            }
        }
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithText("Oracle Cards").performClick()
        assert(selectedDeckId == 1L)
    }
}