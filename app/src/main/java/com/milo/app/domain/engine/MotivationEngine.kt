package com.milo.app.domain.engine

import com.milo.app.domain.models.*

data class MotivationFeedback(
    val headline: String,
    val supportingText: String,
    val catEmotion: MiloEmotion,
    val catQuote: String,
    val badgeLabel: String,
    val isProgressPositive: Boolean
)

class MotivationEngine {

    fun getDailyFeedback(
        score: ProductivityScore,
        completedActivities: Int,
        totalActivities: Int
    ): MotivationFeedback {
        val delta = score.deltaYesterday

        return when {
            delta >= 5 -> MotivationFeedback(
                headline = "+$delta% compared with yesterday",
                supportingText = "You completed $completedActivities activities with steady focus today.",
                catEmotion = MiloEmotion.Proud,
                catQuote = "Look at that pace! You moved forward today.",
                badgeLabel = "+$delta% Better",
                isProgressPositive = true
            )
            delta in 1..4 -> MotivationFeedback(
                headline = "Small improvement. Still improvement.",
                supportingText = "Compounding consistency is what creates real life changes.",
                catEmotion = MiloEmotion.Happy,
                catQuote = "A little better than yesterday. That's the entire formula.",
                badgeLabel = "+$delta% Steady",
                isProgressPositive = true
            )
            delta == 0 -> MotivationFeedback(
                headline = "Solid and consistent.",
                supportingText = "You maintained your baseline across your primary schedule.",
                catEmotion = MiloEmotion.Calm,
                catQuote = "Consistency is quiet strength. You held the line.",
                badgeLabel = "Stable",
                isProgressPositive = true
            )
            delta in -10..-1 -> MotivationFeedback(
                headline = "A slower day does not erase your progress.",
                supportingText = "You still completed $completedActivities activities. Rest and reset for tomorrow.",
                catEmotion = MiloEmotion.Encouraging,
                catQuote = "Today didn't go exactly as planned. That's okay. You still showed up.",
                badgeLabel = "Patience",
                isProgressPositive = false
            )
            else -> MotivationFeedback(
                headline = "Rough day? Tomorrow is another opportunity.",
                supportingText = "No guilt, no catch-up stress. Let's start with one thing tomorrow.",
                catEmotion = MiloEmotion.Encouraging,
                catQuote = "Be gentle with yourself. Rest matters too.",
                badgeLabel = "Reset",
                isProgressPositive = false
            )
        }
    }

    fun getWelcomeBackFeedback(): MotivationFeedback {
        return MotivationFeedback(
            headline = "Good to see you again.",
            supportingText = "You don't need to catch up. Just start today.",
            catEmotion = MiloEmotion.Welcoming,
            catQuote = "Welcome back! No backlog anxiety. Let's do one activity.",
            badgeLabel = "Fresh Start",
            isProgressPositive = true
        )
    }
}
