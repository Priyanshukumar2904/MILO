package com.milo.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.milo.app.domain.models.Activity
import com.milo.app.domain.models.UpdateState
import com.milo.app.ui.components.MiloBottomNav
import com.milo.app.ui.components.MiloNavTab
import com.milo.app.ui.dialogs.*
import com.milo.app.ui.screens.*
import com.milo.app.ui.theme.MiloBlack
import com.milo.app.ui.theme.MiloTheme
import com.milo.app.ui.viewmodel.MiloViewModel

class MainActivity : ComponentActivity() {

    private val viewModel: MiloViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MiloTheme(darkTheme = true) {
                MiloMainApp(viewModel = viewModel)
            }
        }
    }
}

@Composable
fun MiloMainApp(viewModel: MiloViewModel) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    var currentTab by remember { mutableStateOf(MiloNavTab.TODAY) }

    // Dialog state
    var showDailyReport by remember { mutableStateOf(false) }
    var showWeeklyReport by remember { mutableStateOf(false) }
    var showMonthlyReport by remember { mutableStateOf(false) }
    var timerActivity by remember { mutableStateOf<Activity?>(null) }
    var showZenFocus by remember { mutableStateOf(false) }
    var showUpdateDialog by remember { mutableStateOf(false) }
    var showAuthDialog by remember { mutableStateOf(false) }

    // Check for updates silently on app launch
    LaunchedEffect(Unit) {
        viewModel.checkForUpdatesSilently()
    }

    // Prompt user when update becomes available
    LaunchedEffect(state.updateState) {
        if (state.updateState == UpdateState.UPDATE_AVAILABLE) {
            showUpdateDialog = true
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize().background(MiloBlack),
        bottomBar = {
            MiloBottomNav(
                currentTab = currentTab,
                onTabSelected = { currentTab = it }
            )
        }
    ) { innerPadding ->
        Box(modifier = Modifier.fillMaxSize().padding(innerPadding)) {
            when (currentTab) {
                MiloNavTab.TODAY -> TodayScreen(
                    score = state.score,
                    feedback = state.feedback,
                    activities = state.activities,
                    goals = state.goals,
                    userName = state.currentUser?.username ?: "Explorer",
                    onToggleActivity = { viewModel.toggleActivityStatus(it) },
                    onStartTimer = { timerActivity = it },
                    onSelectActivity = { timerActivity = it },
                    onOpenDailyReport = { showDailyReport = true },
                    onOpenReflection = { showDailyReport = true }
                )

                MiloNavTab.PLANNER -> PlannerScreen(
                    activities = state.activities,
                    habits = state.habits,
                    habitCompletions = state.habitCompletions,
                    onToggleActivity = { viewModel.toggleActivityStatus(it) },
                    onStartTimer = { timerActivity = it },
                    onSelectActivity = { timerActivity = it },
                    onToggleHabit = { viewModel.toggleHabit(it) }
                )

                MiloNavTab.INSIGHTS -> InsightsScreen(
                    insights = state.insights,
                    trends = state.trends,
                    scorecards = state.scorecards,
                    records = state.records,
                    achievements = state.achievements,
                    onOpenWeeklyReport = { showWeeklyReport = true },
                    onOpenMonthlyReport = { showMonthlyReport = true }
                )

                MiloNavTab.ACCOUNT -> ProfileScreen(
                    currentUser = state.currentUser,
                    isSyncing = state.isSyncing,
                    syncMessage = state.syncMessage,
                    appVersionName = BuildConfig.VERSION_NAME,
                    onOpenAuth = { showAuthDialog = true },
                    onLogout = { viewModel.logout() },
                    onSyncNow = { viewModel.triggerSync() },
                    onCheckUpdate = {
                        viewModel.checkForUpdates { hasUpdate ->
                            showUpdateDialog = true
                        }
                    }
                )
            }

            // Dialogs
            if (showDailyReport && state.dailyReport != null) {
                DailyReportDialog(
                    report = state.dailyReport!!,
                    onDismiss = { showDailyReport = false },
                    onSaveReflection = { viewModel.saveReflection(it) }
                )
            }

            if (showWeeklyReport && state.weeklyReport != null) {
                WeeklyReportScreen(
                    report = state.weeklyReport!!,
                    onDismiss = { showWeeklyReport = false }
                )
            }

            if (showMonthlyReport && state.monthlyReport != null) {
                MonthlyLifeReportScreen(
                    report = state.monthlyReport!!,
                    onDismiss = { showMonthlyReport = false }
                )
            }

            if (timerActivity != null) {
                LiveTimerBottomSheet(
                    activity = timerActivity!!,
                    onDismiss = { timerActivity = null },
                    onCompleteActivity = { mins ->
                        viewModel.completeLiveActivity(timerActivity!!, mins)
                        timerActivity = null
                    }
                )
            }

            if (showZenFocus) {
                ZenFocusScreen(
                    onDismiss = { showZenFocus = false },
                    onSaveFocus = { showZenFocus = false }
                )
            }

            if (showAuthDialog) {
                AuthDialog(
                    onLogin = { email, pass -> viewModel.login(email, pass) },
                    onRegister = { name, email, pass -> viewModel.register(name, email, pass) },
                    onGuest = { viewModel.continueAsGuest() },
                    onDismiss = { showAuthDialog = false }
                )
            }

            if (showUpdateDialog) {
                UpdateExperienceDialog(
                    updateState = state.updateState,
                    manifest = state.updateManifest,
                    downloadProgress = state.downloadProgress,
                    downloadedMb = state.downloadedMb,
                    totalMb = state.totalMb,
                    errorMessage = state.updateErrorMessage,
                    onStartDownload = { viewModel.startUpdateDownload() },
                    onInstallNow = { viewModel.installUpdate() },
                    onDismiss = {
                        viewModel.dismissUpdate()
                        showUpdateDialog = false
                    }
                )
            }
        }
    }
}
