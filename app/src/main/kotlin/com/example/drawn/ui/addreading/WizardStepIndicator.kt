package com.example.drawn.ui.addreading

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import com.example.drawn.ui.theme.DarkPrimary
import com.example.drawn.ui.theme.DarkSurfaceVariant

@Composable
fun WizardStepIndicator(
    currentStep: AddReadingStep,
    modifier: Modifier = Modifier
) {
    val steps = AddReadingStep.entries.toTypedArray()
    val currentIndex = steps.indexOf(currentStep)

    val stepLabels = listOf("Spread", "Cards", "Notes")

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterHorizontally)
    ) {
        steps.forEachIndexed { index, step ->
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                val isActive = step == currentStep
                val isCompleted = index < currentIndex

                val indicatorColor = when {
                    isActive -> DarkPrimary
                    isCompleted -> DarkPrimary
                    else -> DarkSurfaceVariant
                }

                if (isActive || isCompleted) {
                    Canvas(
                        modifier = Modifier.size(12.dp)
                    ) {
                        drawCircle(color = indicatorColor)
                    }
                } else {
                    Canvas(
                        modifier = Modifier.size(12.dp)
                    ) {
                        drawCircle(
                            color = indicatorColor,
                            style = Stroke(width = 2.dp.toPx())
                        )
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = stepLabels[index],
                    style = MaterialTheme.typography.labelLarge,
                    color = if (isActive) DarkPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
