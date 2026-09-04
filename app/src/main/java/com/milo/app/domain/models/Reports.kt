package com.milo.app.domain.models

import java.time.LocalDate

data class ProductivityScore(
    val overall: Int, // 0 - 100
    val completionScore: Int,
    val timeManagementScore: Int,
    val focusScore: Int,
    val habitScore: Int,
    val consistencyScore: Int,
    val deltaYesterday: Int,
    val explanation: String
)

data class Reflection(
    val date: LocalDate,
    val rating: Int, // 1 to 5
    val wentWell: String,
    val couldImprove: String,
    val tomorrowFocus: String,
    val timestampEpochMs: Long = System.currentTimeMillis()
)

data class DailyReport(
    val date: LocalDate,
    val score: ProductivityScore,
    val plannedMinutes: Int,
    val productiveMinutes: Int,
    val studyMinutes: Int,
    val workMinutes: Int,
    val exerciseMinutes: Int,
    val entertainmentMinutes: Int,
    val otherMinutes: Int,
    val completedActivitiesCount: Int,
    val totalActivitiesCount: Int,
    val completedHabitsCount: Int,
    val totalHabitsCount: Int,
    val personalBestObservation: String?,
    val positiveObservation: String,
    val improvementOpportunity: String,
    val reflection: Reflection? = null
)

data class WeeklyDayScore(
    val date: LocalDate,
    val dayName: String,
    val score: Int,
    val productiveMinutes: Int
)

data class WeeklyReport(
    val weekStartDate: LocalDate,
    val weekEndDate: LocalDate,
    val averageScore: Int,
    val deltaPreviousWeek: Int,
    val dailyScores: List<WeeklyDayScore>,
    val plannedVsActual: Map<String, Pair<Int, Int>>, // Category -> (Planned, Actual)
    val habitConsistencyMatrix: Map<String, List<Boolean>>, // HabitName -> 7 days
    val avgProductiveHours: Float,
    val avgStudyHours: Float,
    val avgWorkHours: Float,
    val avgExerciseHours: Float,
    val habitAdherenceRate: Int,
    val scheduleAdherenceRate: Int,
    val storyHeadline: String
)

data class MonthlyReport(
    val monthYear: String, // "September 2026"
    val score: Int,
    val deltaPreviousMonth: Int,
    val trackedDaysCount: Int,
    val totalProductiveHours: Int,
    val studyHours: Int,
    val workHours: Int,
    val exerciseSessionsCount: Int,
    val longestStreakDays: Int,
    val biggestImprovement: Pair<String, String>, // "Study consistency", "+23%"
    val biggestOpportunity: Pair<String, String>, // "Sleep consistency", "-8%"
    val bestDayDate: LocalDate,
    val bestDayScore: Int,
    val peakProductivityWindow: String, // "9 AM – 12 PM"
    val storyNarrative: String
)

data class PerformanceTrend(
    val metricName: String,
    val currentDisplay: String,
    val previousDisplay: String,
    val percentageChange: Float,
    val isUpward: Boolean,
    val isPositiveTrend: Boolean
)

data class CategoryScorecard(
    val categoryName: String,
    val scorePercentage: Int,
    val targetHoursWeek: Float,
    val actualHoursWeek: Float
)
