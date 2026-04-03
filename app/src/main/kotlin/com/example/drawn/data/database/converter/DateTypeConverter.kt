package com.example.drawn.data.database.converter

import androidx.room.TypeConverter
import com.example.drawn.domain.model.ArcanaType
import java.time.Instant

class DateTypeConverter {
    @TypeConverter
    fun fromTimestamp(value: Long?): Instant? = value?.let { Instant.ofEpochMilli(it) }

    @TypeConverter
    fun dateToTimestamp(date: Instant?): Long? = date?.toEpochMilli()
}
