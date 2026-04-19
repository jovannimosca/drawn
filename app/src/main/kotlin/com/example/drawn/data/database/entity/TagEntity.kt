package com.example.drawn.data.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.drawn.domain.model.Tag

@Entity(tableName = "tags")
data class TagEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val color: String? = null
)

fun TagEntity.toDomain(): Tag = Tag(
    id = id,
    name = name,
    color = color
)

fun Tag.toEntity(): TagEntity = TagEntity(
    id = id,
    name = name,
    color = color
)