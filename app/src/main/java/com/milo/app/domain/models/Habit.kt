package com.milo.app.domain.models

import java.time.LocalDate

data class Habit(
    val id: String,
    val name: String,
    val category: ActivityCategory,
    val iconName: String = "Activity",
    val targetDaysPerWeek: Int = 7,
    val currentStreakDays: Int = 0,
    val bestStreakDays: Int = 0,
    val consistencyPercentage: Int = 100,
    val createdAtEpochMs: Long = System.currentTimeMillis()
)

data class HabitCompletion(
    val id: String,
    val habitId: String,
    val date: LocalDate,
    val isCompleted: Boolean,
    val note: String? = null
)
