package com.milo.app.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.milo.app.BuildConfig
import com.milo.app.data.local.MiloDatabase
import com.milo.app.data.repository.AuthRepository
import com.milo.app.data.repository.MiloRepository
import com.milo.app.data.repository.RemoteSyncService
import com.milo.app.domain.engine.*
import com.milo.app.domain.models.*
import com.milo.app.service.AppUpdateInstaller
import com.milo.app.service.BatteryAwareSyncWorker
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.io.File
import java.time.LocalDate

data class MiloUiState(
    val currentUser: UserAccount? = null,
    val hasCompletedOnboarding: Boolean = false,
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
    val score: ProductivityScore = ProductivityScore(0, 0, 0, 0, 0, 0, 0, ""),
    val feedback: MotivationFeedback = MotivationFeedback(
        headline = "Ready to start today?",
        supportingText = "Plan your first activity or mark a habit to build momentum.",
        catEmotion = MiloEmotion.Welcoming,
        catQuote = "Every journey begins with showing up. Let's make today count!",
        badgeLabel = "Fresh Start",
        isProgressPositive = true
    ),
    val dailyReport: DailyReport? = null,
    val weeklyReport: WeeklyReport? = null,
    val monthlyReport: MonthlyReport? = null,
    val isSyncing: Boolean = false,
    val syncMessage: String? = null,
    val updateState: UpdateState = UpdateState.IDLE,
    val updateErrorMessage: String? = null,
    val updateManifest: UpdateManifest = UpdateManifest(
        versionName = BuildConfig.VERSION_NAME,
        versionCode = BuildConfig.VERSION_CODE,
        apkUrl = "https://github.com/Priyanshukumar2904/MILO/releases/download/v1.5.0/Milo.apk",
        sha256 = "",
        isMandatory = false,
        minimumSupportedVersionCode = 10000,
        releaseDate = "2026-09-10",
        releaseNotes = listOf("Official production signed APK build", "KeyStore AES-GCM secure auth storage", "Streamlined 4-tab UI architecture")
    ),
    val downloadProgress: Float = 0f,
    val downloadedMb: Float = 0f,
    val totalMb: Float = 17f
)

class MiloViewModel(application: Application) : AndroidViewModel(application) {

    private val database = MiloDatabase.getInstance(application)
    private val repository = MiloRepository(database)
    private val authRepository = AuthRepository(application)
    private val syncService = RemoteSyncService(database)
    private val scoringEngine = ProductivityScoringEngine()
    private val analyticsEngine = AnalyticsEngine(scoringEngine)
    private val motivationEngine = MotivationEngine()
    private val insightsEngine = InsightsEngine()
    private val updateInstaller = AppUpdateInstaller(application)

    private val _uiState = MutableStateFlow(MiloUiState())
    val uiState: StateFlow<MiloUiState> = _uiState.asStateFlow()

    val today: LocalDate
        get() = LocalDate.now()

    init {
        BatteryAwareSyncWorker.schedulePeriodicSync(application)

        // Initialize user session & onboarding status
        val session = authRepository.currentUser.value
        val onboardingDone = authRepository.hasCompletedOnboarding.value
        _uiState.update { 
            it.copy(
                currentUser = session,
                hasCompletedOnboarding = onboardingDone
            )
        }

        viewModelScope.launch {
            authRepository.currentUser.collect { user ->
                _uiState.update { it.copy(currentUser = user) }
            }
        }

        viewModelScope.launch {
            authRepository.hasCompletedOnboarding.collect { completed ->
                _uiState.update { it.copy(hasCompletedOnboarding = completed) }
            }
        }

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

                val todayDate = today
                val todayActs = acts.filter { it.date == todayDate }
                val score = scoringEngine.calculateDailyScore(todayDate, acts, hbs, comps, gls, fcs)
                val completedCount = todayActs.count { it.status == ActivityStatus.Completed }
                val feedback = motivationEngine.getDailyFeedback(score, completedCount, todayActs.size)
                val dailyRep = analyticsEngine.generateDailyReport(todayDate, acts, hbs, comps, gls, fcs, refs)
                val weeklyRep = analyticsEngine.generateWeeklyReport(todayDate, acts, hbs, comps, gls, fcs)
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

    // Authentication Functions
    fun login(email: String, pass: String): Result<UserAccount> {
        return authRepository.login(email, pass)
    }

    fun register(name: String, email: String, pass: String): Result<UserAccount> {
        return authRepository.register(name, email, pass)
    }

    fun continueAsGuest(): UserAccount {
        return authRepository.continueAsGuest()
    }

    fun logout() {
        authRepository.logout()
    }

    fun completeOnboarding() {
        authRepository.completeOnboarding()
    }

    // Remote Cloud Sync
    fun triggerSync() {
        val user = _uiState.value.currentUser ?: return
        viewModelScope.launch {
            _uiState.update { it.copy(isSyncing = true, syncMessage = null) }
            val res = syncService.syncPendingData(user)
            if (res.isSuccess) {
                val count = res.getOrDefault(0)
                _uiState.update {
                    it.copy(
                        isSyncing = false,
                        syncMessage = if (count > 0) "Successfully synced $count items with vault" else "All local records up to date"
                    )
                }
            } else {
                _uiState.update {
                    it.copy(
                        isSyncing = false,
                        syncMessage = "Offline changes stored in encrypted vault"
                    )
                }
            }
        }
    }

    // Activities & Habits
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
            val currentDate = today
            val currentDone = _uiState.value.habitCompletions.any { it.habitId == habitId && it.date == currentDate && it.isCompleted }
            repository.toggleHabitCompletion(habitId, currentDate, !currentDone)
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

    // In-App Updates Mechanism
    fun checkForUpdatesSilently(
        manifestUrl: String = "https://raw.githubusercontent.com/Priyanshukumar2904/MILO/main/update.json"
    ) {
        viewModelScope.launch {
            val bustCacheUrl = if (manifestUrl.contains("?")) {
                "$manifestUrl&t=${System.currentTimeMillis()}"
            } else {
                "$manifestUrl?t=${System.currentTimeMillis()}"
            }
            val result = updateInstaller.fetchUpdateManifest(bustCacheUrl)
            if (result.isSuccess) {
                val manifest = result.getOrThrow()
                val currentCode = BuildConfig.VERSION_CODE
                val hasNewVersion = manifest.versionCode > currentCode
                if (hasNewVersion) {
                    val targetMb = if (manifest.fileSizeBytes > 0) {
                        manifest.fileSizeBytes / (1024f * 1024f)
                    } else {
                        11.5f
                    }
                    _uiState.update {
                        it.copy(
                            updateManifest = manifest,
                            totalMb = targetMb,
                            updateState = UpdateState.UPDATE_AVAILABLE,
                            updateErrorMessage = null
                        )
                    }
                }
            }
        }
    }

    fun checkForUpdates(
        manifestUrl: String = "https://raw.githubusercontent.com/Priyanshukumar2904/MILO/main/update.json",
        onComplete: (Boolean) -> Unit = {}
    ) {
        viewModelScope.launch {
            _uiState.update { it.copy(updateState = UpdateState.CHECKING, updateErrorMessage = null) }
            val bustCacheUrl = if (manifestUrl.contains("?")) {
                "$manifestUrl&t=${System.currentTimeMillis()}"
            } else {
                "$manifestUrl?t=${System.currentTimeMillis()}"
            }
            val result = updateInstaller.fetchUpdateManifest(bustCacheUrl)
            if (result.isSuccess) {
                val manifest = result.getOrThrow()
                val currentCode = BuildConfig.VERSION_CODE
                val hasNewVersion = manifest.versionCode > currentCode
                val targetMb = if (manifest.fileSizeBytes > 0) {
                    manifest.fileSizeBytes / (1024f * 1024f)
                } else {
                    11.5f
                }
                _uiState.update {
                    it.copy(
                        updateManifest = manifest,
                        totalMb = targetMb,
                        updateState = if (hasNewVersion) UpdateState.UPDATE_AVAILABLE else UpdateState.IDLE,
                        updateErrorMessage = if (!hasNewVersion) "You are on the latest version (${BuildConfig.VERSION_NAME})" else null
                    )
                }
                onComplete(hasNewVersion)
            } else {
                _uiState.update {
                    it.copy(
                        updateState = UpdateState.FAILED,
                        updateErrorMessage = "Could not reach update server. Please check internet connection."
                    )
                }
                onComplete(false)
            }
        }
    }

    fun startUpdateDownload() {
        val manifest = _uiState.value.updateManifest
        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    updateState = UpdateState.DOWNLOADING,
                    downloadProgress = 0f,
                    downloadedMb = 0f,
                    updateErrorMessage = null
                )
            }

            val app = getApplication<Application>()
            val cacheDir = File(app.cacheDir, "updates").apply { mkdirs() }
            val cacheApk = File(cacheDir, "Milo-${manifest.versionName}.apk")

            val downloadResult = updateInstaller.downloadApk(manifest.apkUrl, cacheApk) { progress, downMb, totMb ->
                _uiState.update {
                    it.copy(
                        downloadProgress = progress,
                        downloadedMb = downMb,
                        totalMb = totMb
                    )
                }
            }

            if (downloadResult.isSuccess) {
                _uiState.update { it.copy(updateState = UpdateState.VERIFYING) }
                val isShaValid = if (manifest.sha256.isNotEmpty()) {
                    updateInstaller.verifyApk(cacheApk, manifest.sha256, manifest.versionCode)
                } else {
                    true
                }

                if (isShaValid) {
                    _uiState.update { it.copy(updateState = UpdateState.READY_TO_INSTALL) }
                    installUpdate()
                } else {
                    _uiState.update {
                        it.copy(
                            updateState = UpdateState.FAILED,
                            updateErrorMessage = "Integrity check failed: Checksum mismatch."
                        )
                    }
                }
            } else {
                _uiState.update {
                    it.copy(
                        updateState = UpdateState.FAILED,
                        updateErrorMessage = downloadResult.exceptionOrNull()?.message ?: "Download failed"
                    )
                }
            }
        }
    }

    fun installUpdate() {
        val manifest = _uiState.value.updateManifest
        val app = getApplication<Application>()
        val cacheApk = File(app.cacheDir, "updates/Milo-${manifest.versionName}.apk")
        if (cacheApk.exists()) {
            val launched = updateInstaller.installApk(cacheApk, manifest.sha256, manifest.versionCode)
            if (launched) {
                _uiState.update { it.copy(updateState = UpdateState.ANDROID_INSTALLER) }
            } else {
                _uiState.update {
                    it.copy(
                        updateState = UpdateState.FAILED,
                        updateErrorMessage = "Could not launch package installer. Please check app install permissions."
                    )
                }
            }
        }
    }

    fun dismissUpdate() {
        _uiState.update { it.copy(updateState = UpdateState.IDLE) }
    }
}
