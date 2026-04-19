package com.example.drawn.data.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.drawn.data.database.entity.CustomCardEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CustomCardDao {
    @Query("SELECT * FROM custom_cards WHERE deckId = :deckId ORDER BY sortOrder, name")
    fun observeCardsForDeck(deckId: Long): Flow<List<CustomCardEntity>>

    @Query("SELECT * FROM custom_cards WHERE id = :id")
    suspend fun getCardById(id: Long): CustomCardEntity?

    @Insert
    suspend fun insert(card: CustomCardEntity): Long

    @Update
    suspend fun update(card: CustomCardEntity)

    @Delete
    suspend fun delete(card: CustomCardEntity)

    @Query("DELETE FROM custom_cards WHERE deckId = :deckId")
    suspend fun deleteByDeck(deckId: Long)
}