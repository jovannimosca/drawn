---
phase: 01-foundation-dev-environment
plan: 03
subsystem: database
tags: [room, sqlite, dao, entities, typeconverters, flow, ksp, kotlin-serialization]

# Dependency graph
requires:
  - phase: 01-01
    provides: Gradle project structure with Room and KSP dependencies
provides:
  - Complete Room database schema with 6 tables (decks, cards, spreads, readings, reading_cards, reading_photos)
  - 6 domain models separated from Room entities
  - 6 DAOs with Flow-based reactive queries and suspend write functions
  - TypeConverters for Instant and ArcanaType
  - AppDatabase singleton with createFromAsset() pre-population
  - Foreign key constraints and indices for referential integrity
affects: [di-modules, repositories, ui-layer]

# Tech tracking
tech-stack:
  added: [Room 2.8.4, KSP 2.2.21-2.0.0, Kotlinx Serialization for JSON conversion]
  patterns: [Entity-Domain model separation with toDomain()/toEntity() extensions, Flow-based reactive queries, suspend write functions, WAL journal mode, schema export to app/schemas/]

key-files:
  created:
    - app/src/main/kotlin/com/example/drawn/domain/model/ArcanaType.kt
    - app/src/main/kotlin/com/example/drawn/domain/model/Card.kt
    - app/src/main/kotlin/com/example/drawn/domain/model/Reading.kt
    - app/src/main/kotlin/com/example/drawn/domain/model/Spread.kt
    - app/src/main/kotlin/com/example/drawn/domain/model/ReadingCard.kt
    - app/src/main/kotlin/com/example/drawn/domain/model/ReadingPhoto.kt
    - app/src/main/kotlin/com/example/drawn/domain/model/Deck.kt
    - app/src/main/kotlin/com/example/drawn/data/database/entity/CardEntity.kt
    - app/src/main/kotlin/com/example/drawn/data/database/entity/ReadingEntity.kt
    - app/src/main/kotlin/com/example/drawn/data/database/entity/SpreadEntity.kt
    - app/src/main/kotlin/com/example/drawn/data/database/entity/ReadingCardEntity.kt
    - app/src/main/kotlin/com/example/drawn/data/database/entity/ReadingPhotoEntity.kt
    - app/src/main/kotlin/com/example/drawn/data/database/entity/DeckEntity.kt
    - app/src/main/kotlin/com/example/drawn/data/database/converter/DateTypeConverter.kt
    - app/src/main/kotlin/com/example/drawn/data/database/converter/ArcanaTypeConverter.kt
    - app/src/main/kotlin/com/example/drawn/data/database/dao/CardDao.kt
    - app/src/main/kotlin/com/example/drawn/data/database/dao/ReadingDao.kt
    - app/src/main/kotlin/com/example/drawn/data/database/dao/SpreadDao.kt
    - app/src/main/kotlin/com/example/drawn/data/database/dao/ReadingCardDao.kt
    - app/src/main/kotlin/com/example/drawn/data/database/dao/ReadingPhotoDao.kt
    - app/src/main/kotlin/com/example/drawn/data/database/dao/DeckDao.kt
    - app/src/main/kotlin/com/example/drawn/data/database/AppDatabase.kt
  modified:
    - app/build.gradle.kts

key-decisions:
  - "Keywords stored as JSON string in CardEntity — avoids separate junction table for simple string lists"
  - "Spread positions stored as JSON array — positions are tightly coupled to spread, no need for separate table"
  - "Instant stored as Long (epoch millis) — Room doesn't natively support java.time types"
  - "ArcanaType stored as String enum name — simpler than ordinal, survives enum reordering"
  - "ReadingCard→Card foreign key uses RESTRICT (not CASCADE) — prevents accidental card deletion"
  - "WAL journal mode for better concurrent read performance"
  - "Schema export enabled to app/schemas/ for future migration reference"

patterns-established:
  - "Entity-Domain separation: each entity has toDomain() and toEntity() extension functions"
  - "All read queries return Flow<List<Entity>> for reactive UI updates"
  - "All write functions are suspend for coroutine-safe database operations"
  - "Foreign keys with appropriate cascade/restrict behavior per relationship semantics"

requirements-completed: [DEV-01]

# Metrics
duration: 20min
completed: 2026-04-03
---

# Phase 01 Plan 03: Database Schema Summary

**Complete Room database with 6 tables, entity-domain separation, Flow-based DAOs, and createFromAsset() pre-population strategy**

## Performance

- **Duration:** 20 min
- **Started:** 2026-04-03T21:25:00Z
- **Completed:** 2026-04-03T21:45:00Z
- **Tasks:** 3
- **Files modified:** 22

## Accomplishments
- 6 domain models (pure Kotlin, no Android dependencies)
- 6 Room entities with foreign keys, indices, and conversion extensions
- 2 TypeConverters (Instant↔Long, ArcanaType↔String)
- 6 DAOs with Flow-based reactive queries and suspend write functions
- AppDatabase singleton with pre-population via createFromAsset()
- Schema export enabled for future migration reference

## Task Commits

Each task was committed atomically:

1. **Task 1: Domain models** - `d9dd6ea` (feat)
2. **Task 1: Room entities and TypeConverters** - `c6c1900` (feat)
3. **Task 2+3: DAOs and AppDatabase** - `ce12d86` (feat)

## Files Created/Modified
- `domain/model/ArcanaType.kt` - MAJOR/MINOR enum
- `domain/model/Card.kt` - Card domain model with keywords list and meanings
- `domain/model/Deck.kt` - Deck domain model with custom flag
- `domain/model/Reading.kt` - Reading domain model with optional notes
- `domain/model/ReadingCard.kt` - ReadingCard linking readings to cards with position and interpretation
- `domain/model/ReadingPhoto.kt` - ReadingPhoto with URI and optional caption
- `domain/model/Spread.kt` - Spread with nested SpreadPosition list
- `data/database/entity/CardEntity.kt` - Room entity with FK to DeckEntity, keywords as JSON
- `data/database/entity/DeckEntity.kt` - Room entity with auto-generated ID
- `data/database/entity/ReadingEntity.kt` - Room entity with FK to SpreadEntity (CASCADE)
- `data/database/entity/ReadingCardEntity.kt` - Room entity with FKs to ReadingEntity (CASCADE) and CardEntity (RESTRICT)
- `data/database/entity/ReadingPhotoEntity.kt` - Room entity with FK to ReadingEntity (CASCADE)
- `data/database/entity/SpreadEntity.kt` - Room entity with positions as JSON array
- `data/database/converter/DateTypeConverter.kt` - Instant ↔ Long conversion
- `data/database/converter/ArcanaTypeConverter.kt` - ArcanaType ↔ String conversion
- `data/database/dao/CardDao.kt` - Flow-based card queries, batch insert
- `data/database/dao/ReadingDao.kt` - Flow-based reading queries, CRUD operations
- `data/database/dao/SpreadDao.kt` - Flow-based spread queries, batch insert
- `data/database/dao/ReadingCardDao.kt` - Flow-based reading card queries, batch insert/delete
- `data/database/dao/ReadingPhotoDao.kt` - Flow-based photo queries, insert/delete
- `data/database/dao/DeckDao.kt` - Flow-based deck queries, CRUD operations
- `data/database/AppDatabase.kt` - Database singleton with all entities, DAOs, pre-population config

## Decisions Made
- Keywords stored as JSON in CardEntity — avoids junction table for simple string lists
- Spread positions as JSON — positions are tightly coupled to spread definition
- RESTRICT on ReadingCard→Card FK — prevents accidental deletion of cards referenced in readings
- WAL journal mode — better concurrent read performance for reading history browsing

## Deviations from Plan

None - plan executed exactly as written.

## Known Stubs

**1. Pre-populated database file (`database/drawn_prepopulated.db`)**
- **File:** `app/src/main/assets/database/` (directory created, DB file not yet generated)
- **Reason:** The actual SQLite database file with 78 RWS cards needs to be generated separately — either by running the app once with programmatic seed insertion and exporting the DB, or by creating it manually with a SQLite tool. This is a data artifact, not a code artifact.
- **Resolution:** Will be created before Phase 3 (reading recording) — can be generated by running a seed script or manually creating the DB file.

## Issues Encountered
- None

## Next Phase Readiness
- Database schema complete — ready for Hilt DI modules (Plan 01-04)
- Repositories can now bridge DAOs to domain layer
- All entities, DAOs, and converters compile-ready for KSP code generation
- Pre-populated DB file needed before app can run with card catalog

---
*Phase: 01-foundation-dev-environment*
*Completed: 2026-04-03*
