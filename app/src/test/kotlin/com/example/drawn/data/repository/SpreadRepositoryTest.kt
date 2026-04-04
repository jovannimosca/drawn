package com.example.drawn.data.repository

import app.cash.turbine.test
import com.example.drawn.data.database.dao.SpreadDao
import com.example.drawn.data.database.entity.SpreadEntity
import com.example.drawn.data.database.entity.toDomain
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(io.mockk.junit5.MockKExtension::class)
class SpreadRepositoryTest {

    private val spreadDao: SpreadDao = mockk()

    private lateinit var repository: SpreadRepository

    private val testSpreadEntity = SpreadEntity(
        id = 1L, name = "Celtic Cross", description = "A 10-card spread",
        positionsJson = """[{"name":"Present","order":0,"meaning":"Current situation"}]"""
    )

    @BeforeEach
    fun setUp() {
        repository = SpreadRepository(spreadDao)
    }

    @Nested
    inner class ObserveAllSpreads {

        @Test
        fun `observeAllSpreads maps entities to domain models`() = runTest {
            every { spreadDao.observeAllSpreads() } returns flowOf(listOf(testSpreadEntity))

            repository.observeAllSpreads().test {
                val spreads = awaitItem()
                assertEquals(1, spreads.size)
                assertEquals(testSpreadEntity.toDomain(), spreads[0])
                cancelAndIgnoreRemainingEvents()
            }
        }
    }

    @Nested
    inner class GetSpreadById {

        @Test
        fun `getSpreadById calls DAO and maps result`() = runTest {
            every { spreadDao.getSpreadById(1L) } returns testSpreadEntity

            val result = repository.getSpreadById(1L)

            assertEquals(testSpreadEntity.toDomain(), result)
        }

        @Test
        fun `getSpreadById returns null when DAO returns null`() = runTest {
            every { spreadDao.getSpreadById(99L) } returns null

            val result = repository.getSpreadById(99L)

            assertEquals(null, result)
        }
    }

    @Nested
    inner class InsertAllSpreads {

        @Test
        fun `insertAllSpreads maps domain spreads to entities and calls DAO insertAll`() = runTest {
            coEvery { spreadDao.insertAll(any()) } returns Unit

            val spreads = listOf(testSpreadEntity.toDomain())
            repository.insertAllSpreads(spreads)

            coVerify { spreadDao.insertAll(listOf(testSpreadEntity)) }
        }
    }
}
