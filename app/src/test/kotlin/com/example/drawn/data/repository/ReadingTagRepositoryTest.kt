package com.example.drawn.data.repository

import app.cash.turbine.test
import com.example.drawn.data.database.dao.ReadingTagDao
import com.example.drawn.data.database.dao.TagDao
import com.example.drawn.data.database.entity.ReadingTagCrossRef
import com.example.drawn.data.database.entity.TagEntity
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
class ReadingTagRepositoryTest {

    private val tagDao: TagDao = mockk()
    private val readingTagDao: ReadingTagDao = mockk()
    private lateinit var repository: ReadingTagRepository

    private val testTagEntity = TagEntity(
        id = 1L,
        name = "Love",
        color = "#E91E63"
    )

    @BeforeEach
    fun setUp() {
        repository = ReadingTagRepository(tagDao, readingTagDao)
    }

    @Nested
    inner class ObserveTagsForReading {

        @Test
        fun `observeTagsForReading returns tags for reading`() = runTest {
            every { readingTagDao.observeTagIdsForReading(1L) } returns flowOf(listOf(1L))
            coEvery { tagDao.getTagById(1L) } returns testTagEntity

            repository.observeTagsForReading(1L).test {
                val tags = awaitItem()
                assertEquals(1, tags.size)
                assertEquals("Love", tags[0].name)
                cancelAndIgnoreRemainingEvents()
            }
        }

        @Test
        fun `observeTagsForReading returns empty when no tags`() = runTest {
            every { readingTagDao.observeTagIdsForReading(1L) } returns flowOf(emptyList())

            repository.observeTagsForReading(1L).test {
                val tags = awaitItem()
                assertEquals(0, tags.size)
                cancelAndIgnoreRemainingEvents()
            }
        }

        @Test
        fun `observeTagsForReading handles null tag IDs gracefully`() = runTest {
            every { readingTagDao.observeTagIdsForReading(1L) } returns flowOf(listOf(1L, 2L))
            coEvery { tagDao.getTagById(1L) } returns testTagEntity
            coEvery { tagDao.getTagById(2L) } returns null

            repository.observeTagsForReading(1L).test {
                val tags = awaitItem()
                assertEquals(1, tags.size)
                cancelAndIgnoreRemainingEvents()
            }
        }
    }

    @Nested
    inner class AddTag {

        @Test
        fun `addTag inserts cross ref`() = runTest {
            coEvery { readingTagDao.insert(any()) } returns Unit

            repository.addTag(1L, 2L)

            coVerify { readingTagDao.insert(ReadingTagCrossRef(1L, 2L)) }
        }
    }

    @Nested
    inner class RemoveTag {

        @Test
        fun `removeTag deletes cross ref`() = runTest {
            coEvery { readingTagDao.delete(any()) } returns Unit

            repository.removeTag(1L, 2L)

            coVerify { readingTagDao.delete(ReadingTagCrossRef(1L, 2L)) }
        }
    }

    @Nested
    inner class ReplaceTags {

        @Test
        fun `replaceTags deletes all then inserts new tags`() = runTest {
            coEvery { readingTagDao.deleteAllForReading(1L) } returns Unit
            coEvery { readingTagDao.insert(any()) } returns Unit

            repository.replaceTags(1L, listOf(1L, 2L, 3L))

            coVerify { readingTagDao.deleteAllForReading(1L) }
            coVerify(atLeast = 3) { readingTagDao.insert(any()) }
        }

        @Test
        fun `replaceTags with empty list clears all tags`() = runTest {
            coEvery { readingTagDao.deleteAllForReading(1L) } returns Unit

            repository.replaceTags(1L, emptyList())

            coVerify { readingTagDao.deleteAllForReading(1L) }
            coVerify(exactly = 0) { readingTagDao.insert(any()) }
        }
    }
}