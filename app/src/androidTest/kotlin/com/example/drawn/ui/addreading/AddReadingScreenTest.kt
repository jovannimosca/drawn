package com.example.drawn.ui.addreading

import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.semantics.ProgressBarRangeInfo
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.drawn.domain.model.ArcanaType
import com.example.drawn.domain.model.Card
import com.example.drawn.domain.model.Spread
import com.example.drawn.domain.model.SpreadPosition
import com.example.drawn.ui.theme.DrawnTheme
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class AddReadingScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private lateinit var viewModel: AddReadingViewModel
    private lateinit var uiStateFlow: MutableStateFlow<AddReadingUiState>

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

    private val isProgressBar = SemanticsMatcher("isProgressBar") {
        it.config.contains(SemanticsProperties.ProgressBarRangeInfo)
    }

    @Before
    fun setUp() {
        viewModel = mockk(relaxed = true)
        uiStateFlow = MutableStateFlow(AddReadingUiState.Loading)
        every { viewModel.uiState } returns uiStateFlow
    }

    @Test
    fun loadingState_showsLoadingIndicator() {
        composeTestRule.setContent {
            DrawnTheme {
                AddReadingScreen(
                    viewModel = viewModel,
                    onNavigateBack = {}
                )
            }
        }
        composeTestRule.waitForIdle()
        composeTestRule.onNode(isProgressBar).assertIsDisplayed()
    }

    @Test
    fun readyState_spreadPickerStep_displaysSpreadOptions() {
        val readyState = AddReadingState(
            currentStep = AddReadingStep.SpreadPicker,
            spreads = listOf(testSpread),
            selectedSpread = null
        )
        uiStateFlow.value = AddReadingUiState.Ready(readyState)
        composeTestRule.setContent {
            DrawnTheme {
                AddReadingScreen(
                    viewModel = viewModel,
                    onNavigateBack = {}
                )
            }
        }
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithText("Three Card Spread").assertIsDisplayed()
        composeTestRule.onNodeWithText("Past, Present, Future").assertIsDisplayed()
        composeTestRule.onNodeWithText("3 positions").assertIsDisplayed()
    }

    @Test
    fun readyState_selectingSpread_triggersViewModelSelectSpread() {
        every { viewModel.selectSpread(any()) } answers { }

        val readyState = AddReadingState(
            currentStep = AddReadingStep.SpreadPicker,
            spreads = listOf(testSpread),
            selectedSpread = null
        )
        uiStateFlow.value = AddReadingUiState.Ready(readyState)
        composeTestRule.setContent {
            DrawnTheme {
                AddReadingScreen(
                    viewModel = viewModel,
                    onNavigateBack = {}
                )
            }
        }
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithText("Three Card Spread").performClick()
        io.mockk.verify { viewModel.selectSpread(testSpread) }
    }

    @Test
    fun readyState_cardAssignmentStep_displaysPositionSlots() {
        val readyState = AddReadingState(
            currentStep = AddReadingStep.CardAssignment,
            spreads = listOf(testSpread),
            selectedSpread = testSpread,
            assignedCards = emptyMap()
        )
        uiStateFlow.value = AddReadingUiState.Ready(readyState)
        composeTestRule.setContent {
            DrawnTheme {
                AddReadingScreen(
                    viewModel = viewModel,
                    onNavigateBack = {}
                )
            }
        }
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithText("0 of 3 positions assigned").assertIsDisplayed()
        composeTestRule.onNodeWithText("Three Card Spread").assertIsDisplayed()
        composeTestRule.onNodeWithText("Past").assertIsDisplayed()
        composeTestRule.onNodeWithText("Present").assertIsDisplayed()
        composeTestRule.onNodeWithText("Future").assertIsDisplayed()
    }

    @Test
    fun readyState_notesAndSaveStep_displaysNotesFieldAndSaveButton() {
        val readyState = AddReadingState(
            currentStep = AddReadingStep.NotesAndSave,
            spreads = listOf(testSpread),
            selectedSpread = testSpread,
            title = "Test Reading",
            notes = "Test notes",
            assignedCards = mapOf(0 to testCard, 1 to testCard, 2 to testCard)
        )
        uiStateFlow.value = AddReadingUiState.Ready(readyState)
        composeTestRule.setContent {
            DrawnTheme {
                AddReadingScreen(
                    viewModel = viewModel,
                    onNavigateBack = {}
                )
            }
        }
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithText("Save Reading").assertIsDisplayed()
    }

    @Test
    fun savingState_showsSavingIndicator() {
        uiStateFlow.value = AddReadingUiState.Saving
        composeTestRule.setContent {
            DrawnTheme {
                AddReadingScreen(
                    viewModel = viewModel,
                    onNavigateBack = {}
                )
            }
        }
        composeTestRule.waitForIdle()
        composeTestRule.onNode(isProgressBar).assertIsDisplayed()
        composeTestRule.onNodeWithText("Saving...").assertIsDisplayed()
    }

    @Test
    fun savedState_triggersNavigationBack() {
        var navigatedBack = false
        uiStateFlow.value = AddReadingUiState.Saved(readingId = 1L)
        composeTestRule.setContent {
            DrawnTheme {
                AddReadingScreen(
                    viewModel = viewModel,
                    onNavigateBack = { navigatedBack = true }
                )
            }
        }
        composeTestRule.waitForIdle()
        assert(navigatedBack)
    }

    @Test
    fun errorState_showsErrorMessageWithRetry() {
        uiStateFlow.value = AddReadingUiState.Error("Failed to save reading")
        composeTestRule.setContent {
            DrawnTheme {
                AddReadingScreen(
                    viewModel = viewModel,
                    onNavigateBack = {}
                )
            }
        }
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithText("Something went wrong").assertIsDisplayed()
        composeTestRule.onNodeWithText("Failed to save reading").assertIsDisplayed()
        composeTestRule.onNodeWithText("Retry").assertIsDisplayed()
    }

    @Test
    fun errorState_retryButton_callsViewModelRetry() {
        uiStateFlow.value = AddReadingUiState.Error("Failed to save reading")
        composeTestRule.setContent {
            DrawnTheme {
                AddReadingScreen(
                    viewModel = viewModel,
                    onNavigateBack = {}
                )
            }
        }
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithText("Retry").performClick()
        io.mockk.verify { viewModel.retry() }
    }

    @Test
    fun wizardStepIndicator_showsStepLabels() {
        val readyState = AddReadingState(
            currentStep = AddReadingStep.SpreadPicker,
            spreads = listOf(testSpread),
            selectedSpread = null
        )
        uiStateFlow.value = AddReadingUiState.Ready(readyState)
        composeTestRule.setContent {
            DrawnTheme {
                AddReadingScreen(
                    viewModel = viewModel,
                    onNavigateBack = {}
                )
            }
        }
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithText("Spread").assertIsDisplayed()
        composeTestRule.onNodeWithText("Cards").assertIsDisplayed()
        composeTestRule.onNodeWithText("Notes").assertIsDisplayed()
    }

    @Test
    fun wizardBottomBar_backButtonHiddenOnFirstStep() {
        val readyState = AddReadingState(
            currentStep = AddReadingStep.SpreadPicker,
            spreads = listOf(testSpread),
            selectedSpread = null
        )
        uiStateFlow.value = AddReadingUiState.Ready(readyState)
        composeTestRule.setContent {
            DrawnTheme {
                AddReadingScreen(
                    viewModel = viewModel,
                    onNavigateBack = {}
                )
            }
        }
        composeTestRule.waitForIdle()
        composeTestRule.onAllNodesWithText("Back").assertCountEquals(0)
    }

    @Test
    fun wizardBottomBar_backButtonVisibleOnLaterSteps() {
        val readyState = AddReadingState(
            currentStep = AddReadingStep.CardAssignment,
            spreads = listOf(testSpread),
            selectedSpread = testSpread,
            assignedCards = emptyMap()
        )
        uiStateFlow.value = AddReadingUiState.Ready(readyState)
        composeTestRule.setContent {
            DrawnTheme {
                AddReadingScreen(
                    viewModel = viewModel,
                    onNavigateBack = {}
                )
            }
        }
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithText("Back").assertIsDisplayed()
    }

    @Test
    fun backNavigation_callsOnNavigateBack() {
        var navigatedBack = false
        val readyState = AddReadingState(
            currentStep = AddReadingStep.CardAssignment,
            spreads = listOf(testSpread),
            selectedSpread = testSpread,
            assignedCards = emptyMap()
        )
        uiStateFlow.value = AddReadingUiState.Ready(readyState)
        composeTestRule.setContent {
            DrawnTheme {
                AddReadingScreen(
                    viewModel = viewModel,
                    onNavigateBack = { navigatedBack = true }
                )
            }
        }
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithContentDescription("Back").performClick()
        assert(navigatedBack)
    }
}
