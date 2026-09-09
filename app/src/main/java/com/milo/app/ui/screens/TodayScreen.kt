package com.milo.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.milo.app.domain.engine.MotivationFeedback
import com.milo.app.domain.models.*
import com.milo.app.ui.components.MetricCard
import com.milo.app.ui.components.MiloScoreRing
import com.milo.app.ui.components.TimelineNode
import com.milo.app.ui.mascot.MiloSpeechBubble
import com.milo.app.ui.theme.*
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter

@Composable
fun TodayScreen(
    score: ProductivityScore,
    feedback: MotivationFeedback,
    activities: List<Activity>,
    goals: List<DailyGoal>,
    userName: String = "Priyanshu",
    onToggleActivity: (Activity) -> Unit,
    onStartTimer: (Activity) -> Unit,
    onSelectActivity: (Activity) -> Unit,
    onOpenDailyReport: () -> Unit,
    onOpenReflection: () -> Unit
) {
    val currentHour = remember { LocalTime.now().hour }
    val greeting = remember(currentHour) {
        when (currentHour) {
            in 5..11 -> "Good morning"
            in 12..16 -> "Good afternoon"
            else -> "Good evening"
        }
    }
    val todayFormatted = remember {
        LocalDate.now().format(DateTimeFormatter.ofPattern("EEEE, MMMM d"))
    }

    val completedMins = activities.filter { it.status == ActivityStatus.Completed }.sumOf { it.actualDurationMinutes }
    val plannedMins = activities.sumOf { it.plannedDurationMinutes }.coerceAtLeast(completedMins)
    val deepFocusMins = activities.filter { it.classification == ActivityClassification.DeepWork && it.status == ActivityStatus.Completed }.sumOf { it.actualDurationMinutes }

    val completedDisplay = if (completedMins >= 60) "${completedMins / 60}h ${completedMins % 60}m" else "${completedMins}m"
    val plannedDisplay = if (plannedMins >= 60) "of ${plannedMins / 60}h planned" else "of ${plannedMins}m planned"
    val deepFocusDisplay = if (deepFocusMins >= 60) "${deepFocusMins / 60}h ${deepFocusMins % 60}m" else "${deepFocusMins}m"

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MiloBlack)
            .padding(horizontal = 20.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {
        // Header
        item {
            Spacer(modifier = Modifier.height(10.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "$greeting, $userName",
                        style = MaterialTheme.typography.headlineLarge,
                        color = MiloWhite
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = todayFormatted,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MiloZinc400
                    )
                }
            }
        }

        // Score Ring
        item {
            MiloScoreRing(
                score = score,
                onClickReport = onOpenDailyReport
            )
        }

        // Milo Mascot Speech Feedback
        item {
            MiloSpeechBubble(
                quote = feedback.catQuote,
                subtext = feedback.headline,
                emotion = feedback.catEmotion
            )
        }

        // Dynamic Metric Row
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                MetricCard(
                    modifier = Modifier.weight(1f),
                    label = "Productive",
                    value = completedDisplay,
                    subtext = plannedDisplay
                )
                MetricCard(
                    modifier = Modifier.weight(1f),
                    label = "Deep Focus",
                    value = deepFocusDisplay,
                    subtext = "Flow active",
                    badge = if (deepFocusMins > 0) "+${deepFocusMins}m" else null
                )
                MetricCard(
                    modifier = Modifier.weight(1f),
                    label = "Day Score",
                    value = "${score.overall}",
                    subtext = feedback.badgeLabel
                )
            }
        }

        // Daily Goals Progress
        if (goals.isNotEmpty()) {
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MiloCardDark, RoundedCornerShape(24.dp))
                        .padding(18.dp)
                ) {
                    Text(
                        text = "DAILY CORE GOALS",
                        style = MaterialTheme.typography.labelSmall,
                        color = MiloZinc500
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    goals.forEach { g ->
                        val completedForCategory = activities.filter { it.category == g.category && it.status == ActivityStatus.Completed }.sumOf { it.actualDurationMinutes }
                        val target = g.targetValue.coerceAtLeast(1)
                        val pct = (completedForCategory.toFloat() / target).coerceIn(0f, 1f)

                        Column(modifier = Modifier.padding(vertical = 5.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = g.title,
                                    style = MaterialTheme.typography.titleMedium,
                                    color = MiloWhite
                                )
                                Text(
                                    text = "${(pct * 100).toInt()}%",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = MiloZinc400
                                )
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            LinearProgressIndicator(
                                progress = { pct },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(5.dp),
                                color = MiloWhite,
                                trackColor = MiloZinc800
                            )
                        }
                    }
                }
            }
        }

        // Daily Timeline
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "TODAY'S SCHEDULE",
                    style = MaterialTheme.typography.labelSmall,
                    color = MiloZinc500
                )
                TextButton(onClick = onOpenDailyReport) {
                    Text(
                        text = "View Life Report →",
                        style = MaterialTheme.typography.labelMedium,
                        color = MiloWhite
                    )
                }
            }
        }

        if (activities.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No activities logged for today yet.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MiloZinc500
                    )
                }
            }
        } else {
            itemsIndexed(activities) { index, activity ->
                TimelineNode(
                    activity = activity,
                    isLast = (index == activities.size - 1),
                    onToggleStatus = { onToggleActivity(activity) },
                    onStartTimer = { onStartTimer(activity) },
                    onSelect = { onSelectActivity(activity) }
                )
            }
        }

        // Evening Reflection Banner
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MiloZinc800.copy(alpha = 0.6f), RoundedCornerShape(20.dp))
                    .padding(18.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "End-of-Day Reflection",
                            style = MaterialTheme.typography.titleMedium,
                            color = MiloWhite
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "Capture wins, learnings, and recalibrate for tomorrow.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MiloZinc400
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Button(
                        onClick = onOpenReflection,
                        colors = ButtonDefaults.buttonColors(containerColor = MiloWhite, contentColor = MiloBlack),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            text = "Reflect",
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.labelMedium
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(80.dp))
        }
    }
}
