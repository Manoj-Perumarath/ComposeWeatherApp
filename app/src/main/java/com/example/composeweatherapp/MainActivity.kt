package com.example.composeweatherapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.toMutableStateList
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import com.example.composeweatherapp.ui.theme.ComposeWeatherAppTheme
import kotlinx.coroutines.delay


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ComposeWeatherAppTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    //Lightning animation
                    var frameCounter by remember { mutableIntStateOf(0) }
                    val lightning = remember { Lightning() }
                    LaunchedEffect(Unit) {
                        while (true) {
                            delay(16)
                            frameCounter++
                            lightning.update()
                        }
                    }

                    //Generate particles and indefinite rain animation
                    val particles = remember { generateRainParticles().toMutableStateList() }

                    val infiniteTransition = rememberInfiniteTransition()
                    val time = infiniteTransition.animateFloat(
                        initialValue = 0f,
                        targetValue = 1f,
                        animationSpec = infiniteRepeatable(
                            animation = tween(16, easing = LinearEasing),
                            repeatMode = RepeatMode.Restart
                        )
                    )


                    val cloudPuffs = scaledCloudPuffs(3f)

                    val center = Offset.Zero

                    Canvas(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                    ) {
                        time.value
                        val width = size.width
                        val height = size.height

//                        drawRect(
//                            brush = skyBlueBackground(size),
//                            size = size
//                        )

                        val canvasCenter = Offset(width / 2f, height / 2f)
                        val cloudCenter = canvasCenter + center
                        lightning.triggerLightning(center = cloudCenter)

                        cloudPuffs.forEach { puff ->
                            drawCloudPuff(
                                centerOffset = cloudCenter,
                                puff = puff,
                                layerOpacity = 1.0f
                            )
                        }
                        updateRainParticles(particles, height / 2)
                        drawRainLayer(particles, canvasCenter)
                        drawLightning(lightning)
                    }
                }
            }
        }
    }
}



