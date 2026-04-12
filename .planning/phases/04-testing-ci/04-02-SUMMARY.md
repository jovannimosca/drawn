---
phase: 04-testing-ci
plan: 02
subsystem: testing
tags: [room, robolectric, mockk, turbine, junit5, dao, repository, kotlin-test]

# Dependency graph
requires:
  - phase: 02-core-recording-loop
    provides: DAO interfaces, Repository implementations, Entity-domain mapping functions
provides:
  - 5 DAO test files with real Room in-memory database verification
  - 3 Repository test files with mocked DAOs
  - DatabaseTest base class for Robolectric + JUnit5
  - 45 passing tests covering CRUD, Flow observation, entity-domain mapping
affects: [CI pipeline setup, coverage enforcement, UI tests]

# Tech tracking
tech-stack:
  added: [robolectric-extension (tech.apter.junit5.jupiter), junit-vintage-engine]
  patterns: [DatabaseTest base class, Robolectric @Config(sdk = [33]), MockK for DAO isolation, Turbine for Flow testing, runBlocking for @BeforeEach suspend calls]

key-files:
  created:
    - app/src/test/kotlin/com/example/drawn/data/database/DatabaseTest.kt
    - app/src/test/kotlin/com/example/drawn/data/database/dao/ReadingDaoTest.kt
    - app/src/test/kotlin/com/example/drawn/data/database/dao/CardDaoTest.kt
    - app/src/test/kotlin/com/example/drawn/data/database/dao/SpreadDaoTest.kt
    - app/src/test/kotlin/com/example/drawn/data/database/dao/ReadingCardDaoTest.kt
    - app/src/test/kotlin/com/example/drawn/data/database/dao/ReadingPhotoDaoTest.kt
    - app/src/test/kotlin/com/example/drawn/data/repository/ReadingRepositoryTest.kt
    - app/src/test/kotlin/com/example/drawn/data/repository/CardRepositoryTest.kt
    - app/src/test/kotlin/com/example/drawn/data/repository/SpreadRepositoryTest.kt
  modified:
    - app/build.gradle.kts
    - gradle/libs.versions.toml

key-decisions:
  - "Used apter-tech/junit5-robolectric-extension instead of native Robolectric JUnit5 (doesn't exist as org.robolectric:junit5)"
  - "DatabaseTest base class with @BeforeEach/@AfterEach for lifecycle management"
  - "Parent entities (DeckEntity, SpreadEntity, ReadingEntity) inserted in @BeforeEach via runBlocking to satisfy foreign key constraints"
  - "coEvery for suspend DAO methods called inside Flow combine lambdas (cardDao.getCardsByIds)"

patterns-established:
  - "DAO tests: @ExtendWith(RobolectricExtension::class) + @Config(sdk = [33]) + DatabaseTest base class"
  - "Repository tests: @ExtendWith(MockKExtension::class) + mockk() DAOs + flowOf() for observe methods + Turbine for Flow verification"
  - "Nested test classes for grouping related test scenarios"

requirements-completed: [TEST-01]

# Metrics
duration: 15min
completed: 2026-04-04
---

# Phase 04 Plan 02: DAO and Repository Tests Summary

**5 DAO test files with Room in-memory database and 3 Repository test files with mocked DAOs — 45 passing tests covering full data layer correctness**

## Performance

- **Duration:** 15 min
- **Started:** 2026-04-04T17:24:00Z
- **Completed:** 2026-04-04T17:39:00Z
- **Tasks:** 2
- **Files modified:** 11

## Accomplishments

- 5 DAO test files with real Room in-memory database (25 tests covering insert, query, update, delete for all entities)
- 3 Repository test files with mocked DAOs (20 tests covering observe, create, update, delete, photo operations)
- Entity-domain mapping (toDomain/toEntity) verified correct in Repository tests
- Complex observeReadingWithDetails combine flow tested with all 5 DAOs
- Robolectric JUnit5 extension integrated for Android context in unit tests

## Task Commits

Each task was committed atomically:

1. **Task 1: Write DAO tests with Room in-memory database** - `b5ca25d` (feat)
2. **Task 2: Write Repository tests with mocked DAOs** - `a2733d7` (feat)

## Files Created/Modified

- `app/src/test/kotlin/com/example/drawn/data/database/DatabaseTest.kt` - Shared in-memory Room database base class
- `app/src/test/kotlin/com/example/drawn/data/database/dao/ReadingDaoTest.kt` - ReadingDao CRUD tests (7 tests)
- `app/src/test/kotlin/com/example/drawn/data/database/dao/CardDaoTest.kt` - CardDao CRUD + filter tests (6 tests)
- `app/src/test/kotlin/com/example/drawn/data/database/dao/SpreadDaoTest.kt` - SpreadDao CRUD tests (4 tests)
- `app/src/test/kotlin/com/example/drawn/data/database/dao/ReadingCardDaoTest.kt` - ReadingCardDao CRUD tests (4 tests)
- `app/src/test/kotlin/com/example/drawn/data/database/dao/ReadingPhotoDaoTest.kt` - ReadingPhotoDao CRUD tests (4 tests)
- `app/src/test/kotlin/com/example/drawn/data/repository/ReadingRepositoryTest.kt` - ReadingRepository tests (11 tests)
- `app/src/test/kotlin/com/example/drawn/data/repository/CardRepositoryTest.kt` - CardRepository tests (5 tests)
- `app/src/test/kotlin/com/example/drawn/data/repository/SpreadRepositoryTest.kt` - SpreadRepository tests (4 tests)
- `app/build.gradle.kts` - Added robolectric-junit5 and junit-vintage-engine dependencies
- `gradle/libs.versions.toml` - Added robolectric-junit5 extension and gradle plugin versions

## Decisions Made

- Used `tech.apter.junit5.jupiter:robolectric-extension` (v0.9.0) instead of non-existent `org.robolectric:junit5` — Robolectric doesn't provide an official JUnit5 extension
- Added `junit-vintage-engine` runtime dependency to allow JUnit4-based Robolectric runner to coexist with JUnit5 platform
- Parent entities inserted via `runBlocking` in `@BeforeEach` (not `= runBlocking {}`) to satisfy JUnit4 runner's void method requirement
- Used `coEvery` for `cardDao.getCardsByIds()` in ReadingRepositoryTest because it's called inside a `combine` lambda which is a suspend context

## Deviations from Plan

### Auto-fixed Issues

**1. [Rule 3 - Blocking] Added Robolectric JUnit5 extension dependency**
- **Found during:** Task 1 (DAO test compilation)
- **Issue:** `ApplicationProvider.getApplicationContext()` requires Android instrumentation; Robolectric doesn't have an official JUnit5 extension artifact
- **Fix:** Added `tech.apter.junit5.jupiter:robolectric-extension` gradle plugin and library dependency; updated all DAO test classes to use `@ExtendWith(RobolectricExtension::class)`
- **Files modified:** `app/build.gradle.kts`, `gradle/libs.versions.toml`, all 5 DAO test files
- **Verification:** All 25 DAO tests pass
- **Committed in:** `b5ca25d` (Task 1 commit)

**2. [Rule 3 - Blocking] Fixed foreign key constraint violations in DAO tests**
- **Found during:** Task 1 (DAO test runtime)
- **Issue:** SQLiteConstraintException because child entities (ReadingEntity, CardEntity, ReadingCardEntity) have foreign key references to parent entities (SpreadEntity, DeckEntity, ReadingEntity)
- **Fix:** Added `@BeforeEach` methods to insert parent entities before each test; used `runBlocking` wrapper to call suspend DAO insert methods
- **Files modified:** All 5 DAO test files
- **Verification:** All 25 DAO tests pass
- **Committed in:** `b5ca25d` (Task 1 commit)

**3. [Rule 3 - Blocking] Fixed suspend function calls in combine lambda mocks**
- **Found during:** Task 2 (Repository test compilation)
- **Issue:** `cardDao.getCardsByIds()` is a suspend function called inside `combine` lambda (which is a suspend context), requiring `coEvery` instead of `every`
- **Fix:** Changed `every { cardDao.getCardsByIds(...) }` to `coEvery { cardDao.getCardsByIds(...) }` in ReadingRepositoryTest
- **Files modified:** `ReadingRepositoryTest.kt`
- **Verification:** All 20 Repository tests pass
- **Committed in:** `a2733d7` (Task 2 commit)

---

**Total deviations:** 3 auto-fixed (3 blocking)
**Impact on plan:** All auto-fixes necessary for test compilation and execution. No scope creep.

## Issues Encountered

- `org.robolectric:junit5` doesn't exist as a Maven artifact — Robolectric has no official JUnit5 extension. Used community `tech.apter.junit5.jupiter:robolectric-extension` instead.
- `@BeforeEach` methods returning `runBlocking {}` caused `InvalidTestClassError` because the apter extension internally uses JUnit4's `BlockJUnit4ClassRunner` which expects void methods. Fixed by wrapping `runBlocking` body inside the method rather than using expression syntax.
- JUnit5 `@Nested` classes work correctly with MockK's `@ExtendWith(MockKExtension::class)` on the outer class.

## Known Stubs

None — all tests use real data and real mappings. No placeholder or TODO values.

## Next Phase Readiness

- Data layer fully tested — ready for ViewModel test expansion or CI pipeline setup
- All DAO query correctness verified against real SQLite
- Repository entity-domain mapping verified correct
- No blockers for next plan

---
*Phase: 04-testing-ci*
*Completed: 2026-04-04*
