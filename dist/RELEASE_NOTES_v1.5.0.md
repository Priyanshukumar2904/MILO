# MILO v1.5.0 (Build 10500)
*“Track your day. Understand your habits. See your progress. Become better than yesterday.”*

### ✦ Production Release Highlights
- **Genuine Signed Android Release Build**: Full unminified release binary (`11 MB`) with all Jetpack Compose, Material 3, and Room components intact.
- **Dedicated Production Keystore**: Signed with RSA 2048-bit release key with APK Signature Scheme v2 & v3.
- **Hardware-Backed User Accounts & Security**: Implemented full authentication (Login, Register, Guest Mode) backed by Android KeyStore AES-GCM 256-bit encrypted storage.
- **Streamlined 4-Tab Navigation**: Redesigned and consolidated navigation into 4 focused, intuitive views:
  1. **Today**: Live productivity score ring, dynamic time-of-day greeting, active goals, and real-time metric cards.
  2. **Planner**: Unified schedule timeline and habit streaks with horizontal date picker and category filtering.
  3. **Insights**: Behavioral intelligence, velocity trends, life domain scorecards, milestone records, and unlocked achievements.
  4. **Account**: Secure session profile, encrypted cloud sync trigger, in-app update management, and privacy vault status.
- **Enhanced Typography & Legibility**: Scaled up font sizes (12–28sp) and comfortable touch targets across all components for effortless readability.
- **Dynamic Real-Time Data**: Replaced all hardcoded mock timestamps and test data with dynamic `LocalDate.now()` time series.
- **Automated In-App Updates**: Full in-app update experience fetching remote manifests, streaming APK downloads with progress tracking, and enforcing cryptographic SHA-256 validation.

### ✦ Package Specifications
- **Artifact**: `Milo.apk` / `Milo-1.5.0.apk`
- **Build**: Release (Signed via Production Keystore)
- **Target SDK**: Android 15 (API 35) | **Min SDK**: API 26 (Android 8.0+)
- **Size**: 11,487,509 bytes (11 MB)
- **SHA-256**: `61b06c57cbe0b51629771ae783366569763c6c0d2dc82f528f7632d10cb83194`
