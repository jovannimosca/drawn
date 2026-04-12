package com.example.drawn.data.database.converter

import androidx.room.TypeConverter
import com.example.drawn.domain.model.ArcanaType

class ArcanaTypeConverter {
    @TypeConverter
    fun fromArcanaType(value: String?): ArcanaType =
        value?.let { ArcanaType.valueOf(it) } ?: ArcanaType.MAJOR

    @TypeConverter
    fun arcanaTypeToString(type: ArcanaType?): String = type?.name ?: ArcanaType.MAJOR.name
}
