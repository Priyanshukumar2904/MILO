package com.milo.app.engine

import com.milo.app.domain.engine.AnalyticsEngine
import com.milo.app.domain.models.*
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.time.LocalDate
import java.time.LocalTime

class AnalyticsEngineTest {

    private lateinit var analyticsEngine: AnalyticsEngine
    private val today = LocalDate.of(2026, 9, 5)

    @Before
    fun setUp() {
        analyticsEngine = AnalyticsEngine()
    }

    @Test
    fun generateDailyReport_computesProductiveTotalsAndObservations() {
        val activities = listOf(
            Activity(
                id = "a1",
                title = "Compiler Architecture",
                category = ActivityCategory.Study,
                date = today,
                startTime = LocalTime.of(9, 0),
                endTime = LocalTime.of(12, 0),
                plannedDurationMinutes = 180,
                actualDurationMinutes = 180,
                status = ActivityStatus.Completed
            ),
            Activity(
                id = "a2",
                title = "Team Meeting",
                category = ActivityCategory.Work,
                date = today,
                startTime = LocalTime.of(14, 0),
                endTime = LocalTime.of(15, 0),
                plannedDurationMinutes = 60,
                actualDurationMinutes = 60,
                status = ActivityStatus.Completed
            )
        )

        val habits = listOf(
            Habit(id = "h1", name = "Morning Focus", category = ActivityCategory.Work)
        )
        val completions = listOf(
            HabitCompletion("c1", "h1", today, isCompleted = true)
        )

        val report = analyticsEngine.generateDailyReport(
            date = today,
            activities = activities,
            habits = habits,
            completions = completions,
            goals = emptyList(),
            focusSessions = emptyList(),
            reflections = emptyList()
        )

        assertEquals(240, report.productiveMinutes)
        assertEquals(180, report.studyMinutes)
        assertEquals(60, report.workMinutes)
        assertEquals(2, report.completedActivitiesCount)
        assertNotNull(report.positiveObservation)
    }

    @Test
    fun generateWeeklyReport_computesTrendAndHabitConsistencyMatrix() {
        val habits = listOf(
            Habit(id = "h1", name = "Daily Exercise", category = ActivityCategory.Exercise)
        )
        val completions = (0..6).map { offset ->
            HabitCompletion("c_$offset", "h1", today.minusDays(offset.toLong()), isCompleted = true)
        }

        val weeklyReport = analyticsEngine.generateWeeklyReport(
            endDate = today,
            activities = emptyList(),
            habits = habits,
            completions = completions,
            goals = emptyList(),
            focusSessions = emptyList()
        )

        assertEquals(7, weeklyReport.dailyScores.size)
        assertTrue(weeklyReport.habitConsistencyMatrix.containsKey("Daily Exercise"))
        assertEquals(7, weeklyReport.habitConsistencyMatrix["Daily Exercise"]?.size)
        assertTrue(weeklyReport.storyHeadline.isNotBlank())
    }

    @Test
    fun generateMonthlyReport_deliversSupportiveNarrative() {
        val report = analyticsEngine.generateMonthlyReport(emptyList())

        assertEquals("September 2026", report.monthYear)
        assertTrue(report.storyNarrative.contains("You showed up"))
        assertTrue(report.score >= 80)
        assertTrue(report.trackedDaysCount > 20)
    }

    @Test
    fun getTrends_returnsMeaningfulPerformanceDeltas() {
        val trends = analyticsEngine.getTrends()
        assertTrue("Trends should include multiple metrics", trends.isNotEmpty())
        val scoreTrend = trends.find { it.metricName == "Productivity Score" }
        assertNotNull(scoreTrend)
        assertTrue(scoreTrend!!.isPositiveTrend)
    }
}
