<div align="center">

# MILO 🐈

**Your personal productivity companion.**

*Track your day. Understand your habits. Become better than yesterday.*

<br/>

[![Latest Release](https://img.shields.io/github/v/release/Priyanshukumar2904/MILO?color=white&label=Latest%20Release&style=flat-square)](https://github.com/Priyanshukumar2904/MILO/releases/latest)
[![Android Target](https://img.shields.io/badge/Android-Target%20SDK%2035-242428?style=flat-square&logo=android&logoColor=white)](https://developer.android.com)
[![Kotlin](https://img.shields.io/badge/Kotlin-2.0.21-7F52FF?style=flat-square&logo=kotlin&logoColor=white)](https://kotlinlang.org)
[![UI](https://img.shields.io/badge/UI-Jetpack%20Compose-4285F4?style=flat-square&logo=jetpackcompose&logoColor=white)](https://developer.android.com/jetpack/compose)
[![Privacy](https://img.shields.io/badge/Data-100%25%20On--Device-10B981?style=flat-square)](https://developer.android.com/training/data-storage/room)
[![License](https://img.shields.io/badge/License-MIT-gray?style=flat-square)](LICENSE)

<br/>

<a href="https://github.com/Priyanshukumar2904/MILO/releases/latest/download/Milo.apk">
  <img src="https://img.shields.io/badge/Download_Latest_APK-Milo.apk-white?style=for-the-badge&logo=android&logoColor=black" alt="Download APK" />
</a>
<a href="https://github.com/Priyanshukumar2904/MILO/releases/latest">
  <img src="https://img.shields.io/badge/GitHub_Release-Latest_Only-18181B?style=for-the-badge&logo=github&logoColor=white" alt="Release Notes" />
</a>

<br/><br/>

[![MILO Hero Banner](docs/screenshots/hero-banner.png)](https://github.com/Priyanshukumar2904/MILO/releases/latest)

</div>

---

## ✦ Why MILO?

Most productivity applications are built like punitive surveillance checklists—they guilt-trip you with broken streaks, induce cognitive burnout, and demand perfection.

**MILO is designed differently.** Native to Android with **Kotlin** and **Jetpack Compose**, MILO acts as your operating system for gradual, sustainable progress. It celebrates showing up, acknowledges rest as recovery, and keeps **100% of your data privately on your device**.

### Core Highlights
- 🎯 **Modular 0–100 Daily Scoring**: Transparent composite score weighted across task completion, time adherence, deep focus stamina, and habit discipline.
- 🐈 **Milo the Companion**: An expressive, procedural canvas mascot with 8 emotional states who cheers your wins and supports you on slower days without guilt.
- 📅 **Fluid Time-Blocking**: Seamless multi-scale day, week, and month scheduling with live focus timer.
- 📊 **Resilient Habit Matrices**: Visual 7-day completion matrices and 4-week velocity curves that never shame missed days.
- 🔒 **100% On-Device Data Sovereignty**: All your tasks, habits, reflections, and scores are stored exclusively on your device inside an encrypted local Room SQLite database. Zero external servers or tracking.
- 🔄 **Seamless 1-Tap Updates**: Automatic in-app notification when a new version is released. One tap downloads, verifies SHA-256 integrity, and upgrades with zero data loss.

---

## ✦ App Preview

<div align="center">
<table>
  <tr>
    <td align="center" width="33%"><b>Today OS</b></td>
    <td align="center" width="33%"><b>Schedule & Time-Blocking</b></td>
    <td align="center" width="33%"><b>Habit Consistency Matrix</b></td>
  </tr>
  <tr>
    <td><img src="docs/screenshots/today-dark.png" alt="Today Screen" /></td>
    <td><img src="docs/screenshots/schedule.png" alt="Schedule Screen" /></td>
    <td><img src="docs/screenshots/habits.png" alt="Habits Screen" /></td>
  </tr>
  <tr>
    <td align="center"><b>Performance Trends</b></td>
    <td align="center"><b>Monthly Life Report</b></td>
    <td align="center"><b>Milo Companion Mascot</b></td>
  </tr>
  <tr>
    <td><img src="docs/screenshots/insights.png" alt="Insights Screen" /></td>
    <td><img src="docs/screenshots/life-report.png" alt="Monthly Life Report" /></td>
    <td><img src="docs/screenshots/milo-mascot.png" alt="Mascot Emotions" /></td>
  </tr>
</table>
</div>

---

## ✦ Download & Installation

### Direct Install (Android Phone)
1. Download [**`Milo.apk`**](https://github.com/Priyanshukumar2904/MILO/releases/latest/download/Milo.apk) directly onto your phone.
2. Tap the downloaded file and select **Install**.
3. All future updates are handled automatically in-app with 1 tap.

### Developer USB Push
Connect your Android phone with **USB Debugging** enabled:
```bash
./milo install
```

---

## ✦ Developer Command Center (`./milo`)

The single executable `./milo` manages the entire build, testing, and release lifecycle:

```bash
./milo <command>
```

| Command | Action |
| :--- | :--- |
| `./milo run` | Launch MILO on Android Emulator / laptop preview |
| `./milo install` | Build debug APK, install on connected phone, and launch |
| `./milo apk` | Build production release APK (`dist/Milo.apk`) |
| `./milo publish [ver] [msg]` | Build, sign, commit, tag, and publish OTA update to GitHub (keeps only latest release) |
| `./milo test` | Run engine test verifiers and unit test suite |
| `./milo clean` | Clean all build caches and generated artifacts |
| `./milo update` | Validate `update.json` manifest syntax |

---

## ✦ In-App Update Engine & Data Preservation

MILO operates autonomously without dependency on third-party app stores:
1. **Silent Background Check**: On launch, MILO checks [`update.json`](./update.json) in the background without interrupting your workflow.
2. **1-Tap Upgrade**: When a new version is live, an in-app companion alert allows you to tap **UPDATE & INSTALL**.
3. **Cryptographic Validation**: The downloaded APK is validated client-side with SHA-256 before invoking Android's `PackageInstaller`.
4. **Guaranteed Data Preservation**: Updates are signed with the dedicated persistent Keystore (`milo-release.jks`), ensuring Android's package manager preserves your entire local database and user history across all updates.

---

## ✦ License
Open source and available under the [MIT License](LICENSE).
