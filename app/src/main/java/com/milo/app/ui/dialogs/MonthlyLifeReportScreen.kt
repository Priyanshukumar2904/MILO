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
import com.milo.app.domain.models.MonthlyReport
import com.milo.app.ui.mascot.MiloCompanion
import com.milo.app.ui.theme.*

@Composable
fun MonthlyLifeReportScreen(
    report: MonthlyReport,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.92f),
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
                            Text(text = "MONTHLY LIFE REPORT", style = MaterialTheme.typography.labelSmall, color = MiloZinc500)
                            Text(text = report.monthYear, style = MaterialTheme.typography.headlineLarge, color = MiloWhite)
                        }
                        TextButton(onClick = onDismiss) {
                            Text(text = "Close", color = MiloZinc400)
                        }
                    }
                }

                // Hero Score
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(MiloBorderDark.copy(alpha = 0.5f), RoundedCornerShape(24.dp))
                            .padding(20.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(text = "${report.score}", style = MaterialTheme.typography.displayLarge, color = MiloWhite)
                            Text(text = "↑ ${report.deltaPreviousMonth}% vs August", style = MaterialTheme.typography.titleMedium, color = MiloGreen)
                        }
                        MiloCompanion(size = 64.dp)
                    }
                }

                // Narrative Story (Section 29)
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(MiloBorderDark.copy(alpha = 0.5f), RoundedCornerShape(24.dp))
                            .padding(20.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Text(text = "YOU SHOWED UP.", style = MaterialTheme.typography.headlineMedium, color = MiloWhite)
                        Text(text = "• You tracked ${report.trackedDaysCount} days this month.", style = MaterialTheme.typography.bodyMedium, color = MiloZinc200)
                        Text(text = "• You completed 86% of planned activities.", style = MaterialTheme.typography.bodyMedium, color = MiloZinc200)
                        Text(text = "• You studied ${report.studyHours} hours with deep focus.", style = MaterialTheme.typography.bodyMedium, color = MiloZinc200)
                        Text(text = "• You exercised ${report.exerciseSessionsCount} times.", style = MaterialTheme.typography.bodyMedium, color = MiloZinc200)
                        Text(text = "• Longest streak maintained: ${report.longestStreakDays} days.", style = MaterialTheme.typography.bodyMedium, color = MiloZinc200)
                    }
                }

                // Highlights
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .weight(1f)
                                .background(MiloBorderDark.copy(alpha = 0.5f), RoundedCornerShape(20.dp))
                                .padding(14.dp)
                        ) {
                            Text(text = "BIGGEST IMPROVEMENT", style = MaterialTheme.typography.labelSmall, color = MiloGreen)
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(text = report.biggestImprovement.first, style = MaterialTheme.typography.titleMedium, color = MiloWhite)
                            Text(text = report.biggestImprovement.second, style = MaterialTheme.typography.headlineMedium, color = MiloGreen)
                        }

                        Column(
                            modifier = Modifier
                                .weight(1f)
                                .background(MiloBorderDark.copy(alpha = 0.5f), RoundedCornerShape(20.dp))
                                .padding(14.dp)
                        ) {
                            Text(text = "BIGGEST OPPORTUNITY", style = MaterialTheme.typography.labelSmall, color = MiloAmber)
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(text = report.biggestOpportunity.first, style = MaterialTheme.typography.titleMedium, color = MiloWhite)
                            Text(text = report.biggestOpportunity.second, style = MaterialTheme.typography.headlineMedium, color = MiloAmber)
                        }
                    }
                }

                item {
                    Text(
                        text = "“You're not the same person who started this month.”",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = MiloWhite
                    )
                }
            }
        }
    }
}
