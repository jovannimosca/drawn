package com.example.drawn.data.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.example.drawn.data.database.entity.ReadingTagCrossRef
import kotlinx.coroutines.flow.Flow

@Dao
interface ReadingTagDao {
    @Insert
    suspend fun insert(crossRef: ReadingTagCrossRef)

    @Delete
    suspend fun delete(crossRef: ReadingTagCrossRef)

    @Query("SELECT tagId FROM reading_tags WHERE readingId = :readingId")
    fun observeTagIdsForReading(readingId: Long): Flow<List<Long>>

    @Query("SELECT readingId FROM reading_tags WHERE tagId = :tagId")
    fun observeReadingIdsForTag(tagId: Long): Flow<List<Long>>

    @Query("DELETE FROM reading_tags WHERE readingId = :readingId")
    suspend fun deleteAllForReading(readingId: Long)

    @Query("DELETE FROM reading_tags WHERE tagId = :tagId")
    suspend fun deleteAllForTag(tagId: Long)
}