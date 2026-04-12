---
phase: 01-foundation-dev-environment
plan: 04
subsystem: database
tags: [hilt, dependency-injection, repositories, dagger, singleton, flow, transactions]

# Dependency graph
requires:
  - phase: 01-03
    provides: Room entities, DAOs, TypeConverters, AppDatabase
provides:
  - Hilt DI graph with DatabaseModule and RepositoryModule
  - 4 repositories: CardRepository, ReadingRepository, SpreadRepository, DeckRepository
  - Entity-domain mapping centralized in repositories
  - Flow streams with distinctUntilChanged() for efficient UI updates
  - Transactional multi-table operations for reading CRUD
affects: [ui-layer, viewmodels]

# Tech tracking
tech-stack:
  added: [Hilt 2.59.2, Hilt Navigation Compose 1.2.0]
  patterns: [Repository as single source of truth, entity-domain mapping in repositories (not ViewModels), Flow streams with distinctUntilChanged(), @Transaction for multi-table writes, blocking DAO methods for repository write operations]

key-files:
  created:
    - app/src/main/kotlin/com/example/drawn/di/DatabaseModule.kt
    - app/src/main/kotlin/com/example/drawn/di/RepositoryModule.kt
    - app/src/main/kotlin/com/example/drawn/data/repository/CardRepository.kt
    - app/src/main/kotlin/com/example/drawn/data/repository/ReadingRepository.kt
    - app/src/main/kotlin/com/example/drawn/data/repository/SpreadRepository.kt
    - app/src/main/kotlin/com/example/drawn/data/repository/DeckRepository.kt
    - app/src/main/kotlin/com/example/drawn/domain/model/ReadingDetail.kt
  modified:
    - app/src/main/kotlin/com/example/drawn/data/database/dao/ReadingDao.kt
    - app/src/main/kotlin/com/example/drawn/data/database/dao/ReadingCardDao.kt
    - app/src/main/kotlin/com/example/drawn/data/database/dao/DeckDao.kt

key-decisions:
  - "Added blocking DAO methods (getReadingById, getCardsForReading, getDeckById) for repository write operations — Flow-based methods are for UI observation, not for transactional operations"
  - "ReadingDetail combines reading + cards + photos via combine() — single source for reading detail screen"
  - "Repositories are not explicitly @Singleton — Hilt infers singleton scope from their dependencies (DAOs are singleton via DatabaseModule)"

patterns-established:
  - "All repositories accept DAOs via constructor injection"
  - "Entity-domain mapping via .toDomain()/.toEntity() extension functions"
  - "Flow streams always use .distinctUntilChanged() before returning"
  - "Multi-table write operations use @Transaction annotation"
  - "Blocking DAO methods for repository writes, Flow methods for UI observation"

requirements-completed: [DEV-01]

# Metrics
duration: 15min
completed: 2026-04-03
---

# Phase 01 Plan 04: Hilt DI and Repositories Summary

**Hilt dependency injection with DatabaseModule and RepositoryModule, 4 repositories with entity-domain mapping and Flow-based reactive streams**

## Performance

- **Duration:** 15 min
- **Started:** 2026-04-03T21:45:00Z
- **Completed:** 2026-04-03T22:00:00Z
- **Tasks:** 2
- **Files modified:** 10

## Accomplishments
- Hilt DI graph with DatabaseModule (AppDatabase + 6 DAOs) and RepositoryModule (4 repositories)
- CardRepository with Flow-based card observation and bulk insert
- ReadingRepository with combined Flow stream for reading details, transactional CRUD
- SpreadRepository and DeckRepository with full CRUD operations
- Entity-domain mapping centralized in repositories
- Added blocking DAO methods for repository write operations

## Task Commits

Each task was committed atomically:

1. **Task 1+2: Hilt modules and repositories** - `2242254` (feat)

## Files Created/Modified
- `di/DatabaseModule.kt` - Hilt module providing AppDatabase singleton and all 6 DAOs
- `di/RepositoryModule.kt` - Hilt module providing 4 repositories with DAO dependencies
- `data/repository/CardRepository.kt` - Card observation by all/id/deck, bulk insert
- `data/repository/ReadingRepository.kt` - Reading observation, detail combine stream, transactional CRUD
- `data/repository/SpreadRepository.kt` - Spread observation, lookup, bulk insert
- `data/repository/DeckRepository.kt` - Deck observation (all/built-in), CRUD
- `domain/model/ReadingDetail.kt` - Combined model: reading + cards + photos
- `data/database/dao/ReadingDao.kt` - Added getReadingById (blocking)
- `data/database/dao/ReadingCardDao.kt` - Added getCardsForReading (blocking)
- `data/database/dao/DeckDao.kt` - Added getDeckById (blocking)

## Decisions Made
- Blocking DAO methods added for repository writes — Flow methods are for UI observation only
- ReadingDetail combines 3 Flow streams via combine() — single source for detail screen
- Repositories not explicitly @Singleton — scope inherited from singleton DAO dependencies

## Deviations from Plan

### Auto-fixed Issues

**1. [Rule 1 - Bug] Fixed Flow-based DAO methods used in transactional repository methods**
- **Found during:** Task 2 (ReadingRepository and DeckRepository implementation)
- **Issue:** `updateReading` and `deleteReading` tried to use Flow-based DAO methods (`observeReadingById`, `observeCardsForReading`) in suspend functions — Flows are cold streams, not suitable for blocking operations
- **Fix:** Added blocking DAO methods: `ReadingDao.getReadingById(id)`, `ReadingCardDao.getCardsForReading(id)`, `DeckDao.getDeckById(id)` — all suspend functions returning single values
- **Files modified:** ReadingDao.kt, ReadingCardDao.kt, DeckDao.kt, ReadingRepository.kt, DeckRepository.kt
- **Verification:** All repository methods compile with correct suspend signatures
- **Committed in:** 2242254 (Task 2 commit)

---

**Total deviations:** 1 auto-fixed (1 bug)
**Impact on plan:** Fix essential for correctness. No scope creep.

## Issues Encountered
- None

## Next Phase Readiness
- DI graph complete — ViewModels can now inject repositories via Hilt
- Ready for UI layer (Plan 01-05) — Material 3 theme, Navigation Compose 3, Reading List screen
- All data access patterns established: Flow for observation, suspend for writes

---
*Phase: 01-foundation-dev-environment*
*Completed: 2026-04-03*
