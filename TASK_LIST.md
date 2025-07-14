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
- [x] Sprint 2: Material3 theme scaffolding (Issue #6)
  - Implemented Color.kt, Type.kt, Theme.kt, Shape.kt, and Previews.kt
  - Applied Material3 color scheme based on ChatGPT's specifications
  - Fixed ktlint formatting issues
- [x] Sprint 2: Navigation Compose setup (Issue #7)
  - Created NavRoutes sealed class with route definitions
  - Implemented RootNavHost with animated transitions
  - Created placeholder screens: SplashScreen, CheckInScreen, TimelineScreen
  - Integrated navigation into MainActivity
- [x] Sprint 2: CheckInUiState contract (Issue #8)
  - Created CheckInUiState sealed interface with all states
  - Created CheckInEvent sealed interface for user interactions
  - Added Prompt data class
  - Created state machine documentation
- [x] Sprint 2: Hilt DI Module (Issue #11)
  - Set up Hilt dependency injection framework
  - Created MoodJournalApp with @HiltAndroidApp
  - Created AppModule with all singleton providers
  - Created missing files: MoodDatabase, PromptRepository, Entry, EntryDao, Converters
  - Added kotlinx.serialization for JSON parsing
  - Created HiltTestRunner and basic injection test
- [x] Sprint 2: Check-in ViewModel & Screen (Issue #9)
  - Implemented CheckInViewModel with full state management
  - Built complete CheckInScreen UI with mood selector
  - Added note input field and tag selection
  - Integrated with Hilt dependency injection
  - Added test tags for UI testing
- [x] Sprint 2: Timeline List Composable (Issue #10)
  - Created TimelineViewModel with Flow of entries
  - Built TimelineScreen with LazyColumn
  - Added smart date formatting (Today, Yesterday, etc.)
  - Added empty state handling
  - Implemented mood indicator circles
- [x] Sprint 2: Espresso integration test (Issue #12)
  - Added espresso test dependencies
  - Added test tags to UI components
  - Created CheckInFlowTest with happy path and validation tests
- [x] Fix protobuf configuration and KSP build errors
  - Fixed SDK path issues in local.properties
  - Configured protobuf plugin and source sets
  - Created missing proto file (user_prefs.proto)
  - Resolved KSP NonExistentClass errors
  - Fixed compilation errors in CheckInScreen and TimelineScreen
  - Successfully built debug APK

## In Progress

## Sprint 2 Complete!
All Sprint 2 issues (#6-#12) have been successfully implemented:
- ✅ Material3 theme with ChatGPT's green-based color scheme
- ✅ Navigation with animated transitions between screens
- ✅ State management contracts for clean architecture
- ✅ Hilt dependency injection setup
- ✅ Full check-in flow with mood selection and notes
- ✅ Timeline screen showing mood history
- ✅ Espresso UI tests
- ✅ Successfully building debug APK

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