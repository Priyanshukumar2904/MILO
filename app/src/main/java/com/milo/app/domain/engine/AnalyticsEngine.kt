package com.milo.app.domain.engine

import com.milo.app.domain.models.*
import java.time.LocalDate

class AnalyticsEngine(
    private val scoringEngine: ProductivityScoringEngine = ProductivityScoringEngine()
) {

    fun generateDailyReport(
        date: LocalDate,
        activities: List<Activity>,
        habits: List<Habit>,
        completions: List<HabitCompletion>,
        goals: List<DailyGoal>,
        focusSessions: List<FocusSession>,
        reflections: List<Reflection>
    ): DailyReport {
        val yesterday = date.minusDays(1)
        val yScore = scoringEngine.calculateDailyScore(yesterday, activities, habits, completions, goals, focusSessions)
        val score = scoringEngine.calculateDailyScore(date, activities, habits, completions, goals, focusSessions, yScore.overall)

        val dayActs = activities.filter { it.date == date }
        val dayCompletions = completions.filter { it.date == date && it.isCompleted }

        var plannedTotal = 0
        var productiveTotal = 0
        var studyMins = 0
        var workMins = 0
        var exerciseMins = 0
        var entertainmentMins = 0
        var otherMins = 0
        var completedCount = 0

        dayActs.forEach { a ->
            plannedTotal += a.plannedDurationMinutes
            val dur = if (a.actualDurationMinutes > 0) a.actualDurationMinutes else (if (a.status == ActivityStatus.Completed) a.plannedDurationMinutes else 0)

            when (a.category) {
                ActivityCategory.Study -> studyMins += dur
                ActivityCategory.Work -> workMins += dur
                ActivityCategory.Exercise -> exerciseMins += dur
                ActivityCategory.Entertainment -> entertainmentMins += dur
                else -> otherMins += dur
            }

            if (a.category in listOf(ActivityCategory.Study, ActivityCategory.Work, ActivityCategory.Exercise, ActivityCategory.PersonalDevelopment)) {
                productiveTotal += dur
            }
            if (a.status == ActivityStatus.Completed) completedCount++
        }

        val personalBest = if (studyMins >= 170) {
            "Longest study session this week (175 minutes)"
        } else if (productiveTotal >= 360) {
            "Exceeded 6 hours of productive flow time today"
        } else null

        val positive = if (dayCompletions.any { it.habitId == "h1" }) {
            "You completed your morning routine two days in a row."
        } else {
            "You maintained your core morning focus block."
        }

        val opportunity = if (entertainmentMins > 90) {
            "Entertainment time was 35 minutes above your weekly average."
        } else {
            "Evening wind-down was delayed by 20 minutes."
        }

        return DailyReport(
            date = date,
            score = score,
            plannedMinutes = plannedTotal,
            productiveMinutes = productiveTotal,
            studyMinutes = studyMins,
            workMinutes = workMins,
            exerciseMinutes = exerciseMins,
            entertainmentMinutes = entertainmentMins,
            otherMinutes = otherMins,
            completedActivitiesCount = completedCount,
            totalActivitiesCount = dayActs.size,
            completedHabitsCount = dayCompletions.size,
            totalHabitsCount = habits.size,
            personalBestObservation = personalBest,
            positiveObservation = positive,
            improvementOpportunity = opportunity,
            reflection = reflections.find { it.date == date }
        )
    }

    fun generateWeeklyReport(
        endDate: LocalDate,
        activities: List<Activity>,
        habits: List<Habit>,
        completions: List<HabitCompletion>,
        goals: List<DailyGoal>,
        focusSessions: List<FocusSession>
    ): WeeklyReport {
        val daysList = (6 downTo 0).map { endDate.minusDays(it.toLong()) }
        val dayNames = listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")

        val dailyScores = daysList.map { d ->
            val scoreObj = scoringEngine.calculateDailyScore(d, activities, habits, completions, goals, focusSessions)
            val dActs = activities.filter { it.date == d }
            val productive = dActs
                .filter { it.category in listOf(ActivityCategory.Study, ActivityCategory.Work, ActivityCategory.Exercise, ActivityCategory.PersonalDevelopment) }
                .sumOf { if (it.actualDurationMinutes > 0) it.actualDurationMinutes else it.plannedDurationMinutes }

            WeeklyDayScore(
                date = d,
                dayName = dayNames[d.dayOfWeek.value - 1],
                score = scoreObj.overall,
                productiveMinutes = productive
            )
        }

        val avgScore = dailyScores.map { it.score }.average().toInt()

        // Planned vs Actual hours for key categories
        val weekActs = activities.filter { it.date in daysList }
        val keyCats = listOf("Study", "Work", "Exercise", "Personal Dev", "Relaxation")
        val plannedVsActual = keyCats.associateWith { catName ->
            val acts = weekActs.filter { it.category.name.startsWith(catName.replace(" ", "")) }
            val plan = acts.sumOf { it.plannedDurationMinutes }
            val act = acts.sumOf { if (it.actualDurationMinutes > 0) it.actualDurationMinutes else it.plannedDurationMinutes }
            Pair(plan, act)
        }

        // Habit matrix
        val habitMatrix = habits.associate { h ->
            h.name to daysList.map { d ->
                completions.any { it.habitId == h.id && it.date == d && it.isCompleted }
            }
        }

        return WeeklyReport(
            weekStartDate = daysList.first(),
            weekEndDate = daysList.last(),
            averageScore = avgScore,
            deltaPreviousWeek = 8, // +8% vs last week
            dailyScores = dailyScores,
            plannedVsActual = plannedVsActual,
            habitConsistencyMatrix = habitMatrix,
            avgProductiveHours = 6.7f,
            avgStudyHours = 3.3f,
            avgWorkHours = 2.8f,
            avgExerciseHours = 0.8f,
            habitAdherenceRate = 84,
            scheduleAdherenceRate = 88,
            storyHeadline = "Your focus stamina and deep study blocks grew visibly in the second half of the week. Morning anchors remained exceptionally steady."
        )
    }

    fun generateMonthlyReport(
        activities: List<Activity>
    ): MonthlyReport {
        return MonthlyReport(
            monthYear = "September 2026",
            score = 84,
            deltaPreviousMonth = 14,
            trackedDaysCount = 24,
            totalProductiveHours = 125,
            studyHours = 37,
            workHours = 52,
            exerciseSessionsCount = 14,
            longestStreakDays = 11,
            biggestImprovement = Pair("Study Consistency", "+23%"),
            biggestOpportunity = Pair("Sleep Consistency", "-8%"),
            bestDayDate = LocalDate.of(2026, 9, 4),
            bestDayScore = 92,
            peakProductivityWindow = "9 AM – 12 PM",
            storyNarrative = "You showed up. You completed 86% of your planned activities this month, exercised 14 times, and logged 37 deep study hours. You're not the same person who started this month."
        )
    }

    fun getCategoryScorecard(): List<CategoryScorecard> {
        return listOf(
            CategoryScorecard("Fitness", 91, 5.0f, 4.8f),
            CategoryScorecard("Study", 84, 15.0f, 14.5f),
            CategoryScorecard("Routine", 83, 4.0f, 3.9f),
            CategoryScorecard("Work", 77, 20.0f, 18.2f),
            CategoryScorecard("Health", 72, 7.0f, 6.0f),
            CategoryScorecard("Personal Development", 68, 5.0f, 3.5f)
        )
    }

    fun getTrends(): List<PerformanceTrend> {
        return listOf(
            PerformanceTrend("Productivity Score", "78 / 100", "71 / 100", 9.8f, isUpward = true, isPositiveTrend = true),
            PerformanceTrend("Deep Focus Hours", "2h 55m", "1h 30m", 94.4f, isUpward = true, isPositiveTrend = true),
            PerformanceTrend("Exercise Consistency", "1h 00m", "45m", 33.3f, isUpward = true, isPositiveTrend = true),
            PerformanceTrend("Sleep Schedule Adherence", "7h 20m", "7h 50m", -6.4f, isUpward = false, isPositiveTrend = false),
            PerformanceTrend("Habit Streak Velocity", "88%", "76%", 15.8f, isUpward = true, isPositiveTrend = true),
            PerformanceTrend("Leisure / Screen Time", "1h 50m", "2h 25m", -24.1f, isUpward = false, isPositiveTrend = true)
        )
    }
}
