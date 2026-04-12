package com.example.drawn.ui.addreading

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.drawn.domain.model.ArcanaType
import com.example.drawn.domain.model.Card

/**
 * Bottom sheet modal showing all 78 cards organized by arcana type and suit.
 *
 * Sections (per D-18, D-19):
 * 1. Major Arcana — sorted by number
 * 2. Wands — sorted by number
 * 3. Cups — sorted by number
 * 4. Swords — sorted by number
 * 5. Pentacles — sorted by number
 *
 * Grid: 3 columns, 8.dp horizontal spacing, 12.dp vertical spacing.
 * Tapping a card calls onCardSelected and dismisses the sheet.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CardPickerBottomSheet(
    onCardSelected: (Card) -> Unit,
    onDismiss: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = false)

    // Access cards via hiltViewModel from the parent scope
    val viewModel: AddReadingViewModel = hiltViewModel()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    val cards = when (val state = uiState) {
        is AddReadingUiState.Ready -> state.state.cards
        else -> emptyList()
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
        ) {
            // Header
            Text(
                text = "Select a Card",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(vertical = 16.dp)
            )

            // Sectioned card grid
            CardSectionedGrid(
                cards = cards,
                onCardSelected = onCardSelected
            )
        }
    }
}

@Composable
private fun CardSectionedGrid(
    cards: List<Card>,
    onCardSelected: (Card) -> Unit
) {
    val sections = buildCardSections(cards)

    LazyVerticalGrid(
        columns = GridCells.Fixed(3),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = PaddingValues(bottom = 32.dp)
    ) {
        for (section in sections) {
            // Section header spanning all columns
            item(span = { GridItemSpan(maxLineSpan) }) {
                Text(
                    text = section.title,
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp)
                )
            }
            items(section.cards, key = { it.id }) { card ->
                CardThumbnail(
                    card = card,
                    modifier = Modifier.clickable { onCardSelected(card) }
                )
            }
        }
    }
}

private data class CardSection(
    val title: String,
    val cards: List<Card>
)

/**
 * Build the 5 sections: Major Arcana, Wands, Cups, Swords, Pentacles.
 * Within each section, cards are sorted by number.
 */
private fun buildCardSections(cards: List<Card>): List<CardSection> {
    val sections = mutableListOf<CardSection>()

    // Major Arcana
    val majorArcana = cards
        .filter { it.arcanaType == ArcanaType.MAJOR }
        .sortedBy { it.number }
    if (majorArcana.isNotEmpty()) {
        sections.add(CardSection("Major Arcana", majorArcana))
    }

    // Minor Arcana suits
    val suits = listOf("Wands", "Cups", "Swords", "Pentacles")
    for (suit in suits) {
        val suitCards = cards
            .filter { it.arcanaType == ArcanaType.MINOR && it.name.contains(suit, ignoreCase = true) }
            .sortedBy { it.number }
        if (suitCards.isNotEmpty()) {
            sections.add(CardSection(suit, suitCards))
        }
    }

    return sections
}
