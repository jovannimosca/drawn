package com.example.drawn.data.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.drawn.domain.model.ArcanaType
import com.example.drawn.domain.model.Deck
import java.time.Instant

@Entity(tableName = "decks")
data class DeckEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val description: String?,
    val isCustom: Boolean = false,
    val createdAt: Long
)

fun DeckEntity.toDomain(): Deck = Deck(
    id = id,
    name = name,
    description = description,
    isCustom = isCustom,
    createdAt = Instant.ofEpochMilli(createdAt)
)

fun Deck.toEntity(): DeckEntity = DeckEntity(
    id = id,
    name = name,
    description = description,
    isCustom = isCustom,
    createdAt = createdAt.toEpochMilli()
)
