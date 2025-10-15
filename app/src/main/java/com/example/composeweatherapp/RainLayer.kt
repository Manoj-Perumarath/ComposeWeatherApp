package com.example.composeweatherapp

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.drawscope.translate

data class Particle(
    var position: Offset,
    var original: Offset,
    var length: Float,
    var speed: Float
)

fun generateRainParticles(particleCount: Int = 100): List<Particle> {
    var x = 200f
    return List(particleCount) {
        x -= (0..20).random().toFloat()
        Particle(
            position = Offset(x, (0..600).random().toFloat()),
            original = Offset.Zero,
            length = (10..40).random().toFloat(),
            speed = (2..6).random().toFloat()
        )
    }
}

fun DrawScope.drawRainLayer(particles: List<Particle>, center: Offset, W: Float = 600f) {
    val rect = Rect(
        left = center.x - 300f,
        top = center.y - 300f,
        right = center.x + 350f,
        bottom = center.y + 300f
    )

    clipRect(rect.left, rect.top, rect.right, rect.bottom) {
        translate(left = W - 250f, top = -200f) {
            rotate(degrees = 45f) {
                particles.forEach { p -> drawRainDrop(p, center) }
            }
        }
    }
}

fun DrawScope.drawRainDrop(p: Particle, center: Offset) {
    val startPos = p.position + center
    val endPos = p.position + Offset(0f, p.length) + center

    val gradient = Brush.linearGradient(
        colors = listOf(
            Color(0xFFDEE2FF).copy(alpha = 0.2f),
            Color(0xFFDEE2FF).copy(alpha = 0.8f),
            Color(0xFFDEE2FF).copy(alpha = 0.4f)
        ),
        start = startPos,
        end = endPos
    )

    drawLine(
        brush = gradient,
        start = startPos,
        end = endPos,
        strokeWidth = 3f,
        cap = StrokeCap.Round
    )

    drawLine(
        color = Color.White.copy(alpha = 0.3f),
        start = Offset(startPos.x - 1f, startPos.y),
        end = Offset(endPos.x - 1f, endPos.y * 0.8f + startPos.y * 0.2f),
        strokeWidth = 1f,
        cap = StrokeCap.Round
    )
}

fun updateRainParticles(particles: MutableList<Particle>, height: Float) {
    for (i in particles.indices) {
        val p = particles[i]
        val newY = p.position.y + p.speed
        if (newY > height) {
            particles[i] = p.copy(position = Offset(p.position.x, p.original.y))
        } else {
            particles[i] = p.copy(position = Offset(p.position.x, newY))
        }
    }
}