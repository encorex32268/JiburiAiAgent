package com.lihan.jiburiaiagent.explore.presentation.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import kotlin.math.sin

@Composable
fun SootSpritesBackground() {
    val particlesCount = 15
    val infiniteTransition = rememberInfiniteTransition(label = "particles")
    val animatedProgress = infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 20000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "progress"
    )

    val randomData = remember {
        List(particlesCount) {
            Triple(
                Math.random().toFloat(),
                Math.random().toFloat(),
                Math.random().toFloat() * 0.3f + 0.1f
            )
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        randomData.forEach { (xPct, yPct, speed) ->
            val currentYPct = (yPct - (animatedProgress.value * speed)) % 1.0f
            val adjustedY = if (currentYPct < 0f) currentYPct + 1.0f else currentYPct
            val adjustedX = xPct + 0.05f * sin((animatedProgress.value * 2 * Math.PI).toFloat())

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .align(Alignment.TopStart)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(adjustedX)
                        .fillMaxHeight(adjustedY)
                        .wrapContentSize(Alignment.BottomEnd)
                        .size(6.dp)
                        .clip(CircleShape)
                        .background(
                            listOf(
                                Color(0x261E3A1E),
                                Color(0x26C2A968),
                                Color(0x26A84C32)
                            ).random()
                        )
                )
            }
        }
    }
}
