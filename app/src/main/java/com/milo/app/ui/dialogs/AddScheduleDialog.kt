package com.milo.app.ui.dialogs

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.milo.app.domain.models.*
import com.milo.app.ui.theme.*
import java.time.LocalDate
import java.time.LocalTime

data class QuickPresetItem(
    val id: String,
    val title: String,
    val timeSubtitle: String,
    val category: ActivityCategory,
    val icon: ImageVector,
    val isHabit: Boolean = false
)

@Composable
fun AddScheduleDialog(
    targetDate: LocalDate = LocalDate.now(),
    initialTab: Int = 0, // 0 = Essentials, 1 = Custom
    onAddPreset: (String, LocalDate) -> Unit,
    onAddActivity: (Activity) -> Unit,
    onAddHabit: (Habit) -> Unit,
    onDismiss: () -> Unit
) {
    var selectedTab by remember { mutableStateOf(initialTab) }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight(),
            shape = RoundedCornerShape(28.dp),
            colors = CardDefaults.cardColors(containerColor = MiloCardDark)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Dialog Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "BUILD YOUR SCHEDULE",
                            style = MaterialTheme.typography.labelSmall,
                            color = MiloZinc500
                        )
                        Text(
                            text = "Add to Planner",
                            style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                            color = MiloWhite
                        )
                    }

                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier
                            .size(36.dp)
                            .background(MiloZinc800, CircleShape)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = MiloWhite,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }

                // Tab Selector (Quick Essentials vs Custom Item)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MiloBlack, RoundedCornerShape(14.dp))
                        .padding(4.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(10.dp))
                            .background(if (selectedTab == 0) MiloZinc800 else Color.Transparent)
                            .clickable { selectedTab = 0 }
                            .padding(vertical = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Quick Essentials",
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = if (selectedTab == 0) FontWeight.Bold else FontWeight.Normal
                            ),
                            color = if (selectedTab == 0) MiloWhite else MiloZinc400
                        )
                    }

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(10.dp))
                            .background(if (selectedTab == 1) MiloZinc800 else Color.Transparent)
                            .clickable { selectedTab = 1 }
                            .padding(vertical = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Custom Item",
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = if (selectedTab == 1) FontWeight.Bold else FontWeight.Normal
                            ),
                            color = if (selectedTab == 1) MiloWhite else MiloZinc400
                        )
                    }
                }

                // Tab Contents
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 460.dp)
                ) {
                    if (selectedTab == 0) {
                        QuickEssentialsTab(
                            onAddPreset = {
                                onAddPreset(it, targetDate)
                                onDismiss()
                            }
                        )
                    } else {
                        CustomItemTab(
                            targetDate = targetDate,
                            onAddActivity = {
                                onAddActivity(it)
                                onDismiss()
                            },
                            onAddHabit = {
                                onAddHabit(it)
                                onDismiss()
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun QuickEssentialsTab(
    onAddPreset: (String) -> Unit
) {
    val presets = listOf(
        QuickPresetItem("wake_up", "Wake Up & Hydrate", "06:30 – 07:00 (30m)", ActivityCategory.Grooming, Icons.Default.WbSunny),
        QuickPresetItem("get_ready", "Get Ready & Breakfast", "07:00 – 07:30 (30m)", ActivityCategory.Grooming, Icons.Default.Checklist),
        QuickPresetItem("classes", "Classes & Lectures", "09:00 – 12:00 (3h Deep Work)", ActivityCategory.Study, Icons.Default.School),
        QuickPresetItem("exercise", "Exercise & Workout", "17:00 – 18:00 (1h)", ActivityCategory.Exercise, Icons.Default.FitnessCenter),
        QuickPresetItem("water", "Drink 2.5L Water Daily", "Daily Habit (7 days/wk)", ActivityCategory.Health, Icons.Default.WaterDrop, isHabit = true),
        QuickPresetItem("reading", "Evening Reading", "20:00 – 20:45 (45m)", ActivityCategory.PersonalDevelopment, Icons.AutoMirrored.Filled.MenuBook),
        QuickPresetItem("wind_down", "Wind Down & Sleep", "22:30 – 23:00 (30m)", ActivityCategory.Relaxation, Icons.Default.Bedtime)
    )

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        // Instant full day routine card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onAddPreset("full_day_routine") },
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MiloWhite)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(14.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .background(MiloBlack, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Bolt,
                            contentDescription = null,
                            tint = MiloWhite,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Column {
                        Text(
                            text = "Add Full Day Routine",
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                            color = MiloBlack
                        )
                        Text(
                            text = "Wake up, get ready, classes, gym, reading, sleep",
                            style = MaterialTheme.typography.bodySmall,
                            color = MiloZinc700
                        )
                    }
                }
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add All",
                    tint = MiloBlack
                )
            }
        }

        Spacer(modifier = Modifier.height(2.dp))

        Text(
            text = "INDIVIDUAL ESSENTIALS",
            style = MaterialTheme.typography.labelSmall,
            color = MiloZinc500
        )

        presets.forEach { item ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MiloBlack)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(34.dp)
                                .background(MiloCardDark, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = item.icon,
                                contentDescription = null,
                                tint = MiloWhite,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                        Column {
                            Text(
                                text = item.title,
                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                color = MiloWhite
                            )
                            Text(
                                text = item.timeSubtitle,
                                style = MaterialTheme.typography.bodySmall,
                                color = MiloZinc400
                            )
                        }
                    }

                    Button(
                        onClick = { onAddPreset(item.id) },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MiloZinc800,
                            contentColor = MiloWhite
                        ),
                        shape = RoundedCornerShape(10.dp),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = null,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = if (item.isHabit) "Add Habit" else "Add",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun CustomItemTab(
    targetDate: LocalDate,
    onAddActivity: (Activity) -> Unit,
    onAddHabit: (Habit) -> Unit
) {
    var isHabitMode by remember { mutableStateOf(false) }
    var title by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf(ActivityCategory.Study) }
    var startHour by remember { mutableStateOf("09") }
    var startMin by remember { mutableStateOf("00") }
    var durationMins by remember { mutableStateOf("60") }
    var classification by remember { mutableStateOf(ActivityClassification.DeepWork) }
    var priority by remember { mutableStateOf(ActivityPriority.High) }
    var targetDaysPerWeek by remember { mutableStateOf(7) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    val focusManager = LocalFocusManager.current

    val categories = listOf(
        ActivityCategory.Study,
        ActivityCategory.Work,
        ActivityCategory.Exercise,
        ActivityCategory.Grooming,
        ActivityCategory.Health,
        ActivityCategory.PersonalDevelopment,
        ActivityCategory.Relaxation,
        ActivityCategory.Entertainment
    )

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Custom Mode Switch (Activity vs Habit)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(MiloBlack, RoundedCornerShape(12.dp))
                .padding(3.dp)
        ) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(8.dp))
                    .background(if (!isHabitMode) MiloZinc800 else Color.Transparent)
                    .clickable { isHabitMode = false }
                    .padding(vertical = 6.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Schedule Block",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = if (!isHabitMode) FontWeight.Bold else FontWeight.Normal
                    ),
                    color = if (!isHabitMode) MiloWhite else MiloZinc400
                )
            }

            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(8.dp))
                    .background(if (isHabitMode) MiloZinc800 else Color.Transparent)
                    .clickable { isHabitMode = true }
                    .padding(vertical = 6.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Daily Habit",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = if (isHabitMode) FontWeight.Bold else FontWeight.Normal
                    ),
                    color = if (isHabitMode) MiloWhite else MiloZinc400
                )
            }
        }

        if (errorMessage != null) {
            Text(
                text = errorMessage!!,
                color = MiloAmber,
                style = MaterialTheme.typography.bodySmall
            )
        }

        // Title Input
        OutlinedTextField(
            value = title,
            onValueChange = { title = it; errorMessage = null },
            label = { Text(if (isHabitMode) "Habit Name (e.g. 10k Steps)" else "Activity Title (e.g. Math Revision)") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
            keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Down) }),
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = MiloWhite,
                unfocusedTextColor = MiloWhite,
                focusedBorderColor = MiloWhite,
                unfocusedBorderColor = MiloZinc700,
                focusedLabelColor = MiloWhite,
                unfocusedLabelColor = MiloZinc400
            ),
            shape = RoundedCornerShape(12.dp)
        )

        // Category Selector Chips
        Column {
            Text(
                text = "CATEGORY",
                style = MaterialTheme.typography.labelSmall,
                color = MiloZinc500
            )
            Spacer(modifier = Modifier.height(6.dp))
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(categories) { cat ->
                    val isSel = (cat == selectedCategory)
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(10.dp))
                            .background(if (isSel) MiloWhite else MiloBlack)
                            .border(1.dp, if (isSel) MiloWhite else MiloZinc800, RoundedCornerShape(10.dp))
                            .clickable { selectedCategory = cat }
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = cat.name,
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = if (isSel) FontWeight.Bold else FontWeight.Normal
                            ),
                            color = if (isSel) MiloBlack else MiloZinc400
                        )
                    }
                }
            }
        }

        if (!isHabitMode) {
            // Start Time & Duration
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedTextField(
                    value = startHour,
                    onValueChange = { if (it.length <= 2 && (it.isEmpty() || it.toIntOrNull() in 0..23)) startHour = it },
                    label = { Text("Hour (0-23)") },
                    singleLine = true,
                    modifier = Modifier.weight(1f),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = MiloWhite,
                        unfocusedTextColor = MiloWhite,
                        focusedBorderColor = MiloWhite,
                        unfocusedBorderColor = MiloZinc700,
                        focusedLabelColor = MiloWhite,
                        unfocusedLabelColor = MiloZinc400
                    ),
                    shape = RoundedCornerShape(12.dp)
                )

                OutlinedTextField(
                    value = startMin,
                    onValueChange = { if (it.length <= 2 && (it.isEmpty() || it.toIntOrNull() in 0..59)) startMin = it },
                    label = { Text("Min (0-59)") },
                    singleLine = true,
                    modifier = Modifier.weight(1f),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = MiloWhite,
                        unfocusedTextColor = MiloWhite,
                        focusedBorderColor = MiloWhite,
                        unfocusedBorderColor = MiloZinc700,
                        focusedLabelColor = MiloWhite,
                        unfocusedLabelColor = MiloZinc400
                    ),
                    shape = RoundedCornerShape(12.dp)
                )

                OutlinedTextField(
                    value = durationMins,
                    onValueChange = { if (it.length <= 3 && (it.isEmpty() || it.toIntOrNull() in 5..720)) durationMins = it },
                    label = { Text("Mins") },
                    singleLine = true,
                    modifier = Modifier.weight(1f),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = MiloWhite,
                        unfocusedTextColor = MiloWhite,
                        focusedBorderColor = MiloWhite,
                        unfocusedBorderColor = MiloZinc700,
                        focusedLabelColor = MiloWhite,
                        unfocusedLabelColor = MiloZinc400
                    ),
                    shape = RoundedCornerShape(12.dp)
                )
            }

            // Classification Selection
            Column {
                Text(
                    text = "FOCUS TYPE",
                    style = MaterialTheme.typography.labelSmall,
                    color = MiloZinc500
                )
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf(
                        ActivityClassification.DeepWork to "Deep Work",
                        ActivityClassification.Maintenance to "Routine",
                        ActivityClassification.ShallowWork to "Shallow"
                    ).forEach { (cls, label) ->
                        val isSel = (cls == classification)
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(10.dp))
                                .background(if (isSel) MiloWhite else MiloBlack)
                                .border(1.dp, if (isSel) MiloWhite else MiloZinc800, RoundedCornerShape(10.dp))
                                .clickable { classification = cls }
                                .padding(vertical = 8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = label,
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = if (isSel) FontWeight.Bold else FontWeight.Normal
                                ),
                                color = if (isSel) MiloBlack else MiloZinc400
                            )
                        }
                    }
                }
            }
        } else {
            // Habit Target Days
            Column {
                Text(
                    text = "TARGET DAYS PER WEEK: $targetDaysPerWeek DAYS",
                    style = MaterialTheme.typography.labelSmall,
                    color = MiloZinc500
                )
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    (1..7).forEach { days ->
                        val isSel = (days == targetDaysPerWeek)
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isSel) MiloWhite else MiloBlack)
                                .border(1.dp, if (isSel) MiloWhite else MiloZinc800, RoundedCornerShape(8.dp))
                                .clickable { targetDaysPerWeek = days }
                                .padding(vertical = 8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "$days",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = if (isSel) FontWeight.Bold else FontWeight.Normal
                                ),
                                color = if (isSel) MiloBlack else MiloZinc400
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(4.dp))

        // Submit Button
        Button(
            onClick = {
                val trimmed = title.trim()
                if (trimmed.isEmpty()) {
                    errorMessage = "Please enter a title"
                    return@Button
                }

                val now = System.currentTimeMillis()
                if (isHabitMode) {
                    val newHabit = Habit(
                        id = "hab_${now}",
                        name = trimmed,
                        category = selectedCategory,
                        iconName = "CheckCircle",
                        targetDaysPerWeek = targetDaysPerWeek,
                        createdAtEpochMs = now
                    )
                    onAddHabit(newHabit)
                } else {
                    val h = startHour.toIntOrNull() ?: 9
                    val m = startMin.toIntOrNull() ?: 0
                    val dur = durationMins.toIntOrNull() ?: 60
                    val start = LocalTime.of(h.coerceIn(0, 23), m.coerceIn(0, 59))
                    val end = start.plusMinutes(dur.toLong())

                    val newActivity = Activity(
                        id = "act_${now}",
                        title = trimmed,
                        category = selectedCategory,
                        date = targetDate,
                        startTime = start,
                        endTime = end,
                        plannedDurationMinutes = dur,
                        classification = classification,
                        priority = priority,
                        createdAtEpochMs = now
                    )
                    onAddActivity(newActivity)
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            colors = ButtonDefaults.buttonColors(containerColor = MiloWhite, contentColor = MiloBlack),
            shape = RoundedCornerShape(14.dp)
        ) {
            Text(
                text = if (isHabitMode) "Create Habit" else "Add to Schedule",
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.labelLarge
            )
        }
    }
}
