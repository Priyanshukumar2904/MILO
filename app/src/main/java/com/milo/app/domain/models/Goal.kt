package com.milo.app.domain.models

enum class GoalUnit {
    Minutes, Count, Boolean
}

data class DailyGoal(
    val id: String,
    val title: String,
    val category: ActivityCategory,
    val targetValue: Int,
    val unit: GoalUnit = GoalUnit.Minutes,
    val isMorningRoutine: Boolean = false
)
