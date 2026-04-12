package com.example.drawn.ui.readingdetail

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.drawn.data.repository.ReadingRepository
import com.example.drawn.domain.model.ReadingDetail
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import java.io.File
import java.util.UUID
import javax.inject.Inject

sealed interface ReadingDetailUiState {
    data object Loading : ReadingDetailUiState
    data class Success(val detail: ReadingDetail) : ReadingDetailUiState
    data class Error(val message: String) : ReadingDetailUiState
}

@HiltViewModel
class ReadingDetailViewModel @Inject constructor(
    private val readingRepository: ReadingRepository,
    @param:ApplicationContext private val context: Context
) : ViewModel() {

    private var readingId: Long = -1L
    private var collectionJob: Job? = null

    private val _uiState = MutableStateFlow<ReadingDetailUiState>(ReadingDetailUiState.Loading)
    val uiState: StateFlow<ReadingDetailUiState> = _uiState

    fun setReadingId(id: Long) {
        if (readingId == id) return
        readingId = id
        collectionJob?.cancel()
        _uiState.value = ReadingDetailUiState.Loading
        collectionJob = viewModelScope.launch {
            readingRepository.observeReadingWithDetails(readingId)
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
                .collect { state ->
                    _uiState.value = state
                }
        }
    }

    // Edit mode state
    private val _isEditMode = MutableStateFlow(false)
    val isEditMode: StateFlow<Boolean> = _isEditMode

    fun toggleEditMode() {
        _isEditMode.value = !_isEditMode.value
    }

    fun exitEditMode() {
        _isEditMode.value = false
    }

    fun saveReading(updatedTitle: String, updatedNotes: String?) {
        viewModelScope.launch {
            val currentState = uiState.value
            if (currentState is ReadingDetailUiState.Success) {
                val detail = currentState.detail
                val updatedReading = detail.reading.copy(
                    title = updatedTitle,
                    notes = updatedNotes
                )
                readingRepository.updateReading(updatedReading, detail.cards.map { it.readingCard })
                _isEditMode.value = false
            }
        }
    }

    fun deleteReading() {
        viewModelScope.launch {
            readingRepository.deleteReading(readingId)
        }
    }

    fun addPhoto(uriString: String) {
        viewModelScope.launch {
            try {
                val uri = Uri.parse(uriString)
                val contentResolver = context.contentResolver
                val inputStream = contentResolver.openInputStream(uri)
                if (inputStream != null) {
                    val photosDir = File(context.filesDir, "reading_photos").also { it.mkdirs() }
                    val fileName = "photo_${UUID.randomUUID()}.jpg"
                    val destFile = File(photosDir, fileName)
                    inputStream.use { input ->
                        destFile.outputStream().use { output ->
                            input.copyTo(output)
                        }
                    }
                    readingRepository.addPhotoToReading(readingId, destFile.absolutePath)
                }
            } catch (e: Exception) {
                // If copy fails, fall back to storing the original URI
                readingRepository.addPhotoToReading(readingId, uriString)
            }
        }
    }

    fun deletePhoto(photoId: Long) {
        viewModelScope.launch {
            readingRepository.removePhotoFromReading(photoId)
        }
    }
}
