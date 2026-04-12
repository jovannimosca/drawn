package com.example.drawn.data.database.dao

import app.cash.turbine.test
import com.example.drawn.data.database.DatabaseTest
import com.example.drawn.data.database.entity.ReadingEntity
import com.example.drawn.data.database.entity.ReadingPhotoEntity
import com.example.drawn.data.database.entity.SpreadEntity
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.robolectric.annotation.Config
import tech.apter.junit.jupiter.robolectric.RobolectricExtension

/**
 * ReadingPhotoDao tests using Room in-memory database.
 */
@ExtendWith(RobolectricExtension::class)
@Config(sdk = [33])
class ReadingPhotoDaoTest : DatabaseTest() {

    private val readingPhotoDao by lazy { db.readingPhotoDao() }
    private val readingDao by lazy { db.readingDao() }
    private val spreadDao by lazy { db.spreadDao() }

    @BeforeEach
    fun insertParentEntities() {
        kotlinx.coroutines.runBlocking {
            val spread = SpreadEntity(id = 1L, name = "Test Spread", description = "", positionsJson = "[]")
            spreadDao.insertAll(listOf(spread))

            val reading = ReadingEntity(id = 1L, title = "Test Reading", spreadId = 1L, createdAt = 1000L)
            readingDao.insert(reading)
        }
    }

    private fun createReadingPhotoEntity(
        id: Long = 0,
        readingId: Long = 1L,
        photoUri: String = "content://test/photo.jpg",
        caption: String? = null
    ) = ReadingPhotoEntity(
        id = id,
        readingId = readingId,
        photoUri = photoUri,
        caption = caption
    )

    @Test
    fun `insert photo and observePhotosForReading emits it`() = runTest {
        val photo = createReadingPhotoEntity(readingId = 1L, photoUri = "content://test/photo.jpg")

        readingPhotoDao.observePhotosForReading(1L).test {
            val initial = awaitItem()
            assertTrue(initial.isEmpty())

            readingPhotoDao.insert(photo)

            val afterInsert = awaitItem()
            assertEquals(1, afterInsert.size)
            assertEquals("content://test/photo.jpg", afterInsert[0].photoUri)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `insert returns generated ID greater than 0`() = runTest {
        val photo = createReadingPhotoEntity()

        val id = readingPhotoDao.insert(photo)

        assertTrue(id > 0)
    }

    @Test
    fun `getPhotoById returns correct entity`() = runTest {
        val photo = createReadingPhotoEntity(photoUri = "content://test/specific.jpg", caption = "Test caption")
        val id = readingPhotoDao.insert(photo)

        val result = readingPhotoDao.getPhotoById(id)

        assertNotNull(result)
        assertEquals(id, result!!.id)
        assertEquals("content://test/specific.jpg", result.photoUri)
        assertEquals("Test caption", result.caption)
    }

    @Test
    fun `delete photo and observePhotosForReading no longer emits it`() = runTest {
        val photo = createReadingPhotoEntity(readingId = 1L)
        val id = readingPhotoDao.insert(photo)

        readingPhotoDao.observePhotosForReading(1L).test {
            val beforeDelete = awaitItem()
            assertEquals(1, beforeDelete.size)

            val entity = readingPhotoDao.getPhotoById(id)!!
            readingPhotoDao.delete(entity)

            val afterDelete = awaitItem()
            assertTrue(afterDelete.isEmpty())
            cancelAndIgnoreRemainingEvents()
        }
    }
}
