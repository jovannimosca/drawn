package com.example.drawn.data.repository

import com.example.drawn.data.database.dao.CardDao
import com.example.drawn.data.database.dao.DeckDao
import com.example.drawn.data.database.entity.toDomain
import com.example.drawn.data.database.entity.toEntity
import com.example.drawn.domain.model.Card
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map

class CardRepository(
    private val cardDao: CardDao,
    private val deckDao: DeckDao
) {
    fun observeAllCards(): Flow<List<Card>> =
        cardDao.observeAllCards()
            .map { entities -> entities.map { it.toDomain() } }
            .distinctUntilChanged()

    fun observeCardById(id: Long): Flow<Card?> =
        cardDao.observeCardById(id)
            .map { it?.toDomain() }
            .distinctUntilChanged()

    fun observeCardsByDeck(deckId: Long): Flow<List<Card>> =
        cardDao.observeCardsByDeck(deckId)
            .map { entities -> entities.map { it.toDomain() } }
            .distinctUntilChanged()

    suspend fun insertAllCards(cards: List<Card>) {
        cardDao.insertAll(cards.map { it.toEntity() })
    }
}
