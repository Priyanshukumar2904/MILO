package com.milo.app.engine

import com.milo.app.domain.engine.ProductivityScoringEngine
import com.milo.app.domain.models.*
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.time.LocalDate
import java.time.LocalTime

class ProductivityScoringEngineTest {

    private lateinit var scoringEngine: ProductivityScoringEngine
    private val today = LocalDate.of(2026, 9, 5)

    @Before
    fun setUp() {
        scoringEngine = ProductivityScoringEngine()
    }

    @Test
    fun calculateDailyScore_perfectDay_yieldsHighScore() {
        val habits = listOf(
            Habit(id = "h1", name = "Morning Hydration", category = ActivityCategory.Health),
            Habit(id = "h2", name = "Deep Reading", category = ActivityCategory.PersonalDevelopment),
            Habit(id = "h3", name = "Evening Reflection", category = ActivityCategory.Relaxation)
        )

        val completions = listOf(
            HabitCompletion("c1", "h1", today, isCompleted = true),
            HabitCompletion("c2", "h2", today, isCompleted = true),
            HabitCompletion("c3", "h3", today, isCompleted = true)
        )

        val activities = listOf(
            Activity(
                id = "a1",
                title = "Deep Algorithm Study",
                category = ActivityCategory.Study,
                date = today,
                startTime = LocalTime.of(8, 0),
                endTime = LocalTime.of(10, 0),
                plannedDurationMinutes = 120,
                actualDurationMinutes = 120,
                status = ActivityStatus.Completed,
                priority = ActivityPriority.High,
                classification = ActivityClassification.DeepWork
            ),
            Activity(
                id = "a2",
                title = "Core Architecture",
                category = ActivityCategory.Work,
                date = today,
                startTime = LocalTime.of(10, 30),
                endTime = LocalTime.of(12, 0),
                plannedDurationMinutes = 90,
                actualDurationMinutes = 90,
                status = ActivityStatus.Completed,
                priority = ActivityPriority.High,
                classification = ActivityClassification.DeepWork
            )
        )

        val goals = listOf(
            DailyGoal("g1", "Study 90m", ActivityCategory.Study, targetValue = 90)
        )

        val focusSessions = listOf(
            FocusSession("f1", "a1", "Deep Algorithm Study", ActivityCategory.Study, today, LocalTime.of(8, 0), durationMinutes = 120, isCompleted = true),
            FocusSession("f2", "a2", "Core Architecture", ActivityCategory.Work, today, LocalTime.of(10, 30), durationMinutes = 90, isCompleted = true)
        )

        val result = scoringEngine.calculateDailyScore(
            date = today,
            activities = activities,
            habits = habits,
            completions = completions,
            goals = goals,
            focusSessions = focusSessions,
            yesterdayScore = 70
        )

        assertTrue("Score should be 80 or higher on high-performance day", result.overall >= 80)
        assertEquals(100, result.habitScore)
        assertEquals(100, result.completionScore)
        assertTrue(result.deltaYesterday > 0)
    }

    @Test
    fun calculateDailyScore_partialEffort_doesNotDropToZero() {
        val habits = listOf(
            Habit(id = "h1", name = "Morning Run", category = ActivityCategory.Exercise),
            Habit(id = "h2", name = "Read Book", category = ActivityCategory.PersonalDevelopment)
        )

        val completions = listOf(
            HabitCompletion("c1", "h1", today, isCompleted = true),
            HabitCompletion("c2", "h2", today, isCompleted = false)
        )

        val activities = listOf(
            Activity(
                id = "a1",
                title = "Architecture Review",
                category = ActivityCategory.Work,
                date = today,
                startTime = LocalTime.of(14, 0),
                endTime = LocalTime.of(15, 0),
                plannedDurationMinutes = 60,
                actualDurationMinutes = 40,
                status = ActivityStatus.PartiallyCompleted,
                priority = ActivityPriority.Medium
            )
        )

        val result = scoringEngine.calculateDailyScore(
            date = today,
            activities = activities,
            habits = habits,
            completions = completions,
            goals = emptyList(),
            focusSessions = emptyList(),
            yesterdayScore = 65
        )

        assertTrue("Partial effort should produce non-zero baseline score", result.overall > 30)
        assertEquals(50, result.habitScore)
    }

    @Test
    fun calculateDailyScore_emptyDay_hasGracefulFloor() {
        val result = scoringEngine.calculateDailyScore(
            date = today,
            activities = emptyList(),
            habits = emptyList(),
            completions = emptyList(),
            goals = emptyList(),
            focusSessions = emptyList(),
            yesterdayScore = 71
        )

        assertTrue("Empty day retains sensible baseline without punitive crash", result.overall in 60..80)
    }
}
