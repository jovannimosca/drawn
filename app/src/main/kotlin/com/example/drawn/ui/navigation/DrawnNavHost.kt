package com.example.drawn.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.ui.NavDisplay
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
                            // TODO: Navigate to add reading screen (Phase 2)
                        },
                        onReadingSelected = { readingId ->
                            // TODO: Reading detail screen (Phase 2)
                            backStack.add(ReadingDetail(readingId = readingId))
                        }
                    )
                }

                is ReadingDetail -> NavEntry(key) {
                    // TODO: Reading detail screen (Phase 2)
                    androidx.compose.material3.Text(text = "Reading detail: ${key.readingId}")
                }

                else -> error("Unknown route: $key")
            }
        }
    )
}
