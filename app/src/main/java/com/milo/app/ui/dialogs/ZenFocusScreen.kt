package com.milo.app.ui.dialogs

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.milo.app.domain.models.MiloEmotion
import com.milo.app.ui.mascot.MiloCompanion
import com.milo.app.ui.theme.*

@Composable
fun ZenFocusScreen(
    onDismiss: () -> Unit,
    onSaveFocus: (Int) -> Unit
) {
    var targetMinutes by remember { mutableStateOf(25) }
    var secondsLeft by remember { mutableStateOf(targetMinutes * 60) }
    var isRunning by remember { mutableStateOf(false) }

    // Box Breathing Pacing Animation (Inhale 4s, Hold 4s, Exhale 4s, Hold 4s)
    val breathingAnim = rememberInfiniteTransition(label = "breath")
    val breathScale by breathingAnim.animateFloat(
        initialValue = 0.85f,
        targetValue = 1.15f,
        animationSpec = infiniteRepeatable(
            animation = tween(4000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scale"
    )

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MiloBlack)
                .padding(24.dp)
        ) {
            IconButton(
                onClick = onDismiss,
                modifier = Modifier.align(Alignment.TopEnd)
            ) {
                Icon(imageVector = Icons.Default.Close, contentDescription = "Close", tint = MiloWhite)
            }

            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                // Breathing ring & Milo companion
                Box(
                    modifier = Modifier
                        .size(200.dp)
                        .scale(if (isRunning) breathScale else 1.0f)
                        .clip(CircleShape)
                        .background(MiloCardDark),
                    contentAlignment = Alignment.Center
                ) {
                    MiloCompanion(
                        emotion = if (isRunning) MiloEmotion.Calm else MiloEmotion.Curious,
                        size = 80.dp
                    )
                }

                Spacer(modifier = Modifier.height(28.dp))

                val mins = secondsLeft / 60
                val secs = secondsLeft % 60
                Text(
                    text = "%02d:%02d".format(mins, secs),
                    style = MaterialTheme.typography.displayLarge.copy(fontSize = 54.sp),
                    color = MiloWhite
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = if (isRunning) "“Inhale... Hold... Exhale... Flow.”" else "Uninterrupted flow compounds into mastery.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MiloZinc400
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Presets
                if (!isRunning) {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        listOf(15, 25, 45, 60).forEach { m ->
                            Button(
                                onClick = { targetMinutes = m; secondsLeft = m * 60 },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (targetMinutes == m) MiloWhite else MiloCardDark,
                                    contentColor = if (targetMinutes == m) MiloBlack else MiloWhite
                                ),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Text(text = "${m}m", fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                Button(
                    onClick = { isRunning = !isRunning },
                    colors = ButtonDefaults.buttonColors(containerColor = MiloWhite, contentColor = MiloBlack),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.width(180.dp).height(50.dp)
                ) {
                    Text(text = if (isRunning) "Pause" else "Begin Flow", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
