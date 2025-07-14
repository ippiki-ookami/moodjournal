# Mood Journal

![CI](https://github.com/ippiki-ookami/moodjournal/actions/workflows/android.yml/badge.svg)

A privacy-first mood tracking Android app built with Kotlin and Jetpack Compose.

## Features (Planned)
- Daily mood tracking with guided prompts
- Visual analytics with charts and graphs
- Privacy-focused: all data stored locally
- Export and backup functionality
- Customizable prompts and themes

## Tech Stack
- **Language**: Kotlin
- **UI**: Jetpack Compose
- **Database**: Room
- **Preferences**: DataStore
- **Charts**: MPAndroidChart
- **Architecture**: MVP with Repository pattern

## Development Setup

### Prerequisites
- Android Studio Hedgehog or newer
- JDK 17
- Android SDK with API 35

### Building
```bash
# Build debug APK
./gradlew assembleDebug

# Run tests
./gradlew test

# Run linting
./gradlew ktlintCheck detekt
```

## Project Structure
```
app/
├── src/
│   ├── main/
│   │   ├── java/com/example/moodjournal/
│   │   │   ├── data/        # Room database, repositories
│   │   │   ├── domain/      # Use cases, business logic
│   │   │   ├── presentation/# UI components, screens
│   │   │   └── MainActivity.kt
│   │   └── res/
│   └── test/
└── build.gradle.kts
```

## Contributing
This project uses:
- **ktlint** for Kotlin code formatting
- **detekt** for static analysis
- GitHub Actions for CI/CD

Please ensure all checks pass before submitting PRs.

## License
TBD