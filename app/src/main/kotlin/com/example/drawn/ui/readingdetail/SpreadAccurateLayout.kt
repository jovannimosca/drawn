package com.example.drawn.ui.readingdetail

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.dp
import com.example.drawn.domain.model.ReadingCardWithDetails
import kotlin.math.max

/** Fixed position order for the Layout content block.
 *  Each entry maps to a placement slot using the actual positionOrder values
 *  from the Celtic Cross spread definition (0-9, not 1-10).
 *  Null positions emit an empty Box so indices stay stable. */
private val CELTIC_CROSS_POSITIONS = listOf(
    4,  // idx 0: Possible Outcome (above/crown)
    3,  // idx 1: Recent Past (left/challenge)
    0,  // idx 2: Present (center)
    5,  // idx 3: Near Future (right)
    2,  // idx 4: Foundation (below)
    9,  // idx 5: Outcome (staff top)
    8,  // idx 6: Hopes and Fears
    7,  // idx 7: Environment
    6,  // idx 8: Self (staff bottom)
    1,  // idx 9: Challenge (overlay on center — placed LAST for z-order)
)

@Composable
fun SpreadAccurateLayout(
    spreadName: String,
    cards: List<ReadingCardWithDetails>,
    cardContent: @Composable (ReadingCardWithDetails) -> Unit,
    emptyCard: @Composable () -> Unit = { Box(modifier = Modifier.size(1.dp)) },
    modifier: Modifier = Modifier
) {
    val sortedCards = cards.sortedBy { it.readingCard.positionOrder }

    fun cardByPosition(order: Int): ReadingCardWithDetails? =
        sortedCards.find { it.readingCard.positionOrder == order }

    when (spreadName) {
        "Celtic Cross" -> {
            CrossAndStaffLayout(
                cardByPosition = { cardByPosition(it) },
                cardContent = cardContent,
                emptyCard = emptyCard,
                modifier = modifier
            )
        }
        "Three Card", "Past/Present/Future" -> {
            RowLayout(
                cards = sortedCards,
                cardContent = cardContent,
                modifier = modifier
            )
        }
        else -> {
            FallbackGridLayout(
                cards = sortedCards,
                cardContent = cardContent,
                modifier = modifier
            )
        }
    }
}

@Composable
private fun RowLayout(
    cards: List<ReadingCardWithDetails>,
    cardContent: @Composable (ReadingCardWithDetails) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = modifier.fillMaxWidth()
    ) {
        cards.forEach { card ->
            Box(modifier = Modifier.weight(1f)) {
                cardContent(card)
            }
        }
    }
}

@Composable
private fun CrossAndStaffLayout(
    cardByPosition: (Int) -> ReadingCardWithDetails?,
    cardContent: @Composable (ReadingCardWithDetails) -> Unit,
    emptyCard: @Composable () -> Unit,
    modifier: Modifier = Modifier
) {
    Layout(
        content = {
            // Emit exactly CELTIC_CROSS_POSITIONS.size composables so indices are stable.
            CELTIC_CROSS_POSITIONS.forEach { pos ->
                val card = cardByPosition(pos)
                if (card != null) {
                    cardContent(card)
                } else {
                    emptyCard()
                }
            }
        },
        modifier = modifier.fillMaxWidth()
    ) { measurables, constraints ->
        val spacing = 8.dp.roundToPx()
        val crossGap = 4.dp.roundToPx()
        val staffGap = 4.dp.roundToPx()

        // Cross: 3 columns. Staff: 1 column. Total 4 columns.
        val columnCount = 4
        val totalSpacing = spacing + crossGap * 2
        val cardWidth = (constraints.maxWidth - totalSpacing) / columnCount

        // Uniform card height: image (2:3 ratio) + text reserve for position name + card name
        val imageHeight = (cardWidth * 3) / 2
        val textReserve = 60.dp.roundToPx()
        val uniformCardHeight = imageHeight + textReserve

        val placeables = measurables.map { measurable ->
            measurable.measure(
                Constraints(maxWidth = cardWidth, maxHeight = uniformCardHeight)
            )
        }

        val crossWidth = cardWidth * 3 + crossGap * 2
        val crossHeight = uniformCardHeight * 3 + crossGap * 2
        val staffHeight = uniformCardHeight * 4 + staffGap * 3
        val layoutHeight = max(crossHeight, staffHeight)
        val layoutWidth = crossWidth + spacing + cardWidth

        layout(layoutWidth, layoutHeight) {
            val staffX = crossWidth + spacing

            // idx 0: Position 5 — Crown (top center)
            placeables[0].placeRelative(
                crossGap + cardWidth,
                0
            )

            // idx 1: Position 4 — Challenge (left)
            placeables[1].placeRelative(
                0,
                uniformCardHeight + crossGap
            )

            // idx 2: Position 1 — Present (center)
            placeables[2].placeRelative(
                crossGap + cardWidth,
                uniformCardHeight + crossGap
            )

            // idx 3: Position 6 — Future (right)
            placeables[3].placeRelative(
                (crossGap + cardWidth) * 2,
                uniformCardHeight + crossGap
            )

            // idx 4: Position 3 — Foundation (bottom center)
            placeables[4].placeRelative(
                crossGap + cardWidth,
                (uniformCardHeight + crossGap) * 2
            )

            // idx 5-8: Staff (top to bottom: 10, 9, 8, 7)
            for (i in 0 until 4) {
                placeables[5 + i].placeRelative(
                    staffX,
                    i * (uniformCardHeight + staffGap)
                )
            }

            // idx 9: Position 2 — Crossing (overlay on center, placed last for z-order)
            placeables[9].placeRelativeWithLayer(
                crossGap + cardWidth,
                uniformCardHeight + crossGap,
                layerBlock = {
                    rotationZ = 90f
                    translationY = -4.dp.toPx()
                }
            )
        }
    }
}

@Composable
private fun FallbackGridLayout(
    cards: List<ReadingCardWithDetails>,
    cardContent: @Composable (ReadingCardWithDetails) -> Unit,
    modifier: Modifier = Modifier
) {
    val columns = 3
    cards.chunked(columns).forEach { rowCards ->
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            rowCards.forEach { card ->
                Box(modifier = Modifier.weight(1f)) {
                    cardContent(card)
                }
            }
            repeat(columns - rowCards.size) {
                Spacer(modifier = Modifier.weight(1f))
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
    }
}
