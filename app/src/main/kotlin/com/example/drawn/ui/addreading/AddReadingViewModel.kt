package com.example.drawn.ui.addreading

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.drawn.data.repository.CardRepository
import com.example.drawn.data.repository.ReadingRepository
import com.example.drawn.data.repository.SpreadRepository
import com.example.drawn.domain.model.Card
import com.example.drawn.domain.model.Reading
import com.example.drawn.domain.model.ReadingCard
import com.example.drawn.domain.model.Spread
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import java.time.Instant
import javax.inject.Inject

@HiltViewModel
class AddReadingViewModel @Inject constructor(
    private val spreadRepository: SpreadRepository,
    private val cardRepository: CardRepository,
    private val readingRepository: ReadingRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<AddReadingUiState>(AddReadingUiState.Loading)
    val uiState: StateFlow<AddReadingUiState> = _uiState.asStateFlow()

    private var spreads: List<Spread> = emptyList()
    private var cards: List<Card> = emptyList()

    init {
        viewModelScope.launch {
            combine(
                spreadRepository.observeAllSpreads(),
                cardRepository.observeAllCards()
            ) { spreads, cards ->
                this@AddReadingViewModel.spreads = spreads
                this@AddReadingViewModel.cards = cards
                AddReadingUiState.Ready(
                    AddReadingState(
                        spreads = spreads,
                        cards = cards
                    )
                )
            }.collect { state ->
                if (_uiState.value is AddReadingUiState.Loading) {
                    _uiState.value = state
                }
            }
        }
    }

    fun selectSpread(spread: Spread) {
        viewModelScope.launch {
            val currentState = (_uiState.value as? AddReadingUiState.Ready)?.state ?: return@launch
            val updatedState = currentState.copy(
                selectedSpread = spread,
                assignedCards = emptyMap(),
                currentStep = AddReadingStep.CardAssignment,
                title = generateTitle(spread.name)
            )
            _uiState.value = AddReadingUiState.Ready(updatedState)
        }
    }

    fun assignCard(positionOrder: Int, card: Card) {
        viewModelScope.launch {
            val currentState = (_uiState.value as? AddReadingUiState.Ready)?.state ?: return@launch
            val updatedAssigned = currentState.assignedCards + (positionOrder to card)
            val updatedState = currentState.copy(assignedCards = updatedAssigned)
            _uiState.value = AddReadingUiState.Ready(updatedState)
        }
    }

    fun unassignCard(positionOrder: Int) {
        viewModelScope.launch {
            val currentState = (_uiState.value as? AddReadingUiState.Ready)?.state ?: return@launch
            val updatedAssigned = currentState.assignedCards - positionOrder
            val updatedState = currentState.copy(assignedCards = updatedAssigned)
            _uiState.value = AddReadingUiState.Ready(updatedState)
        }
    }

    fun togglePositionReversed(positionOrder: Int) {
        viewModelScope.launch {
            val currentState = (_uiState.value as? AddReadingUiState.Ready)?.state ?: return@launch
            val updatedReversed = if (currentState.reversedPositions.contains(positionOrder)) {
                currentState.reversedPositions - positionOrder
            } else {
                currentState.reversedPositions + positionOrder
            }
            val updatedState = currentState.copy(reversedPositions = updatedReversed)
            _uiState.value = AddReadingUiState.Ready(updatedState)
        }
    }

    fun goToNextStep() {
        viewModelScope.launch {
            val currentState = (_uiState.value as? AddReadingUiState.Ready)?.state ?: return@launch
            if (!currentState.canProceedToNext()) return@launch
            val nextStep = when (currentState.currentStep) {
                AddReadingStep.SpreadPicker -> AddReadingStep.CardAssignment
                AddReadingStep.CardAssignment -> AddReadingStep.NotesAndSave
                AddReadingStep.NotesAndSave -> return@launch
            }
            val updatedState = currentState.copy(currentStep = nextStep)
            _uiState.value = AddReadingUiState.Ready(updatedState)
        }
    }

    fun goToPreviousStep() {
        viewModelScope.launch {
            val currentState = (_uiState.value as? AddReadingUiState.Ready)?.state ?: return@launch
            val previousStep = when (currentState.currentStep) {
                AddReadingStep.SpreadPicker -> return@launch
                AddReadingStep.CardAssignment -> AddReadingStep.SpreadPicker
                AddReadingStep.NotesAndSave -> AddReadingStep.CardAssignment
            }
            val updatedState = currentState.copy(currentStep = previousStep)
            _uiState.value = AddReadingUiState.Ready(updatedState)
        }
    }

    fun updateTitle(title: String) {
        viewModelScope.launch {
            val currentState = (_uiState.value as? AddReadingUiState.Ready)?.state ?: return@launch
            val updatedState = currentState.copy(title = title)
            _uiState.value = AddReadingUiState.Ready(updatedState)
        }
    }

    fun updateNotes(notes: String) {
        viewModelScope.launch {
            val currentState = (_uiState.value as? AddReadingUiState.Ready)?.state ?: return@launch
            val updatedState = currentState.copy(notes = notes)
            _uiState.value = AddReadingUiState.Ready(updatedState)
        }
    }

    fun addPhoto(uri: String) {
        viewModelScope.launch {
            val currentState = (_uiState.value as? AddReadingUiState.Ready)?.state ?: return@launch
            val updatedPhotos = currentState.photoUris + uri
            val updatedState = currentState.copy(photoUris = updatedPhotos)
            _uiState.value = AddReadingUiState.Ready(updatedState)
        }
    }

    fun removePhoto(uri: String) {
        viewModelScope.launch {
            val currentState = (_uiState.value as? AddReadingUiState.Ready)?.state ?: return@launch
            val updatedPhotos = currentState.photoUris - uri
            val updatedState = currentState.copy(photoUris = updatedPhotos)
            _uiState.value = AddReadingUiState.Ready(updatedState)
        }
    }

    fun saveReading() {
        viewModelScope.launch {
            val currentState = (_uiState.value as? AddReadingUiState.Ready)?.state ?: return@launch
            val spread = currentState.selectedSpread ?: return@launch

            _uiState.value = AddReadingUiState.Saving

            try {
                val reading = Reading(
                    id = 0,
                    title = currentState.title,
                    spreadId = spread.id,
                    createdAt = Instant.now(),
                    notes = currentState.notes.ifBlank { null }
                )

                val readingCards = currentState.assignedCards.map { (positionOrder, card) ->
                    val position = spread.positions.find { it.order == positionOrder }
                    ReadingCard(
                        id = 0,
                        readingId = 0,
                        cardId = card.id,
                        positionName = position?.name ?: "Position $positionOrder",
                        positionOrder = positionOrder,
                        interpretation = null,
                        isReversed = currentState.reversedPositions.contains(positionOrder)
                    )
                }

                val readingId = readingRepository.createReading(reading, readingCards)
                
                currentState.photoUris.forEach { photoUri ->
                    readingRepository.addPhotoToReading(readingId, photoUri)
                }
                
                _uiState.value = AddReadingUiState.Saved(readingId)
            } catch (e: Exception) {
                _uiState.value = AddReadingUiState.Error(e.message ?: "Failed to save reading")
            }
        }
    }

    fun retry() {
        viewModelScope.launch {
            if (_uiState.value is AddReadingUiState.Error) {
                resetToReady()
            }
        }
    }

    fun reset() {
        resetToReady()
    }

    private fun resetToReady() {
        if (spreads.isNotEmpty() && cards.isNotEmpty()) {
            _uiState.value = AddReadingUiState.Ready(
                AddReadingState(
                    spreads = spreads,
                    cards = cards
                )
            )
        } else {
            _uiState.value = AddReadingUiState.Loading
        }
    }
}
