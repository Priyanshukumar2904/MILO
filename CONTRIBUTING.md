# Contributing to MILO 🐈

Thank you for your interest in improving **MILO**!

MILO is a native Android personal productivity and routine companion built with **Kotlin 2.0** and **Jetpack Compose**. Its central philosophy is:
> *“Track your day. Understand your habits. See your progress. Become better than yesterday.”*

---

## 🛠️ Developer Workflow

We have streamlined development into a single simple command:
```bash
./milo <command>
```

### Common Commands
- **Run on Emulator / Laptop**:
  ```bash
  ./milo run
  ```
- **Install on Connected Android Device**:
  ```bash
  ./milo install
  ```
- **Build APK**:
  ```bash
  ./milo apk
  ```
- **Run Tests**:
  ```bash
  ./milo test
  ```
- **Regenerate Screenshots & Demo GIF**:
  ```bash
  ./milo screenshots
  ```

---

## 📐 Architecture & Principles
1. **Offline-First**: All core persistence is in local Room SQLite. No external network dependencies are required for core features.
2. **Minimalist OLED Monochrome**: High-contrast black, white, and zinc palette.
3. **No Guilt / No Shame**: Copy and mascot reactions should never demean the user on slower days.
4. **Clean Code**: Follow modern Kotlin & Compose state management (Unidirectional Data Flow via StateFlow in `MiloViewModel`).

---

## 🚀 Submitting Pull Requests
1. Fork and create your feature branch: `git checkout -b feat/your-feature`
2. Ensure tests pass: `./milo test`
3. Commit with conventional commits: `feat:`, `fix:`, `refactor:`, `docs:`
4. Open a pull request against `main`.
