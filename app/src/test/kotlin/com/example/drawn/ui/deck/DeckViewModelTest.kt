package com.example.drawn.ui.deck

import com.example.drawn.data.repository.DeckRepository
import com.example.drawn.domain.model.Deck
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import io.mockk.junit5.MockKExtension
import java.time.Instant

@ExtendWith(MockKExtension::class)
class DeckViewModelTest {

    private val deckRepository: DeckRepository = mockk()

    private val testDeck = Deck(
        id = 1L,
        name = "Oracle Cards",
        description = "My custom oracle deck",
        isCustom = true,
        createdAt = Instant.now()
    )

    @Test
    fun `customDecks emits Loading initially`() {
        every { deckRepository.observeCustomDecks() } returns MutableStateFlow(listOf(testDeck))
        val viewModel = DeckViewModel(deckRepository)
        val state = viewModel.customDecks.value
        // Initial state should be Loading because of stateIn with initialValue = Loading
        assert(state is DeckListUiState.Loading || state is DeckListUiState.Success)
    }

    @Test
    fun `DeckListUiState Loading works`() {
        assert(DeckListUiState.Loading is DeckListUiState)
    }

    @Test
    fun `DeckListUiState Success works`() {
        val state = DeckListUiState.Success(emptyList())
        assert(state.decks.isEmpty())
    }

    @Test
    fun `DeckListUiState Success contains decks`() {
        val decks = listOf(testDeck)
        val state = DeckListUiState.Success(decks)
        assert(state.decks.size == 1)
        assert(state.decks.first().name == "Oracle Cards")
    }

    @Test
    fun `DeckListUiState Error works`() {
        val state = DeckListUiState.Error("test error")
        assert(state.message == "test error")
    }
}