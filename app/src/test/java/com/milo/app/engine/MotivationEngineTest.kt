package com.milo.app.engine

import com.milo.app.domain.engine.MotivationEngine
import com.milo.app.domain.models.MiloEmotion
import com.milo.app.domain.models.ProductivityScore
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class MotivationEngineTest {

    private lateinit var motivationEngine: MotivationEngine

    @Before
    fun setUp() {
        motivationEngine = MotivationEngine()
    }

    @Test
    fun getDailyFeedback_freshStartZeroActivities_returnsWelcomingFeedback() {
        val cleanScore = ProductivityScore(0, 0, 0, 0, 0, 0, 0, "")
        val feedback = motivationEngine.getDailyFeedback(
            score = cleanScore,
            completedActivities = 0,
            totalActivities = 0
        )

        assertEquals("Ready to start today?", feedback.headline)
        assertEquals(MiloEmotion.Welcoming, feedback.catEmotion)
        assertEquals("Fresh Start", feedback.badgeLabel)
        assertTrue(feedback.isProgressPositive)
    }

    @Test
    fun getDailyFeedback_positiveDelta_returnsProudFeedback() {
        val positiveScore = ProductivityScore(85, 90, 80, 85, 80, 85, deltaYesterday = 12, "")
        val feedback = motivationEngine.getDailyFeedback(
            score = positiveScore,
            completedActivities = 3,
            totalActivities = 4
        )

        assertEquals(MiloEmotion.Proud, feedback.catEmotion)
        assertEquals("+12% Better", feedback.badgeLabel)
        assertTrue(feedback.isProgressPositive)
    }

    @Test
    fun getDailyFeedback_dipInScore_returnsEncouragingFeedback() {
        val dipScore = ProductivityScore(40, 40, 40, 40, 40, 40, deltaYesterday = -5, "")
        val feedback = motivationEngine.getDailyFeedback(
            score = dipScore,
            completedActivities = 1,
            totalActivities = 4
        )

        assertEquals(MiloEmotion.Encouraging, feedback.catEmotion)
        assertEquals("Patience", feedback.badgeLabel)
        assertFalse(feedback.isProgressPositive)
    }
}
