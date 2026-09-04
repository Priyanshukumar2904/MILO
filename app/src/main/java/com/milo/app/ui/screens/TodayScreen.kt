package com.milo.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
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

@Composable
fun TodayScreen(
    score: ProductivityScore,
    feedback: MotivationFeedback,
    activities: List<Activity>,
    goals: List<DailyGoal>,
    onToggleActivity: (Activity) -> Unit,
    onStartTimer: (Activity) -> Unit,
    onSelectActivity: (Activity) -> Unit,
    onOpenDailyReport: () -> Unit,
    onOpenReflection: () -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MiloBlack)
            .padding(horizontal = 20.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {
        // Header
        item {
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Good evening, Priyanshu",
                        style = MaterialTheme.typography.headlineLarge,
                        color = MiloWhite
                    )
                    Text(
                        text = "September 4, 2026",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MiloZinc500
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

        // Metric Row
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                MetricCard(
                    modifier = Modifier.weight(1f),
                    label = "Productive",
                    value = "6h 42m",
                    subtext = "of 8h planned"
                )
                MetricCard(
                    modifier = Modifier.weight(1f),
                    label = "Deep Focus",
                    value = "2h 55m",
                    subtext = "Flow active",
                    badge = "+25m"
                )
                MetricCard(
                    modifier = Modifier.weight(1f),
                    label = "Habit Streak",
                    value = "9 days",
                    subtext = "Morning routine"
                )
            }
        }

        // Daily Goals Progress
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
                    val pct = if (g.isMorningRoutine) 1.0f else if (g.category == ActivityCategory.Study) 0.95f else 0.8f
                    Column(modifier = Modifier.padding(vertical = 4.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(text = g.title, style = MaterialTheme.typography.titleMedium, color = MiloWhite)
                            Text(text = "${(pct * 100).toInt()}%", style = MaterialTheme.typography.labelSmall, color = MiloZinc400)
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        LinearProgressIndicator(
                            progress = { pct },
                            modifier = Modifier.fillMaxWidth().height(4.dp),
                            color = MiloWhite,
                            trackColor = MiloZinc800
                        )
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
                    text = "DAILY TIMELINE",
                    style = MaterialTheme.typography.labelSmall,
                    color = MiloZinc500
                )
                TextButton(onClick = onOpenDailyReport) {
                    Text(text = "View Full Report →", style = MaterialTheme.typography.labelSmall, color = MiloWhite)
                }
            }
        }

        itemsIndexed(activities) { index, activity ->
            TimelineNode(
                activity = activity,
                isLast = (index == activities.size - 1),
                onToggleStatus = { onToggleActivity(activity) },
                onStartTimer = { onStartTimer(activity) },
                onSelect = { onSelectActivity(activity) }
            )
        }

        // Evening Reflection Banner
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MiloZinc800.copy(alpha = 0.6f), RoundedCornerShape(20.dp))
                    .padding(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(text = "End-of-Day Reflection", style = MaterialTheme.typography.titleMedium, color = MiloWhite)
                        Text(text = "Capture what went well and what to improve tomorrow", style = MaterialTheme.typography.bodyMedium.copy(fontSize = 11.sp), color = MiloZinc400)
                    }
                    Button(
                        onClick = onOpenReflection,
                        colors = ButtonDefaults.buttonColors(containerColor = MiloWhite, contentColor = MiloBlack),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(text = "Reflect", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }
                }
            }
            Spacer(modifier = Modifier.height(72.dp))
        }
    }
}
