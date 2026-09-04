# MILO — Personal Productivity OS
> *“Be better than yesterday.”*

[![Kotlin](https://img.shields.io/badge/Kotlin-2.0.21-purple.svg)](https://kotlinlang.org)
[![Jetpack Compose](https://img.shields.io/badge/Jetpack%20Compose-2024.10.00-brightgreen.svg)](https://developer.android.com/jetpack/compose)
[![Target SDK](https://img.shields.io/badge/Target%20SDK-35-blue.svg)](https://developer.android.com)
[![Room SQLite](https://img.shields.io/badge/Room%20DB-Offline--First-orange.svg)](https://developer.android.com/training/data-storage/room)
[![License](https://img.shields.io/badge/License-MIT-black.svg)](LICENSE)

MILO is an offline-first, data-driven personal productivity, routine, habit, and behavioral analytics operating system built natively for **Android** with **Kotlin** and **Jetpack Compose**.

Instead of treating productivity as a punitive checklist, MILO operates on a supportive behavioral philosophy: **incremental, sustainable self-improvement** without guilt, shame, or cognitive burnout.

---

## Video Demonstration
The repository includes a comprehensive 1080p demonstration video showcasing the user experience, Milo companion emotions, scoring calculations, habit matrices, and reports:
- **Demonstration Video**: [`milo_demo.mp4`](./milo_demo.mp4)

---

## Core Systems & Architecture

```
                                  ┌───────────────────────────────┐
                                  │      Milo Mascot Engine       │
                                  │  8 Canvas States & Coaching   │
                                  └───────────────▲───────────────┘
                                                  │
┌───────────────────────────────┐  ┌──────────────┴───────────────┐  ┌───────────────────────────────┐
│     Productivity Scoring      │  │        Milo ViewModel        │  │       Analytics Engine        │
│  Modular 0-100 & Delta Calc   │◄─┤  StateFlow & Unidirectional  ├─►│  Daily, Weekly & Life Report  │
└───────────────────────────────┘  └──────────────┬───────────────┘  └───────────────────────────────┘
                                                  │
                                   ┌──────────────▼───────────────┐
                                   │        Milo Repository       │
                                   │  Room SQLite (Offline-First) │
                                   └──────────────┬───────────────┘
                                                  │
                                   ┌──────────────▼───────────────┐
                                   │      WorkManager Queue       │
                                   │  Battery & Network Optimized │
                                   └──────────────────────────────┘
```

### 1. Milo Companion Mascot
A supportive, canvas-rendered graphical companion with 8 reactive emotional states:
- `Calm`: Baseline serene state with gentle breathing.
- `Curious`: Head-tilted with one cocked ear and inquisitive whiskers.
- `Happy`: Expressive smiling arcs with reactive blinking.
- `Proud`: Confident posture celebrating high consistency.
- `Encouraging`: Supportive presence during difficult days.
- `Sleepy`: Relaxed evening posture signaling wind-down.
- `Celebrating`: Raised paws with festive confetti particles.
- `Welcoming`: Warm waving greeting upon launch.

### 2. Today Dashboard & Scoring Engine (0-100)
A holistic daily scoring engine with transparent weighted inputs:
- **Task Completion (25%)**: Proportional progress across scheduled activities.
- **Time Management & Priority (20%)**: Variance tracking and priority weighting.
- **Focus Stamina (20%)**: Uninterrupted deep work duration.
- **Habit Adherence (20%)**: Daily habit checkmark completion.
- **Routine Consistency (15%)**: Morning anchor timing and goal fulfillment.
- **Improvement Delta**: Clear comparative indicator (e.g. `+9% vs yesterday`).

### 3. Multi-Scale Schedule Engine
- **Day View**: Vertical timeline with active time blocks, completed nodes, and live progress indicators.
- **Week View**: Visual heatmap matrix showing productive distribution across categories.
- **Month View**: Macro adherence calendar with high-contrast indicator dots.

### 4. Habit Consistency Matrix
- **7-Day Visual Matrix**: Minimalist rounded squares showing recent consistency.
- **Resilient Streaks**: Designed to reward consistency velocity rather than inducing panic when a streak is missed.
- **Behavioral Insights**: Contextual metrics citing exact sample sizes (e.g. *“Based on the last 4 weeks, your consistency velocity is up +15.8%.”*).

### 5. Reflective Reports
- **Daily Productivity Report**: Breakdown of productive, study, work, and exercise minutes with personal bests and growth opportunities.
- **Weekly Report**: Planned vs actual category distributions and habit consistency matrices.
- **Signature Monthly Life Report**: Deep retrospective anchored by the core sentiment:
  > *“You showed up. You completed 86% of your planned activities this month, exercised 14 times, and logged 37 deep study hours. You're not the same person who started this month.”*

### 6. Zen Ambient Focus Mode
- Fullscreen distraction-free dark canvas.
- Concentric breathing focus rings.
- Battery-aware session tracking powered by Android `WorkManager`.

### 7. Self-Hosted Verified Distribution
- In-app update checker querying `update.json`.
- Client-side **SHA-256 cryptographic checksum verification** ensuring APK integrity prior to `PackageInstaller` handoff.
- Automated release script (`scripts/release.sh`) and GitHub Actions workflow (`.github/workflows/release.yml`).

---

## Directory Structure

```
milo-android/
├── app/
│   ├── build.gradle.kts
│   ├── proguard-rules.pro
│   └── src/
│       ├── main/
│       │   ├── AndroidManifest.xml
│       │   ├── java/com/milo/app/
│       │   │   ├── MiloApplication.kt
│       │   │   ├── MainActivity.kt
│       │   │   ├── domain/
│       │   │   │   ├── models/ (Activity, Habit, FocusSession, Reports, MiloEmotion, ...)
│       │   │   │   └── engine/ (ProductivityScoringEngine, AnalyticsEngine, MotivationEngine, UpdateVerifier)
│       │   │   ├── data/
│       │   │   │   ├── local/ (MiloDatabase, Daos, Entities, Converters)
│       │   │   │   └── repository/ (MiloRepository with 30-day realistic seed data)
│       │   │   ├── ui/
│       │   │   │   ├── theme/ (Color, Type, Shape, Theme - OLED minimal grayscale)
│       │   │   │   ├── mascot/ (MiloCompanion canvas rendering, MiloSpeechBubble)
│       │   │   │   ├── components/ (MiloScoreRing, MetricCard, TimelineNode, MiloBottomNav)
│       │   │   │   ├── screens/ (TodayScreen, ScheduleScreen, HabitsScreen, InsightsScreen, ProfileScreen)
│       │   │   │   ├── dialogs/ (DailyReportDialog, WeeklyReportScreen, MonthlyLifeReportScreen, ZenFocusScreen, UpdateExperienceDialog)
│       │   │   │   └── viewmodel/ (MiloViewModel)
│       │   │   └── service/ (AppUpdateInstaller, BatteryAwareSyncWorker)
│       │   └── res/ (strings, themes, file_paths, backup_rules)
│       └── test/
│           └── java/com/milo/app/engine/ (Unit tests for Scoring, Analytics, UpdateVerifier)
├── gradle/
│   ├── wrapper/
│   └── libs.versions.toml
├── scripts/
│   ├── release.sh (Automated build, SHA-256 hashing, and GitHub release pipeline)
│   └── generate_demo_video.py (1080p full-length video demonstration generator)
├── update.json (Self-hosted distribution update manifest)
├── milo_demo.mp4 (Full-length demonstration video)
└── build.gradle.kts
```

---

## Building & Running

### Requirements
- **JDK 21** (Temurin / OpenJDK)
- **Android SDK 35**
- **Gradle 8.10+**

### Local Build
```bash
# Set Java 21 environment
export JAVA_HOME=~/.jdk
export PATH=$JAVA_HOME/bin:$PATH

# Run unit tests
./gradlew testReleaseUnitTest

# Build signed release APK
./gradlew assembleRelease
```

### Publishing a Release
Run the automated release script:
```bash
./scripts/release.sh
```
This script will:
1. Compile the release APK with ProGuard optimization.
2. Calculate the SHA-256 checksum.
3. Update `update.json` with the new version, hash, and release notes.
4. Create a tagged GitHub Release via the GitHub CLI (`gh`).

---

## Design System & Aesthetics
MILO utilizes a strict **OLED Black + White + Grayscale** palette with subtle positive accents (Zinc, Obsidian, White, Muted Green):
- **Background**: `#09090B` (Deep Obsidian Black)
- **Cards**: `#18181B` (Zinc-900 with `#27272A` borders)
- **Primary Text**: `#FAFAFA` (Zinc-50)
- **Secondary Text**: `#A1A1AA` (Zinc-400)
- **Accents**: `#34C759` (Positive Improvement), `#FFB340` (Curious / Attention)

---

## Philosophy & Coaching Guardrails
- **No Shame**: If a user completes 1 out of 5 tasks, MILO celebrates the 1 task completed.
- **Resilient Habits**: A missed day is treated as recovery, not a reset to zero.
- **Long-Term Lens**: Focus on 4-week velocity curves rather than daily perfection.
- **You Showed Up**: Every day you engage is a victory.
