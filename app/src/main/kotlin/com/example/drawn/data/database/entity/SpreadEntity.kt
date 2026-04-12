package com.example.drawn.data.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.drawn.domain.model.Spread
import com.example.drawn.domain.model.SpreadPosition
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

@Entity(tableName = "spreads")
data class SpreadEntity(
    @PrimaryKey
    val id: Long,
    val name: String,
    val description: String,
    val positionsJson: String
)

@Serializable
data class SpreadPositionDto(
    val name: String,
    val order: Int,
    val meaning: String
)

private val json = Json { ignoreUnknownKeys = true }

fun SpreadEntity.toDomain(): Spread = Spread(
    id = id,
    name = name,
    description = description,
    positions = json.decodeFromString<List<SpreadPositionDto>>(positionsJson).map {
        SpreadPosition(name = it.name, order = it.order, meaning = it.meaning)
    }
)

fun Spread.toEntity(): SpreadEntity = SpreadEntity(
    id = id,
    name = name,
    description = description,
    positionsJson = json.encodeToString(positions.map {
        SpreadPositionDto(name = it.name, order = it.order, meaning = it.meaning)
    })
)
