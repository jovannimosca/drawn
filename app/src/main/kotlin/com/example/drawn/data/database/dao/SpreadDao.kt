package com.example.drawn.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.drawn.data.database.entity.SpreadEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface SpreadDao {
    @Query("SELECT * FROM spreads ORDER BY id")
    fun observeAllSpreads(): Flow<List<SpreadEntity>>

    @Query("SELECT * FROM spreads WHERE id = :id")
    fun getSpreadById(id: Long): SpreadEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(spreads: List<SpreadEntity>)
}
