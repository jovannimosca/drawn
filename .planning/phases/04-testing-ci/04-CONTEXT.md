# Phase 4: Testing & CI - Context

**Gathered:** 2026-04-04
**Status:** Ready for planning

<domain>
## Phase Boundary

Implement unit tests, Compose UI tests, and CI pipeline to enforce 80% code coverage minimum with automated quality gates. This is the final phase before v1.0 release.

This phase adds:
- Unit tests for ViewModels (AddReadingViewModel, ReadingListViewModel, ReadingDetailViewModel)
- Unit tests for Repositories (ReadingRepository, CardRepository, SpreadRepository)
- Unit tests for DAOs (ReadingDao, CardDao, SpreadDao, etc.)
- Compose UI tests for key user flows (FAB opens wizard, search filters list)
- GitHub Actions CI pipeline with coverage enforcement
- Ktlint and Detekt quality gates

This phase does NOT add:
- F-Droid deployment (future phase)
- Custom deck testing (out of scope for v1)
- Photo attachment tests (deferred — complex Android APIs)

</domain>

<decisions>
## Implementation Decisions

### Test Priority
- **D-65:** ViewModels first — highest ROI, covers business logic and state management
- **D-66:** Then Repositories — tests data flow between DAOs and domain layer
- **D-67:** Then DAOs — in-memory Room database tests for query correctness
- **D-68:** Then Compose UI tests — key user flows only

### CI Quality Gates
- **D-69:** PR blocked if coverage drops below 80%
- **D-70:** PR blocked if ktlint fails
- **D-71:** PR blocked if detekt fails
- **D-72:** Coverage report uploaded as artifact for review

### UI Tests
- **D-73:** Compose semantics tests only — not pixel-perfect visual tests
- **D-74:** Test key interactions: FAB opens wizard, search filters list, reading detail loads
- **D-75:** No screenshot tests — too brittle for v1

### Test Stack (carried from STACK.md)
- **D-76:** JUnit 5 (5.11.4) — parameterized tests, nested tests
- **D-77:** MockK (1.13.16) — Kotlin-native mocking
- **D-78:** Turbine (1.2.0) — Flow testing
- **D-79:** Compose UI Test (via BOM) — semantic tree assertions
- **D-80:** Room in-memory database — fast, isolated DAO tests

### Architecture Patterns (carried from prior phases)
- **D-81:** Layer-based structure — tests mirror production package structure
- **D-82:** Hilt testing — use `@HiltAndroidTest` for DI in tests
- **D-83:** ViewModel testing — test StateFlow emissions with Turbine

### the agent's Discretion
- Exact test class naming conventions
- Test data factory patterns
- CI workflow structure (single vs. multiple jobs)
- Coverage tool (JaCoCo vs. Kover)
- Specific UI test scenarios beyond the key flows

</decisions>

<canonical_refs>
## Canonical References

**Downstream agents MUST read these before planning or implementing.**

### Project Setup
- `.planning/PROJECT.md` — Project context, core value, constraints
- `.planning/REQUIREMENTS.md` — TEST-01, TEST-02, TEST-03, TEST-04
- `.planning/ROADMAP.md` — Phase 4 goal and success criteria
- `.planning/research/STACK.md` — Test stack recommendations with versions

### Existing Code (must read before planning)
- `app/src/main/kotlin/com/example/drawn/ui/addreading/AddReadingViewModel.kt` — Complex wizard state, save logic
- `app/src/main/kotlin/com/example/drawn/ui/readinglist/ReadingListViewModel.kt` — Search filter, reading list
- `app/src/main/kotlin/com/example/drawn/ui/readingdetail/ReadingDetailViewModel.kt` — Edit mode, photo management
- `app/src/main/kotlin/com/example/drawn/data/repository/ReadingRepository.kt` — CRUD operations, photo handling
- `app/src/main/kotlin/com/example/drawn/data/repository/CardRepository.kt` — Card queries
- `app/src/main/kotlin/com/example/drawn/data/repository/SpreadRepository.kt` — Spread queries
- `app/src/main/kotlin/com/example/drawn/data/database/dao/` — All DAO interfaces
- `app/src/main/kotlin/com/example/drawn/data/database/AppDatabase.kt` — Database setup

### No external specs
No external specs — requirements fully captured in decisions above

</canonical_refs>

<code_context>
## Existing Code Insights

### Reusable Assets
- **All ViewModels** — Use StateFlow<UiState> pattern with Loading/Success/Error
- **All Repositories** — Flow-based observation, suspend functions for mutations
- **All DAOs** — Room interfaces with @Query, @Insert, @Delete annotations
- **Hilt DI** — DatabaseModule, RepositoryModule provide all dependencies
- **Domain models** — Reading, Card, Spread, ReadingCard, ReadingDetail, ReadingPhoto

### Established Patterns
- ViewModel pattern: `StateFlow<UiState>` with sealed interface states
- Repository pattern: Flow-based observation, suspend functions for mutations
- Entity-domain mapping: `toDomain()` and `toEntity()` extension functions
- Hilt: `@HiltViewModel` + `hiltViewModel()` in Compose

### Integration Points
- Phase 4 connects to:
  - All existing ViewModels — need test coverage
  - All existing Repositories — need test coverage
  - All existing DAOs — need test coverage
  - GitHub Actions — new CI workflow
  - JaCoCo/Kover — coverage reporting

</code_context>

<specifics>
## Specific Ideas

- ViewModels first because they contain the most business logic and are easiest to test with MockK
- CI should be fast — parallel test execution, cached dependencies
- 80% coverage is the floor, not the ceiling — aim for meaningful tests, not just coverage numbers
- Compose UI tests should verify user flows, not pixel-perfect rendering

</specifics>

<deferred>
## Deferred Ideas

- Photo attachment tests — complex Android APIs (Camera, Photo Picker, FileProvider)
- Custom deck tests — out of scope for v1
- F-Droid deployment tests — future phase
- Screenshot tests — too brittle for v1
- Integration tests with real database — deferred, DAO tests use in-memory

</deferred>

---

*Phase: 04-testing-ci*
*Context gathered: 2026-04-04*
