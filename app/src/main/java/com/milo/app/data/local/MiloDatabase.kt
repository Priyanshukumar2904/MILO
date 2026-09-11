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

                // One-time maintenance cleanup of legacy pregenerated data
                val prefs = context.getSharedPreferences("milo_db_maintenance", Context.MODE_PRIVATE)
                if (!prefs.getBoolean("clean_initial_state_v1_5_1", false)) {
                    CoroutineScope(Dispatchers.IO).launch {
                        instance.cleanLegacyData()
                        prefs.edit().putBoolean("clean_initial_state_v1_5_1", true).apply()
                    }
                }

                instance
            }
        }
    }

    suspend fun cleanLegacyData() {
        activityDao().clearAll()
        habitDao().clearAllCompletions()
        focusDao().clearAll()
        reflectionDao().clearAll()
        recordDao().clearAll()

        // Reset achievements to locked, authentic state
        achievementDao().clearAll()
        seedBaseAchievements()

        // Reset starter habits with 0 completions
        habitDao().clearAll()
        seedStarterHabits()

        // Reset starter goals
        goalDao().clearAll()
        seedStarterGoals()
    }

    suspend fun seedInitialData() {
        seedStarterHabits()
        seedStarterGoals()
        seedBaseAchievements()
    }

    private suspend fun seedStarterHabits() {
        val habits = listOf(
            HabitEntity("h1", "Morning Routine & Hydration", ActivityCategory.Grooming, "Sun", 7, 0, 0, 0, System.currentTimeMillis()),
            HabitEntity("h2", "Physical Exercise & Movement", ActivityCategory.Exercise, "Activity", 5, 0, 0, 0, System.currentTimeMillis()),
            HabitEntity("h3", "Deep Study & Focus", ActivityCategory.Study, "BookOpen", 5, 0, 0, 0, System.currentTimeMillis()),
            HabitEntity("h4", "Read 15 Pages", ActivityCategory.PersonalDevelopment, "BookMarked", 7, 0, 0, 0, System.currentTimeMillis())
        )
        habitDao().insertAll(habits)
    }

    private suspend fun seedStarterGoals() {
        val goals = listOf(
            GoalEntity("g1", "Deep Focus Work", ActivityCategory.Study, 60, GoalUnit.Minutes, false),
            GoalEntity("g2", "Exercise & Movement", ActivityCategory.Exercise, 30, GoalUnit.Minutes, false),
            GoalEntity("g3", "Daily Habit Routine", ActivityCategory.Grooming, 1, GoalUnit.Boolean, true)
        )
        goalDao().insertAll(goals)
    }

    private suspend fun seedBaseAchievements() {
        val achievements = listOf(
            AchievementEntity("ach_1", "First Step", "Complete your first tracked day.", "Routine", false, null, 0, 1, null, null),
            AchievementEntity("ach_2", "Seven Strong", "Track seven consecutive days.", "Consistency", false, null, 0, 7, null, null),
            AchievementEntity("ach_3", "Two Weeks", "Maintain a 14-day consistency streak.", "Consistency", false, null, 0, 14, null, null),
            AchievementEntity("ach_4", "Deep Focus", "Complete a 2-hour focus session.", "Focus", false, null, 0, 120, null, null),
            AchievementEntity("ach_5", "Personal Best", "Beat your previous productivity record.", "Improvement", false, null, 0, 80, null, null),
            AchievementEntity("ach_6", "Comeback", "Return and improve significantly after inactivity.", "Comeback", false, null, 0, 20, null, null),
            AchievementEntity("ach_7", "Consistency Wins", "Complete a habit 20 times.", "Habits", false, null, 0, 20, null, null),
            AchievementEntity("ach_8", "Better Than Yesterday", "Improve your productivity score five times.", "Improvement", false, null, 0, 5, null, null)
        )
        achievementDao().insertAll(achievements)
    }
}
