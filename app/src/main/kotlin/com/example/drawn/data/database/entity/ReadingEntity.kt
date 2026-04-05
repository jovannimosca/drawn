package com.example.drawn.data.database.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.example.drawn.domain.model.Reading
import java.time.Instant

@Entity(
    tableName = "readings",
    foreignKeys = [
        ForeignKey(
            entity = SpreadEntity::class,
            parentColumns = ["id"],
            childColumns = ["spreadId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("spreadId")]
)
data class ReadingEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val spreadId: Long,
    val createdAt: Long,
    val notes: String? = null
)

fun ReadingEntity.toDomain(): Reading = Reading(
    id = id,
    title = title,
    spreadId = spreadId,
    createdAt = Instant.ofEpochMilli(createdAt),
    notes = notes
)

fun Reading.toEntity(): ReadingEntity = ReadingEntity(
    id = id,
    title = title,
    spreadId = spreadId,
    createdAt = createdAt.toEpochMilli(),
    notes = notes
)

data class ReadingWithSpread(
    val id: Long,
    val title: String,
    val spreadId: Long,
    val spreadName: String,
    val createdAt: Long,
    val notes: String? = null
)
