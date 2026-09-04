# Changelog

All notable changes to the **MILO** Android application will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

---

## [1.4.0] - 2026-09-05

### Added
- **Interactive Mascot Engine**: 8 dynamic canvas-drawn emotional states (`Calm`, `Happy`, `Proud`, `Curious`, `Encouraging`, `Sleepy`, `Celebrating`, `Welcoming`) with real-time vector animations.
- **Holistic Productivity Scoring Engine (0-100)**: Modular algorithm balancing task completion, time adherence, deep focus stamina, habit disciplines, and routine consistency.
- **Improvement Delta (+9%)**: Transparent daily baseline comparison showing incremental growth.
- **Multi-Scale Schedule**: Time-blocking with Day, Week, and Month perspectives.
- **Habit Consistency Matrix**: 7-day visual grids and 4-week velocity curves without streak-shaming.
- **Monthly Life Report**: Signature retrospective (*“You showed up.”*) summarizing active days, flow hours, and personal bests.
- **Zen Focus Ambient Timer**: Distraction-free countdown mode with concentric breathing rings and WorkManager sync.
- **Developer CLI (`./milo`)**: Unified single-command developer experience for running, installing, building APKs, and releasing.
- **Self-Hosted In-App Updates**: Client-side SHA-256 APK checksum verification before package installation.
- **Automated Media Assets**: High-resolution screenshot suite in `docs/screenshots/` and animated preview in `docs/demo/milo-demo.gif`.

### Improved
- Offline-first Room database performance with battery-aware WorkManager synchronization.
- Android 15 (Target SDK 35) edge-to-edge layout compatibility.
- ProGuard rules and release bundle size optimization.

### Known Issues
- None currently known.
