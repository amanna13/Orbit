package com.orbit.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

fun Modifier.glassEffect(roundedCorner: Dp) = this
    .background(
        brush = Brush.verticalGradient(
            colors = listOf(
                Color.White.copy(alpha = 0.25f), Color.White.copy(alpha = 0.15f)
            )
        )
    )
    .border(
        width = 1.dp, brush = Brush.verticalGradient(
            colors = listOf(
                Color.White.copy(alpha = 0.4f), Color.White.copy(alpha = 0.1f)
            )
        ), shape = RoundedCornerShape(roundedCorner)
    )

fun Modifier.darkGlassEffect() = this
    .background(
        brush = Brush.verticalGradient(
            colors = listOf(
                Color.Black.copy(alpha = 0.3f), Color.Black.copy(alpha = 0.1f)
            )
        )
    )
    .border(
        width = 1.dp, brush = Brush.verticalGradient(
            colors = listOf(
                Color.White.copy(alpha = 0.2f), Color.White.copy(alpha = 0.05f)
            )
        ), shape = RoundedCornerShape(20.dp)
    )