package com.milo.app.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.milo.app.domain.models.*
import java.time.LocalDate
import java.time.LocalTime

@Entity(tableName = "activities")
data class ActivityEntity(
    @PrimaryKey val id: String,
    val title: String,
    val category: ActivityCategory,
    val customCategoryName: String?,
    val date: LocalDate,
    val startTime: LocalTime,
    val endTime: LocalTime,
    val plannedDurationMinutes: Int,
    val actualDurationMinutes: Int,
    val status: ActivityStatus,
    val priority: ActivityPriority,
    val classification: ActivityClassification,
    val notes: String?,
    val reminderEnabled: Boolean,
    val isRecurring: Boolean,
    val recurrenceRule: String?,
    val createdAtEpochMs: Long
)

@Entity(tableName = "habits")
data class HabitEntity(
    @PrimaryKey val id: String,
    val name: String,
    val category: ActivityCategory,
    val iconName: String,
    val targetDaysPerWeek: Int,
    val currentStreakDays: Int,
    val bestStreakDays: Int,
    val consistencyPercentage: Int,
    val createdAtEpochMs: Long
)

@Entity(tableName = "habit_completions")
data class HabitCompletionEntity(
    @PrimaryKey val id: String,
    val habitId: String,
    val date: LocalDate,
    val isCompleted: Boolean,
    val note: String?
)

@Entity(tableName = "goals")
data class GoalEntity(
    @PrimaryKey val id: String,
    val title: String,
    val category: ActivityCategory,
    val targetValue: Int,
    val unit: GoalUnit,
    val isMorningRoutine: Boolean
)

@Entity(tableName = "focus_sessions")
data class FocusSessionEntity(
    @PrimaryKey val id: String,
    val activityId: String?,
    val title: String,
    val category: ActivityCategory,
    val date: LocalDate,
    val startTime: LocalTime,
    val durationMinutes: Int,
    val isCompleted: Boolean,
    val notes: String?
)

@Entity(tableName = "reflections")
data class ReflectionEntity(
    @PrimaryKey val date: LocalDate,
    val rating: Int,
    val wentWell: String,
    val couldImprove: String,
    val tomorrowFocus: String,
    val timestampEpochMs: Long
)

@Entity(tableName = "achievements")
data class AchievementEntity(
    @PrimaryKey val id: String,
    val title: String,
    val description: String,
    val category: String,
    val isUnlocked: Boolean,
    val unlockedDate: String?,
    val progress: Int,
    val maxProgress: Int,
    val previousStat: String?,
    val currentStat: String?
)

@Entity(tableName = "personal_records")
data class PersonalRecordEntity(
    @PrimaryKey val id: String,
    val title: String,
    val valueDisplay: String,
    val rawNumericValue: Float,
    val dateAchieved: String,
    val previousValueDisplay: String?,
    val category: String
)

@Entity(tableName = "sync_queue")
data class SyncQueueEntity(
    @PrimaryKey(autoGenerate = true) val syncId: Long = 0,
    val entityType: String,
    val entityId: String,
    val operation: String, // "INSERT", "UPDATE", "DELETE"
    val payloadJson: String,
    val timestampEpochMs: Long = System.currentTimeMillis()
)
