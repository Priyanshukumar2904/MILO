package com.milo.app.domain.models

enum class AchievementCategory {
    Consistency, Focus, Habits, Improvement, Routine, Goals, Comeback
}

data class Achievement(
    val id: String,
    val title: String,
    val description: String,
    val category: AchievementCategory,
    val isUnlocked: Boolean,
    val unlockedDate: String? = null,
    val progress: Int,
    val maxProgress: Int,
    val previousStat: String? = null,
    val currentStat: String? = null
)
