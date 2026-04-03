package com.example.drawn.data.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.example.drawn.data.database.entity.ReadingCardEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ReadingCardDao {
    @Query("SELECT * FROM reading_cards WHERE readingId = :readingId ORDER BY positionOrder")
    fun observeCardsForReading(readingId: Long): Flow<List<ReadingCardEntity>>

    @Insert
    suspend fun insertAll(cards: List<ReadingCardEntity>)

    @Delete
    suspend fun delete(card: ReadingCardEntity)
}
