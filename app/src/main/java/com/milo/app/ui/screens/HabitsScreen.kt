package com.milo.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.milo.app.domain.models.Habit
import com.milo.app.domain.models.HabitCompletion
import com.milo.app.ui.theme.*
import java.time.LocalDate

@Composable
fun HabitsScreen(
    habits: List<Habit>,
    completions: List<HabitCompletion>,
    onToggleHabit: (String) -> Unit
) {
    val today = LocalDate.of(2026, 9, 4)

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MiloBlack)
            .padding(horizontal = 20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(10.dp))
            Column {
                Text(text = "DAILY DISCIPLINES", style = MaterialTheme.typography.labelSmall, color = MiloZinc500)
                Text(text = "Habit Engine", style = MaterialTheme.typography.headlineLarge, color = MiloWhite)
            }
        }

        items(habits) { h ->
            val isCompletedToday = completions.any { it.habitId == h.id && it.date == today && it.isCompleted }
            val last7Days = (6 downTo 0).map { today.minusDays(it.toLong()) }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MiloCardDark, RoundedCornerShape(24.dp))
                    .border(1.dp, MiloBorderDark, RoundedCornerShape(24.dp))
                    .padding(18.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                            Text(text = h.name, style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold), color = MiloWhite)
                            Box(
                                modifier = Modifier
                                    .background(MiloZinc800, RoundedCornerShape(8.dp))
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text(text = "${h.currentStreakDays}d streak", style = MaterialTheme.typography.labelSmall, color = MiloWhite)
                            }
                        }
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(text = "${h.category.name} • ${h.consistencyPercentage}% consistency", style = MaterialTheme.typography.labelSmall, color = MiloZinc400)
                    }

                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(if (isCompletedToday) MiloWhite else MiloCardDark)
                            .border(1.dp, if (isCompletedToday) MiloWhite else MiloZinc700, CircleShape)
                            .clickable { onToggleHabit(h.id) },
                        contentAlignment = Alignment.Center
                    ) {
                        if (isCompletedToday) {
                            Icon(imageVector = Icons.Default.Check, contentDescription = "Done", tint = MiloBlack, modifier = Modifier.size(18.dp))
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // 7-day completion dots
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    last7Days.forEach { d ->
                        val done = completions.any { it.habitId == h.id && it.date == d && it.isCompleted }
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(text = d.dayOfWeek.name.take(1), style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp), color = MiloZinc500)
                            Spacer(modifier = Modifier.height(4.dp))
                            Box(
                                modifier = Modifier
                                    .size(16.dp)
                                    .clip(CircleShape)
                                    .background(if (done) MiloWhite else MiloZinc800),
                                contentAlignment = Alignment.Center
                            ) {
                                if (done) {
                                    Icon(imageVector = Icons.Default.Check, contentDescription = null, tint = MiloBlack, modifier = Modifier.size(10.dp))
                                }
                            }
                        }
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(72.dp))
        }
    }
}
