package com.example.drawn.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Settings
import androidx.compose.ui.graphics.vector.ImageVector
import kotlinx.serialization.Serializable

@Serializable
object ReadingList

@Serializable
object AddReading

@Serializable
data class ReadingDetail(val readingId: Long)

sealed interface BottomTab {
    val route: String
    val label: String
    val icon: ImageVector
}

data object ReadingsTab : BottomTab {
    override val route: String = "readings"
    override val label: String = "Readings"
    override val icon: ImageVector = Icons.Filled.Home
}

data object DecksTab : BottomTab {
    override val route: String = "decks"
    override val label: String = "Decks"
    override val icon: ImageVector = Icons.Filled.List
}

data object SettingsTab : BottomTab {
    override val route: String = "settings"
    override val label: String = "Settings"
    override val icon: ImageVector = Icons.Filled.Settings
}

val bottomTabs = listOf(ReadingsTab, DecksTab, SettingsTab)
