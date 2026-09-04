package com.milo.app.ui.dialogs

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.milo.app.domain.models.Activity
import com.milo.app.domain.models.MiloEmotion
import com.milo.app.ui.mascot.MiloCompanion
import com.milo.app.ui.theme.*
import kotlinx.coroutines.delay

@Composable
fun LiveTimerBottomSheet(
    activity: Activity,
    onDismiss: () -> Unit,
    onCompleteActivity: (Int) -> Unit
) {
    var seconds by remember { mutableStateOf(activity.actualDurationMinutes * 60) }
    var isRunning by remember { mutableStateOf(false) }

    LaunchedEffect(isRunning) {
        while (isRunning) {
            delay(1000)
            seconds++
        }
    }

    val mins = seconds / 60
    val secs = seconds % 60
    val diff = mins - activity.plannedDurationMinutes

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight(),
            shape = RoundedCornerShape(32.dp),
            colors = CardDefaults.cardColors(containerColor = MiloCardDark)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                MiloCompanion(
                    emotion = if (isRunning) MiloEmotion.Curious else MiloEmotion.Calm,
                    size = 64.dp
                )

                Text(
                    text = activity.title,
                    style = MaterialTheme.typography.headlineMedium,
                    color = MiloWhite
                )

                Text(
                    text = "${activity.category.name} • Planned: ${activity.plannedDurationMinutes}m",
                    style = MaterialTheme.typography.labelSmall,
                    color = MiloZinc400
                )

                // Timer Display
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MiloBorderDark.copy(alpha = 0.5f), RoundedCornerShape(24.dp))
                        .padding(vertical = 20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "%02d:%02d".format(mins, secs),
                        style = MaterialTheme.typography.displayLarge.copy(fontWeight = FontWeight.Black),
                        color = MiloWhite
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = if (diff >= 0) "Variance: +${diff}m" else "Variance: ${diff}m",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = if (diff >= 0) MiloGreen else MiloZinc400
                    )
                }

                // Controls
                Row(
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = { seconds = 0; isRunning = false },
                        modifier = Modifier.size(44.dp).background(MiloZinc800, CircleShape)
                    ) {
                        Icon(imageVector = Icons.Default.Refresh, contentDescription = "Reset", tint = MiloWhite)
                    }

                    Button(
                        onClick = { isRunning = !isRunning },
                        modifier = Modifier.height(52.dp).weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = MiloWhite, contentColor = MiloBlack),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Icon(imageVector = if (isRunning) Icons.Default.Pause else Icons.Default.PlayArrow, contentDescription = null)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(text = if (isRunning) "Pause" else "Start Focus", fontWeight = FontWeight.Bold)
                    }

                    IconButton(
                        onClick = {
                            onCompleteActivity(maxOf(1, mins))
                            onDismiss()
                        },
                        modifier = Modifier.size(44.dp).background(MiloGreen, CircleShape)
                    ) {
                        Icon(imageVector = Icons.Default.Check, contentDescription = "Complete", tint = MiloBlack)
                    }
                }
            }
        }
    }
}
