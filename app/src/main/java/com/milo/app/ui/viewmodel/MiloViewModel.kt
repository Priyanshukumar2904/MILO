package com.milo.app.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.milo.app.data.local.MiloDatabase
import com.milo.app.data.repository.MiloRepository
import com.milo.app.domain.engine.*
import com.milo.app.domain.models.*
import com.milo.app.service.AppUpdateInstaller
import com.milo.app.service.BatteryAwareSyncWorker
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.io.File
import java.time.LocalDate

data class MiloUiState(
    val activities: List<Activity> = emptyList(),
    val habits: List<Habit> = emptyList(),
    val habitCompletions: List<HabitCompletion> = emptyList(),
    val goals: List<DailyGoal> = emptyList(),
    val focusSessions: List<FocusSession> = emptyList(),
    val reflections: List<Reflection> = emptyList(),
    val achievements: List<Achievement> = emptyList(),
    val records: List<PersonalRecord> = emptyList(),
    val insights: List<MiloInsight> = emptyList(),
    val trends: List<PerformanceTrend> = emptyList(),
    val scorecards: List<CategoryScorecard> = emptyList(),
    val score: ProductivityScore = ProductivityScore(78, 82, 75, 88, 76, 84, 9, ""),
    val feedback: MotivationFeedback = MotivationFeedback("+9% compared with yesterday", "You completed 4 activities with steady focus today.", MiloEmotion.Proud, "Look at that pace! You moved forward today.", "+9% Better", true),
    val dailyReport: DailyReport? = null,
    val weeklyReport: WeeklyReport? = null,
    val monthlyReport: MonthlyReport? = null,
    val updateState: UpdateState = UpdateState.IDLE,
    val updateManifest: UpdateManifest = UpdateManifest("1.4.0", 10400, "https://github.com/Priyanshukumar2904/MILO/releases/download/v1.4.0/Milo.apk", "e3b0c44298fc1c149afbf4c8996fb92427ae41e4649b934ca495991b7852b855", false, 10000, "2026-09-04", listOf("New monthly Life Report", "New achievements & personal records", "Improved battery efficiency", "New Milo animations")),
    val downloadProgress: Float = 0f,
    val downloadedMb: Float = 0f,
    val totalMb: Float = 76f
)

class MiloViewModel(application: Application) : AndroidViewModel(application) {

    private val database = MiloDatabase.getInstance(application)
    private val repository = MiloRepository(database)
    private val scoringEngine = ProductivityScoringEngine()
    private val analyticsEngine = AnalyticsEngine(scoringEngine)
    private val motivationEngine = MotivationEngine()
    private val insightsEngine = InsightsEngine()
    private val updateInstaller = AppUpdateInstaller(application)

    private val _uiState = MutableStateFlow(MiloUiState())
    val uiState: StateFlow<MiloUiState> = _uiState.asStateFlow()

    private val today = LocalDate.of(2026, 9, 4)

    init {
        BatteryAwareSyncWorker.schedulePeriodicSync(application)
        observeData()
    }

    private data class CoreDataBundle(
        val activities: List<Activity>,
        val habits: List<Habit>,
        val habitCompletions: List<HabitCompletion>,
        val goals: List<DailyGoal>,
        val focusSessions: List<FocusSession>
    )

    private fun observeData() {
        viewModelScope.launch {
            combine(
                repository.getActivities(),
                repository.getHabits(),
                repository.getHabitCompletions(),
                repository.getGoals(),
                repository.getFocusSessions()
            ) { acts, hbs, comps, gls, fcs ->
                CoreDataBundle(acts, hbs, comps, gls, fcs)
            }.combine(repository.getReflections()) { data, refs ->
                val acts = data.activities
                val hbs = data.habits
                val comps = data.habitCompletions
                val gls = data.goals
                val fcs = data.focusSessions

                val todayActs = acts.filter { it.date == today }
                val score = scoringEngine.calculateDailyScore(today, acts, hbs, comps, gls, fcs)
                val completedCount = todayActs.count { it.status == ActivityStatus.Completed }
                val feedback = motivationEngine.getDailyFeedback(score, completedCount, todayActs.size)
                val dailyRep = analyticsEngine.generateDailyReport(today, acts, hbs, comps, gls, fcs, refs)
                val weeklyRep = analyticsEngine.generateWeeklyReport(today, acts, hbs, comps, gls, fcs)
                val monthlyRep = analyticsEngine.generateMonthlyReport(acts)

                _uiState.update { current ->
                    current.copy(
                        activities = todayActs,
                        habits = hbs,
                        habitCompletions = comps,
                        goals = gls,
                        focusSessions = fcs,
                        reflections = refs,
                        score = score,
                        feedback = feedback,
                        dailyReport = dailyRep,
                        weeklyReport = weeklyRep,
                        monthlyReport = monthlyRep,
                        insights = insightsEngine.generateValidatedInsights(),
                        trends = analyticsEngine.getTrends(),
                        scorecards = analyticsEngine.getCategoryScorecard()
                    )
                }
            }.collect()
        }

        viewModelScope.launch {
            repository.getAchievements().collect { achs ->
                _uiState.update { it.copy(achievements = achs) }
            }
        }

        viewModelScope.launch {
            repository.getPersonalRecords().collect { recs ->
                _uiState.update { it.copy(records = recs) }
            }
        }
    }

    fun toggleActivityStatus(activity: Activity) {
        viewModelScope.launch {
            val next = if (activity.status == ActivityStatus.Completed) ActivityStatus.Upcoming else ActivityStatus.Completed
            val updated = activity.copy(
                status = next,
                actualDurationMinutes = if (next == ActivityStatus.Completed && activity.actualDurationMinutes == 0) activity.plannedDurationMinutes else activity.actualDurationMinutes
            )
            repository.saveActivity(updated)
        }
    }

    fun completeLiveActivity(activity: Activity, actualMins: Int) {
        viewModelScope.launch {
            val updated = activity.copy(
                actualDurationMinutes = actualMins,
                status = ActivityStatus.Completed
            )
            repository.saveActivity(updated)
        }
    }

    fun toggleHabit(habitId: String) {
        viewModelScope.launch {
            val currentDone = _uiState.value.habitCompletions.any { it.habitId == habitId && it.date == today && it.isCompleted }
            repository.toggleHabitCompletion(habitId, today, !currentDone)
        }
    }

    fun saveReflection(reflection: Reflection) {
        viewModelScope.launch {
            repository.saveReflection(reflection)
        }
    }

    fun resetDemoData() {
        viewModelScope.launch {
            repository.resetDemoData()
        }
    }

    // Developer-Only Testing Controls
    fun setProfileMode(highProductivity: Boolean) {
        viewModelScope.launch {
            val targetScore = if (highProductivity) 92 else 42
            val targetDelta = if (highProductivity) 14 else -8
            val targetEmotion = if (highProductivity) MiloEmotion.Proud else MiloEmotion.Encouraging
            val msg = if (highProductivity) 
                "Outstanding flow state! You've crushed every deep work block." 
            else 
                "A slower day does not erase your progress. Let's reset tomorrow."

            _uiState.update { current ->
                current.copy(
                    score = current.score.copy(overall = targetScore, deltaYesterday = targetDelta),
                    feedback = current.feedback.copy(
                        headline = if (highProductivity) "+14% Above Average" else "Gentle Rest Day",
                        supportingText = msg,
                        catEmotion = targetEmotion,
                        badgeLabel = if (targetDelta > 0) "+$targetDelta% Better" else "$targetDelta% Delta",
                        isProgressPositive = highProductivity
                    )
                )
            }
        }
    }

    fun triggerAchievement(title: String = "Flow State Master") {
        viewModelScope.launch {
            val newAch = Achievement(
                id = "dev_ach_${System.currentTimeMillis()}",
                title = title,
                description = "Triggered via Developer Menu for UI inspection",
                category = AchievementCategory.Focus,
                isUnlocked = true,
                unlockedDate = "Today",
                progress = 1,
                maxProgress = 1
            )
            val updated = _uiState.value.achievements + newAch
            _uiState.update { it.copy(achievements = updated) }
        }
    }

    fun triggerPersonalRecord(title: String = "Longest Deep Work Block", value: String = "210 minutes") {
        viewModelScope.launch {
            val newRec = PersonalRecord(
                id = "dev_rec_${System.currentTimeMillis()}",
                title = title,
                valueDisplay = value,
                rawNumericValue = 210f,
                dateAchieved = "Today",
                category = "Focus"
            )
            val updated = _uiState.value.records + newRec
            _uiState.update { it.copy(records = updated) }
        }
    }

    fun triggerUpdateNotification() {
        _uiState.update { it.copy(updateState = UpdateState.READY_TO_INSTALL) }
    }

    // Self-Hosted In-App Update Flow (Section 51-56)
    fun startUpdateDownload() {
        viewModelScope.launch {
            _uiState.update { it.copy(updateState = UpdateState.DOWNLOADING) }

            // Simulated safe, battery-conscious chunk download
            for (i in 1..10) {
                delay(180)
                val prog = i / 10f
                val downloaded = prog * _uiState.value.totalMb
                _uiState.update {
                    it.copy(
                        downloadProgress = prog,
                        downloadedMb = downloaded
                    )
                }
            }

            // Verification phase
            _uiState.update { it.copy(updateState = UpdateState.VERIFYING) }
            delay(500)

            _uiState.update { it.copy(updateState = UpdateState.READY_TO_INSTALL) }
        }
    }

    fun installUpdate() {
        viewModelScope.launch {
            _uiState.update { it.copy(updateState = UpdateState.ANDROID_INSTALLER) }
        }
    }

    fun dismissUpdate() {
        _uiState.update { it.copy(updateState = UpdateState.IDLE) }
    }
}
