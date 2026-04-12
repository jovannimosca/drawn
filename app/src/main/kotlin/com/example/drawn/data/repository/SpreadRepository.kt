package com.example.drawn.data.repository

import com.example.drawn.data.database.dao.SpreadDao
import com.example.drawn.data.database.entity.toDomain
import com.example.drawn.data.database.entity.toEntity
import com.example.drawn.domain.model.Spread
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map

class SpreadRepository(
    private val spreadDao: SpreadDao
) {
    fun observeAllSpreads(): Flow<List<Spread>> =
        spreadDao.observeAllSpreads()
            .map { entities -> entities.map { it.toDomain() } }
            .distinctUntilChanged()

    suspend fun getSpreadById(id: Long): Spread? =
        spreadDao.getSpreadById(id)?.toDomain()

    suspend fun insertAllSpreads(spreads: List<Spread>) {
        spreadDao.insertAll(spreads.map { it.toEntity() })
    }
}
