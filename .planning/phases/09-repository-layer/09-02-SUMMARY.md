---
phase: 09-repository-layer
plan: "02"
subsystem: data
tags: [repository, favorites, tags, filtering]
dependency_graph:
  requires: [09-01]
  provides: [ReadingRepository.favorites, ReadingRepository.tag-filtering]
  affects: [ui-layer]
tech_stack:
  added: [ReadingRepository favorites methods, ReadingRepository tag filtering]
  patterns: [Flow-based reactive data access, Room DAO mapping, Room queries]
key_files:
  created: []
  modified:
    - app/src/main/kotlin/com/example/drawn/data/database/dao/ReadingDao.kt
    - app/src/main/kotlin/com/example/drawn/data/database/dao/ReadingTagDao.kt
    - app/src/main/kotlin/com/example/drawn/data/repository/ReadingRepository.kt
    - app/src/main/kotlin/com/example/drawn/di/RepositoryModule.kt
decisions:
  - "[Task 1] ReadingDao.observeFavorites() returns all ReadingEntity fields"
  - "[Task 1] ReadingDao.observeFavoritesWithSpread() matches observeAllReadingsWithSpread() structure per D-02"
  - "[Task 3] Tag filter uses ANY (OR) logic - reading with ANY selected tag appears in results"
  - "[Task 4] observeReadingsByTags() fetches readings by IDs from ReadingTagDao"
metrics:
  started: "2026-04-19T17:47:26Z"
  completed: "2026-04-19T17:47:45Z"
  tasks: 5
  files_created: 0
  files_modified: 4
---

# Phase 09 Plan 02: Repository Layer - Favorites and Tag Filtering

## Summary

Extended ReadingRepository with favorites and tag-based filtering capabilities. Added query methods to ReadingDao and ReadingTagDao, and corresponding repository methods to support filtering readings by favorite status and tags.

## Completed Tasks

### Task 1: Add favorites observe methods to ReadingDao
- **Status:** Complete
- **Commit:** 55e7598
- **Modified:** `app/src/main/kotlin/com/example/drawn/data/database/dao/ReadingDao.kt`
- **Methods added:**
  - `observeFavorites(): Flow<List<ReadingEntity>>` - observes all favorite readings
  - `observeFavoritesWithSpread(): Flow<List<ReadingWithSpread>>` - observes favorites with spread info

### Task 2: Extend ReadingRepository with favorites
- **Status:** Complete
- **Commit:** 55e7598
- **Modified:** `app/src/main/kotlin/com/example/drawn/data/repository/ReadingRepository.kt`
- **Methods added:**
  - `toggleFavorite(readingId: Long)` - toggles favorite status for a reading
  - `observeFavorites(): Flow<List<Reading>>` - observes favorite readings (domain model)
  - `observeFavoritesWithSpread(): Flow<List<ReadingWithSpread>>` - observes favorites with spread

### Task 3: Add tag-based reading query to ReadingTagDao
- **Status:** Complete
- **Commit:** 55e7598
- **Modified:** `app/src/main/kotlin/com/example/drawn/data/database/dao/ReadingTagDao.kt`
- **Methods added:**
  - `observeReadingIdsByTags(tagIds: List<Long>): Flow<List<Long>>` - returns reading IDs matching ANY of the provided tags (OR logic)

### Task 4: Add tag filter to ReadingRepository
- **Status:** Complete
- **Commit:** 55e7598
- **Modified:** `app/src/main/kotlin/com/example/drawn/data/repository/ReadingRepository.kt`
- **Methods added:**
  - `observeReadingsByTags(tagIds: List<Long>): Flow<List<Reading>>` - observes readings matching ANY of the provided tag IDs
- **Dependency:** ReadingTagDao added to constructor (was already present per 09-01)

### Task 5: Update RepositoryModule DI
- **Status:** Complete
- **Commit:** 55e7598
- **Modified:** `app/src/main/kotlin/com/example/drawn/di/RepositoryModule.kt`
- **Providers added:**
  - `provideCustomCardRepository(CustomCardDao)` - provides CustomCardRepository
  - `provideTagRepository(TagDao)` - provides TagRepository
  - `provideReadingTagRepository(TagDao, ReadingTagDao)` - provides ReadingTagRepository
- **Updated:**
  - `provideReadingRepository` now includes `readingTagDao: ReadingTagDao` parameter

## Deviations from Plan

### Auto-fixed Issues

None - plan executed exactly as written.

## Known Stubs

None.

## Threat Flags

None.

## Self-Check: PASSED

All 4 files modified as planned:
- ReadingDao: ✅ Has observeFavorites(), observeFavoritesWithSpread()
- ReadingTagDao: ✅ Has observeReadingIdsByTags()
- ReadingRepository: ✅ Has toggleFavorite(), observeFavorites(), observeFavoritesWithSpread(), observeReadingsByTags()
- RepositoryModule: ✅ Has all repository providers

Build verification: ✅ Compilation successful
