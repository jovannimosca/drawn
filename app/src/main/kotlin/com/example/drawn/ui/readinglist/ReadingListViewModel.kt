package com.example.drawn.ui.readinglist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.drawn.data.repository.ReadingRepository
import com.example.drawn.data.database.entity.ReadingWithSpread
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class ReadingListViewModel @Inject constructor(
    private val readingRepository: ReadingRepository
) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery

    private val _selectedTagIds = MutableStateFlow<Set<Long>>(emptySet())
    val selectedTagIds: StateFlow<Set<Long>> = _selectedTagIds

    private val _showFavoritesOnly = MutableStateFlow(false)
    val showFavoritesOnly: StateFlow<Boolean> = _showFavoritesOnly

    fun onSearchQueryChange(query: String) {
        _searchQuery.value = query
    }

    fun onTagFilterChange(tagIds: Set<Long>) {
        _selectedTagIds.value = tagIds
    }

    fun onFavoriteFilterChange(showFavoritesOnly: Boolean) {
        _showFavoritesOnly.value = showFavoritesOnly
    }

    fun clearFilters() {
        _selectedTagIds.value = emptySet()
        _showFavoritesOnly.value = false
    }

    val uiState: StateFlow<ReadingListUiState> = combine(
        readingRepository.observeAllReadingsWithSpread(),
        searchQuery,
        selectedTagIds,
        showFavoritesOnly
    ) { readings, query, tagIds, favOnly ->
        var result = readings

        if (query.isNotBlank()) {
            val lowerQuery = query.lowercase()
            result = result.filter { reading ->
                reading.title.lowercase().contains(lowerQuery) ||
                    (reading.notes?.lowercase()?.contains(lowerQuery) == true) ||
                    reading.spreadName.lowercase().contains(lowerQuery)
            }
        }

        if (favOnly) {
            result = result.filter { it.isFavorite }
        }

        result
    }
        .map { readings -> ReadingListUiState.Success(readings) as ReadingListUiState }
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
    data class Success(val readings: List<ReadingWithSpread>) : ReadingListUiState
    data class Error(val message: String) : ReadingListUiState
}
