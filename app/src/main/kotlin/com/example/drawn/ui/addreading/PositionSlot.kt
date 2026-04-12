package com.example.drawn.ui.addreading

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.drawn.domain.model.Card
import com.example.drawn.domain.model.SpreadPosition
import com.example.drawn.ui.theme.DarkOnSecondary
import com.example.drawn.ui.theme.DarkPrimary
import com.example.drawn.ui.theme.DarkSecondary
import com.example.drawn.ui.theme.DarkSurface
import com.example.drawn.ui.theme.DarkSurfaceVariant

/**
 * Single position slot showing an assigned card or an unassigned placeholder.
 *
 * Two visual states:
 * - Unassigned: DarkSurfaceVariant background, DarkSurfaceVariant border, position name + meaning + tap hint
 * - Assigned: DarkSurface background, solid DarkPrimary border (2dp), card thumbnail + position name
 */
@Composable
fun PositionSlot(
    position: SpreadPosition,
    assignedCard: Card?,
    isAssigned: Boolean,
    onTap: () -> Unit,
    isReversed: Boolean = false,
    onToggleReversed: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val backgroundColor = if (isAssigned) DarkSurface else DarkSurfaceVariant
    val borderColor = if (isAssigned) DarkPrimary else DarkSurfaceVariant

    Card(
        onClick = onTap,
        colors = CardDefaults.cardColors(containerColor = backgroundColor),
        border = BorderStroke(
            width = if (isAssigned) 2.dp else 1.dp,
            color = borderColor
        ),
        modifier = modifier
            .fillMaxWidth()
            .heightIn(min = 120.dp)
    ) {
        Box(
            modifier = Modifier.padding(16.dp)
        ) {
            if (isAssigned && assignedCard != null) {
                AssignedSlotContent(position, assignedCard, isReversed, onToggleReversed)
            } else {
                UnassignedSlotContent(position)
            }
        }
    }
}

@Composable
private fun AssignedSlotContent(
    position: SpreadPosition,
    card: Card,
    isReversed: Boolean,
    onToggleReversed: (() -> Unit)? = null
) {
    var isAnimated by remember { mutableStateOf(false) }
    val targetAngle = if (isReversed) 180f else 0f
    val flipAngle by animateFloatAsState(
        targetValue = if (isAnimated) targetAngle else 0f,
        animationSpec = tween(durationMillis = 400),
        label = "cardFlip"
    )
    LaunchedEffect(isReversed) {
        isAnimated = true
    }

    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier
                .padding(8.dp)
        ) {
            Text(
                text = position.name,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
            CardThumbnail(
                card = card,
                imageModifier = Modifier.graphicsLayer {
                    rotationZ = flipAngle
                    cameraDistance = 12f
                }
            )
        }

        // Reversed toggle button
        if (onToggleReversed != null) {
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .combinedClickable(
                        onClick = onToggleReversed,
                        onDoubleClick = {}
                    )
                    .size(24.dp)
                    .background(
                        if (isReversed) DarkSecondary else DarkSurfaceVariant,
                        CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "↻",
                    color = if (isReversed) DarkOnSecondary else MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 14.sp
                )
            }
        } else if (isReversed) {
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .size(16.dp)
                    .background(DarkSecondary, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "↻",
                    color = DarkOnSecondary,
                    fontSize = 10.sp
                )
            }
        }
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
