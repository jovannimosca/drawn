package com.example.drawn

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.example.drawn.ui.navigation.BottomTab
import com.example.drawn.ui.navigation.DrawnNavHost
import com.example.drawn.ui.navigation.ReadingsTab
import com.example.drawn.ui.navigation.bottomTabs
import com.example.drawn.ui.theme.DarkPrimary
import com.example.drawn.ui.theme.DarkSecondary
import com.example.drawn.ui.theme.DrawnTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        setContent {
            DrawnTheme {
                DrawnApp()
            }
        }
    }
}

@Composable
fun DrawnApp() {
    val selectedTab = remember { mutableStateOf<BottomTab>(ReadingsTab) }

    Scaffold(
        bottomBar = {
            DrawnBottomNavigation(
                selectedTab = selectedTab,
                tabs = bottomTabs
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            DrawnNavHost(selectedTab = selectedTab)
        }
    }
}

@Composable
fun DrawnBottomNavigation(
    selectedTab: androidx.compose.runtime.MutableState<BottomTab>,
    tabs: List<BottomTab>
) {
    NavigationBar(
        containerColor = MaterialTheme.colorScheme.surface
    ) {
        tabs.forEach { tab ->
            val selected = selectedTab.value == tab
            NavigationBarItem(
                icon = {
                    Icon(
                        imageVector = tab.icon,
                        contentDescription = tab.label
                    )
                },
                label = { Text(tab.label) },
                selected = selected,
                onClick = { selectedTab.value = tab },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = DarkSecondary,
                    selectedTextColor = DarkSecondary,
                    indicatorColor = Color.Transparent
                )
            )
        }
    }
}