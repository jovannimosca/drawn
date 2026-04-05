package com.example.drawn.ui.readinglist

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.drawn.data.database.entity.ReadingWithSpread
import com.example.drawn.ui.components.EmptyState
import com.example.drawn.ui.components.ErrorBanner
import com.example.drawn.ui.components.NebulaBackground
import com.example.drawn.ui.theme.DarkSecondary
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReadingListScreen(
    viewModel: ReadingListViewModel = hiltViewModel(),
    onAddReading: () -> Unit,
    onReadingSelected: (Long) -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
    val isSearching = searchQuery.isNotEmpty()
    var listVisible by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Drawn") }
            )
        },
        floatingActionButton = {
            if (!isSearching) {
                FloatingActionButton(onClick = onAddReading) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Add reading"
                    )
                }
            }
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            NebulaBackground(modifier = Modifier.fillMaxSize())
            Column(
                modifier = Modifier.fillMaxSize()
            ) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = viewModel::onSearchQueryChange,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                placeholder = { Text("Search readings\u2026") },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = null
                    )
                },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { viewModel.onSearchQueryChange("") }) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Clear search"
                            )
                        }
                    }
                },
                singleLine = true
            )

            when (uiState) {
                is ReadingListUiState.Loading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }

                is ReadingListUiState.Error -> {
                    val message = (uiState as ReadingListUiState.Error).message
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        ErrorBanner(
                            message = message,
                            modifier = Modifier.padding(16.dp)
                        )
                    }
                }

                is ReadingListUiState.Success -> {
                    val readings = (uiState as ReadingListUiState.Success).readings
                    if (readings.isNotEmpty() && !listVisible) {
                        listVisible = true
                    }
                    if (readings.isEmpty()) {
                        if (isSearching) {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "No readings match your search",
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier.padding(32.dp)
                                )
                            }
                        } else {
                            EmptyState(
                                title = "No readings yet",
                                message = "Tap + to record your first reading",
                                modifier = Modifier.fillMaxSize()
                            )
                        }
                    } else {
                        AnimatedVisibility(
                            visible = listVisible,
                            enter = fadeIn(animationSpec = androidx.compose.animation.core.tween(durationMillis = 300))
                        ) {
                            ReadingList(
                                readings = readings,
                                onReadingSelected = onReadingSelected
                            )
                        }
                    }
                }
            }
        }
        }
    }
}

@Composable
private fun ReadingList(
    readings: List<ReadingWithSpread>,
    onReadingSelected: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        modifier = modifier
    ) {
        items(readings, key = { it.id }) { reading ->
            ReadingListItem(
                reading = reading,
                onClick = { onReadingSelected(reading.id) }
            )
        }
    }
}

@Composable
private fun ReadingListItem(
    reading: ReadingWithSpread,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        onClick = onClick,
        modifier = modifier.padding(vertical = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier.padding(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = androidx.compose.foundation.layout.Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = reading.title,
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.weight(1f)
                )
                val formattedDate = DateTimeFormatter
                    .ofPattern("MMM d, yyyy")
                    .withZone(ZoneId.systemDefault())
                    .format(Instant.ofEpochMilli(reading.createdAt))
                Text(
                    text = formattedDate,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = reading.spreadName,
                style = MaterialTheme.typography.labelMedium,
                color = DarkSecondary
            )
            reading.notes?.let { notes ->
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = notes,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}
