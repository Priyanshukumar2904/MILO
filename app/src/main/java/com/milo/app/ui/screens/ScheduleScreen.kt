package com.milo.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.milo.app.domain.models.Activity
import com.milo.app.ui.theme.*
import java.time.LocalDate

@Composable
fun ScheduleScreen(
    activities: List<Activity>,
    onSelectActivity: (Activity) -> Unit
) {
    var selectedMode by remember { mutableStateOf("Daily") }
    var selectedDate by remember { mutableStateOf(LocalDate.of(2026, 9, 4)) }

    val daysStrip = (0..13).map { selectedDate.minusDays(7).plusDays(it.toLong()) }
    val dayActs = activities.filter { it.date == selectedDate }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MiloBlack)
            .padding(horizontal = 20.dp)
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        // Header & View Selector
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(text = "SCHEDULE & CALENDAR", style = MaterialTheme.typography.labelSmall, color = MiloZinc500)
                Text(text = "Timeblocks", style = MaterialTheme.typography.headlineLarge, color = MiloWhite)
            }

            Row(
                modifier = Modifier
                    .background(MiloCardDark, RoundedCornerShape(16.dp))
                    .padding(4.dp)
            ) {
                listOf("Daily", "Weekly", "Monthly").forEach { mode ->
                    val isSel = (mode == selectedMode)
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(if (isSel) MiloWhite else MiloCardDark)
                            .clickable { selectedMode = mode }
                            .padding(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = mode,
                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp, fontWeight = FontWeight.Bold),
                            color = if (isSel) MiloBlack else MiloZinc400
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Horizontal Date Strip
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            items(daysStrip) { d ->
                val isSel = (d == selectedDate)
                Column(
                    modifier = Modifier
                        .width(52.dp)
                        .clip(RoundedCornerShape(18.dp))
                        .background(if (isSel) MiloWhite else MiloCardDark)
                        .clickable { selectedDate = d }
                        .padding(vertical = 12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = d.dayOfWeek.name.take(3),
                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp),
                        color = if (isSel) MiloBlack else MiloZinc500
                    )
                    Text(
                        text = "${d.dayOfMonth}",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = if (isSel) MiloBlack else MiloWhite
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Activities for date
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(dayActs) { act ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MiloCardDark, RoundedCornerShape(20.dp))
                        .clickable { onSelectActivity(act) }
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text(text = "${act.startTime} – ${act.endTime}", style = MaterialTheme.typography.labelSmall, color = MiloZinc400)
                            Text(text = act.category.name, style = MaterialTheme.typography.labelSmall, color = MiloZinc500)
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(text = act.title, style = MaterialTheme.typography.titleMedium, color = MiloWhite)
                    }
                    Text(
                        text = "${act.plannedDurationMinutes}m",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = MiloWhite
                    )
                }
            }
        }
    }
}
