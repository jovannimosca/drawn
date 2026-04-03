package com.example.drawn.ui.addreading

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.drawn.domain.model.Card
import com.example.drawn.domain.model.SpreadPosition
import com.example.drawn.ui.theme.DarkPrimary
import com.example.drawn.ui.theme.DarkSurface
import com.example.drawn.ui.theme.DarkSurfaceVariant

/**
 * Single position slot showing an assigned card or an unassigned placeholder.
 *
 * Two visual states:
 * - Unassigned: DarkSurfaceVariant background, dashed border, position name + meaning + tap hint
 * - Assigned: DarkSurface background, solid DarkPrimary border, card thumbnail + position name
 */
@Composable
fun PositionSlot(
    position: SpreadPosition,
    assignedCard: Card?,
    isAssigned: Boolean,
    onTap: () -> Unit,
    modifier: Modifier = Modifier
) {
    val backgroundColor = if (isAssigned) DarkSurface else DarkSurfaceVariant
    val borderColor = if (isAssigned) DarkPrimary else DarkSurfaceVariant

    Surface(
        color = backgroundColor,
        modifier = modifier
            .fillMaxWidth()
            .heightIn(min = 120.dp)
            .border(
                width = 1.dp,
                color = borderColor,
                shape = MaterialTheme.shapes.medium
            )
            .clickable(onClick = onTap)
            .padding(12.dp)
    ) {
        if (isAssigned && assignedCard != null) {
            AssignedSlotContent(position, assignedCard)
        } else {
            UnassignedSlotContent(position)
        }
    }
}

@Composable
private fun AssignedSlotContent(
    position: SpreadPosition,
    card: Card
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = position.name,
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurface
        )
        CardThumbnail(card = card)
    }
}

@Composable
private fun UnassignedSlotContent(position: SpreadPosition) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Icon(
            imageVector = Icons.Default.AddCircle,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(32.dp)
        )
        Text(
            text = position.name,
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurface
        )
        Text(
            text = position.meaning,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = "Tap to select a card",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
