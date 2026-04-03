package com.example.drawn.ui.addreading

import com.example.drawn.domain.model.Card
import com.example.drawn.domain.model.Spread
import java.time.Instant
import java.time.format.DateTimeFormatter

enum class AddReadingStep {
    SpreadPicker,
    CardAssignment,
    NotesAndSave
}

data class AddReadingState(
    val currentStep: AddReadingStep = AddReadingStep.SpreadPicker,
    val spreads: List<Spread> = emptyList(),
    val selectedSpread: Spread? = null,
    val cards: List<Card> = emptyList(),
    val assignedCards: Map<Int, Card> = emptyMap(),
    val title: String = "",
    val notes: String = "",
    val isLoading: Boolean = false,
    val error: String? = null
) {
    fun canProceedToNext(): Boolean {
        return when (currentStep) {
            AddReadingStep.SpreadPicker -> selectedSpread != null
            AddReadingStep.CardAssignment -> {
                selectedSpread?.let { spread ->
                    val requiredPositions = spread.positions.size
                    assignedCards.size == requiredPositions
                } ?: false
            }
            AddReadingStep.NotesAndSave -> true
        }
    }
}

sealed interface AddReadingUiState {
    data object Loading : AddReadingUiState
    data class Ready(val state: AddReadingState) : AddReadingUiState
    data object Saving : AddReadingUiState
    data class Saved(val readingId: Long) : AddReadingUiState
    data class Error(val message: String) : AddReadingUiState
}

fun generateTitle(spreadName: String): String {
    val formatter = DateTimeFormatter.ofPattern("MMM d, yyyy")
    val date = Instant.now().atZone(java.time.ZoneId.systemDefault()).toLocalDate()
    return "$spreadName — ${date.format(formatter)}"
}
