package com.milo.app.domain.engine

import com.milo.app.domain.models.*
import java.time.LocalDate

class ProductivityScoringEngine {

    /**
     * Calculates modular 0-100 Personal Productivity Score.
     * Weights: 25% Task Completion, 20% Time Management, 20% Deep Focus, 20% Habits, 15% Routine
     */
    fun calculateDailyScore(
        date: LocalDate,
        activities: List<Activity>,
        habits: List<Habit>,
        completions: List<HabitCompletion>,
        goals: List<DailyGoal>,
        focusSessions: List<FocusSession>,
        yesterdayScore: Int = 71
    ): ProductivityScore {
        val dayActs = activities.filter { it.date == date }
        val dayCompletions = completions.filter { it.date == date && it.isCompleted }
        val daySessions = focusSessions.filter { it.date == date && it.isCompleted }

        // 1. Completion Score (25%)
        val completionScore = if (dayActs.isNotEmpty()) {
            val completed = dayActs.count { it.status == ActivityStatus.Completed }
            val partial = dayActs.count { it.status == ActivityStatus.PartiallyCompleted }
            val effective = completed + (partial * 0.5f)
            ((effective / dayActs.size) * 100).toInt().coerceIn(0, 100)
        } else {
            70 // Sensible starting baseline
        }

        // 2. Time Management & Priority Score (20%)
        val timeScore = if (dayActs.isNotEmpty()) {
            var weightedEarned = 0f
            var maxWeighted = 0f
            dayActs.forEach { a ->
                val weight = when (a.priority) {
                    ActivityPriority.High -> 3f
                    ActivityPriority.Medium -> 2f
                    ActivityPriority.Low -> 1f
                }
                maxWeighted += weight
                if (a.status == ActivityStatus.Completed) {
                    weightedEarned += weight
                    if (a.actualDurationMinutes in (a.plannedDurationMinutes - 15)..(a.plannedDurationMinutes + 35)) {
                        weightedEarned += 0.2f // Variance bonus
                    }
                } else if (a.status == ActivityStatus.PartiallyCompleted) {
                    weightedEarned += weight * 0.6f
                }
            }
            ((weightedEarned / maxOf(1f, maxWeighted)) * 100).toInt().coerceIn(0, 100)
        } else {
            75
        }

        // 3. Focus Stamina Score (20%)
        val totalFocusMins = daySessions.sumOf { it.durationMinutes }
        val focusScore = when {
            totalFocusMins >= 170 -> 95
            totalFocusMins >= 120 -> 88
            totalFocusMins >= 60 -> 75
            totalFocusMins > 0 -> 65
            else -> {
                val deepActs = dayActs.count { it.classification == ActivityClassification.DeepWork && it.status == ActivityStatus.Completed }
                (55 + deepActs * 12).coerceAtMost(80)
            }
        }

        // 4. Habits Score (20%)
        val habitScore = if (habits.isNotEmpty()) {
            ((dayCompletions.size.toFloat() / habits.size) * 100).toInt().coerceIn(0, 100)
        } else {
            80
        }

        // 5. Routine Consistency Score (15%)
        val morningItem = dayActs.find { it.startTime.hour <= 8 && it.status == ActivityStatus.Completed }
        val morningBonus = if (morningItem != null) 12 else 0
        val goalsHit = goals.count { g ->
            val matching = dayActs.filter { it.category == g.category && it.status == ActivityStatus.Completed }
            val mins = matching.sumOf { if (it.actualDurationMinutes > 0) it.actualDurationMinutes else it.plannedDurationMinutes }
            mins >= g.targetValue
        }
        val consistencyScore = (65 + morningBonus + (goalsHit * 5)).coerceIn(0, 100)

        // Composite weighted score
        val overall = (
            completionScore * 0.25f +
            timeScore * 0.20f +
            focusScore * 0.20f +
            habitScore * 0.20f +
            consistencyScore * 0.15f
        ).toInt().coerceIn(0, 100)

        val deltaYesterday = overall - yesterdayScore

        return ProductivityScore(
            overall = overall,
            completionScore = completionScore,
            timeManagementScore = timeScore,
            focusScore = focusScore,
            habitScore = habitScore,
            consistencyScore = consistencyScore,
            deltaYesterday = deltaYesterday,
            explanation = "Calculated personal metric reflecting task completion, focus depth, habit discipline, and schedule flow. This guides your self-reflection, not external ranking."
        )
    }
}
