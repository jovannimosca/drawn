package com.example.drawn.ui.settings

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.drawn.ui.theme.DrawnTheme
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class SettingsScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun settingsScreen_showsAppInfoSection() {
        composeTestRule.setContent {
            DrawnTheme {
                SettingsScreen()
            }
        }
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithText("Settings").assertIsDisplayed()
        composeTestRule.onNodeWithText("App Info").assertIsDisplayed()
        composeTestRule.onNodeWithText("Version").assertIsDisplayed()
    }

    @Test
    fun settingsScreen_showsBackupSection() {
        composeTestRule.setContent {
            DrawnTheme {
                SettingsScreen()
            }
        }
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithText("Backup & Restore").assertIsDisplayed()
        composeTestRule.onNodeWithText("Backup").assertIsDisplayed()
        composeTestRule.onNodeWithText("Restore").assertIsDisplayed()
    }

    @Test
    fun settingsScreen_showsBackupDescriptions() {
        composeTestRule.setContent {
            DrawnTheme {
                SettingsScreen()
            }
        }
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithText("Export readings and custom decks").assertIsDisplayed()
        composeTestRule.onNodeWithText("Import from backup file").assertIsDisplayed()
    }
}