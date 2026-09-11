package com.milo.app.data.repository

import com.milo.app.data.local.MiloDatabase
import com.milo.app.data.local.entities.*
import com.milo.app.domain.models.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.LocalDate

class MiloRepository(private val database: MiloDatabase) {

    fun getActivities(): Flow<List<Activity>> {
        return database.activityDao().getAllActivities().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    suspend fun saveActivity(activity: Activity) {
        database.activityDao().insertOrUpdate(activity.toEntity())
        database.syncDao().enqueue(
            SyncQueueEntity(
                entityType = "ACTIVITY",
                entityId = activity.id,
                operation = "UPSERT",
                payloadJson = "{\"title\":\"${activity.title}\",\"date\":\"${activity.date}\"}"
            )
        )
    }

    suspend fun deleteActivity(id: String) {
        database.activityDao().deleteById(id)
        database.syncDao().enqueue(
            SyncQueueEntity(
                entityType = "ACTIVITY",
                entityId = id,
                operation = "DELETE",
                payloadJson = "{}"
            )
        )
    }

    fun getHabits(): Flow<List<Habit>> {
        return database.habitDao().getAllHabits().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    suspend fun saveHabit(habit: Habit) {
        database.habitDao().insertOrUpdate(habit.toEntity())
    }

    suspend fun deleteHabit(id: String) {
        database.habitDao().deleteById(id)
    }

    fun getHabitCompletions(): Flow<List<HabitCompletion>> {
        return database.habitDao().getAllCompletions().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    suspend fun toggleHabitCompletion(habitId: String, date: LocalDate, isDone: Boolean) {
        database.habitDao().insertCompletion(
            HabitCompletionEntity("hc_${habitId}_$date", habitId, date, isDone, null)
        )
        database.syncDao().enqueue(
            SyncQueueEntity(
                entityType = "HABIT_COMPLETION",
                entityId = "hc_${habitId}_$date",
                operation = "UPSERT",
                payloadJson = "{\"habitId\":\"$habitId\",\"date\":\"$date\",\"isCompleted\":$isDone}"
            )
        )
    }

    fun getGoals(): Flow<List<DailyGoal>> {
        return database.goalDao().getAllGoals().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    fun getFocusSessions(): Flow<List<FocusSession>> {
        return database.focusDao().getAllSessions().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    suspend fun saveFocusSession(session: FocusSession) {
        database.focusDao().insert(session.toEntity())
    }

    fun getReflections(): Flow<List<Reflection>> {
        return database.reflectionDao().getAllReflections().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    suspend fun saveReflection(reflection: Reflection) {
        database.reflectionDao().insertOrUpdate(reflection.toEntity())
    }

    fun getAchievements(): Flow<List<Achievement>> {
        return database.achievementDao().getAll().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    fun getPersonalRecords(): Flow<List<PersonalRecord>> {
        return database.recordDao().getAll().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    suspend fun resetDemoData() {
        database.activityDao().clearAll()
        database.habitDao().clearAll()
        database.goalDao().clearAll()
        database.seedInitialData()
    }

    // Mappers
    private fun ActivityEntity.toDomain() = Activity(
        id = id, title = title, category = category, customCategoryName = customCategoryName,
        date = date, startTime = startTime, endTime = endTime, plannedDurationMinutes = plannedDurationMinutes,
        actualDurationMinutes = actualDurationMinutes, status = status, priority = priority,
        classification = classification, notes = notes, reminderEnabled = reminderEnabled,
        isRecurring = isRecurring, recurrenceRule = recurrenceRule, createdAtEpochMs = createdAtEpochMs
    )

    private fun Activity.toEntity() = ActivityEntity(
        id = id, title = title, category = category, customCategoryName = customCategoryName,
        date = date, startTime = startTime, endTime = endTime, plannedDurationMinutes = plannedDurationMinutes,
        actualDurationMinutes = actualDurationMinutes, status = status, priority = priority,
        classification = classification, notes = notes, reminderEnabled = reminderEnabled,
        isRecurring = isRecurring, recurrenceRule = recurrenceRule, createdAtEpochMs = createdAtEpochMs
    )

    private fun HabitEntity.toDomain() = Habit(
        id = id, name = name, category = category, iconName = iconName,
        targetDaysPerWeek = targetDaysPerWeek, currentStreakDays = currentStreakDays,
        bestStreakDays = bestStreakDays, consistencyPercentage = consistencyPercentage,
        createdAtEpochMs = createdAtEpochMs
    )

    private fun Habit.toEntity() = HabitEntity(
        id = id, name = name, category = category, iconName = iconName,
        targetDaysPerWeek = targetDaysPerWeek, currentStreakDays = currentStreakDays,
        bestStreakDays = bestStreakDays, consistencyPercentage = consistencyPercentage,
        createdAtEpochMs = createdAtEpochMs
    )

    private fun HabitCompletionEntity.toDomain() = HabitCompletion(
        id = id, habitId = habitId, date = date, isCompleted = isCompleted, note = note
    )

    private fun GoalEntity.toDomain() = DailyGoal(
        id = id, title = title, category = category, targetValue = targetValue,
        unit = unit, isMorningRoutine = isMorningRoutine
    )

    private fun FocusSessionEntity.toDomain() = FocusSession(
        id = id, activityId = activityId, title = title, category = category,
        date = date, startTime = startTime, durationMinutes = durationMinutes,
        isCompleted = isCompleted, notes = notes
    )

    private fun FocusSession.toEntity() = FocusSessionEntity(
        id = id, activityId = activityId, title = title, category = category,
        date = date, startTime = startTime, durationMinutes = durationMinutes,
        isCompleted = isCompleted, notes = notes
    )

    private fun ReflectionEntity.toDomain() = Reflection(
        date = date, rating = rating, wentWell = wentWell, couldImprove = couldImprove,
        tomorrowFocus = tomorrowFocus, timestampEpochMs = timestampEpochMs
    )

    private fun Reflection.toEntity() = ReflectionEntity(
        date = date, rating = rating, wentWell = wentWell, couldImprove = couldImprove,
        tomorrowFocus = tomorrowFocus, timestampEpochMs = timestampEpochMs
    )

    private fun AchievementEntity.toDomain() = Achievement(
        id = id, title = title, description = description,
        category = try { AchievementCategory.valueOf(category) } catch (e: Exception) { AchievementCategory.Routine },
        isUnlocked = isUnlocked, unlockedDate = unlockedDate, progress = progress,
        maxProgress = maxProgress, previousStat = previousStat, currentStat = currentStat
    )

    private fun PersonalRecordEntity.toDomain() = PersonalRecord(
        id = id, title = title, valueDisplay = valueDisplay, rawNumericValue = rawNumericValue,
        dateAchieved = dateAchieved, previousValueDisplay = previousValueDisplay, category = category
    )
}
