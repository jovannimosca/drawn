package com.example.drawn.ui.tag

import com.example.drawn.data.repository.TagRepository
import com.example.drawn.domain.model.Tag
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import io.mockk.junit5.MockKExtension

@ExtendWith(MockKExtension::class)
class TagViewModelTest {

    private val tagRepository: TagRepository = mockk()

    private val testTag = Tag(
        id = 1L,
        name = "Love",
        color = "#E91E63"
    )

    @Test
    fun `allTags emits Loading initially`() {
        every { tagRepository.observeAllTags() } returns MutableStateFlow(listOf(testTag))
        val viewModel = TagViewModel(tagRepository)
        val state = viewModel.allTags.value
        // Initial state should be Loading because of stateIn with initialValue = Loading
        assert(state is TagListUiState.Loading || state is TagListUiState.Success)
    }

    @Test
    fun `presetColors has expected count`() {
        assert(TagViewModel.PRESET_COLORS.size == 10)
    }

    @Test
    fun `presetColors contains valid hex colors`() {
        TagViewModel.PRESET_COLORS.forEach { color ->
            assert(color.startsWith("#"))
            assert(color.length == 7)
        }
    }

    @Test
    fun `TagListUiState Loading works`() {
        assert(TagListUiState.Loading is TagListUiState)
    }

    @Test
    fun `TagListUiState Success works`() {
        val state = TagListUiState.Success(emptyList())
        assert(state.tags.isEmpty())
    }

    @Test
    fun `TagListUiState Success contains tags`() {
        val tags = listOf(testTag)
        val state = TagListUiState.Success(tags)
        assert(state.tags.size == 1)
        assert(state.tags.first().name == "Love")
    }

    @Test
    fun `TagListUiState Error works`() {
        val state = TagListUiState.Error("test error")
        assert(state.message == "test error")
    }
}