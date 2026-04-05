package com.example.drawn.ui.readingdetail

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyHorizontalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.ui.window.Dialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.FileProvider
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import com.example.drawn.domain.model.ReadingCardWithDetails
import com.example.drawn.domain.model.ReadingPhoto
import com.example.drawn.ui.addreading.CardThumbnail
import com.example.drawn.ui.components.GoldDivider
import com.example.drawn.ui.components.NebulaBackground
import com.example.drawn.ui.theme.DarkOnSecondary
import com.example.drawn.ui.theme.DarkPrimary
import com.example.drawn.ui.theme.DarkSecondary
import com.example.drawn.ui.theme.DarkSurface
import com.example.drawn.ui.theme.DarkSurfaceVariant
import java.io.File

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
    LaunchedEffect(readingId) {
        viewModel.setReadingId(readingId)
    }

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val isEditMode by viewModel.isEditMode.collectAsStateWithLifecycle()

    // Edit field local state
    var editedTitle by remember { mutableStateOf("") }
    var editedNotes by remember { mutableStateOf("") }

    // Delete confirmation dialog state
    var showDeleteDialog by remember { mutableStateOf(false) }

    // Photo-related state
    var showAddPhotoSheet by remember { mutableStateOf(false) }
    var showPhotoViewer by remember { mutableStateOf<ReadingPhoto?>(null) }
    var showDeletePhotoDialog by remember { mutableStateOf<ReadingPhoto?>(null) }

    // Photo pickers
    val context = LocalContext.current
    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { uri ->
            uri?.let { viewModel.addPhoto(it.toString()) }
        }
    )

    var cameraPhotoUri by remember { mutableStateOf<android.net.Uri?>(null) }
    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture(),
        onResult = { success ->
            if (success) {
                cameraPhotoUri?.let { viewModel.addPhoto(it.toString()) }
            }
        }
    )

    val createTempImageUri: () -> android.net.Uri = {
        val file = File.createTempFile("reading_photo_", ".jpg", context.cacheDir)
        FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            file
        )
    }

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
                    Text(if (isEditMode) "Edit Reading" else "Reading Details")
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
                        IconButton(onClick = { viewModel.exitEditMode() }) {
                            Icon(
                                Icons.Default.Close,
                                contentDescription = "Discard Changes",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        IconButton(
                            onClick = {
                                viewModel.saveReading(editedTitle, editedNotes)
                            }
                        ) {
                            Icon(
                                Icons.Default.Check,
                                contentDescription = "Save Changes",
                                tint = DarkPrimary
                            )
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
                Box(modifier = Modifier.fillMaxSize()) {
                    NebulaBackground(modifier = Modifier.fillMaxSize())
                    ReadingDetailContent(
                        detail = detail,
                        isEditMode = isEditMode,
                        editedTitle = editedTitle,
                        editedNotes = editedNotes,
                        onTitleChange = { editedTitle = it },
                        onNotesChange = { editedNotes = it },
                        onAddPhotoClick = { showAddPhotoSheet = true },
                        onPhotoTap = { photo -> showPhotoViewer = photo },
                        onPhotoLongPress = { photo -> showDeletePhotoDialog = photo },
                        modifier = Modifier.padding(padding)
                    )
                }
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

        // Add photo bottom sheet
        if (showAddPhotoSheet) {
            ModalBottomSheet(
                onDismissRequest = { showAddPhotoSheet = false },
                sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        "Add Photo",
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )
                    TextButton(
                        onClick = {
                            showAddPhotoSheet = false
                            val uri = createTempImageUri()
                            cameraPhotoUri = uri
                            cameraLauncher.launch(uri)
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Take Photo")
                    }
                    TextButton(
                        onClick = {
                            showAddPhotoSheet = false
                            galleryLauncher.launch(
                                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                            )
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Choose from Gallery")
                    }
                }
            }
        }

        // Full-screen photo viewer
        showPhotoViewer?.let { photo ->
            Dialog(onDismissRequest = { showPhotoViewer = null }) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black),
                    contentAlignment = Alignment.Center
                ) {
                    val imageModel = if (photo.photoUri.startsWith("/")) {
                        java.io.File(photo.photoUri)
                    } else {
                        photo.photoUri
                    }
                    AsyncImage(
                        model = imageModel,
                        contentDescription = "Full screen photo",
                        contentScale = ContentScale.Fit,
                        modifier = Modifier.fillMaxSize()
                    )
                    IconButton(
                        onClick = { showPhotoViewer = null },
                        modifier = Modifier
                            .align(Alignment.TopStart)
                            .padding(8.dp)
                    ) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }
                    if (isEditMode) {
                        IconButton(
                            onClick = {
                                showPhotoViewer = null
                                showDeletePhotoDialog = photo
                            },
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .padding(8.dp)
                        ) {
                            Icon(
                                Icons.Default.Delete,
                                contentDescription = "Delete photo",
                                tint = Color.White
                            )
                        }
                    }
                }
            }
        }

        // Delete photo confirmation dialog
        showDeletePhotoDialog?.let { photo ->
            AlertDialog(
                onDismissRequest = { showDeletePhotoDialog = null },
                title = { Text("Remove photo?") },
                text = { Text("This photo will be removed from this reading.") },
                confirmButton = {
                    TextButton(
                        onClick = {
                            viewModel.deletePhoto(photo.id)
                            showDeletePhotoDialog = null
                        },
                        colors = ButtonDefaults.textButtonColors(
                            contentColor = MaterialTheme.colorScheme.error
                        )
                    ) {
                        Text("Remove")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showDeletePhotoDialog = null }) {
                        Text("Cancel")
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
    editedTitle: String,
    editedNotes: String,
    onTitleChange: (String) -> Unit,
    onNotesChange: (String) -> Unit,
    onAddPhotoClick: () -> Unit,
    onPhotoTap: (ReadingPhoto) -> Unit,
    onPhotoLongPress: (ReadingPhoto) -> Unit,
    modifier: Modifier = Modifier
) {
    val reading = detail.reading
    val cards = detail.cards.sortedBy { it.readingCard.positionOrder }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Spread name
        item {
            Text(
                text = detail.spreadName,
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(4.dp))
            if (isEditMode) {
                OutlinedTextField(
                    value = editedTitle,
                    onValueChange = onTitleChange,
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Title") },
                    singleLine = true
                )
            } else {
                Text(
                    text = reading.title,
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Cards",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(8.dp))
        }

        // Cards layout (spread-accurate geometry)
        item {
            SpreadAccurateLayout(
                spreadName = detail.spreadName,
                cards = cards,
                cardContent = { cardWithDetails ->
                    ReadingDetailCardItem(
                        readingCardWithDetails = cardWithDetails,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            )
        }

        // Divider between Cards and Notes
        item {
            GoldDivider(modifier = Modifier.padding(vertical = 8.dp))
        }

        // Notes
        item {
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Notes",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(8.dp))
        }

        item {
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
                    text = "No notes",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        // Divider between Notes and Photos
        item {
            GoldDivider(modifier = Modifier.padding(vertical = 8.dp))
        }

        // Photos
        item {
            Spacer(modifier = Modifier.height(24.dp))
            Text(
                text = "Photos",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(8.dp))

            val photos = detail.photos
            LazyHorizontalGrid(
                rows = GridCells.Fixed(1),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(vertical = 4.dp),
                modifier = Modifier.height(80.dp)
            ) {
                if (isEditMode) {
                    item {
                        Card(
                            onClick = onAddPhotoClick,
                            colors = CardDefaults.cardColors(containerColor = DarkSurfaceVariant),
                            modifier = Modifier.size(80.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    Icons.Default.Add,
                                    contentDescription = "Add photo",
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
                items(photos, key = { it.id }) { photo ->
                    Card(
                        onClick = { onPhotoTap(photo) },
                        modifier = Modifier.size(80.dp)
                    ) {
                        val imageModel = if (photo.photoUri.startsWith("/")) {
                            java.io.File(photo.photoUri)
                        } else {
                            photo.photoUri
                        }
                        AsyncImage(
                            model = imageModel,
                            contentDescription = "Reading photo",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                }
            }
            if (photos.isEmpty() && !isEditMode) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "No photos attached",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        // Date
        item {
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
}

@Composable
private fun ReadingDetailCardItem(
    readingCardWithDetails: ReadingCardWithDetails,
    modifier: Modifier = Modifier
) {
    val readingCard = readingCardWithDetails.readingCard
    val card = readingCardWithDetails.card
    val flipAngle by animateFloatAsState(
        targetValue = if (readingCard.isReversed) 180f else 0f,
        animationSpec = tween(durationMillis = 400),
        label = "detailCardFlip"
    )

    Card(
        colors = CardDefaults.cardColors(containerColor = DarkSurface),
        border = androidx.compose.foundation.BorderStroke(
            width = 1.dp,
            color = DarkSurfaceVariant
        ),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = readingCard.positionName,
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(8.dp))
            CardThumbnail(
                card = card,
                modifier = Modifier.graphicsLayer {
                    rotationY = flipAngle
                    cameraDistance = 12f
                }
            )
            if (readingCard.interpretation != null) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = readingCard.interpretation,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
