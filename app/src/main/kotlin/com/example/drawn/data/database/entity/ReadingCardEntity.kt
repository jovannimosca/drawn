package com.example.drawn.data.database.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.example.drawn.domain.model.ReadingCard

@Entity(
    tableName = "reading_cards",
    foreignKeys = [
        ForeignKey(
            entity = ReadingEntity::class,
            parentColumns = ["id"],
            childColumns = ["readingId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = CardEntity::class,
            parentColumns = ["id"],
            childColumns = ["cardId"],
            onDelete = ForeignKey.RESTRICT
        )
    ],
    indices = [Index("readingId"), Index("cardId")]
)
data class ReadingCardEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val readingId: Long,
    val cardId: Long,
    val positionName: String,
    val positionOrder: Int,
    val interpretation: String? = null,
    val isReversed: Boolean = false
)

fun ReadingCardEntity.toDomain(): ReadingCard = ReadingCard(
    id = id,
    readingId = readingId,
    cardId = cardId,
    positionName = positionName,
    positionOrder = positionOrder,
    interpretation = interpretation,
    isReversed = isReversed
)

fun ReadingCard.toEntity(): ReadingCardEntity = ReadingCardEntity(
    id = id,
    readingId = readingId,
    cardId = cardId,
    positionName = positionName,
    positionOrder = positionOrder,
    interpretation = interpretation,
    isReversed = isReversed
)
