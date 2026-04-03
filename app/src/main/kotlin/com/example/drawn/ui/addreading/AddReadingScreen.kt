package com.example.drawn.ui.addreading

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddReadingScreen(
    viewModel: AddReadingViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("New Reading") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        }
    ) { padding ->
        when (uiState) {
            is AddReadingUiState.Loading -> {
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

            is AddReadingUiState.Ready -> {
                val state = (uiState as AddReadingUiState.Ready).state
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                ) {
                    WizardStepIndicator(currentStep = state.currentStep)

                    Spacer(modifier = Modifier.height(24.dp))

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                            .padding(horizontal = 24.dp)
                    ) {
                        when (state.currentStep) {
                            AddReadingStep.SpreadPicker -> {
                                SpreadPickerStep(
                                    spreads = state.spreads,
                                    selectedSpread = state.selectedSpread,
                                    onSpreadSelected = { viewModel.selectSpread(it) }
                                )
                            }

                            AddReadingStep.CardAssignment -> {
                                // Placeholder — to be implemented in Plan 03
                                Column(
                                    modifier = Modifier.fillMaxSize(),
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.Center
                                ) {
                                    Text(
                                        text = "Step 2: Card Assignment",
                                        style = MaterialTheme.typography.headlineSmall
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        text = "Coming next",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }

                            AddReadingStep.NotesAndSave -> {
                                NotesAndSaveStep(
                                    title = state.title,
                                    notes = state.notes,
                                    isSaving = false,
                                    onTitleChange = { viewModel.updateTitle(it) },
                                    onNotesChange = { viewModel.updateNotes(it) },
                                    onSave = { viewModel.saveReading() }
                                )
                            }
                        }
                    }

                    // Bottom navigation buttons
                    WizardBottomBar(
                        currentStep = state.currentStep,
                        canProceed = state.canProceedToNext(),
                        onNext = { viewModel.goToNextStep() },
                        onBack = { viewModel.goToPreviousStep() }
                    )
                }
            }

            is AddReadingUiState.Saving -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    CircularProgressIndicator()
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Saving...",
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }

            is AddReadingUiState.Saved -> {
                // Navigation happens on save success
                onNavigateBack()
            }

            is AddReadingUiState.Error -> {
                val message = (uiState as AddReadingUiState.Error).message
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                        .padding(24.dp),
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
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(onClick = { viewModel.retry() }) {
                        Text("Retry")
                    }
                }
            }
        }
    }
}

@Composable
private fun WizardBottomBar(
    currentStep: AddReadingStep,
    canProceed: Boolean,
    onNext: () -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        if (currentStep != AddReadingStep.SpreadPicker) {
            OutlinedButton(onClick = onBack) {
                Text("Back")
            }
        } else {
            Spacer(modifier = Modifier)
        }

        if (currentStep != AddReadingStep.NotesAndSave) {
            Button(
                onClick = onNext,
                enabled = canProceed
            ) {
                Text("Next")
            }
        }
    }
}


