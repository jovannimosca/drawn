package com.example.drawn.ui.addreading

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.drawn.domain.model.Card
import com.example.drawn.domain.model.Spread
import com.example.drawn.ui.theme.DarkSurfaceVariant

/**
 * Step 2 of the AddReading wizard — assign cards to spread positions.
 *
 * Shows all positions for the selected spread as tappable slots.
 * Tapping a slot opens the card picker bottom sheet.
 */
@Composable
fun CardAssignmentStep(
    spread: Spread,
    assignedCards: Map<Int, Card>,
    onPositionCardSelected: (positionOrder: Int, Card) -> Unit,
    reversedPositions: Set<Int> = emptySet(),
    onToggleReversed: ((Int) -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    var showCardPicker by remember { mutableStateOf(false) }
    var selectedPositionOrder by remember { mutableStateOf<Int?>(null) }

    val positions = spread.positions.sortedBy { it.order }
    val assignedCount = assignedCards.size
    val totalCount = positions.size

    androidx.compose.foundation.layout.Column(
        modifier = modifier.padding(horizontal = 24.dp, vertical = 12.dp)
    ) {
        // Progress indicator
        Text(
            text = "$assignedCount of $totalCount positions assigned",
            style = androidx.compose.material3.MaterialTheme.typography.bodyMedium,
            color = androidx.compose.material3.MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // Spread name header
        Text(
            text = spread.name,
            style = androidx.compose.material3.MaterialTheme.typography.headlineSmall,
            color = androidx.compose.material3.MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.padding(bottom = 12.dp)
        )

        // Position slots
        androidx.compose.foundation.lazy.LazyColumn(
            verticalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(16.dp)
        ) {
            items(positions.size) { index ->
                val position = positions[index]
                val assignedCard = assignedCards[position.order]
                val isAssigned = assignedCard != null

                PositionSlot(
                    position = position,
                    assignedCard = assignedCard,
                    isAssigned = isAssigned,
                    onTap = {
                        selectedPositionOrder = position.order
                        showCardPicker = true
                    },
                    isReversed = reversedPositions.contains(position.order),
                    onToggleReversed = onToggleReversed?.let { { it(position.order) } }
                )
            }
        }
    }

    // Card picker bottom sheet
    if (showCardPicker) {
        CardPickerBottomSheet(
            onDismiss = {
                showCardPicker = false
                selectedPositionOrder = null
            },
            onCardSelected = { card ->
                selectedPositionOrder?.let { positionOrder ->
                    onPositionCardSelected(positionOrder, card)
                }
                showCardPicker = false
                selectedPositionOrder = null
            }
        )
    }
}
