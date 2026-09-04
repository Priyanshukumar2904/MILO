package com.milo.app.ui.dialogs

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.milo.app.domain.models.DailyReport
import com.milo.app.domain.models.Reflection
import com.milo.app.ui.theme.*
import java.time.LocalDate

@Composable
fun DailyReportDialog(
    report: DailyReport,
    onDismiss: () -> Unit,
    onSaveReflection: (Reflection) -> Unit
) {
    var rating by remember { mutableStateOf(report.reflection?.rating ?: 4) }
    var wentWell by remember { mutableStateOf(report.reflection?.wentWell ?: "") }
    var couldImprove by remember { mutableStateOf(report.reflection?.couldImprove ?: "") }
    var tomorrowFocus by remember { mutableStateOf(report.reflection?.tomorrowFocus ?: "") }

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
                            Text(text = "DAILY REPORT", style = MaterialTheme.typography.labelSmall, color = MiloZinc500)
                            Text(text = "September 4, 2026", style = MaterialTheme.typography.headlineLarge, color = MiloWhite)
                        }
                        TextButton(onClick = onDismiss) {
                            Text(text = "Close", color = MiloZinc400)
                        }
                    }
                }

                // Productivity
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(MiloBorderDark.copy(alpha = 0.5f), RoundedCornerShape(20.dp))
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(text = "PRODUCTIVITY", style = MaterialTheme.typography.labelSmall, color = MiloZinc500)
                            Text(text = "${report.score.overall} / 100", style = MaterialTheme.typography.headlineLarge, color = MiloWhite)
                        }
                        Box(
                            modifier = Modifier
                                .background(MiloZinc800, RoundedCornerShape(10.dp))
                                .padding(horizontal = 10.dp, vertical = 4.dp)
                        ) {
                            Text(text = "+9% vs yesterday", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold), color = MiloGreen)
                        }
                    }
                }

                // Time Breakdown
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(MiloBorderDark.copy(alpha = 0.5f), RoundedCornerShape(20.dp))
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(text = "TIME DISTRIBUTION", style = MaterialTheme.typography.labelSmall, color = MiloZinc500)
                        Text(text = "Productive: 6h 42m", style = MaterialTheme.typography.titleMedium, color = MiloWhite)
                        Text(text = "Study: 3h 20m  •  Work: 2h 45m  •  Exercise: 45m", style = MaterialTheme.typography.bodyMedium, color = MiloZinc400)
                        Text(text = "Entertainment: 1h 50m  •  Other: 1h 20m", style = MaterialTheme.typography.bodyMedium, color = MiloZinc500)
                    }
                }

                // Observations
                item {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(text = "Positive Observation", style = MaterialTheme.typography.labelSmall, color = MiloGreen)
                        Text(text = report.positiveObservation, style = MaterialTheme.typography.bodyMedium, color = MiloWhite)

                        Spacer(modifier = Modifier.height(4.dp))
                        Text(text = "Opportunity", style = MaterialTheme.typography.labelSmall, color = MiloAmber)
                        Text(text = report.improvementOpportunity, style = MaterialTheme.typography.bodyMedium, color = MiloZinc400)
                    }
                }

                // End-of-Day Reflection
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(MiloBorderDark.copy(alpha = 0.5f), RoundedCornerShape(20.dp))
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Text(text = "END-OF-DAY REFLECTION", style = MaterialTheme.typography.labelSmall, color = MiloZinc500)
                        Text(text = "How did today feel? (1–5)", style = MaterialTheme.typography.bodyMedium, color = MiloWhite)

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            (1..5).forEach { num ->
                                Button(
                                    onClick = { rating = num },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = if (rating == num) MiloWhite else MiloZinc800,
                                        contentColor = if (rating == num) MiloBlack else MiloWhite
                                    ),
                                    shape = RoundedCornerShape(10.dp),
                                    modifier = Modifier.size(44.dp)
                                ) {
                                    Text(text = "$num", fontWeight = FontWeight.Bold)
                                }
                            }
                        }

                        OutlinedTextField(
                            value = wentWell,
                            onValueChange = { wentWell = it },
                            label = { Text("What went well?") },
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = MiloWhite,
                                unfocusedTextColor = MiloWhite,
                                focusedBorderColor = MiloWhite,
                                unfocusedBorderColor = MiloZinc700
                            )
                        )

                        OutlinedTextField(
                            value = tomorrowFocus,
                            onValueChange = { tomorrowFocus = it },
                            label = { Text("One thing to improve tomorrow?") },
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = MiloWhite,
                                unfocusedTextColor = MiloWhite,
                                focusedBorderColor = MiloWhite,
                                unfocusedBorderColor = MiloZinc700
                            )
                        )

                        Button(
                            onClick = {
                                onSaveReflection(
                                    Reflection(LocalDate.of(2026, 9, 4), rating, wentWell, couldImprove, tomorrowFocus)
                                )
                                onDismiss()
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = MiloWhite, contentColor = MiloBlack),
                            shape = RoundedCornerShape(14.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(text = "Save Reflection", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}
