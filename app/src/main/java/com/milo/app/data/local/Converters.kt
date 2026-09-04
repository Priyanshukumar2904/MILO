package com.milo.app.data.local

import androidx.room.TypeConverter
import com.milo.app.domain.models.*
import java.time.LocalDate
import java.time.LocalTime

class Converters {
    @TypeConverter
    fun fromLocalDate(value: LocalDate?): String? = value?.toString()

    @TypeConverter
    fun toLocalDate(value: String?): LocalDate? = value?.let { LocalDate.parse(it) }

    @TypeConverter
    fun fromLocalTime(value: LocalTime?): String? = value?.toString()

    @TypeConverter
    fun toLocalTime(value: String?): LocalTime? = value?.let { LocalTime.parse(it) }

    @TypeConverter
    fun fromActivityCategory(value: ActivityCategory?): String? = value?.name

    @TypeConverter
    fun toActivityCategory(value: String?): ActivityCategory? = value?.let { ActivityCategory.valueOf(it) }

    @TypeConverter
    fun fromActivityStatus(value: ActivityStatus?): String? = value?.name

    @TypeConverter
    fun toActivityStatus(value: String?): ActivityStatus? = value?.let { ActivityStatus.valueOf(it) }

    @TypeConverter
    fun fromActivityPriority(value: ActivityPriority?): String? = value?.name

    @TypeConverter
    fun toActivityPriority(value: String?): ActivityPriority? = value?.let { ActivityPriority.valueOf(it) }

    @TypeConverter
    fun fromActivityClassification(value: ActivityClassification?): String? = value?.name

    @TypeConverter
    fun toActivityClassification(value: String?): ActivityClassification? = value?.let { ActivityClassification.valueOf(it) }

    @TypeConverter
    fun fromGoalUnit(value: GoalUnit?): String? = value?.name

    @TypeConverter
    fun toGoalUnit(value: String?): GoalUnit? = value?.let { GoalUnit.valueOf(it) }
}
