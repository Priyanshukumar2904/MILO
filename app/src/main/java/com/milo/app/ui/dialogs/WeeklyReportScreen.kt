package com.milo.app.ui.dialogs

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.milo.app.domain.models.WeeklyReport
import com.milo.app.ui.theme.*

@Composable
fun WeeklyReportScreen(
    report: WeeklyReport,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.9f),
            shape = RoundedCornerShape(32.dp),
            colors = CardDefaults.cardColors(containerColor = MiloCardDark)
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(text = "WEEKLY REPORT", style = MaterialTheme.typography.labelSmall, color = MiloZinc500)
                            Text(text = "81 / 100", style = MaterialTheme.typography.displayLarge, color = MiloWhite)
                            Text(text = "+8% vs last week", style = MaterialTheme.typography.titleMedium, color = MiloGreen)
                        }
                        TextButton(onClick = onDismiss) {
                            Text(text = "Close", color = MiloZinc400)
                        }
                    }
                }

                // 7-Day Trend Chart
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(MiloBorderDark.copy(alpha = 0.5f), RoundedCornerShape(20.dp))
                            .padding(16.dp)
                    ) {
                        Text(text = "7-DAY PRODUCTIVITY TREND", style = MaterialTheme.typography.labelSmall, color = MiloZinc500)
                        Spacer(modifier = Modifier.height(14.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.Bottom
                        ) {
                            report.dailyScores.forEach { day ->
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(text = "${day.score}", style = MaterialTheme.typography.labelSmall, color = MiloWhite)
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Box(
                                        modifier = Modifier
                                            .width(22.dp)
                                            .height((day.score * 0.9f).dp)
                                            .background(MiloWhite, RoundedCornerShape(6.dp))
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(text = day.dayName, style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp), color = MiloZinc500)
                                }
                            }
                        }
                    }
                }

                // Averages Summary
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(MiloBorderDark.copy(alpha = 0.5f), RoundedCornerShape(20.dp))
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(text = "WEEKLY AVERAGES", style = MaterialTheme.typography.labelSmall, color = MiloZinc500)
                        Text(text = "Daily Productive: ${report.avgProductiveHours} hours", style = MaterialTheme.typography.titleMedium, color = MiloWhite)
                        Text(text = "Daily Study: ${report.avgStudyHours} hours", style = MaterialTheme.typography.bodyMedium, color = MiloZinc400)
                        Text(text = "Habit Adherence: ${report.habitAdherenceRate}%", style = MaterialTheme.typography.bodyMedium, color = MiloZinc400)
                        Text(text = "Schedule Adherence: ${report.scheduleAdherenceRate}%", style = MaterialTheme.typography.bodyMedium, color = MiloZinc400)
                    }
                }

                item {
                    Text(text = report.storyHeadline, style = MaterialTheme.typography.bodyMedium, color = MiloZinc400)
                }
            }
        }
    }
}
