package com.milo.app.ui.mascot

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.milo.app.domain.models.MiloEmotion

@Composable
fun MiloCompanion(
    modifier: Modifier = Modifier,
    emotion: MiloEmotion = MiloEmotion.Calm,
    size: Dp = 72.dp,
    onClick: (() -> Unit)? = null
) {
    // Subtle tail wag animation
    val infiniteTransition = rememberInfiniteTransition(label = "milo_tail")
    val tailRotation by infiniteTransition.animateFloat(
        initialValue = -8f,
        targetValue = 8f,
        animationSpec = infiniteRepeatable(
            animation = tween(2400, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "tail_angle"
    )

    // Periodic blinking animation
    var isBlinking by remember { mutableStateOf(false) }
    LaunchedEffect(emotion) {
        if (emotion != MiloEmotion.Sleepy) {
            while (true) {
                kotlinx.coroutines.delay(4200)
                isBlinking = true
                kotlinx.coroutines.delay(160)
                isBlinking = false
            }
        }
    }

    val catColor = if (MaterialTheme.colorScheme.background == Color.Black) Color.White else Color(0xFF18181B)
    val eyeColor = if (MaterialTheme.colorScheme.background == Color.Black) Color.Black else Color.White
    val innerEarColor = Color(0xFF71717A)

    Box(
        modifier = modifier
            .size(size)
            .then(if (onClick != null) Modifier.clickable { onClick() } else Modifier),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val w = this.size.width
            val h = this.size.height

            // 1. Soft Shadow
            drawOval(
                color = Color(0x3371717A),
                topLeft = Offset(w * 0.15f, h * 0.88f),
                size = Size(w * 0.7f, h * 0.1f)
            )

            // 2. Animated Tail
            val tailPath = Path().apply {
                moveTo(w * 0.72f, h * 0.78f)
                cubicTo(
                    w * 0.85f + (tailRotation * 0.6f), h * 0.75f,
                    w * 0.92f + (tailRotation * 1.2f), h * 0.55f,
                    w * 0.82f + (tailRotation * 0.8f), h * 0.42f
                )
            }
            drawPath(
                path = tailPath,
                color = catColor,
                style = Stroke(width = w * 0.08f, cap = StrokeCap.Round)
            )

            // 3. Body
            val bodyPath = Path().apply {
                moveTo(w * 0.30f, h * 0.50f)
                cubicTo(w * 0.22f, h * 0.65f, w * 0.24f, h * 0.84f, w * 0.35f, h * 0.86f)
                lineTo(w * 0.65f, h * 0.86f)
                cubicTo(w * 0.76f, h * 0.84f, w * 0.78f, h * 0.65f, w * 0.70f, h * 0.50f)
                close()
            }
            drawPath(bodyPath, color = catColor)

            // Paws
            drawRoundRect(
                color = catColor,
                topLeft = Offset(w * 0.34f, h * 0.82f),
                size = Size(w * 0.13f, h * 0.08f),
                cornerRadius = CornerRadius(12f, 12f)
            )
            drawRoundRect(
                color = catColor,
                topLeft = Offset(w * 0.53f, h * 0.82f),
                size = Size(w * 0.13f, h * 0.08f),
                cornerRadius = CornerRadius(12f, 12f)
            )

            // 4. Ears
            val leftEar = Path().apply {
                moveTo(w * 0.32f, h * 0.35f)
                lineTo(w * 0.22f, h * 0.14f)
                lineTo(w * 0.42f, h * 0.22f)
                close()
            }
            drawPath(leftEar, color = catColor)

            val leftInner = Path().apply {
                moveTo(w * 0.33f, h * 0.31f)
                lineTo(w * 0.26f, h * 0.18f)
                lineTo(w * 0.40f, h * 0.23f)
                close()
            }
            drawPath(leftInner, color = innerEarColor)

            val rightEar = Path().apply {
                val tilt = if (emotion == MiloEmotion.Curious) 0.08f else 0f
                moveTo(w * 0.68f, h * 0.35f)
                lineTo(w * (0.78f + tilt), h * 0.14f)
                lineTo(w * 0.58f, h * 0.22f)
                close()
            }
            drawPath(rightEar, color = catColor)

            val rightInner = Path().apply {
                moveTo(w * 0.67f, h * 0.31f)
                lineTo(w * 0.74f, h * 0.18f)
                lineTo(w * 0.60f, h * 0.23f)
                close()
            }
            drawPath(rightInner, color = innerEarColor)

            // 5. Head
            drawCircle(
                color = catColor,
                radius = w * 0.26f,
                center = Offset(w * 0.50f, h * 0.40f)
            )

            // 6. Eyes according to emotion & blink state
            if (isBlinking || emotion == MiloEmotion.Sleepy) {
                // Sleek curved sleeping / blinking eyes
                drawLine(
                    color = eyeColor,
                    start = Offset(w * 0.38f, h * 0.38f),
                    end = Offset(w * 0.46f, h * 0.38f),
                    strokeWidth = 3f,
                    cap = StrokeCap.Round
                )
                drawLine(
                    color = eyeColor,
                    start = Offset(w * 0.54f, h * 0.38f),
                    end = Offset(w * 0.62f, h * 0.38f),
                    strokeWidth = 3f,
                    cap = StrokeCap.Round
                )
            } else if (emotion == MiloEmotion.Happy || emotion == MiloEmotion.Celebrating) {
                // Joyful ^ ^ curves
                val leftHappy = Path().apply {
                    moveTo(w * 0.38f, h * 0.40f)
                    quadraticBezierTo(w * 0.42f, h * 0.34f, w * 0.46f, h * 0.40f)
                }
                drawPath(leftHappy, color = eyeColor, style = Stroke(width = 3.5f, cap = StrokeCap.Round))

                val rightHappy = Path().apply {
                    moveTo(w * 0.54f, h * 0.40f)
                    quadraticBezierTo(w * 0.58f, h * 0.34f, w * 0.62f, h * 0.40f)
                }
                drawPath(rightHappy, color = eyeColor, style = Stroke(width = 3.5f, cap = StrokeCap.Round))
            } else {
                // Expressive round eyes with subtle reflection point
                drawCircle(color = eyeColor, radius = w * 0.045f, center = Offset(w * 0.41f, h * 0.38f))
                drawCircle(color = eyeColor, radius = w * 0.045f, center = Offset(w * 0.59f, h * 0.38f))

                // Glimmer
                drawCircle(color = catColor, radius = w * 0.015f, center = Offset(w * 0.42f, h * 0.37f))
                drawCircle(color = catColor, radius = w * 0.015f, center = Offset(w * 0.60f, h * 0.37f))
            }

            // 7. Minimalist Nose & Whiskers
            drawCircle(
                color = innerEarColor,
                radius = w * 0.02f,
                center = Offset(w * 0.50f, h * 0.44f)
            )

            // Gentle mouth
            val mouthPath = Path().apply {
                moveTo(w * 0.46f, h * 0.46f)
                quadraticBezierTo(w * 0.50f, h * 0.49f, w * 0.54f, h * 0.46f)
            }
            drawPath(mouthPath, color = eyeColor, style = Stroke(width = 2.5f, cap = StrokeCap.Round))

            // Whiskers (fine lines)
            drawLine(eyeColor.copy(alpha = 0.5f), Offset(w * 0.28f, h * 0.42f), Offset(w * 0.36f, h * 0.43f), strokeWidth = 1.5f)
            drawLine(eyeColor.copy(alpha = 0.5f), Offset(w * 0.27f, h * 0.46f), Offset(w * 0.36f, h * 0.45f), strokeWidth = 1.5f)
            drawLine(eyeColor.copy(alpha = 0.5f), Offset(w * 0.72f, h * 0.42f), Offset(w * 0.64f, h * 0.43f), strokeWidth = 1.5f)
            drawLine(eyeColor.copy(alpha = 0.5f), Offset(w * 0.73f, h * 0.46f), Offset(w * 0.64f, h * 0.45f), strokeWidth = 1.5f)
        }
    }
}
