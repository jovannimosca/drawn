package com.example.drawn.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import com.example.drawn.ui.theme.DarkBackground
import com.example.drawn.ui.theme.DarkPrimary

data class Star(
    val x: Float,
    val y: Float,
    val radius: Float,
    val alpha: Float
)

private val DETERMINISTIC_STARS = listOf(
    Star(0.12f, 0.08f, 1.0f, 0.30f),
    Star(0.25f, 0.15f, 0.8f, 0.20f),
    Star(0.38f, 0.05f, 1.2f, 0.35f),
    Star(0.50f, 0.22f, 0.6f, 0.15f),
    Star(0.65f, 0.10f, 1.0f, 0.28f),
    Star(0.78f, 0.18f, 0.7f, 0.22f),
    Star(0.90f, 0.07f, 1.1f, 0.32f),
    Star(0.05f, 0.30f, 0.9f, 0.25f),
    Star(0.18f, 0.40f, 0.5f, 0.18f),
    Star(0.32f, 0.35f, 1.0f, 0.30f),
    Star(0.45f, 0.28f, 0.7f, 0.20f),
    Star(0.58f, 0.42f, 1.3f, 0.38f),
    Star(0.72f, 0.32f, 0.6f, 0.15f),
    Star(0.85f, 0.45f, 0.9f, 0.27f),
    Star(0.95f, 0.38f, 0.8f, 0.22f),
    Star(0.08f, 0.55f, 1.1f, 0.33f),
    Star(0.22f, 0.60f, 0.6f, 0.16f),
    Star(0.35f, 0.52f, 1.0f, 0.29f),
    Star(0.48f, 0.65f, 0.8f, 0.21f),
    Star(0.62f, 0.58f, 1.2f, 0.36f),
    Star(0.75f, 0.70f, 0.7f, 0.19f),
    Star(0.88f, 0.62f, 0.9f, 0.26f),
    Star(0.98f, 0.55f, 0.5f, 0.14f),
    Star(0.15f, 0.75f, 1.0f, 0.30f),
    Star(0.28f, 0.82f, 0.8f, 0.23f),
    Star(0.42f, 0.78f, 1.1f, 0.34f),
    Star(0.55f, 0.88f, 0.6f, 0.17f),
    Star(0.68f, 0.80f, 0.9f, 0.25f),
    Star(0.82f, 0.92f, 1.0f, 0.28f),
    Star(0.92f, 0.85f, 0.7f, 0.20f),
    Star(0.03f, 0.90f, 1.2f, 0.37f),
    Star(0.10f, 0.95f, 0.5f, 0.15f),
    Star(0.20f, 0.25f, 0.8f, 0.24f),
    Star(0.40f, 0.12f, 1.0f, 0.31f),
    Star(0.52f, 0.50f, 0.7f, 0.18f),
    Star(0.70f, 0.55f, 1.1f, 0.33f),
    Star(0.80f, 0.25f, 0.6f, 0.16f),
    Star(0.60f, 0.75f, 0.9f, 0.27f),
    Star(0.30f, 0.68f, 1.0f, 0.29f),
    Star(0.47f, 0.92f, 0.8f, 0.22f),
    Star(0.73f, 0.88f, 1.3f, 0.39f),
    Star(0.87f, 0.78f, 0.5f, 0.14f),
    Star(0.13f, 0.48f, 0.9f, 0.26f),
    Star(0.33f, 0.90f, 0.7f, 0.19f),
    Star(0.57f, 0.35f, 1.0f, 0.30f),
    Star(0.77f, 0.48f, 0.6f, 0.17f),
    Star(0.93f, 0.68f, 1.1f, 0.34f),
    Star(0.07f, 0.72f, 0.8f, 0.21f),
    Star(0.67f, 0.15f, 0.9f, 0.25f),
    Star(0.83f, 0.05f, 1.2f, 0.36f),
)

@Composable
fun NebulaBackground(modifier: Modifier = Modifier) {
    val density = LocalDensity.current
    val stars = remember { DETERMINISTIC_STARS }

    Canvas(modifier = modifier) {
        val width = size.width
        val height = size.height
        val center = Offset(width / 2f, height / 2f)
        val radius = maxOf(width, height)

        drawRect(color = DarkBackground)

        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(
                    DarkPrimary.copy(alpha = 0.08f),
                    DarkPrimary.copy(alpha = 0.03f),
                    Color.Transparent
                ),
                center = center,
                radius = radius * 0.7f
            ),
            center = center,
            radius = radius
        )

        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(
                    Color(0xFF7B1FA2).copy(alpha = 0.06f),
                    Color.Transparent
                ),
                center = Offset(width * 0.3f, height * 0.4f),
                radius = radius * 0.4f
            ),
            center = Offset(width * 0.3f, height * 0.4f),
            radius = radius * 0.5f
        )

        drawStars(stars, width, height, density)
    }
}

private fun DrawScope.drawStars(stars: List<Star>, width: Float, height: Float, density: androidx.compose.ui.unit.Density) {
    val starRadiusDp = with(density) { 1.dp.toPx() }
    stars.forEach { star ->
        val x = star.x * width
        val y = star.y * height
        val radius = star.radius * starRadiusDp
        val color = Color.White.copy(alpha = star.alpha)
        drawCircle(
            color = color,
            radius = radius,
            center = Offset(x, y)
        )
    }
}
