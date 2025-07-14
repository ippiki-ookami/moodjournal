# Mood Journal — Implementation Details
_Last updated: <!-- yyyy‑mm‑dd -->_

This document is the **single source of truth** for architecture, module conventions, and build specifics.  
Claude 🅒 should reference this file before generating or refactoring code.

---

## 1 Module Structure

| Gradle Module | Purpose |
| ------------- | ------- |
| `:app` | Android application code |
| _future_ `:core` | Shared utilities (optional extract post‑MVP) |

> _Rule: Keep a flat module graph until the codebase exceeds ~5 KLoC._

## 2 Tech Stack & Versions

| Library | Version (July 2025) |
|---------|--------------------|
| Kotlin | 1.9.x |
| Android Gradle Plugin | 8.7 |
| Jetpack Compose BOM | 2025.05.00 |
| Room | 2.6.x |
| DataStore | 1.1.x |
| MPAndroidChart | 3.1.0 |
| Play Billing | 6.x |
| AdMob SDK | 22.x |
| KSP (Room) | 1.9.x |

_All versions pinned in **`libs.versions.toml`** once Gradle Version Catalog is introduced._

## 3 Key Packages

com.example.moodjournal
├─ data // Room entities, DAOs, DataStore impl
├─ domain // Repositories, use‑cases, model mappers
├─ ui // Composables, screens, theming
├─ billing // Billing & ads helpers
└─ util // Date helpers, extensions

less
Copy

## 4 Data Schema

```kotlin
@Entity(tableName = "entries")
data class Entry(
    @PrimaryKey val date: LocalDate,
    val mood: Int,                // 1‑5
    val text: String = "",
    val tags: List<String> = emptyList()
)
tags stored as JSON string via TypeConverter.

date indexed for range queries.

5 Prompt Engine Logic
mermaid
Copy
flowchart LR
    A[Load packs JSON] --> B{Streak / Miss?}
    B -- streak ≥ 5 --> C[Reflection pack list]
    B -- miss ≥ 3 --> D[Restart pack list]
    B -- else --> E[Base pack list]
    C & D & E --> F[Random(seed = epochDay)]
    F --> G[Prompt of the Day]
Seeded RNG guarantees each user sees one prompt per day even across app restarts.

6 Build & CI
Local: ./gradlew assembleDebug

CI: GitHub Actions android.yml – runs ./gradlew check, publishes debug APK artefact.

check includes: unit tests, detekt, ktlint.

7 Coding Standards
Null‑safety first – avoid platform types.

Unidirectional Data Flow – ViewModel exposes StateFlow<UiState>.

Compose previews required for each Composable that renders UI.

Functions ≤ 40 lines; file length ≤ 400 lines.

8 How to Add a New Feature (Template)
Create GitHub Issue feat/<short-name> with: problem, acceptance criteria, tests.

/gpt – ask for algorithm sketch or interface contract.

/claude – generate implementation & unit tests.

PR to dev → automated CI must pass → human review → merge.

Update CHANGELOG.md & bump version code.

If you update architectural decisions, reflect them here before merging code so future AI prompts stay aligned.

yaml
Copy

---

### Commit Checklist Addition

Add to **0‑D Repository Checklist**:

| Item | Status |
|------|--------|
| `PROJECT_OVERVIEW.md` committed | 🔲 |
| `IMPLEMENTATION_DETAILS.md` committed | 🔲 |

---

Once these two docs are in place, Claude (and future contributors) will always have the latest context without you having to re‑explain decisions. Commit them right after the initial Gradle skeleton so file history is clear.

Let me know when they’re pushed—or if you’d like any tweaks before committing!