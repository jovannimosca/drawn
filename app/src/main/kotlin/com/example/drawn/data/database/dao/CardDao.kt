package com.example.drawn.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.drawn.data.database.entity.CardEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CardDao {
    @Query("SELECT * FROM cards ORDER BY arcanaType, number")
    fun observeAllCards(): Flow<List<CardEntity>>

    @Query("SELECT * FROM cards WHERE id = :id")
    fun observeCardById(id: Long): Flow<CardEntity?>

    @Query("SELECT * FROM cards WHERE deckId = :deckId ORDER BY number")
    fun observeCardsByDeck(deckId: Long): Flow<List<CardEntity>>

    @Query("SELECT * FROM cards WHERE id IN (:ids)")
    fun observeCardsByIds(ids: List<Long>): Flow<List<CardEntity>>

    @Query("SELECT * FROM cards WHERE id IN (:ids)")
    suspend fun getCardsByIds(ids: List<Long>): List<CardEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(cards: List<CardEntity>)
}
