package com.milo.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.milo.app.domain.models.Activity
import com.milo.app.domain.models.Habit
import com.milo.app.domain.models.HabitCompletion
import com.milo.app.ui.components.TimelineNode
import com.milo.app.ui.theme.*
import java.time.LocalDate

@Composable
fun PlannerScreen(
    activities: List<Activity>,
    habits: List<Habit>,
    habitCompletions: List<HabitCompletion>,
    onToggleActivity: (Activity) -> Unit,
    onStartTimer: (Activity) -> Unit,
    onSelectActivity: (Activity) -> Unit,
    onToggleHabit: (String) -> Unit
) {
    var selectedSection by remember { mutableStateOf("Schedule") }
    var selectedDate by remember { mutableStateOf(LocalDate.now()) }

    val daysStrip = remember(selectedDate) {
        (-3..7).map { LocalDate.now().plusDays(it.toLong()) }
    }
    val dayActs = activities.filter { it.date == selectedDate }
    val today = LocalDate.now()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MiloBlack)
            .padding(horizontal = 20.dp)
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        // Header & Section Switcher
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "PLANNER & DISCIPLINES",
                    style = MaterialTheme.typography.labelSmall,
                    color = MiloZinc500
                )
                Text(
                    text = if (selectedSection == "Schedule") "Schedule" else "Habits",
                    style = MaterialTheme.typography.headlineLarge,
                    color = MiloWhite
                )
            }

            Row(
                modifier = Modifier
                    .background(MiloCardDark, RoundedCornerShape(16.dp))
                    .padding(4.dp)
            ) {
                listOf("Schedule", "Habits").forEach { section ->
                    val isSel = (section == selectedSection)
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(if (isSel) MiloWhite else MiloCardDark)
                            .clickable { selectedSection = section }
                            .padding(horizontal = 14.dp, vertical = 8.dp)
                    ) {
                        Text(
                            text = section,
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = FontWeight.Bold
                            ),
                            color = if (isSel) MiloBlack else MiloZinc400
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (selectedSection == "Schedule") {
            // Horizontal Date Strip
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(daysStrip) { d ->
                    val isSel = (d == selectedDate)
                    val isToday = (d == today)
                    Column(
                        modifier = Modifier
                            .width(56.dp)
                            .clip(RoundedCornerShape(18.dp))
                            .background(if (isSel) MiloWhite else MiloCardDark)
                            .border(
                                1.dp,
                                if (isToday && !isSel) MiloZinc600 else MiloBorderDark,
                                RoundedCornerShape(18.dp)
                            )
                            .clickable { selectedDate = d }
                            .padding(vertical = 12.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = d.dayOfWeek.name.take(3),
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontSize = 11.sp,
                                fontWeight = if (isSel) FontWeight.Bold else FontWeight.Normal
                            ),
                            color = if (isSel) MiloBlack else MiloZinc500
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "${d.dayOfMonth}",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp
                            ),
                            color = if (isSel) MiloBlack else MiloWhite
                        )
                        if (isToday) {
                            Box(
                                modifier = Modifier
                                    .padding(top = 4.dp)
                                    .size(4.dp)
                                    .background(if (isSel) MiloBlack else MiloWhite, CircleShape)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Activities list
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                if (dayActs.isEmpty()) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 40.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = "No activities scheduled",
                                    style = MaterialTheme.typography.titleMedium,
                                    color = MiloZinc400
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "Enjoy your free flow or check off your daily habits.",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MiloZinc600
                                )
                            }
                        }
                    }
                } else {
                    itemsIndexed(dayActs) { index, activity ->
                        TimelineNode(
                            activity = activity,
                            isLast = (index == dayActs.size - 1),
                            onToggleStatus = { onToggleActivity(activity) },
                            onStartTimer = { onStartTimer(activity) },
                            onSelect = { onSelectActivity(activity) }
                        )
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(80.dp))
                }
            }
        } else {
            // Habits section
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                item {
                    Text(
                        text = "ACTIVE DISCIPLINES",
                        style = MaterialTheme.typography.labelSmall,
                        color = MiloZinc500
                    )
                }

                items(habits) { h ->
                    val isCompletedToday = habitCompletions.any { it.habitId == h.id && it.date == today && it.isCompleted }
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
                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = h.name,
                                        style = MaterialTheme.typography.titleMedium.copy(
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 17.sp
                                        ),
                                        color = MiloWhite
                                    )
                                    Box(
                                        modifier = Modifier
                                            .background(MiloZinc800, RoundedCornerShape(8.dp))
                                            .padding(horizontal = 8.dp, vertical = 3.dp)
                                    ) {
                                        Text(
                                            text = "${h.currentStreakDays}d streak",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = MiloWhite
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "${h.category.name} • ${h.consistencyPercentage}% consistency",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MiloZinc400
                                )
                            }

                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(CircleShape)
                                    .background(if (isCompletedToday) MiloWhite else MiloCardDark)
                                    .border(1.5.dp, if (isCompletedToday) MiloWhite else MiloZinc700, CircleShape)
                                    .clickable { onToggleHabit(h.id) },
                                contentAlignment = Alignment.Center
                            ) {
                                if (isCompletedToday) {
                                    Icon(
                                        imageVector = Icons.Default.Check,
                                        contentDescription = "Done",
                                        tint = MiloBlack,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // 7-day completion dots
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Past 7 Days",
                                style = MaterialTheme.typography.labelSmall,
                                color = MiloZinc500
                            )
                            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                last7Days.forEach { d ->
                                    val done = habitCompletions.any { it.habitId == h.id && it.date == d && it.isCompleted }
                                    Box(
                                        modifier = Modifier
                                            .size(14.dp)
                                            .clip(CircleShape)
                                            .background(if (done) MiloWhite else MiloZinc800)
                                    )
                                }
                            }
                        }
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(80.dp))
                }
            }
        }
    }
}
