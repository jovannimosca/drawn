package com.example.drawn.data.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.drawn.data.database.entity.DeckEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface DeckDao {
    @Query("SELECT * FROM decks ORDER BY name")
    fun observeAllDecks(): Flow<List<DeckEntity>>

    @Query("SELECT * FROM decks WHERE isCustom = 0")
    fun observeBuiltInDecks(): Flow<List<DeckEntity>>

    @Insert
    suspend fun insert(deck: DeckEntity): Long

    @Update
    suspend fun update(deck: DeckEntity)

    @Delete
    suspend fun delete(deck: DeckEntity)
}
