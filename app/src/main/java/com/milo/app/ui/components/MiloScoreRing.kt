package com.milo.app.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.milo.app.domain.models.MiloEmotion
import com.milo.app.domain.models.ProductivityScore
import com.milo.app.ui.mascot.MiloCompanion
import com.milo.app.ui.theme.*

@Composable
fun MiloScoreRing(
    modifier: Modifier = Modifier,
    score: ProductivityScore,
    onClickReport: () -> void = {}
) {
    var animationTriggered by remember { mutableStateOf(false) }
    LaunchedEffect(score.overall) {
        animationTriggered = true
    }

    val animatedSweep by animateFloatAsState(
        targetValue = if (animationTriggered) (score.overall / 100f) * 360f else 0f,
        animationSpec = tween(durationMillis = 1000),
        label = "score_sweep"
    )

    val emotion = when {
        score.deltaYesterday >= 5 -> MiloEmotion.Proud
        score.deltaYesterday > 0 -> MiloEmotion.Happy
        score.deltaYesterday == 0 -> MiloEmotion.Calm
        else -> MiloEmotion.Encouraging
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(MiloCardDark, RoundedCornerShape(32.dp))
            .border(1.dp, MiloBorderDark, RoundedCornerShape(32.dp))
            .padding(20.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "TODAY'S PRODUCTIVITY",
                    style = MaterialTheme.typography.labelSmall,
                    color = MiloZinc500
                )
                Text(
                    text = "Personal Performance Index",
                    style = MaterialTheme.typography.bodyMedium.copy(fontSize = 12.sp),
                    color = MiloZinc400
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Animated Progress Ring
            Box(
                modifier = Modifier.size(140.dp),
                contentAlignment = Alignment.Center
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val strokeW = 12.dp.toPx()
                    val pad = strokeW / 2
                    val arcSize = Size(size.width - strokeW, size.height - strokeW)

                    // Track
                    drawArc(
                        color = MiloZinc800,
                        startAngle = -90f,
                        sweepAngle = 360f,
                        useCenter = false,
                        topLeft = Offset(pad, pad),
                        size = arcSize,
                        style = Stroke(width = strokeW, cap = StrokeCap.Round)
                    )

                    // Filled Arc
                    drawArc(
                        color = MiloWhite,
                        startAngle = -90f,
                        sweepAngle = animatedSweep,
                        useCenter = false,
                        topLeft = Offset(pad, pad),
                        size = arcSize,
                        style = Stroke(width = strokeW, cap = StrokeCap.Round)
                    )
                }

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "${score.overall}",
                        style = MaterialTheme.typography.displayLarge.copy(fontSize = 38.sp),
                        color = MiloWhite
                    )
                    Text(
                        text = "/ 100",
                        style = MaterialTheme.typography.labelSmall,
                        color = MiloZinc500
                    )
                }
            }

            // Milo Mascot & Delta Badge
            Column(
                horizontalAlignment = Alignment.End
            ) {
                MiloCompanion(
                    emotion = emotion,
                    size = 64.dp
                )

                Spacer(modifier = Modifier.height(8.dp))

                Box(
                    modifier = Modifier
                        .background(MiloZinc800, RoundedCornerShape(12.dp))
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = if (score.deltaYesterday >= 0) "+${score.deltaYesterday}% vs yesterday" else "${score.deltaYesterday}% vs yesterday",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = if (score.deltaYesterday >= 0) MiloGreen else MiloZinc400
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = if (score.deltaYesterday >= 0) "Better than yesterday." else "A slower day does not erase progress.",
                    style = MaterialTheme.typography.bodyMedium.copy(fontSize = 10.sp),
                    color = MiloZinc400
                )
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // Sub-metric chips
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            SubMetricChip(Modifier.weight(1f), "Tasks", "${score.completionScore}%")
            SubMetricChip(Modifier.weight(1f), "Time", "${score.timeManagementScore}%")
            SubMetricChip(Modifier.weight(1f), "Focus", "${score.focusScore}%")
            SubMetricChip(Modifier.weight(1f), "Habits", "${score.habitScore}%")
            SubMetricChip(Modifier.weight(1f), "Routine", "${score.consistencyScore}%")
        }
    }
}

@Composable
private fun SubMetricChip(modifier: Modifier, label: String, value: String) {
    Column(
        modifier = modifier
            .background(MiloBorderDark.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
            .padding(vertical = 6.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = label, style = MaterialTheme.typography.labelSmall.copy(fontSize = 8.sp), color = MiloZinc500)
        Text(text = value, style = MaterialTheme.typography.titleMedium.copy(fontSize = 11.sp, fontWeight = FontWeight.Bold), color = MiloWhite)
    }
}
