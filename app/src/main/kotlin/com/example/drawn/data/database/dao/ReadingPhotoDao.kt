package com.example.drawn.data.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.example.drawn.data.database.entity.ReadingPhotoEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ReadingPhotoDao {
    @Query("SELECT * FROM reading_photos WHERE readingId = :readingId")
    fun observePhotosForReading(readingId: Long): Flow<List<ReadingPhotoEntity>>

    @Insert
    suspend fun insert(photo: ReadingPhotoEntity): Long

    @Delete
    suspend fun delete(photo: ReadingPhotoEntity)

    @Query("SELECT * FROM reading_photos WHERE id = :photoId")
    suspend fun getPhotoById(photoId: Long): ReadingPhotoEntity?
}
