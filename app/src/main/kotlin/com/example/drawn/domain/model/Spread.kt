package com.example.drawn.domain.model

import java.time.Instant

data class SpreadPosition(
    val name: String,
    val order: Int,
    val meaning: String
)

data class Spread(
    val id: Long,
    val name: String,
    val description: String,
    val positions: List<SpreadPosition>
)
