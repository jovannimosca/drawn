package com.example.drawn.data.repository

import app.cash.turbine.test
import com.example.drawn.data.database.dao.TagDao
import com.example.drawn.data.database.entity.TagEntity
import com.example.drawn.domain.model.Tag
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
import io.mockk.junit5.MockKExtension

@ExtendWith(io.mockk.junit5.MockKExtension::class)
class TagRepositoryTest {

    private val tagDao: TagDao = mockk()
    private lateinit var repository: TagRepository

    private val testTagEntity = TagEntity(
        id = 1L,
        name = "Love",
        color = "#E91E63"
    )

    private val testTag = Tag(
        id = 1L,
        name = "Love",
        color = "#E91E63"
    )

    @BeforeEach
    fun setUp() {
        repository = TagRepository(tagDao)
    }

    @Nested
    inner class ObserveAllTags {

        @Test
        fun `observeAllTags returns flow of tags`() = runTest {
            every { tagDao.observeAllTags() } returns flowOf(listOf(testTagEntity))

            repository.observeAllTags().test {
                val result = awaitItem()
                assertEquals(1, result.size)
                assertEquals("Love", result[0].name)
                cancelAndIgnoreRemainingEvents()
            }
        }

        @Test
        fun `observeAllTags emits empty list when no tags`() = runTest {
            every { tagDao.observeAllTags() } returns flowOf(emptyList())

            repository.observeAllTags().test {
                val result = awaitItem()
                assertEquals(0, result.size)
                cancelAndIgnoreRemainingEvents()
            }
        }
    }

    @Nested
    inner class CreateTag {

        @Test
        fun `create inserts tag and returns id`() = runTest {
            coEvery { tagDao.insert(any()) } returns 1L

            val result = repository.create(testTag)

            assertEquals(1L, result)
            coVerify { tagDao.insert(any()) }
        }

        @Test
        fun `create converts domain to entity before inserting`() = runTest {
            coEvery { tagDao.insert(any()) } returns 5L

            val result = repository.create(testTag.copy(id = 0))

            assertEquals(5L, result)
        }
    }

    @Nested
    inner class UpdateTag {

        @Test
        fun `update calls dao update`() = runTest {
            coEvery { tagDao.update(any()) } returns Unit

            repository.update(testTag)

            coVerify { tagDao.update(any()) }
        }
    }

    @Nested
    inner class DeleteTag {

        @Test
        fun `delete calls dao deleteById`() = runTest {
            coEvery { tagDao.deleteById(1L) } returns Unit

            repository.delete(1L)

            coVerify { tagDao.deleteById(1L) }
        }

        @Test
        fun `delete handles non-existent tag`() = runTest {
            coEvery { tagDao.deleteById(99L) } returns Unit

            repository.delete(99L)

            coVerify { tagDao.deleteById(99L) }
        }
    }
}