package com.example.drawn.ui.navigation

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
        }
    )
}
