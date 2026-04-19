package com.example.drawn.ui.tag

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.drawn.data.repository.TagRepository
import com.example.drawn.domain.model.Tag
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TagViewModel @Inject constructor(
    private val tagRepository: TagRepository
) : ViewModel() {

    val allTags: StateFlow<TagListUiState> = tagRepository.observeAllTags()
        .map { tags -> TagListUiState.Success(tags) as TagListUiState }
        .catch { emit(TagListUiState.Error(it.message ?: "Unknown error")) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = TagListUiState.Loading
        )

    fun createTag(name: String, color: String? = null) {
        viewModelScope.launch {
            val tag = Tag(name = name, color = color)
            tagRepository.create(tag)
        }
    }

    fun updateTag(tag: Tag) {
        viewModelScope.launch {
            tagRepository.update(tag)
        }
    }

    fun deleteTag(tagId: Long) {
        viewModelScope.launch {
            tagRepository.delete(tagId)
        }
    }

    companion object {
        val PRESET_COLORS = listOf(
            "#9B59B6",
            "#F39C12",
            "#1ABC9C",
            "#E74C3C",
            "#3498DB",
            "#2ECC71",
            "#E91E63",
            "#FF9800",
            "#795548",
            "#607D8B"
        )
    }
}

sealed interface TagListUiState {
    data object Loading : TagListUiState
    data class Success(val tags: List<Tag>) : TagListUiState
    data class Error(val message: String) : TagListUiState
}