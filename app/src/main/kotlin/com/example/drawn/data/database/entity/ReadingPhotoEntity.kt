package com.example.drawn.data.database.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.example.drawn.domain.model.ReadingPhoto

@Entity(
    tableName = "reading_photos",
    foreignKeys = [
        ForeignKey(
            entity = ReadingEntity::class,
            parentColumns = ["id"],
            childColumns = ["readingId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("readingId")]
)
data class ReadingPhotoEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val readingId: Long,
    val photoUri: String,
    val caption: String? = null
)

fun ReadingPhotoEntity.toDomain(): ReadingPhoto = ReadingPhoto(
    id = id,
    readingId = readingId,
    photoUri = photoUri,
    caption = caption
)

fun ReadingPhoto.toEntity(): ReadingPhotoEntity = ReadingPhotoEntity(
    id = id,
    readingId = readingId,
    photoUri = photoUri,
    caption = caption
)
