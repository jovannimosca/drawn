package com.example.drawn.ui.settings

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Divider
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.drawn.BuildConfig
import com.example.drawn.ui.components.NebulaBackground

@Composable
fun SettingsScreen(
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxSize()) {
        NebulaBackground(modifier = Modifier.fillMaxSize())

        Text(
            text = "Settings",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(16.dp)
        )

        // App Info section
        Text(
            text = "App Info",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
        )

        ListItem(
            headlineContent = { Text("Version") },
            supportingContent = { Text("${BuildConfig.VERSION_CODE} (${BuildConfig.VERSION_NAME})") }
        )

        HorizontalDivider()

        // Backup & Restore section  
        Text(
            text = "Backup & Restore",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
        )

        ListItem(
            headlineContent = { Text("Backup") },
            supportingContent = { Text("Export readings and custom decks") }
        )

        ListItem(
            headlineContent = { Text("Restore") },
            supportingContent = { Text("Import from backup file") }
        )
    }
}