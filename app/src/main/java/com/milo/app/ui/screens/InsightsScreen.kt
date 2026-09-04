package com.milo.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.milo.app.domain.engine.MiloInsight
import com.milo.app.domain.models.CategoryScorecard
import com.milo.app.domain.models.PerformanceTrend
import com.milo.app.ui.mascot.MiloCompanion
import com.milo.app.ui.theme.*

@Composable
fun InsightsScreen(
    insights: List<MiloInsight>,
    trends: List<PerformanceTrend>,
    scorecards: List<CategoryScorecard>,
    onOpenWeeklyReport: () -> Unit,
    onOpenMonthlyReport: () -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MiloBlack)
            .padding(horizontal = 20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(10.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(text = "BEHAVIORAL INTELLIGENCE", style = MaterialTheme.typography.labelSmall, color = MiloZinc500)
                    Text(text = "Insights & Trends", style = MaterialTheme.typography.headlineLarge, color = MiloWhite)
                }
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(
                        onClick = onOpenWeeklyReport,
                        colors = ButtonDefaults.buttonColors(containerColor = MiloCardDark, contentColor = MiloWhite),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(text = "Weekly", fontSize = 11.sp)
                    }
                    Button(
                        onClick = onOpenMonthlyReport,
                        colors = ButtonDefaults.buttonColors(containerColor = MiloWhite, contentColor = MiloBlack),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(text = "Monthly", fontWeight = FontWeight.Bold, fontSize = 11.sp)
                    }
                }
            }
        }

        // Milo Wisdom Card
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MiloCardDark, RoundedCornerShape(24.dp))
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                MiloCompanion(size = 56.dp)
                Column {
                    Text(text = "MILO'S OBSERVATION", style = MaterialTheme.typography.labelSmall, color = MiloZinc500)
                    Text(
                        text = "“Movement fuels momentum. When your body moves early, the rest of your day flows.”",
                        style = MaterialTheme.typography.titleMedium,
                        color = MiloWhite
                    )
                }
            }
        }

        // Trends Velocity
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MiloCardDark, RoundedCornerShape(24.dp))
                    .padding(18.dp)
            ) {
                Text(text = "PERFORMANCE VELOCITY TRENDS", style = MaterialTheme.typography.labelSmall, color = MiloZinc500)
                Spacer(modifier = Modifier.height(10.dp))

                trends.forEach { t ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 6.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(text = t.metricName, style = MaterialTheme.typography.titleMedium, color = MiloWhite)
                            Text(text = "${t.currentDisplay} vs ${t.previousDisplay}", style = MaterialTheme.typography.labelSmall, color = MiloZinc500)
                        }
                        Text(
                            text = if (t.percentageChange > 0) "+${t.percentageChange}%" else "${t.percentageChange}%",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = if (t.isPositiveTrend) MiloGreen else MiloZinc400
                        )
                    }
                }
            }
        }

        // Life Domain Scorecard
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MiloCardDark, RoundedCornerShape(24.dp))
                    .padding(18.dp)
            ) {
                Text(text = "PERSONAL DEVELOPMENT SCORECARD", style = MaterialTheme.typography.labelSmall, color = MiloZinc500)
                Spacer(modifier = Modifier.height(10.dp))

                scorecards.forEach { sc ->
                    Column(modifier = Modifier.padding(vertical = 4.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(text = sc.categoryName, style = MaterialTheme.typography.titleMedium, color = MiloWhite)
                            Text(text = "${sc.scorePercentage}%", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold), color = MiloWhite)
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        LinearProgressIndicator(
                            progress = { sc.scorePercentage / 100f },
                            modifier = Modifier.fillMaxWidth().height(4.dp),
                            color = MiloWhite,
                            trackColor = MiloZinc800
                        )
                    }
                }
            }
        }

        // Verified Behavioral Insights
        item {
            Text(text = "VALIDATED BEHAVIORAL PATTERNS", style = MaterialTheme.typography.labelSmall, color = MiloZinc500)
        }

        items(insights) { ins ->
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MiloCardDark, RoundedCornerShape(24.dp))
                    .padding(18.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(text = ins.title, style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold), color = MiloWhite)
                    Box(
                        modifier = Modifier
                            .background(MiloZinc800, RoundedCornerShape(8.dp))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(text = ins.metricHighlight, style = MaterialTheme.typography.labelSmall, color = MiloWhite)
                    }
                }
                Text(text = ins.observation, style = MaterialTheme.typography.bodyMedium, color = MiloZinc400)
                Text(text = ins.evidenceText, style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp), color = MiloZinc500)
            }
        }

        item {
            Spacer(modifier = Modifier.height(72.dp))
        }
    }
}
