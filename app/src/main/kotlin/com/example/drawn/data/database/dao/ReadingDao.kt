package com.example.drawn.data.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.drawn.data.database.entity.ReadingEntity
import com.example.drawn.data.database.entity.ReadingWithSpread
import kotlinx.coroutines.flow.Flow

@Dao
interface ReadingDao {
    @Query("SELECT * FROM readings ORDER BY createdAt DESC")
    fun observeAllReadings(): Flow<List<ReadingEntity>>

    @Query("""
        SELECT r.id, r.title, r.spreadId, s.name as spreadName, r.createdAt, r.notes, r.isFavorite
        FROM readings r
        INNER JOIN spreads s ON r.spreadId = s.id
        ORDER BY r.createdAt DESC
    """)
    fun observeAllReadingsWithSpread(): Flow<List<ReadingWithSpread>>

    @Query("SELECT * FROM readings WHERE id = :id")
    fun observeReadingById(id: Long): Flow<ReadingEntity?>

    @Query("SELECT * FROM readings WHERE id = :id")
    suspend fun getReadingById(id: Long): ReadingEntity?

    @Query("SELECT * FROM readings WHERE isFavorite = 1 ORDER BY createdAt DESC")
    fun observeFavorites(): Flow<List<ReadingEntity>>

    @Query("""
        SELECT r.id, r.title, r.spreadId, s.name as spreadName, r.createdAt, r.notes, r.isFavorite
        FROM readings r
        INNER JOIN spreads s ON r.spreadId = s.id
        WHERE r.isFavorite = 1
        ORDER BY r.createdAt DESC
    """)
    fun observeFavoritesWithSpread(): Flow<List<ReadingWithSpread>>

    @Insert
    suspend fun insert(reading: ReadingEntity): Long

    @Update
    suspend fun update(reading: ReadingEntity)

    @Delete
    suspend fun delete(reading: ReadingEntity)
}
