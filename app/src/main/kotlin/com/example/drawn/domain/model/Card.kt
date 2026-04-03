package com.example.drawn.domain.model

data class Card(
    val id: Long,
    val deckId: Long,
    val name: String,
    val number: Int,
    val arcanaType: ArcanaType,
    val imageResId: Int,
    val keywords: List<String>,
    val meaningUpright: String,
    val meaningReversed: String
)
