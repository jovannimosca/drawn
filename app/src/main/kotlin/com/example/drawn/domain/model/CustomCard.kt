package com.example.drawn.domain.model

data class CustomCard(
    val id: Long = 0,
    val deckId: Long,
    val name: String,
    val imagePath: String? = null,
    val keywords: String = "",
    val uprightMeaning: String = "",
    val reversedMeaning: String = "",
    val sortOrder: Int = 0
)