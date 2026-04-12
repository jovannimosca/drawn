package com.example.drawn.ui.addreading

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.drawn.domain.model.ArcanaType
import com.example.drawn.domain.model.Card

/**
 * Reusable card thumbnail component showing the card image and name.
 *
 * Per UI-SPEC: 80.dp width, 2:3 aspect ratio for tarot card proportions.
 * Images are bundled as drawable resources named by arcana type and number.
 */
@Composable
fun CardThumbnail(
    card: Card,
    modifier: Modifier = Modifier,
    imageModifier: Modifier = Modifier
) {
    val drawableResId = cardDrawableRes(card)

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
    ) {
        if (drawableResId != 0) {
            androidx.compose.foundation.Image(
                painter = painterResource(id = drawableResId),
                contentDescription = card.name,
                contentScale = ContentScale.Crop,
                modifier = imageModifier
                    .fillMaxWidth()
                    .aspectRatio(2f / 3f)
            )
        } else {
            androidx.compose.foundation.layout.Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(2f / 3f),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "?",
                    style = androidx.compose.material3.MaterialTheme.typography.headlineLarge,
                    color = androidx.compose.material3.MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        Text(
            text = card.name,
            style = androidx.compose.material3.MaterialTheme.typography.labelMedium,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = 4.dp)
        )
    }
}

/**
 * Maps a Card to its bundled drawable resource ID.
 * Naming convention: card_major_ar00, card_minor_wands_wa02, etc.
 */
private fun cardDrawableRes(card: Card): Int {
    val drawableName = when (card.arcanaType) {
        ArcanaType.MAJOR -> "card_major_ar${card.number.toString().padStart(2, '0')}"
        ArcanaType.MINOR -> {
            val suitPrefix = card.name.substringAfterLast(" ").lowercase()
            val suit = when {
                card.name.contains("Wands") -> "wands"
                card.name.contains("Cups") -> "cups"
                card.name.contains("Swords") -> "swords"
                card.name.contains("Pentacles") -> "pentacles"
                else -> "unknown"
            }
            val cardCode = when {
                card.name.contains("Ace") -> "ac"
                card.name.contains("Two") -> "02"
                card.name.contains("Three") -> "03"
                card.name.contains("Four") -> "04"
                card.name.contains("Five") -> "05"
                card.name.contains("Six") -> "06"
                card.name.contains("Seven") -> "07"
                card.name.contains("Eight") -> "08"
                card.name.contains("Nine") -> "09"
                card.name.contains("Ten") -> "10"
                card.name.contains("Page") -> "pa"
                card.name.contains("Knight") -> "kn"
                card.name.contains("Queen") -> "qu"
                card.name.contains("King") -> "ki"
                else -> return 0
            }
            "card_minor_${suit}_${suit.take(2)}$cardCode"
        }
    }
    return com.example.drawn.BuildConfig::class.java.classLoader?.let { loader ->
        try {
            val rClass = Class.forName("com.example.drawn.R\$drawable")
            rClass.getField(drawableName).getInt(null)
        } catch (e: Exception) {
            0
        }
    } ?: 0
}
