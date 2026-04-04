package com.example.drawn.data.repository

import androidx.room.Transaction
import com.example.drawn.data.database.dao.ReadingCardDao
import com.example.drawn.data.database.dao.ReadingDao
import com.example.drawn.data.database.dao.ReadingPhotoDao
import com.example.drawn.data.database.entity.toDomain
import com.example.drawn.data.database.entity.toEntity
import com.example.drawn.domain.model.Reading
import com.example.drawn.domain.model.ReadingCard
import com.example.drawn.domain.model.ReadingDetail
import com.example.drawn.domain.model.ReadingPhoto
import com.example.drawn.data.database.entity.ReadingPhotoEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map

class ReadingRepository(
    private val readingDao: ReadingDao,
    private val readingCardDao: ReadingCardDao,
    private val readingPhotoDao: ReadingPhotoDao
) {
    fun observeAllReadings(): Flow<List<Reading>> =
        readingDao.observeAllReadings()
            .map { entities -> entities.map { it.toDomain() } }
            .distinctUntilChanged()

    fun observeReadingWithDetails(id: Long): Flow<ReadingDetail?> =
        combine(
            readingDao.observeReadingById(id),
            readingCardDao.observeCardsForReading(id),
            readingPhotoDao.observePhotosForReading(id)
        ) { readingEntity, cardEntities, photoEntities ->
            readingEntity?.let {
                ReadingDetail(
                    reading = it.toDomain(),
                    cards = cardEntities.map { card -> card.toDomain() },
                    photos = photoEntities.map { photo -> photo.toDomain() }
                )
            }
        }
            .distinctUntilChanged()

    @Transaction
    suspend fun createReading(reading: Reading, cards: List<ReadingCard>): Long {
        val readingId = readingDao.insert(reading.toEntity())
        val readingCards = cards.map { it.copy(readingId = readingId).toEntity() }
        readingCardDao.insertAll(readingCards)
        return readingId
    }

    @Transaction
    suspend fun updateReading(reading: Reading, cards: List<ReadingCard>) {
        readingDao.update(reading.toEntity())
        val existingCards = readingCardDao.getCardsForReading(reading.id)
        existingCards.forEach { readingCardDao.delete(it) }
        readingCardDao.insertAll(cards.map { it.toEntity() })
    }

    @Transaction
    suspend fun deleteReading(id: Long) {
        val reading = readingDao.getReadingById(id)
        reading?.let { readingDao.delete(it) }
    }

    suspend fun addPhotoToReading(readingId: Long, photoUri: String): Long {
        val photoEntity = ReadingPhotoEntity(
            readingId = readingId,
            photoUri = photoUri
        )
        return readingPhotoDao.insert(photoEntity)
    }

    suspend fun removePhotoFromReading(photoId: Long) {
        val photo = readingPhotoDao.getPhotoById(photoId)
        photo?.let { readingPhotoDao.delete(it) }
    }
}
