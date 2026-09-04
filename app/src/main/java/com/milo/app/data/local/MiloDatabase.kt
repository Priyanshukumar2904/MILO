package com.milo.app.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
import com.milo.app.data.local.dao.*
import com.milo.app.data.local.entities.*
import com.milo.app.domain.models.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalTime

@Database(
    entities = [
        ActivityEntity::class,
        HabitEntity::class,
        HabitCompletionEntity::class,
        GoalEntity::class,
        FocusSessionEntity::class,
        ReflectionEntity::class,
        AchievementEntity::class,
        PersonalRecordEntity::class,
        SyncQueueEntity::class
    ],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class MiloDatabase : RoomDatabase() {

    abstract fun activityDao(): ActivityDao
    abstract fun habitDao(): HabitDao
    abstract fun goalDao(): GoalDao
    abstract fun focusDao(): FocusDao
    abstract fun reflectionDao(): ReflectionDao
    abstract fun achievementDao(): AchievementDao
    abstract fun recordDao(): RecordDao
    abstract fun syncDao(): SyncDao

    companion object {
        @Volatile
        private var INSTANCE: MiloDatabase? = null

        fun getInstance(context: Context): MiloDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    MiloDatabase::class.java,
                    "milo.db"
                )
                .addCallback(object : Callback() {
                    override fun onCreate(db: SupportSQLiteDatabase) {
                        super.onCreate(db)
                        CoroutineScope(Dispatchers.IO).launch {
                            INSTANCE?.seedInitialData()
                        }
                    }
                })
                .fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                instance
            }
        }
    }

    suspend fun seedInitialData() {
        val today = LocalDate.of(2026, 9, 4)

        // Seed Core Habits
        val habits = listOf(
            HabitEntity("h1", "Morning Routine", ActivityCategory.Grooming, "Sun", 7, 9, 14, 92, System.currentTimeMillis()),
            HabitEntity("h2", "Gym & Strength", ActivityCategory.Exercise, "Activity", 5, 4, 8, 84, System.currentTimeMillis()),
            HabitEntity("h3", "Deep Study (2h)", ActivityCategory.Study, "BookOpen", 6, 6, 12, 88, System.currentTimeMillis()),
            HabitEntity("h4", "Read 20 Pages", ActivityCategory.PersonalDevelopment, "BookMarked", 7, 11, 16, 90, System.currentTimeMillis()),
            HabitEntity("h5", "Sleep by 11:30 PM", ActivityCategory.Sleep, "Moon", 7, 3, 7, 74, System.currentTimeMillis())
        )
        habitDao().insertAll(habits)

        // Seed Daily Goals
        val goals = listOf(
            GoalEntity("g1", "Deep Study", ActivityCategory.Study, 180, GoalUnit.Minutes, false),
            GoalEntity("g2", "Exercise & Movement", ActivityCategory.Exercise, 45, GoalUnit.Minutes, false),
            GoalEntity("g3", "Reading & Learning", ActivityCategory.PersonalDevelopment, 30, GoalUnit.Minutes, false),
            GoalEntity("g4", "Restorative Sleep", ActivityCategory.Sleep, 480, GoalUnit.Minutes, false),
            GoalEntity("g5", "Morning Routine", ActivityCategory.Grooming, 1, GoalUnit.Boolean, true)
        )
        goalDao().insertAll(goals)

        // Seed 30 Days of realistic activities, completions, and sessions
        val activitiesList = mutableListOf<ActivityEntity>()
        val completionsList = mutableListOf<HabitCompletionEntity>()
        val focusList = mutableListOf<FocusSessionEntity>()
        val reflectionsList = mutableListOf<ReflectionEntity>()

        for (i in 29 downTo 0) {
            val d = today.minusDays(i.toLong())
            val isToday = (i == 0)
            val isYesterday = (i == 1)

            // Morning Routine
            activitiesList.add(
                ActivityEntity("act_${d}_1", "Morning Routine & Hydration", ActivityCategory.Grooming, null, d, LocalTime.of(7, 0), LocalTime.of(7, 30), 30, 30, ActivityStatus.Completed, ActivityPriority.High, ActivityClassification.Maintenance, null, true, true, "daily", System.currentTimeMillis())
            )
            // Exercise
            activitiesList.add(
                ActivityEntity("act_${d}_2", "Gym: Strength Training", ActivityCategory.Exercise, null, d, LocalTime.of(7, 30), LocalTime.of(8, 30), 60, if (isYesterday) 45 else 60, if (isYesterday) ActivityStatus.PartiallyCompleted else ActivityStatus.Completed, ActivityPriority.High, ActivityClassification.Maintenance, null, true, true, "daily", System.currentTimeMillis())
            )
            // Deep Study
            activitiesList.add(
                ActivityEntity("act_${d}_3", "Deep Study: Systems & Architecture", ActivityCategory.Study, null, d, LocalTime.of(9, 0), LocalTime.of(11, 30), 150, if (isToday) 175 else if (isYesterday) 90 else 150, if (isYesterday) ActivityStatus.PartiallyCompleted else ActivityStatus.Completed, ActivityPriority.High, ActivityClassification.DeepWork, "Worked on core consensus and storage engine.", true, true, "daily", System.currentTimeMillis())
            )
            // Work
            activitiesList.add(
                ActivityEntity("act_${d}_4", "Core Engineering Work", ActivityCategory.Work, null, d, LocalTime.of(12, 0), LocalTime.of(14, 0), 120, if (isToday) 130 else 120, ActivityStatus.Completed, ActivityPriority.High, ActivityClassification.DeepWork, null, true, true, "weekdays", System.currentTimeMillis())
            )
            // Reading
            activitiesList.add(
                ActivityEntity("act_${d}_5", "Reading: Non-Fiction & Tech", ActivityCategory.PersonalDevelopment, null, d, LocalTime.of(21, 0), LocalTime.of(21, 45), 45, 45, if (isYesterday) ActivityStatus.Skipped else ActivityStatus.Completed, ActivityPriority.High, ActivityClassification.DeepWork, null, true, true, "daily", System.currentTimeMillis())
            )

            // Habit completions
            habits.forEach { h ->
                val done = !(isYesterday && (h.id == "h4" || h.id == "h5"))
                completionsList.add(HabitCompletionEntity("hc_${h.id}_$d", h.id, d, done, null))
            }

            // Focus sessions
            if (!isYesterday) {
                focusList.add(FocusSessionEntity("f_$d", "act_${d}_3", "Deep Focus Study Session", ActivityCategory.Study, d, LocalTime.of(9, 15), if (isToday) 175 else 150, true, "Flow state maintained"))
            }

            // Reflection
            if (i % 2 == 0 || isYesterday) {
                reflectionsList.add(ReflectionEntity(d, if (isToday) 4 else if (isYesterday) 3 else 5, if (isToday) "Exceptional focus block with zero distraction" else "Completed basic tasks despite fatigue", "Evening screen time drifted late", "Maintain early morning focus without checking notifications", System.currentTimeMillis()))
            }
        }

        activityDao().insertAll(activitiesList)
        habitDao().insertAllCompletions(completionsList)
        focusDao().insertAll(focusList)
        reflectionDao().insertAll(reflectionsList)

        // Seed Achievements
        val achievements = listOf(
            AchievementEntity("ach_1", "First Step", "Complete your first tracked day.", "Routine", true, "2026-08-06", 1, 1, null, null),
            AchievementEntity("ach_2", "Seven Strong", "Track seven consecutive days.", "Consistency", true, "2026-08-14", 7, 7, "4d", "7d"),
            AchievementEntity("ach_3", "Two Weeks", "Maintain a 14-day consistency streak.", "Consistency", true, "2026-08-21", 14, 14, "7d", "14d"),
            AchievementEntity("ach_4", "Deep Focus", "Complete a 2-hour focus session.", "Focus", true, "2026-08-19", 175, 120, "95m", "175m"),
            AchievementEntity("ach_5", "Personal Best", "Beat your previous productivity record.", "Improvement", true, "2026-09-04", 91, 90, "87", "91"),
            AchievementEntity("ach_6", "Comeback", "Return and improve significantly after inactivity.", "Comeback", true, "2026-08-25", 24, 20, "+12%", "+24%"),
            AchievementEntity("ach_7", "Consistency Wins", "Complete a habit 20 times.", "Habits", true, "2026-08-28", 24, 20, "15", "24"),
            AchievementEntity("ach_8", "Better Than Yesterday", "Improve your productivity score five times.", "Improvement", true, "2026-08-30", 5, 5, "4", "5")
        )
        achievementDao().insertAll(achievements)

        // Seed Personal Records
        val records = listOf(
            PersonalRecordEntity("rec_1", "Longest Focus Session", "175 minutes", 175f, "2026-09-04", "150 minutes", "Focus"),
            PersonalRecordEntity("rec_2", "Longest Habit Streak", "14 days", 14f, "2026-08-28", "9 days", "Habits"),
            PersonalRecordEntity("rec_3", "Highest Productivity Score", "91 / 100", 91f, "2026-09-02", "87 / 100", "Productivity"),
            PersonalRecordEntity("rec_4", "Most Study Hours in a Week", "21.5 hours", 21.5f, "2026-08-30", "16.0 hours", "Study"),
            PersonalRecordEntity("rec_5", "Best Schedule Adherence", "94%", 94f, "2026-09-03", "88%", "Discipline")
        )
        recordDao().insertAll(records)
    }
}
