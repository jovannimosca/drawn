package com.example.drawn.data.database.dao

import app.cash.turbine.test
import com.example.drawn.data.database.DatabaseTest
import com.example.drawn.data.database.entity.SpreadEntity
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.robolectric.annotation.Config
import tech.apter.junit.jupiter.robolectric.RobolectricExtension

/**
 * SpreadDao tests using Room in-memory database.
 */
@ExtendWith(RobolectricExtension::class)
@Config(sdk = [33])
class SpreadDaoTest : DatabaseTest() {

    private val spreadDao by lazy { db.spreadDao() }

    private fun createSpreadEntity(
        id: Long,
        name: String = "Test Spread",
        description: String = "A test spread",
        positionsJson: String = "[{\"name\":\"Position 1\",\"order\":0,\"meaning\":\"Test\"}]"
    ) = SpreadEntity(
        id = id,
        name = name,
        description = description,
        positionsJson = positionsJson
    )

    @Test
    fun `insert spread and observeAllSpreads emits it`() = runTest {
        val spread = createSpreadEntity(id = 1L, name = "Celtic Cross")

        spreadDao.insertAll(listOf(spread))

        spreadDao.observeAllSpreads().test {
            val items = awaitItem()
            assertEquals(1, items.size)
            assertEquals("Celtic Cross", items[0].name)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `getSpreadById returns correct entity`() = runTest {
        val spread = createSpreadEntity(id = 1L, name = "Three Card")
        spreadDao.insertAll(listOf(spread))

        val result = spreadDao.getSpreadById(1L)

        assertNotNull(result)
        assertEquals("Three Card", result!!.name)
    }

    @Test
    fun `observeSpreadById emits entity`() = runTest {
        val spread = createSpreadEntity(id = 1L, name = "Past Present Future")
        spreadDao.insertAll(listOf(spread))

        spreadDao.observeSpreadById(1L).test {
            val entity = awaitItem()
            assertNotNull(entity)
            assertEquals("Past Present Future", entity!!.name)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `insertAll with OnConflictStrategy REPLACE updates existing spreads`() = runTest {
        val original = createSpreadEntity(id = 1L, name = "Original Name")
        spreadDao.insertAll(listOf(original))

        val updated = createSpreadEntity(id = 1L, name = "Updated Name")
        spreadDao.insertAll(listOf(updated))

        spreadDao.observeSpreadById(1L).test {
            val entity = awaitItem()
            assertEquals("Updated Name", entity!!.name)
            cancelAndIgnoreRemainingEvents()
        }
    }
}
