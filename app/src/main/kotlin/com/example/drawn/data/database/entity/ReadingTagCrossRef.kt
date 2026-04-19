package com.example.drawn.data.database.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index

@Entity(
    tableName = "reading_tags",
    primaryKeys = ["readingId", "tagId"],
    foreignKeys = [
        ForeignKey(
            entity = ReadingEntity::class,
            parentColumns = ["id"],
            childColumns = ["readingId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = TagEntity::class,
            parentColumns = ["id"],
            childColumns = ["tagId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("readingId"), Index("tagId")]
)
data class ReadingTagCrossRef(
    val readingId: Long,
    val tagId: Long
)