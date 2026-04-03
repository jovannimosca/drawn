package com.example.drawn.ui.navigation

import kotlinx.serialization.Serializable

@Serializable
object ReadingList

@Serializable
data class ReadingDetail(val readingId: Long)
