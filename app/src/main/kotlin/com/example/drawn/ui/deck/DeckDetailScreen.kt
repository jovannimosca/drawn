package com.example.drawn.ui.deck

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.example.drawn.ui.components.NebulaBackground

@Composable
fun DeckDetailScreen(
    deckId: Long,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.fillMaxSize()
    ) {
        NebulaBackground(modifier = Modifier.fillMaxSize())
        Text(
            text = "Deck Detail for deck $deckId",
            modifier = Modifier.align(Alignment.Center)
        )
    }
}