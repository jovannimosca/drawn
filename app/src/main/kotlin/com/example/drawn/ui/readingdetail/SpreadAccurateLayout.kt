package com.example.drawn.ui.readingdetail

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import com.example.drawn.domain.model.ReadingCardWithDetails

@Composable
fun SpreadAccurateLayout(
    spreadName: String,
    cards: List<ReadingCardWithDetails>,
    cardContent: @Composable (ReadingCardWithDetails) -> Unit,
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
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        modifier = modifier.fillMaxWidth()
    ) {
        cards.forEach { card ->
            cardContent(card)
        }
    }
}

@Composable
private fun CrossAndStaffLayout(
    cardByPosition: (Int) -> ReadingCardWithDetails?,
    cardContent: @Composable (ReadingCardWithDetails) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(24.dp),
        modifier = modifier.fillMaxWidth()
    ) {
        // Cross section (positions 1-6)
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Position 5: Above (Crown)
            Row(
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxWidth()
            ) {
                cardByPosition(5)?.let { cardContent(it) }
            }

            // Positions 4, 1+2, 6: Left, Center (with crossing), Right
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                cardByPosition(4)?.let { cardContent(it) }

                // Center with crossing card overlay
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.weight(1f)
                ) {
                    cardByPosition(1)?.let { cardContent(it) }
                    cardByPosition(2)?.let { crossingCard ->
                        Box(
                            modifier = Modifier
                                .graphicsLayer {
                                    rotationZ = 90f
                                    translationY = -4.dp.toPx()
                                }
                                .align(Alignment.Center)
                        ) {
                            cardContent(crossingCard)
                        }
                    }
                }

                cardByPosition(6)?.let { cardContent(it) }
            }

            // Position 3: Below (Foundation)
            Row(
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxWidth()
            ) {
                cardByPosition(3)?.let { cardContent(it) }
            }
        }

        Spacer(modifier = Modifier.width(16.dp))

        // Staff section (positions 7-10)
        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = "Advice",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            cardByPosition(7)?.let { cardContent(it) }
            cardByPosition(8)?.let { cardContent(it) }
            cardByPosition(9)?.let { cardContent(it) }
            cardByPosition(10)?.let { cardContent(it) }
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
                cardContent(card)
            }
            repeat(columns - rowCards.size) {
                Spacer(modifier = Modifier.weight(1f))
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
    }
}
