package com.example.composeweatherapp

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import kotlinx.coroutines.delay
import kotlin.random.Random
import android.graphics.Paint as AndroidPaint

data class LightningState(
    val points: List<Offset> = emptyList(), val opacity: Float = 0f, val frameCount: Int = 0
)

class Lightning {
    private var state = LightningState()

    val points get() = state.points
    val opacity get() = state.opacity
    val frameCount get() = state.frameCount
    val isActive get() = state.opacity > 0f

    fun generate(start: Offset, end: Offset) {
        val newPoints = mutableListOf<Offset>()
        newPoints.add(start)

        val segments = 8 + Random.nextInt(5)

//       1/8 = 12.5% 90
        for (i in 1 until segments) {
            val progress = i.toFloat() / segments
            val targetY = start.y + (end.y - start.y) * progress
            val targetX = start.x + (end.x - start.x) * progress

            val offset = Offset(
                targetX + (Random.nextFloat() - 0.5f) * 60,
                targetY + (Random.nextFloat() - 0.5f) * 20
            )
            newPoints.add(offset)
        }

        newPoints.add(end)
        state = LightningState(
            points = newPoints, opacity = 1f, frameCount = 0
        )
    }

    fun update() {
        val newFrameCount = frameCount + 1
        val newOpacity = when {
            newFrameCount < 3 -> 1f
            newFrameCount < 8 -> 0.3f + Random.nextFloat() * 0.4f
            else -> 0f
        }

        state = state.copy(
            opacity = newOpacity, frameCount = newFrameCount
        )
    }

    fun triggerLightning(center: Offset) {
        if (!isActive && Random.nextFloat() < 0.05f) {
            val startX = -100 + Random.nextFloat() * 400
            val start = Offset(startX, -300f) + center
            val end = Offset(startX + Random.nextFloat() * 100 - 50, 400f) + center
            generate(start, end)
        }
    }
}

fun DrawScope.drawLightning(lightning: Lightning) {
    if (lightning.isActive && lightning.points.isNotEmpty()) {
        // Draw glow layers
        for (layer in 3 downTo 1) {
            val paint = AndroidPaint().apply {
                color = android.graphics.Color.WHITE
                strokeWidth = 5.0f
                strokeCap = AndroidPaint.Cap.ROUND
                isAntiAlias = true
                alpha = (lightning.opacity * 255 * layer).toInt()
                style = AndroidPaint.Style.STROKE
                maskFilter = android.graphics.BlurMaskFilter(
                    8.0f * layer, android.graphics.BlurMaskFilter.Blur.NORMAL
                )
            }

            drawLightningPath(
                points = lightning.points,
                paint = paint
            )
        }
        val paint = AndroidPaint().apply {
            color = android.graphics.Color.WHITE
            strokeWidth = 5.0f
            strokeCap = AndroidPaint.Cap.ROUND
            isAntiAlias = true
            alpha = (lightning.opacity * 255).toInt()
            style = AndroidPaint.Style.STROKE
            maskFilter = android.graphics.BlurMaskFilter(
                1f, android.graphics.BlurMaskFilter.Blur.NORMAL
            )
        }

        drawLightningPath(
            points = lightning.points,
            paint = paint
        )

        drawLightningBranches(lightning.points, lightning.opacity)
    }
}


private fun DrawScope.drawLightningPath(
    points: List<Offset>, paint: AndroidPaint
) {
    if (points.size < 2) return
    val path = android.graphics.Path().apply {
        moveTo(points[0].x, points[0].y)
        for (i in 1 until points.size) {
            lineTo(points[i].x, points[i].y)
        }
    }

    drawIntoCanvas { canvas ->
        canvas.nativeCanvas.drawPath(path, paint)
    }
}

private fun DrawScope.drawLightningBranches(
    points: List<Offset>, opacity: Float
) {
    if (points.size < 3) return

    val numBranches = 2 + Random.nextInt(2)

    for (b in 0 until numBranches) {
        val branchStart = points[2 + Random.nextInt(points.size - 3)]
        val branchLength = 3 + Random.nextInt(3)

        drawIntoCanvas { canvas ->
            val path = android.graphics.Path().apply {
                moveTo(branchStart.x, branchStart.y)
                var current = branchStart

                for (i in 0 until branchLength) {
                    current = Offset(
                        current.x + (Random.nextFloat() - 0.5f) * 40,
                        current.y + Random.nextFloat() * 30 + 10
                    )
                    lineTo(current.x, current.y)
                }
            }

            val paint = AndroidPaint().apply {
                color = android.graphics.Color.WHITE
                alpha = (opacity * 0.6f * 255).toInt()
                strokeWidth = 2f
                strokeCap = AndroidPaint.Cap.ROUND
                style = AndroidPaint.Style.STROKE
                maskFilter = android.graphics.BlurMaskFilter(
                    1.5f, android.graphics.BlurMaskFilter.Blur.NORMAL
                )
            }
            canvas.nativeCanvas.drawPath(path, paint)
        }
    }
}
