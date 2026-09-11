package com.milo.app.domain.engine

import com.milo.app.domain.models.Activity
import com.milo.app.domain.models.ActivityCategory
import com.milo.app.domain.models.ActivityClassification
import com.milo.app.domain.models.ActivityStatus
import com.milo.app.domain.models.Habit
import com.milo.app.domain.models.HabitCompletion
import com.milo.app.domain.models.MiloEmotion

data class MiloInsight(
    val id: String,
    val title: String,
    val observation: String,
    val evidenceText: String,
    val metricHighlight: String,
    val catEmotion: MiloEmotion,
    val catQuote: String
)

class InsightsEngine {

    fun generateValidatedInsights(
        activities: List<Activity> = emptyList(),
        habits: List<Habit> = emptyList(),
        completions: List<HabitCompletion> = emptyList()
    ): List<MiloInsight> {
        // Zero fake insights on clean launch: require authentic tracked activity data
        val completed = activities.filter { it.status == ActivityStatus.Completed }
        if (completed.size < 3) {
            return emptyList()
        }

        val insights = mutableListOf<MiloInsight>()

        // Insight: Morning vs afternoon focus
        val morningActs = completed.filter { it.startTime.hour in 5..12 }
        if (morningActs.isNotEmpty()) {
            val pct = (morningActs.size * 100) / completed.size
            if (pct >= 50) {
                insights.add(
                    MiloInsight(
                        id = "ins_morning_focus",
                        title = "Morning Flow Window",
                        observation = "$pct% of your completed tasks were accomplished before 1:00 PM.",
                        evidenceText = "Based on ${completed.size} completed schedule blocks",
                        metricHighlight = "$pct% morning focus",
                        catEmotion = MiloEmotion.Proud,
                        catQuote = "Your mornings are consistently strong. Protect those early hours!"
                    )
                )
            }
        }

        // Insight: Active Habit consistency
        if (habits.isNotEmpty()) {
            val doneCompletions = completions.filter { it.isCompleted }
            if (doneCompletions.isNotEmpty()) {
                val topHabit = habits.maxByOrNull { h -> completions.count { it.habitId == h.id && it.isCompleted } }
                if (topHabit != null) {
                    val count = completions.count { it.habitId == topHabit.id && it.isCompleted }
                    if (count > 0) {
                        insights.add(
                            MiloInsight(
                                id = "ins_top_habit",
                                title = "Consistency Anchor",
                                observation = "'${topHabit.name}' is your most reliable daily discipline.",
                                evidenceText = "Based on $count marked completions",
                                metricHighlight = "$count completions",
                                catEmotion = MiloEmotion.Happy,
                                catQuote = "Compounding small habits creates real mastery. Keep going!"
                            )
                        )
                    }
                }
            }
        }

        // Insight: Deep Work proportion
        val deepWorkActs = completed.filter { it.classification == ActivityClassification.DeepWork }
        if (deepWorkActs.isNotEmpty()) {
            val deepMins = deepWorkActs.sumOf { if (it.actualDurationMinutes > 0) it.actualDurationMinutes else it.plannedDurationMinutes }
            insights.add(
                MiloInsight(
                    id = "ins_deep_work",
                    title = "Deep Focus Allocation",
                    observation = "You logged $deepMins minutes in high-intensity deep focus sessions.",
                    evidenceText = "Based on your tracked deep work blocks",
                    metricHighlight = "${deepMins}m deep focus",
                    catEmotion = MiloEmotion.Welcoming,
                    catQuote = "Deep work is rare and valuable. Excellent dedication today."
                )
            )
        }

        return insights
    }
}
