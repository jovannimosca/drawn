package com.example.drawn.data.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.drawn.data.database.entity.ReadingEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ReadingDao {
    @Query("SELECT * FROM readings ORDER BY createdAt DESC")
    fun observeAllReadings(): Flow<List<ReadingEntity>>

    @Query("SELECT * FROM readings WHERE id = :id")
    fun observeReadingById(id: Long): Flow<ReadingEntity?>

    @Query("SELECT * FROM readings WHERE id = :id")
    suspend fun getReadingById(id: Long): ReadingEntity?

    @Insert
    suspend fun insert(reading: ReadingEntity): Long

    @Update
    suspend fun update(reading: ReadingEntity)

    @Delete
    suspend fun delete(reading: ReadingEntity)
}
