package com.example.drawn.ui.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.drawn.ui.components.NebulaBackground
import com.example.drawn.ui.deck.DeckDetailScreen
import com.example.drawn.ui.deck.DeckListScreen

@Composable
fun DeckListScreenPlaceholder(
    onNavigateToDetail: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier.fillMaxSize()) {
        NebulaBackground(modifier = Modifier.fillMaxSize())
        DeckListScreen(
            onNavigateToDetail = onNavigateToDetail,
            viewModel = hiltViewModel()
        )
    }
}

@Composable
fun DeckDetailScreenPlaceholder(
    deckId: Long,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier.fillMaxSize()) {
        NebulaBackground(modifier = Modifier.fillMaxSize())
        DeckDetailScreen(
            deckId = deckId,
            onNavigateBack = onNavigateBack
        )
    }
}

@Composable
fun SettingsScreenPlaceholder(
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier.fillMaxSize()) {
        NebulaBackground(modifier = Modifier.fillMaxSize())
    }
}