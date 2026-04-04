package com.example.drawn.ui.readingdetail

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.drawn.domain.model.ReadingCard
import com.example.drawn.ui.theme.DarkOnSecondary
import com.example.drawn.ui.theme.DarkPrimary
import com.example.drawn.ui.theme.DarkSecondary
import com.example.drawn.ui.theme.DarkSurface
import com.example.drawn.ui.theme.DarkSurfaceVariant

/**
 * Full reading detail view showing spread layout with assigned cards, notes, and date.
 *
 * Per UI-SPEC:
 * - TopAppBar shows reading title with back arrow
 * - Spread name as section header (headlineSmall)
 * - Cards in position order: position name + card name
 * - Notes section (if present): bodyLarge text, 16.dp top padding
 * - Date: bodyMedium in onSurfaceVariant, formatted as "MMM d, yyyy"
 * - Loading: centered CircularProgressIndicator
 * - Error: "Something went wrong" + error message (consistent with ReadingListScreen pattern)
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReadingDetailScreen(
    readingId: Long,
    viewModel: ReadingDetailViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val isEditMode by viewModel.isEditMode.collectAsStateWithLifecycle()

    // Edit field local state
    var editedTitle by remember { mutableStateOf("") }
    var editedNotes by remember { mutableStateOf("") }

    // Delete confirmation dialog state
    var showDeleteDialog by remember { mutableStateOf(false) }

    // Sync edited values when entering edit mode
    if (isEditMode) {
        val currentState = uiState
        if (currentState is ReadingDetailUiState.Success) {
            editedTitle = currentState.detail.reading.title
            editedNotes = currentState.detail.reading.notes ?: ""
        }
    }

    Scaffold(
        topBar = {
            val title = when (val state = uiState) {
                is ReadingDetailUiState.Success -> state.detail.reading.title
                else -> "Reading Detail"
            }
            TopAppBar(
                title = {
                    if (isEditMode) {
                        OutlinedTextField(
                            value = editedTitle,
                            onValueChange = { editedTitle = it },
                            modifier = Modifier.fillMaxWidth(),
                            label = { Text("Title") },
                            singleLine = true
                        )
                    } else {
                        Text(title)
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                actions = {
                    if (isEditMode) {
                        TextButton(onClick = { viewModel.exitEditMode() }) {
                            Text("Discard Changes", color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        TextButton(
                            onClick = {
                                viewModel.saveReading(editedTitle, editedNotes.takeIf { it.isNotBlank() })
                            }
                        ) {
                            Text("Save Changes", color = DarkPrimary)
                        }
                    } else {
                        IconButton(onClick = { viewModel.toggleEditMode() }) {
                            Icon(Icons.Default.Edit, contentDescription = "Edit reading")
                        }
                        IconButton(onClick = { showDeleteDialog = true }) {
                            Icon(Icons.Default.Delete, contentDescription = "Delete reading")
                        }
                    }
                }
            )
        }
    ) { padding ->
        when (uiState) {
            is ReadingDetailUiState.Loading -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    CircularProgressIndicator()
                }
            }

            is ReadingDetailUiState.Error -> {
                val message = (uiState as ReadingDetailUiState.Error).message
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "Something went wrong",
                        style = MaterialTheme.typography.headlineSmall
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = message,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }

            is ReadingDetailUiState.Success -> {
                val detail = (uiState as ReadingDetailUiState.Success).detail
                ReadingDetailContent(
                    detail = detail,
                    isEditMode = isEditMode,
                    editedNotes = editedNotes,
                    onNotesChange = { editedNotes = it },
                    modifier = Modifier.padding(padding)
                )
            }
        }

        // Delete confirmation dialog
        if (showDeleteDialog) {
            AlertDialog(
                onDismissRequest = { showDeleteDialog = false },
                title = { Text("Delete reading?") },
                text = { Text("This will permanently delete this reading and all attached photos. This action cannot be undone.") },
                confirmButton = {
                    TextButton(
                        onClick = {
                            viewModel.deleteReading()
                            showDeleteDialog = false
                            onNavigateBack()
                        },
                        colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.error)
                    ) {
                        Text("Delete")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showDeleteDialog = false }) {
                        Text("Keep Reading")
                    }
                }
            )
        }
    }
}

@Composable
private fun ReadingDetailContent(
    detail: com.example.drawn.domain.model.ReadingDetail,
    isEditMode: Boolean,
    editedNotes: String,
    onNotesChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val reading = detail.reading
    val cards = detail.cards.sortedBy { it.positionOrder }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        // Spread name / reading title as section header (title is in TopAppBar when edit mode)
        if (!isEditMode) {
            Text(
                text = reading.title,
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(16.dp))
        }

        // Cards in position order
        Text(
            text = "Cards",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(8.dp))

        for (card in cards) {
            ReadingDetailCardItem(
                readingCard = card,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))
        }

        // Notes section
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Notes",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(8.dp))

        if (isEditMode) {
            OutlinedTextField(
                value = editedNotes,
                onValueChange = onNotesChange,
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Notes") },
                minLines = 3
            )
        } else {
            Text(
                text = reading.notes ?: "No notes",
                style = MaterialTheme.typography.bodyLarge,
                color = if (reading.notes != null) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        // Date
        Spacer(modifier = Modifier.height(16.dp))
        val formattedDate = java.time.format.DateTimeFormatter
            .ofPattern("MMM d, yyyy")
            .format(reading.createdAt.atZone(java.time.ZoneId.systemDefault()).toLocalDate())
        Text(
            text = formattedDate,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun ReadingDetailCardItem(
    readingCard: ReadingCard,
    modifier: Modifier = Modifier
) {
    androidx.compose.material3.Surface(
        color = DarkSurface,
        shape = MaterialTheme.shapes.medium,
        modifier = modifier
    ) {
        Box(modifier = Modifier.padding(12.dp)) {
            Column {
                Text(
                    text = readingCard.positionName,
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = readingCard.cardId.toString(), // Show card ID since we don't have Card lookup
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.graphicsLayer {
                        rotationZ = if (readingCard.isReversed) 180f else 0f
                    }
                )
                if (readingCard.interpretation != null) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = readingCard.interpretation,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            if (readingCard.isReversed) {
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .size(16.dp)
                        .background(DarkSecondary, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "R",
                        style = MaterialTheme.typography.labelSmall,
                        color = DarkOnSecondary,
                        fontSize = 8.sp
                    )
                }
            }
        }
    }
}
