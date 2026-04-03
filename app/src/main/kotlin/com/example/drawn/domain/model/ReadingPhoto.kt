package com.example.drawn.domain.model

data class ReadingPhoto(
    val id: Long,
    val readingId: Long,
    val photoUri: String,
    val caption: String? = null
)
