package com.example.drawn.domain.model

data class ReadingDetail(
    val reading: Reading,
    val spreadName: String,
    val cards: List<ReadingCardWithDetails>,
    val photos: List<ReadingPhoto>
)

data class ReadingCardWithDetails(
    val readingCard: ReadingCard,
    val card: Card
)
