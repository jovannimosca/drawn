package com.example.drawn.ui.deck

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.drawn.data.repository.CustomCardRepository
import com.example.drawn.domain.model.CustomCard
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CardManagementViewModel @Inject constructor(
    private val customCardRepository: CustomCardRepository
) : ViewModel() {

    private val _selectedDeckId = MutableStateFlow<Long?>(null)
    val selectedDeckId: StateFlow<Long?> = _selectedDeckId

    @OptIn(ExperimentalCoroutinesApi::class)
    val cards: StateFlow<CardListUiState> = _selectedDeckId
        .filterNotNull()
        .flatMapLatest { deckId ->
            customCardRepository.observeCardsForDeck(deckId)
                .map { cards -> CardListUiState.Success(cards) as CardListUiState }
                .catch { emit(CardListUiState.Error(it.message ?: "Unknown error")) }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = CardListUiState.Loading
        )

    fun selectDeck(deckId: Long) {
        _selectedDeckId.value = deckId
    }

    fun addCard(
        deckId: Long,
        name: String,
        imagePath: String? = null,
        keywords: String = "",
        uprightMeaning: String = "",
        reversedMeaning: String = ""
    ) {
        viewModelScope.launch {
            val card = CustomCard(
                deckId = deckId,
                name = name,
                imagePath = imagePath,
                keywords = keywords,
                uprightMeaning = uprightMeaning,
                reversedMeaning = reversedMeaning
            )
            customCardRepository.insert(card)
        }
    }

    fun updateCard(card: CustomCard) {
        viewModelScope.launch {
            customCardRepository.update(card)
        }
    }

    fun deleteCard(card: CustomCard) {
        viewModelScope.launch {
            customCardRepository.delete(card)
        }
    }
}

sealed interface CardListUiState {
    data object Loading : CardListUiState
    data class Success(val cards: List<CustomCard>) : CardListUiState
    data class Error(val message: String) : CardListUiState
}