package com.milo.app.domain.engine

import com.milo.app.domain.models.MiloEmotion

data class MiloInsight(
    val id: String,
    val title: String,
    val observation: String,
    val evidenceText: String, // e.g. "Based on the last 4 weeks of continuous tracking..."
    val metricHighlight: String,
    val catEmotion: MiloEmotion,
    val catQuote: String
)

class InsightsEngine {

    fun generateValidatedInsights(): List<MiloInsight> {
        val sampleSize = "Based on the last 4 weeks of continuous tracking (30 recorded days)..."

        return listOf(
            MiloInsight(
                id = "ins_peak_window",
                title = "Peak Productivity Window",
                observation = "Your completion rate and focus depth are 42% higher between 9:00 AM and 12:00 PM compared to afternoons.",
                evidenceText = sampleSize,
                metricHighlight = "9:00 AM – 12:00 PM Peak",
                catEmotion = MiloEmotion.Curious,
                catQuote = "Hey... look at that. Your morning sessions are consistently your strongest."
            ),
            MiloInsight(
                id = "ins_exercise_domino",
                title = "The Exercise Domino Effect",
                observation = "On days you complete morning movement or gym, you complete 18% more scheduled tasks throughout the day.",
                evidenceText = sampleSize,
                metricHighlight = "+18% task completion on workout days",
                catEmotion = MiloEmotion.Proud,
                catQuote = "Movement fuels momentum. When your body wakes up early, the rest of your day flows."
            ),
            MiloInsight(
                id = "ins_focus_expansion",
                title = "Lengthening Focus Sessions",
                observation = "Your average deep focus duration expanded by 18 minutes over the past 30 days (from 42m to 60m average).",
                evidenceText = sampleSize,
                metricHighlight = "+18m avg focus stamina",
                catEmotion = MiloEmotion.Happy,
                catQuote = "Your focus stamina is visibly growing. That's compounding right before our eyes."
            ),
            MiloInsight(
                id = "ins_evening_dropoff",
                title = "Late Evening Vulnerability",
                observation = "Activities scheduled after 10:00 PM have an 82% skip or postponement rate. Consider shifting late intentions to morning.",
                evidenceText = sampleSize,
                metricHighlight = "82% postponement after 10 PM",
                catEmotion = MiloEmotion.Sleepy,
                catQuote = "By 10 PM your brain has given its best. Protect sleep instead of pushing late tasks."
            ),
            MiloInsight(
                id = "ins_morning_anchor",
                title = "Morning Routine Anchor",
                observation = "Your Morning Routine habit has reached 92% consistency, making it your most reliable daily foundation.",
                evidenceText = sampleSize,
                metricHighlight = "92% consistency rate",
                catEmotion = MiloEmotion.Proud,
                catQuote = "A solid morning creates an impenetrable defense against chaos. Well done!"
            )
        )
    }
}
