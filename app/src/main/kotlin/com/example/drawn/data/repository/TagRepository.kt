package com.example.drawn.data.repository

import com.example.drawn.data.database.dao.TagDao
import com.example.drawn.data.database.entity.toDomain
import com.example.drawn.data.database.entity.toEntity
import com.example.drawn.domain.model.Tag
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map

class TagRepository(
    private val tagDao: TagDao
) {
    fun observeAllTags(): Flow<List<Tag>> =
        tagDao.observeAllTags()
            .map { entities -> entities.map { it.toDomain() } }
            .distinctUntilChanged()

    suspend fun create(tag: Tag): Long =
        tagDao.insert(tag.toEntity())

    suspend fun update(tag: Tag) {
        tagDao.update(tag.toEntity())
    }

    suspend fun delete(tagId: Long) {
        tagDao.deleteById(tagId)
    }
}