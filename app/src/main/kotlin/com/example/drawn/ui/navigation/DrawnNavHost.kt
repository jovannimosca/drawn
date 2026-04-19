package com.example.drawn.ui.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.ui.NavDisplay
import com.example.drawn.ui.addreading.AddReadingScreen
import com.example.drawn.ui.components.NebulaBackground
import com.example.drawn.ui.readingdetail.ReadingDetailScreen
import com.example.drawn.ui.readinglist.ReadingListScreen
import com.example.drawn.ui.readinglist.ReadingListViewModel
import com.example.drawn.ui.navigation.BottomTab
import com.example.drawn.ui.navigation.DecksTab
import com.example.drawn.ui.navigation.ReadingsTab
import com.example.drawn.ui.navigation.SettingsTab

@Composable
fun DrawnNavHost(
    modifier: Modifier = Modifier,
    selectedTab: MutableState<BottomTab>
) {
    val readingsStack = remember { mutableStateListOf<Any>(ReadingList) }
    val decksStack = remember { mutableStateListOf<Any>(DeckList) }
    val settingsStack = remember { mutableStateListOf<Any>(SettingsTab) }

    val currentStack = when (selectedTab.value) {
        is ReadingsTab -> readingsStack
        is DecksTab -> decksStack
        else -> settingsStack
    }

    val currentOnBack = {
        if (currentStack.size > 1) {
            currentStack.removeLastOrNull()
        }
    }

    Box(modifier = modifier.fillMaxSize()) {
        NebulaBackground(modifier = Modifier.fillMaxSize())

        NavDisplay(
            backStack = currentStack,
            onBack = currentOnBack,
            modifier = Modifier.fillMaxSize(),
            entryProvider = { key: Any ->
                when (key) {
                    is ReadingList -> NavEntry(key) {
                        val readingListViewModel: ReadingListViewModel = hiltViewModel()
                        ReadingListScreen(
                            viewModel = readingListViewModel,
                            onAddReading = {
                                readingsStack.add(AddReading)
                            },
                            onReadingSelected = { readingId ->
                                readingsStack.add(ReadingDetail(readingId = readingId))
                            }
                        )
                    }

                    is AddReading -> NavEntry(key) {
                        AddReadingScreen(
                            onNavigateBack = { readingsStack.removeLastOrNull() }
                        )
                    }

                    is ReadingDetail -> NavEntry(key) {
                        ReadingDetailScreen(
                            readingId = key.readingId,
                            onNavigateBack = { readingsStack.removeLastOrNull() }
                        )
                    }

                    is DeckList -> NavEntry(key) {
                        DeckListScreenPlaceholder(
                            onNavigateToDetail = { deckId ->
                                decksStack.add(DeckDetail(deckId))
                            }
                        )
                    }

                    is DeckDetail -> NavEntry(key) {
                        DeckDetailScreenPlaceholder(
                            deckId = key.deckId,
                            onNavigateBack = { decksStack.removeLastOrNull() }
                        )
                    }

                    is SettingsTab -> NavEntry(key) {
                        SettingsScreenPlaceholder()
                    }

                    else -> error("Unknown route: $key")
                }
            },
            transitionSpec = {
                fadeIn(animationSpec = tween(300)) + scaleIn(
                    initialScale = 0.95f,
                    animationSpec = tween(300)
                ) togetherWith
                    fadeOut(animationSpec = tween(300)) + scaleOut(
                        targetScale = 0.95f,
                        animationSpec = tween(300)
                    )
            },
            popTransitionSpec = {
                fadeIn(animationSpec = tween(300)) + scaleIn(
                    initialScale = 0.95f,
                    animationSpec = tween(300)
                ) togetherWith
                    fadeOut(animationSpec = tween(300)) + scaleOut(
                        targetScale = 0.95f,
                        animationSpec = tween(300)
                    )
            }
        )
    }
}

object DeckList
data class DeckDetail(val deckId: Long)