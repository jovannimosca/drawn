package com.example.drawn.domain.model

import java.time.Instant

data class Deck(
    val id: Long,
    val name: String,
    val description: String?,
    val isCustom: Boolean = false,
    val createdAt: Instant
)
