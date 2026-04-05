package com.example.drawn.ui.components

import androidx.compose.material3.Divider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.drawn.ui.theme.DarkSecondary
import androidx.compose.ui.graphics.Color

@Composable
fun GoldDivider(
    modifier: Modifier = Modifier,
    thickness: Dp = 1.dp,
    color: Color = DarkSecondary
) {
    Divider(
        modifier = modifier,
        thickness = thickness,
        color = color
    )
}
