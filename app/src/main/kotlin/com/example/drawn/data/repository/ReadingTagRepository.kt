package com.example.drawn.data.repository

import com.example.drawn.data.database.dao.ReadingTagDao
import com.example.drawn.data.database.dao.TagDao
import com.example.drawn.data.database.entity.ReadingTagCrossRef
import com.example.drawn.data.database.entity.toDomain
import com.example.drawn.domain.model.Tag
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map

class ReadingTagRepository(
    private val tagDao: TagDao,
    private val readingTagDao: ReadingTagDao
) {
    fun observeTagsForReading(readingId: Long): Flow<List<Tag>> =
        readingTagDao.observeTagIdsForReading(readingId)
            .map { tagIds ->
                tagIds.mapNotNull { tagId ->
                    tagDao.getTagById(tagId)?.toDomain()
                }
            }
            .distinctUntilChanged()

    suspend fun addTag(readingId: Long, tagId: Long) {
        readingTagDao.insert(ReadingTagCrossRef(readingId, tagId))
    }

    suspend fun removeTag(readingId: Long, tagId: Long) {
        readingTagDao.delete(ReadingTagCrossRef(readingId, tagId))
    }

    suspend fun replaceTags(readingId: Long, tagIds: List<Long>) {
        readingTagDao.deleteAllForReading(readingId)
        tagIds.forEach { tagId ->
            readingTagDao.insert(ReadingTagCrossRef(readingId, tagId))
        }
    }
}