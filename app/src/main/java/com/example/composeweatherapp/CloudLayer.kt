package com.example.composeweatherapp

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope

fun defaultCloudPuffs(scale: Float = 1f): List<CloudPuff> = listOf(
    CloudPuff(Offset(-50f, -40f), 90f * scale, 0.85f),
    CloudPuff(Offset(-10f, -50f), 100f * scale, 1.0f),
    CloudPuff(Offset(10f, -10f), 90f * scale, 0.9f),
    CloudPuff(Offset(50f, -10f), 110f * scale, 0.95f),
    CloudPuff(Offset(140f, -10f), 60f * scale, 0.8f),
    CloudPuff(Offset(90f, -25f), 70f * scale, 0.75f),
    CloudPuff(Offset(-30f, -15f), 65f * scale, 0.7f)
)

fun scaledCloudPuffs(scale: Float = 1.0f): List<CloudPuff> {
    val base = defaultCloudPuffs()

    val centerX = base.map { it.position.x }.average().toFloat()
    val centerY = base.map { it.position.y }.average().toFloat()
    val center = Offset(centerX, centerY)

    return base.map { puff ->
        val newPosition = center + (puff.position - center) * scale
        puff.copy(
            position = newPosition,
            radius = puff.radius * scale
        )
    }
}

fun DrawScope.drawCloudPuff(centerOffset: Offset, puff: CloudPuff, layerOpacity: Float) {
    val puffOpacity = puff.baseOpacity * layerOpacity

    val gradient = Brush.radialGradient(
        colors = listOf(
            Color(0xFF8E9AAF).copy(alpha = puffOpacity),
            Color(0xFF8E9AAF).copy(alpha = puffOpacity * 0.8f),
            Color(0xFF8E9AAF).copy(alpha = 0f)
        ),
        center = puff.position + centerOffset,
        radius = puff.radius
    )

    val gradientWithStops  = Brush.radialGradient(
        colorStops = arrayOf(
            0.0f to Color(0xFF8E9AAF).copy(alpha = puffOpacity),
            0.6f to Color(0xFF8E9AAF).copy(alpha = puffOpacity * 0.8f),
            1.0f to Color(0xFF8E9AAF).copy(alpha = 0f)
        ),
        center = puff.position + centerOffset,
        radius = puff.radius
    )

    drawCircle(
        brush = gradientWithStops,
        radius = puff.radius,
        center = puff.position + centerOffset
    )
}

data class CloudPuff(
    val position: Offset,
    val radius: Float,
    val baseOpacity: Float
)