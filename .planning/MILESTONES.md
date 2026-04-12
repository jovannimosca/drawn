# Milestones

## v1.0 MVP (Shipped: 2026-04-12)

**Phases completed:** 7 phases, 26 plans, 38 tasks

**Key accomplishments:**

- Complete Android project with Gradle 8.13 + AGP 9.1.0, version catalog with 20+ libraries, Hilt Application entry point, and layer-based package structure
- GitHub Actions CI/CD with build verification, unit tests, Ktlint formatting checks, Detekt static analysis, and Android lint quality gates
- Complete Room database with 6 tables, entity-domain separation, Flow-based DAOs, and createFromAsset() pre-population strategy
- Hilt dependency injection with DatabaseModule and RepositoryModule, 4 repositories with entity-domain mapping and Flow-based reactive streams
- Material 3 dark theme, Navigation Compose 3 with type-safe routes, ReadingListScreen with empty state, and Hilt-wired MainActivity
- One-liner:
- 3-step wizard UI with spread picker grid, notes/save form, and step progress indicator — following Material 3 dark theme and UI-SPEC contract
- Step 2 card-to-position assignment UI with bottom sheet card picker showing all 78 cards in 5 sectioned groups
- Complete user journey: FAB → wizard → save → list → detail view with all navigation stubs resolved
- One-liner:
- Search bar with real-time filtering of readings by title and notes, case-insensitive matching, clear button, and FAB hidden during active search
- Inline edit mode for reading title and notes with save/discard, plus delete reading with confirmation dialog and navigation back to list
- One-liner:
- One-liner:
- Kover coverage enforcement (80% threshold) with 41 passing ViewModel unit tests across AddReadingViewModel, ReadingListViewModel, and ReadingDetailViewModel using JUnit 5, MockK, and coroutines test dispatcher
- 5 DAO test files with Room in-memory database and 3 Repository test files with mocked DAOs — 45 passing tests covering full data layer correctness
- 3 Compose UI test files (39 tests) covering key user flows + CI workflow updated with koverVerify coverage gate and artifact uploads
- One-liner:
- Status:
- Status:
- Status:
- Status:
- Status:
- Status:
- Fix AnimatedVisibility fade-in to trigger on state transition — uses LaunchedEffect to animate on data load
- Implemented shared element-like transitions using NavDisplay transitionSpec with fade+scale animation

---
