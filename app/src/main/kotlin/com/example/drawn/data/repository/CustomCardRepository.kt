package com.example.drawn.data.repository

import com.example.drawn.data.database.dao.CustomCardDao
import com.example.drawn.data.database.entity.toDomain
import com.example.drawn.data.database.entity.toEntity
import com.example.drawn.domain.model.CustomCard
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map

class CustomCardRepository(
    private val customCardDao: CustomCardDao
) {
    fun observeCardsForDeck(deckId: Long): Flow<List<CustomCard>> =
        customCardDao.observeCardsForDeck(deckId)
            .map { entities -> entities.map { it.toDomain() } }
            .distinctUntilChanged()

    suspend fun insert(card: CustomCard): Long =
        customCardDao.insert(card.toEntity())

    suspend fun update(card: CustomCard) {
        customCardDao.update(card.toEntity())
    }

    suspend fun delete(card: CustomCard) {
        customCardDao.delete(card.toEntity())
    }
}