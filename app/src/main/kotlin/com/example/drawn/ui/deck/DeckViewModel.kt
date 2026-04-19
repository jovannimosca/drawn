package com.example.drawn.ui.deck

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.drawn.data.repository.DeckRepository
import com.example.drawn.domain.model.Deck
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.Instant
import javax.inject.Inject

@HiltViewModel
class DeckViewModel @Inject constructor(
    private val deckRepository: DeckRepository
) : ViewModel() {

    val customDecks: StateFlow<DeckListUiState> = deckRepository.observeCustomDecks()
        .map { decks -> DeckListUiState.Success(decks) as DeckListUiState }
        .catch { emit(DeckListUiState.Error(it.message ?: "Unknown error")) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = DeckListUiState.Loading
        )

    fun createDeck(name: String, description: String?) {
        viewModelScope.launch {
            val deck = Deck(
                id = 0,
                name = name,
                description = description,
                isCustom = true,
                createdAt = Instant.now()
            )
            deckRepository.createDeck(deck)
        }
    }

    fun updateDeck(deck: Deck) {
        viewModelScope.launch {
            deckRepository.updateDeck(deck)
        }
    }

    fun deleteDeck(deckId: Long) {
        viewModelScope.launch {
            deckRepository.deleteDeck(deckId)
        }
    }
}

sealed interface DeckListUiState {
    data object Loading : DeckListUiState
    data class Success(val decks: List<Deck>) : DeckListUiState
    data class Error(val message: String) : DeckListUiState
}