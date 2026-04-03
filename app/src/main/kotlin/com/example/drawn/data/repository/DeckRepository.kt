package com.example.drawn.data.repository

import com.example.drawn.data.database.dao.DeckDao
import com.example.drawn.data.database.entity.toDomain
import com.example.drawn.data.database.entity.toEntity
import com.example.drawn.domain.model.Deck
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map

class DeckRepository(
    private val deckDao: DeckDao
) {
    fun observeAllDecks(): Flow<List<Deck>> =
        deckDao.observeAllDecks()
            .map { entities -> entities.map { it.toDomain() } }
            .distinctUntilChanged()

    fun observeBuiltInDecks(): Flow<List<Deck>> =
        deckDao.observeBuiltInDecks()
            .map { entities -> entities.map { it.toDomain() } }
            .distinctUntilChanged()

    suspend fun createDeck(deck: Deck): Long =
        deckDao.insert(deck.toEntity())

    suspend fun updateDeck(deck: Deck) {
        deckDao.update(deck.toEntity())
    }

    suspend fun deleteDeck(id: Long) {
        deckDao.getDeckById(id)?.let { deckDao.delete(it) }
    }
}
