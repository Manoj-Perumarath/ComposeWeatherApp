package com.example.composeweatherapp

import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

fun skyBlueBackground(size: Size) = Brush.verticalGradient(
    colors = listOf(
//        Color(0xFF343A5E), // deep indigo-blue
//        Color(0xFF4E557A), // muted steel blue
//        Color(0xFF7A88A1)

        Color(0xFF1B2735), // near-black blue (top)
        Color(0xFF283E51), // deep navy slate
        Color(0xFF485563)
    ),
    startY = 0f,
    endY = size.height
)

