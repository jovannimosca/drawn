package com.example.drawn.data.database.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.example.drawn.domain.model.CustomCard

@Entity(
    tableName = "custom_cards",
    foreignKeys = [
        ForeignKey(
            entity = DeckEntity::class,
            parentColumns = ["id"],
            childColumns = ["deckId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("deckId")]
)
data class CustomCardEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val deckId: Long,
    val name: String,
    val imagePath: String? = null,
    val keywords: String = "",
    val uprightMeaning: String = "",
    val reversedMeaning: String = "",
    val sortOrder: Int = 0
)

fun CustomCardEntity.toDomain(): CustomCard = CustomCard(
    id = id,
    deckId = deckId,
    name = name,
    imagePath = imagePath,
    keywords = keywords,
    uprightMeaning = uprightMeaning,
    reversedMeaning = reversedMeaning,
    sortOrder = sortOrder
)

fun CustomCard.toEntity(): CustomCardEntity = CustomCardEntity(
    id = id,
    deckId = deckId,
    name = name,
    imagePath = imagePath,
    keywords = keywords,
    uprightMeaning = uprightMeaning,
    reversedMeaning = reversedMeaning,
    sortOrder = sortOrder
)