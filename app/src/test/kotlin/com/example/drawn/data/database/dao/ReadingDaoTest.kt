package com.example.drawn.data.database.dao

import app.cash.turbine.test
import com.example.drawn.data.database.DatabaseTest
import com.example.drawn.data.database.entity.ReadingEntity
import com.example.drawn.data.database.entity.SpreadEntity
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.robolectric.annotation.Config
import tech.apter.junit.jupiter.robolectric.RobolectricExtension

/**
 * ReadingDao tests using Room in-memory database.
 * Tests CRUD operations and Flow-based observation.
 */
@ExtendWith(RobolectricExtension::class)
@Config(sdk = [33])
class ReadingDaoTest : DatabaseTest() {

    private val readingDao by lazy { db.readingDao() }
    private val spreadDao by lazy { db.spreadDao() }

    @BeforeEach
    fun insertTestSpread() {
        val spread = SpreadEntity(
            id = 1L,
            name = "Test Spread",
            description = "A test spread",
            positionsJson = "[]"
        )
        kotlinx.coroutines.runBlocking {
            spreadDao.insertAll(listOf(spread))
        }
    }

    private fun createReadingEntity(
        id: Long = 0,
        title: String = "Test Reading",
        spreadId: Long = 1L,
        createdAt: Long = 1000L,
        notes: String? = null
    ) = ReadingEntity(
        id = id,
        title = title,
        spreadId = spreadId,
        createdAt = createdAt,
        notes = notes
    )

    @Test
    fun `insert reading and observeAllReadings emits it`() = runTest {
        val reading = createReadingEntity(title = "First Reading", createdAt = 1000L)

        readingDao.observeAllReadings().test {
            val initial = awaitItem()
            assertTrue(initial.isEmpty())

            readingDao.insert(reading)

            val afterInsert = awaitItem()
            assertEquals(1, afterInsert.size)
            assertEquals("First Reading", afterInsert[0].title)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `insert multiple readings ordered by createdAt DESC`() = runTest {
        val reading1 = createReadingEntity(title = "Older", createdAt = 1000L)
        val reading2 = createReadingEntity(title = "Newer", createdAt = 2000L)

        readingDao.insert(reading1)
        readingDao.insert(reading2)

        readingDao.observeAllReadings().test {
            val items = awaitItem()
            assertEquals(2, items.size)
            assertEquals("Newer", items[0].title)
            assertEquals("Older", items[1].title)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `getReadingById returns correct entity`() = runTest {
        val reading = createReadingEntity(title = "Lookup Test", createdAt = 1000L)
        val id = readingDao.insert(reading)

        val result = readingDao.getReadingById(id)

        assertNotNull(result)
        assertEquals(id, result!!.id)
        assertEquals("Lookup Test", result.title)
        assertEquals(1000L, result.createdAt)
    }

    @Test
    fun `getReadingById with non-existent ID returns null`() = runTest {
        val result = readingDao.getReadingById(999L)
        assertNull(result)
    }

    @Test
    fun `observeReadingById emits entity`() = runTest {
        val reading = createReadingEntity(title = "Observable", createdAt = 1000L)
        val id = readingDao.insert(reading)

        readingDao.observeReadingById(id).test {
            val entity = awaitItem()
            assertNotNull(entity)
            assertEquals("Observable", entity!!.title)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `update entity and observeReadingById emits updated version`() = runTest {
        val reading = createReadingEntity(title = "Original", createdAt = 1000L)
        val id = readingDao.insert(reading)

        readingDao.observeReadingById(id).test {
            val original = awaitItem()
            assertEquals("Original", original!!.title)

            val updated = original.copy(title = "Updated Title", notes = "Some notes")
            readingDao.update(updated)

            val afterUpdate = awaitItem()
            assertEquals("Updated Title", afterUpdate!!.title)
            assertEquals("Some notes", afterUpdate.notes)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `delete entity and observeAllReadings no longer emits it`() = runTest {
        val reading = createReadingEntity(title = "To Delete", createdAt = 1000L)
        val id = readingDao.insert(reading)

        readingDao.observeAllReadings().test {
            val beforeDelete = awaitItem()
            assertEquals(1, beforeDelete.size)

            val entity = readingDao.getReadingById(id)!!
            readingDao.delete(entity)

            val afterDelete = awaitItem()
            assertTrue(afterDelete.isEmpty())
            cancelAndIgnoreRemainingEvents()
        }
    }
}
