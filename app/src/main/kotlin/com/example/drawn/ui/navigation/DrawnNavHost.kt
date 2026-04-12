package com.example.drawn.ui.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.togetherWith
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.ui.NavDisplay
import com.example.drawn.ui.addreading.AddReadingScreen
import com.example.drawn.ui.readingdetail.ReadingDetailScreen
import com.example.drawn.ui.readinglist.ReadingListScreen
import com.example.drawn.ui.readinglist.ReadingListViewModel

@Composable
fun DrawnNavHost(
    modifier: Modifier = Modifier
) {
    val backStack = remember { mutableStateListOf<Any>(ReadingList) }

    NavDisplay(
        backStack = backStack,
        onBack = { backStack.removeLastOrNull() },
        modifier = modifier,
        entryProvider = { key: Any ->
            when (key) {
                is ReadingList -> NavEntry(key) {
                    val readingListViewModel: ReadingListViewModel = hiltViewModel()
                    ReadingListScreen(
                        viewModel = readingListViewModel,
                        onAddReading = {
                            backStack.add(AddReading)
                        },
                        onReadingSelected = { readingId ->
                            backStack.add(ReadingDetail(readingId = readingId))
                        }
                    )
                }

                is AddReading -> NavEntry(key) {
                    AddReadingScreen(
                        onNavigateBack = { backStack.removeLastOrNull() }
                    )
                }

                is ReadingDetail -> NavEntry(key) {
                    ReadingDetailScreen(
                        readingId = key.readingId,
                        onNavigateBack = { backStack.removeLastOrNull() }
                    )
                }

                else -> error("Unknown route: $key")
            }
        },
        transitionSpec = {
            // Fade in with scale up for forward navigation (list -> detail)
            fadeIn(animationSpec = tween(300)) + scaleIn(
                initialScale = 0.95f,
                animationSpec = tween(300)
            ) togetherWith
                // Fade out with scale down for exit
                fadeOut(animationSpec = tween(300)) + scaleOut(
                    targetScale = 0.95f,
                    animationSpec = tween(300)
                )
        },
        popTransitionSpec = {
            // Reverse animation for back navigation (detail -> list)
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
