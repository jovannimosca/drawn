package com.example.drawn.ui.readinglist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.drawn.data.repository.ReadingRepository
import com.example.drawn.domain.model.Reading
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class ReadingListViewModel @Inject constructor(
    private val readingRepository: ReadingRepository
) : ViewModel() {

    val uiState: StateFlow<ReadingListUiState> = readingRepository.observeAllReadings()
        .map { readings -> ReadingListUiState.Success(readings) }
        .catch { error ->
            emit(ReadingListUiState.Error(error.message ?: "Unknown error"))
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = ReadingListUiState.Loading
        )
}

sealed interface ReadingListUiState {
    data object Loading : ReadingListUiState
    data class Success(val readings: List<Reading>) : ReadingListUiState
    data class Error(val message: String) : ReadingListUiState
}
