package com.example.drawn.ui.readingdetail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.drawn.data.repository.ReadingRepository
import com.example.drawn.domain.model.ReadingCard
import com.example.drawn.domain.model.ReadingDetail
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

sealed interface ReadingDetailUiState {
    data object Loading : ReadingDetailUiState
    data class Success(val detail: ReadingDetail) : ReadingDetailUiState
    data class Error(val message: String) : ReadingDetailUiState
}

@HiltViewModel
class ReadingDetailViewModel @Inject constructor(
    private val readingRepository: ReadingRepository,
    savedStateHandle: androidx.lifecycle.SavedStateHandle
) : ViewModel() {

    private val readingId: Long = savedStateHandle["readingId"] ?: -1L

    val uiState: StateFlow<ReadingDetailUiState> = readingRepository.observeReadingWithDetails(readingId)
        .map { detail ->
            if (detail != null) {
                ReadingDetailUiState.Success(detail)
            } else {
                ReadingDetailUiState.Error("Reading not found")
            }
        }
        .catch { error ->
            emit(ReadingDetailUiState.Error(error.message ?: "Unknown error"))
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = ReadingDetailUiState.Loading
        )
}
