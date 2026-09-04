package com.milo.app.domain.models

data class PersonalRecord(
    val id: String,
    val title: String,
    val valueDisplay: String,
    val rawNumericValue: Float,
    val dateAchieved: String,
    val previousValueDisplay: String? = null,
    val category: String
)
