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

        val personalBest = if (studyMins >= 120) {
            "Strong study block of $studyMins minutes logged today"
        } else if (productiveTotal >= 240) {
            "Logged over 4 hours of focused activity today"
        } else null

        val positive = if (completedCount > 0) {
            "You completed $completedCount activities on your schedule today."
        } else if (dayCompletions.isNotEmpty()) {
            "You marked ${dayCompletions.size} habits today."
        } else {
            "Ready for a fresh start today."
        }

        val opportunity = if (plannedTotal > 0 && completedCount == 0) {
            "Tasks planned but not yet marked complete."
        } else {
            "Maintain steady pacing without rushing."
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

        val weekActs = activities.filter { it.date in daysList }
        val avgScore = if (weekActs.isNotEmpty()) dailyScores.map { it.score }.average().toInt() else 0

        val keyCats = listOf("Study", "Work", "Exercise", "Personal Dev", "Relaxation")
        val plannedVsActual = keyCats.associateWith { catName ->
            val acts = weekActs.filter { it.category.name.startsWith(catName.replace(" ", "")) }
            val plan = acts.sumOf { it.plannedDurationMinutes }
            val act = acts.sumOf { if (it.actualDurationMinutes > 0) it.actualDurationMinutes else (if (it.status == ActivityStatus.Completed) it.plannedDurationMinutes else 0) }
            Pair(plan, act)
        }

        val habitMatrix = habits.associate { h ->
            h.name to daysList.map { d ->
                completions.any { it.habitId == h.id && it.date == d && it.isCompleted }
            }
        }

        val totalProdMins = dailyScores.sumOf { it.productiveMinutes }
        val avgProdHours = (totalProdMins / 60f) / 7f

        val completedActs = weekActs.count { it.status == ActivityStatus.Completed }
        val adherence = if (weekActs.isNotEmpty()) (completedActs * 100) / weekActs.size else 0

        return WeeklyReport(
            weekStartDate = daysList.first(),
            weekEndDate = daysList.last(),
            averageScore = avgScore,
            deltaPreviousWeek = 0,
            dailyScores = dailyScores,
            plannedVsActual = plannedVsActual,
            habitConsistencyMatrix = habitMatrix,
            avgProductiveHours = (avgProdHours * 10).toInt() / 10f,
            avgStudyHours = 0f,
            avgWorkHours = 0f,
            avgExerciseHours = 0f,
            habitAdherenceRate = if (habits.isNotEmpty()) 0 else 0,
            scheduleAdherenceRate = adherence,
            storyHeadline = if (weekActs.isNotEmpty()) 
                "You logged $completedActs completed tasks this week. Keep showing up daily!" 
            else 
                "A clean week awaits. Add your daily routine to start building your momentum."
        )
    }

    fun generateMonthlyReport(
        activities: List<Activity>
    ): MonthlyReport {
        val completed = activities.filter { it.status == ActivityStatus.Completed }
        val totalMins = completed.sumOf { if (it.actualDurationMinutes > 0) it.actualDurationMinutes else it.plannedDurationMinutes }
        val totalHours = totalMins / 60
        val studyMins = completed.filter { it.category == ActivityCategory.Study }.sumOf { if (it.actualDurationMinutes > 0) it.actualDurationMinutes else it.plannedDurationMinutes }
        val workMins = completed.filter { it.category == ActivityCategory.Work }.sumOf { if (it.actualDurationMinutes > 0) it.actualDurationMinutes else it.plannedDurationMinutes }
        val exerciseCount = completed.count { it.category == ActivityCategory.Exercise }
        val trackedDays = activities.map { it.date }.distinct().size

        val currentMonthName = LocalDate.now().month.name.lowercase().replaceFirstChar { it.uppercase() }
        val currentYear = LocalDate.now().year

        return MonthlyReport(
            monthYear = "$currentMonthName $currentYear",
            score = if (activities.isNotEmpty()) (completed.size * 100) / activities.size else 0,
            deltaPreviousMonth = 0,
            trackedDaysCount = trackedDays,
            totalProductiveHours = totalHours,
            studyHours = studyMins / 60,
            workHours = workMins / 60,
            exerciseSessionsCount = exerciseCount,
            longestStreakDays = 0,
            biggestImprovement = Pair("Routine", "Fresh Start"),
            biggestOpportunity = Pair("Consistency", "Day 1"),
            bestDayDate = LocalDate.now(),
            bestDayScore = 0,
            peakProductivityWindow = if (completed.isNotEmpty()) "Morning" else "Building profile",
            storyNarrative = if (activities.isNotEmpty()) 
                "You logged $totalHours productive hours across $trackedDays days this month. Consistency is building!" 
            else 
                "Ready to begin your journey. Add schedule blocks and habits to watch your life progress unfold."
        )
    }

    fun getCategoryScorecard(activities: List<Activity> = emptyList()): List<CategoryScorecard> {
        if (activities.isEmpty()) return emptyList()

        val grouped = activities.groupBy { it.category }
        return grouped.map { (cat, acts) ->
            val plannedHours = acts.sumOf { it.plannedDurationMinutes } / 60f
            val actualHours = acts.sumOf { if (it.actualDurationMinutes > 0) it.actualDurationMinutes else (if (it.status == ActivityStatus.Completed) it.plannedDurationMinutes else 0) } / 60f
            val completedCount = acts.count { it.status == ActivityStatus.Completed }
            val score = if (acts.isNotEmpty()) (completedCount * 100) / acts.size else 0

            CategoryScorecard(
                categoryName = cat.name,
                scorePercentage = score,
                targetHoursWeek = (plannedHours * 10).toInt() / 10f,
                actualHoursWeek = (actualHours * 10).toInt() / 10f
            )
        }
    }

    fun getTrends(activities: List<Activity> = emptyList(), currentScore: ProductivityScore = ProductivityScore(0, 0, 0, 0, 0, 0, 0, "")): List<PerformanceTrend> {
        if (activities.isEmpty()) return emptyList()

        val completed = activities.filter { it.status == ActivityStatus.Completed }
        val totalMins = completed.sumOf { if (it.actualDurationMinutes > 0) it.actualDurationMinutes else it.plannedDurationMinutes }
        val hours = totalMins / 60
        val mins = totalMins % 60
        val timeDisplay = if (hours > 0) "${hours}h ${mins}m" else "${mins}m"

        return listOf(
            PerformanceTrend(
                metricName = "Day Score",
                currentDisplay = "${currentScore.overall} / 100",
                previousDisplay = "Baseline",
                percentageChange = currentScore.deltaYesterday.toFloat(),
                isUpward = currentScore.deltaYesterday >= 0,
                isPositiveTrend = true
            ),
            PerformanceTrend(
                metricName = "Completed Focus Time",
                currentDisplay = timeDisplay,
                previousDisplay = "0m",
                percentageChange = 100f,
                isUpward = true,
                isPositiveTrend = true
            )
        )
    }
}
