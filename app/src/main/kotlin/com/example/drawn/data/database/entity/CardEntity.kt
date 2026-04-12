package com.example.drawn.data.database.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.example.drawn.domain.model.ArcanaType
import com.example.drawn.domain.model.Card
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

@Entity(
    tableName = "cards",
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
data class CardEntity(
    @PrimaryKey
    val id: Long,
    val deckId: Long,
    val name: String,
    val number: Int,
    val arcanaType: String,
    val imageResId: Int,
    val keywordsJson: String,
    val meaningUpright: String,
    val meaningReversed: String
)

private val json = Json { ignoreUnknownKeys = true }

fun CardEntity.toDomain(): Card = Card(
    id = id,
    deckId = deckId,
    name = name,
    number = number,
    arcanaType = ArcanaType.valueOf(arcanaType),
    imageResId = imageResId,
    keywords = json.decodeFromString(keywordsJson),
    meaningUpright = meaningUpright,
    meaningReversed = meaningReversed
)

fun Card.toEntity(): CardEntity = CardEntity(
    id = id,
    deckId = deckId,
    name = name,
    number = number,
    arcanaType = arcanaType.name,
    imageResId = imageResId,
    keywordsJson = json.encodeToString(keywords),
    meaningUpright = meaningUpright,
    meaningReversed = meaningReversed
)
