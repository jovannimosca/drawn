package com.example.drawn.domain.model

import java.time.Instant

data class Reading(
    val id: Long,
    val title: String,
    val spreadId: Long,
    val createdAt: Instant,
    val notes: String? = null,
    val isFavorite: Boolean = false
)
