package com.example.drawn.domain.model

data class ReadingCard(
    val id: Long,
    val readingId: Long,
    val cardId: Long,
    val positionName: String,
    val positionOrder: Int,
    val interpretation: String? = null,
    val isReversed: Boolean = false
)
