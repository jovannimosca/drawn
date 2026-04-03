package com.example.drawn.domain.model

data class ReadingDetail(
    val reading: Reading,
    val cards: List<ReadingCard>,
    val photos: List<ReadingPhoto>
)
