# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.1.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## Artifact Naming Convention
- Internal builds: `0.x.y-alphaZZ`
- Pre-release (feature-complete): `1.0.0-rcNN`
- Store launch: `1.0.0`

## [Unreleased]

## [Sprint 2] - 2025-07-15 ✅

### Added
- Complete Material3 theme implementation with dynamic colors
- Working Espresso test infrastructure with Hilt integration
- CheckInScreen with mood selection, note input, and tag functionality
- Comprehensive test coverage for check-in flow
- Prompt system with JSON asset loading
- Proto DataStore for user preferences
- Room database with Entry and related entities

### Fixed
- Espresso test synchronization issues with coroutine dispatchers
- Missing test database and asset configurations
- Navigation timing problems in instrumentation tests
- Proto DataStore compilation and dependency issues

### Technical Improvements
- MainDispatcherRule for proper test coroutine handling
- TestAppModule with in-memory database for isolated testing
- Comprehensive AI agent collaboration guidelines in CLAUDE.md
- Feature regression prevention protocols
- Task list management for multi-agent development

### Coverage Achievements
- ✅ Material3 UI implementation complete
- ✅ Core check-in functionality working end-to-end
- ✅ Espresso tests passing on emulator
- ✅ Data persistence layer established

### Next Steps
- Voice Mode MVP implementation (Sprint 3)
- Timeline view and trend visualization
- Mood Wrap PNG export functionality

## [Sprint 1] - Previous

### Added
- Initial Android project setup with Jetpack Compose, Room, DataStore, and MPAndroidChart
- ktlint and detekt for code quality checks
- GitHub Actions CI/CD workflow
- README.md with project overview and setup instructions
- GitHub issue and PR templates
- Repository setup documentation

### Technical Improvements
- Configured Gradle with Kotlin 1.9.24 and Compose BOM 2025.05.00
- Set up project structure following MVP architecture
- Added code linting with ktlint 11.6.1 and detekt 1.23.6
- Completed all Phase 0 setup tasks

## [0.0.1-alpha01] - TBD
- Initial project scaffolding