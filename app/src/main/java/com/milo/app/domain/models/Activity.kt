package com.milo.app.domain.models

import java.time.LocalDate
import java.time.LocalTime

enum class ActivityCategory {
    Study, Work, Exercise, Grooming, Sleep, Food,
    Health, PersonalDevelopment, Relaxation, Entertainment,
    Chores, Travel, Social, Custom
}

enum class ActivityStatus {
    Upcoming, InProgress, Completed, PartiallyCompleted, Skipped, Missed
}

enum class ActivityPriority {
    Low, Medium, High
}

enum class ActivityClassification {
    DeepWork, ShallowWork, Recovery, Maintenance, Leisure
}

data class Activity(
    val id: String,
    val title: String,
    val category: ActivityCategory,
    val customCategoryName: String? = null,
    val date: LocalDate,
    val startTime: LocalTime,
    val endTime: LocalTime,
    val plannedDurationMinutes: Int,
    val actualDurationMinutes: Int = 0,
    val status: ActivityStatus = ActivityStatus.Upcoming,
    val priority: ActivityPriority = ActivityPriority.Medium,
    val classification: ActivityClassification = ActivityClassification.DeepWork,
    val notes: String? = null,
    val reminderEnabled: Boolean = true,
    val isRecurring: Boolean = false,
    val recurrenceRule: String? = null,
    val createdAtEpochMs: Long = System.currentTimeMillis()
)
