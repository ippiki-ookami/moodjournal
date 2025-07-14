# TASK_LIST.md - Mood Journal Project Tasks

## Completed Tasks
- [x] Create TASK_LIST.md file to track project tasks
- [x] Phase 0-A: Create Android project skeleton with Gradle files
  - Created Gradle build files with Kotlin 1.9.x, Compose BOM, Room 2.6.x, DataStore 1.1.x, MPAndroidChart 3.1.0
  - Set up MainActivity with Jetpack Compose
  - Created theme files and resources
  - Configured project for minSdk 24, targetSdk 34 (compileSdk 35 for latest Compose)
  - Added .gitignore and Gradle wrapper
  - Successfully built APK with ./gradlew assembleDebug
- [x] Phase 0-B: Add ktlint and detekt to Gradle setup
  - Added ktlint 11.6.1 with Android configuration
  - Added detekt 1.23.6 with custom configuration
  - Both tools run with `./gradlew check` (or individually)
  - Fixed all initial linting issues
- [x] Phase 0-C: Create GitHub Actions workflow
  - Created .github/workflows/android.yml with CI pipeline
  - Runs on push/PR to dev and main branches
  - Includes ktlint, detekt, build, and test steps
  - Uploads debug APK as artifact
  - Created CHANGELOG.md with artifact naming convention
- [x] Phase 0-D: Complete repository checklist
  - Created README.md with project overview
  - Added GitHub issue templates (bug report, feature request)
  - Added pull request template
  - Created REPOSITORY_SETUP.md with instructions for GitHub setup
  - All code-side repository setup complete

## In Progress

## Pending Tasks
- [ ] Create initial Android project structure
- [ ] Set up basic project configuration (Gradle, dependencies)
- [ ] Implement data layer (Room database, DataStore)
- [ ] Create domain layer (repositories, use-cases)
- [ ] Build UI layer (Compose screens, navigation)
- [ ] Implement prompt engine logic
- [ ] Add monetization (AdMob, Play Billing)
- [ ] Create export functionality (Mood Wrap)
- [ ] Add data export/delete features

## Notes
- This is a privacy-first mood journal Android app
- Following guidance from ChatGPT with implementation by Claude Code
- Tech stack: Kotlin, Jetpack Compose, Room, DataStore, MPAndroidChart
- MVP scope defined in PROJECT_OVERVIEW.md
- Technical details in IMPLEMENTATION_DETAILS.md