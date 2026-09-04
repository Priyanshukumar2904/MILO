package com.milo.app.domain.models

import java.time.LocalDate
import java.time.LocalTime

data class FocusSession(
    val id: String,
    val activityId: String? = null,
    val title: String,
    val category: ActivityCategory,
    val date: LocalDate,
    val startTime: LocalTime,
    val durationMinutes: Int,
    val isCompleted: Boolean = true,
    val notes: String? = null
)
