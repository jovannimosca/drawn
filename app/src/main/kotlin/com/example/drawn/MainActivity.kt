package com.example.drawn

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.drawn.ui.navigation.DrawnNavHost
import com.example.drawn.ui.readinglist.ReadingListViewModel
import com.example.drawn.ui.theme.DrawnTheme
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var readingListViewModel: ReadingListViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        setContent {
            DrawnTheme {
                DrawnNavHost(readingListViewModel = readingListViewModel)
            }
        }
    }
}
