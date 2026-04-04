package com.example.drawn.ui.readingdetail

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
import com.example.drawn.domain.model.ArcanaType
import com.example.drawn.domain.model.Card
import com.example.drawn.domain.model.Reading
import com.example.drawn.domain.model.ReadingCard
import com.example.drawn.domain.model.ReadingCardWithDetails
import com.example.drawn.domain.model.ReadingDetail
import com.example.drawn.domain.model.ReadingPhoto
import com.example.drawn.ui.theme.DrawnTheme
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.Rule
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.time.Instant

class ReadingDetailScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private lateinit var viewModel: ReadingDetailViewModel
    private lateinit var uiStateFlow: MutableStateFlow<ReadingDetailUiState>
    private lateinit var editModeFlow: MutableStateFlow<Boolean>

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

    private val testReading = Reading(
        id = 1L,
        title = "Test Reading",
        spreadId = 1L,
        createdAt = Instant.now(),
        notes = "Test notes"
    )

    private val testDetail = ReadingDetail(
        reading = testReading,
        spreadName = "Three Card Spread",
        cards = listOf(ReadingCardWithDetails(testReadingCard, testCard)),
        photos = emptyList()
    )

    private val testPhoto = ReadingPhoto(
        id = 1L,
        readingId = 1L,
        photoUri = "content://test/photo.jpg"
    )

    private val isProgressBar = SemanticsMatcher("isProgressBar") {
        it.config.contains(SemanticsProperties.ProgressBarRangeInfo)
    }

    @BeforeEach
    fun setUp() {
        viewModel = mockk(relaxed = true)
        uiStateFlow = MutableStateFlow(ReadingDetailUiState.Loading)
        editModeFlow = MutableStateFlow(false)
        every { viewModel.uiState } returns uiStateFlow
        every { viewModel.isEditMode } returns editModeFlow
    }

    @Test
    fun loadingState_showsLoadingIndicator() {
        composeTestRule.setContent {
            DrawnTheme {
                ReadingDetailScreen(
                    readingId = 1L,
                    viewModel = viewModel,
                    onNavigateBack = {}
                )
            }
        }
        composeTestRule.waitForIdle()
        composeTestRule.onNode(isProgressBar).assertIsDisplayed()
    }

    @Test
    fun successState_displaysReadingTitleAndSpreadName() {
        uiStateFlow.value = ReadingDetailUiState.Success(testDetail)
        composeTestRule.setContent {
            DrawnTheme {
                ReadingDetailScreen(
                    readingId = 1L,
                    viewModel = viewModel,
                    onNavigateBack = {}
                )
            }
        }
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithText("Test Reading").assertIsDisplayed()
        composeTestRule.onNodeWithText("Three Card Spread").assertIsDisplayed()
    }

    @Test
    fun successState_displaysCardPositionAndName() {
        uiStateFlow.value = ReadingDetailUiState.Success(testDetail)
        composeTestRule.setContent {
            DrawnTheme {
                ReadingDetailScreen(
                    readingId = 1L,
                    viewModel = viewModel,
                    onNavigateBack = {}
                )
            }
        }
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithText("Past").assertIsDisplayed()
        composeTestRule.onNodeWithText("The Fool").assertIsDisplayed()
    }

    @Test
    fun successState_displaysNotesSection() {
        uiStateFlow.value = ReadingDetailUiState.Success(testDetail)
        composeTestRule.setContent {
            DrawnTheme {
                ReadingDetailScreen(
                    readingId = 1L,
                    viewModel = viewModel,
                    onNavigateBack = {}
                )
            }
        }
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithText("Notes").assertIsDisplayed()
        composeTestRule.onNodeWithText("No notes").assertIsDisplayed()
    }

    @Test
    fun errorState_showsErrorMessage() {
        uiStateFlow.value = ReadingDetailUiState.Error("Reading not found")
        composeTestRule.setContent {
            DrawnTheme {
                ReadingDetailScreen(
                    readingId = 1L,
                    viewModel = viewModel,
                    onNavigateBack = {}
                )
            }
        }
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithText("Something went wrong").assertIsDisplayed()
        composeTestRule.onNodeWithText("Reading not found").assertIsDisplayed()
    }

    @Test
    fun editMode_tappingEditButton_entersEditMode() {
        uiStateFlow.value = ReadingDetailUiState.Success(testDetail)
        composeTestRule.setContent {
            DrawnTheme {
                ReadingDetailScreen(
                    readingId = 1L,
                    viewModel = viewModel,
                    onNavigateBack = {}
                )
            }
        }
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithContentDescription("Edit reading").performClick()
        io.mockk.verify { viewModel.toggleEditMode() }
    }

    @Test
    fun editMode_showsEditableTitleAndNotesFields() {
        uiStateFlow.value = ReadingDetailUiState.Success(testDetail)
        editModeFlow.value = true
        composeTestRule.setContent {
            DrawnTheme {
                ReadingDetailScreen(
                    readingId = 1L,
                    viewModel = viewModel,
                    onNavigateBack = {}
                )
            }
        }
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithText("Edit Reading").assertIsDisplayed()
    }

    @Test
    fun editMode_saveButton_exitsEditMode() {
        uiStateFlow.value = ReadingDetailUiState.Success(testDetail)
        editModeFlow.value = true
        composeTestRule.setContent {
            DrawnTheme {
                ReadingDetailScreen(
                    readingId = 1L,
                    viewModel = viewModel,
                    onNavigateBack = {}
                )
            }
        }
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithContentDescription("Save Changes").performClick()
        io.mockk.verify { viewModel.saveReading(any(), any()) }
    }

    @Test
    fun editMode_discardButton_exitsEditMode() {
        uiStateFlow.value = ReadingDetailUiState.Success(testDetail)
        editModeFlow.value = true
        composeTestRule.setContent {
            DrawnTheme {
                ReadingDetailScreen(
                    readingId = 1L,
                    viewModel = viewModel,
                    onNavigateBack = {}
                )
            }
        }
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithContentDescription("Discard Changes").performClick()
        io.mockk.verify { viewModel.exitEditMode() }
    }

    @Test
    fun deleteButton_showsConfirmationDialog() {
        uiStateFlow.value = ReadingDetailUiState.Success(testDetail)
        composeTestRule.setContent {
            DrawnTheme {
                ReadingDetailScreen(
                    readingId = 1L,
                    viewModel = viewModel,
                    onNavigateBack = {}
                )
            }
        }
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithContentDescription("Delete reading").performClick()
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithText("Delete reading?").assertIsDisplayed()
        composeTestRule.onNodeWithText("Keep Reading").assertIsDisplayed()
        composeTestRule.onNodeWithText("Delete").assertIsDisplayed()
    }

    @Test
    fun deleteConfirmation_confirmDeletesReading() {
        uiStateFlow.value = ReadingDetailUiState.Success(testDetail)
        var navigatedBack = false
        composeTestRule.setContent {
            DrawnTheme {
                ReadingDetailScreen(
                    readingId = 1L,
                    viewModel = viewModel,
                    onNavigateBack = { navigatedBack = true }
                )
            }
        }
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithContentDescription("Delete reading").performClick()
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithText("Delete").performClick()
        io.mockk.verify { viewModel.deleteReading() }
        assert(navigatedBack)
    }

    @Test
    fun deleteConfirmation_cancelDismissesDialog() {
        uiStateFlow.value = ReadingDetailUiState.Success(testDetail)
        composeTestRule.setContent {
            DrawnTheme {
                ReadingDetailScreen(
                    readingId = 1L,
                    viewModel = viewModel,
                    onNavigateBack = {}
                )
            }
        }
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithContentDescription("Delete reading").performClick()
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithText("Keep Reading").performClick()
        composeTestRule.waitForIdle()
        composeTestRule.onAllNodesWithText("Delete").assertCountEquals(0)
    }

    @Test
    fun successState_withPhotos_displaysPhotoGrid() {
        val detailWithPhotos = testDetail.copy(photos = listOf(testPhoto))
        uiStateFlow.value = ReadingDetailUiState.Success(detailWithPhotos)
        composeTestRule.setContent {
            DrawnTheme {
                ReadingDetailScreen(
                    readingId = 1L,
                    viewModel = viewModel,
                    onNavigateBack = {}
                )
            }
        }
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithText("Photos").assertIsDisplayed()
        composeTestRule.onNodeWithContentDescription("Reading photo").assertIsDisplayed()
    }

    @Test
    fun successState_noPhotos_showsNoPhotosMessage() {
        uiStateFlow.value = ReadingDetailUiState.Success(testDetail)
        composeTestRule.setContent {
            DrawnTheme {
                ReadingDetailScreen(
                    readingId = 1L,
                    viewModel = viewModel,
                    onNavigateBack = {}
                )
            }
        }
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithText("Photos").assertIsDisplayed()
        composeTestRule.onNodeWithText("No photos attached").assertIsDisplayed()
    }

    @Test
    fun editMode_withPhotos_showsAddPhotoButton() {
        uiStateFlow.value = ReadingDetailUiState.Success(testDetail)
        editModeFlow.value = true
        composeTestRule.setContent {
            DrawnTheme {
                ReadingDetailScreen(
                    readingId = 1L,
                    viewModel = viewModel,
                    onNavigateBack = {}
                )
            }
        }
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithContentDescription("Add photo").assertIsDisplayed()
    }

    @Test
    fun backNavigation_callsOnNavigateBack() {
        uiStateFlow.value = ReadingDetailUiState.Success(testDetail)
        var navigatedBack = false
        composeTestRule.setContent {
            DrawnTheme {
                ReadingDetailScreen(
                    readingId = 1L,
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
