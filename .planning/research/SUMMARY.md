# Project Research Summary

**Project:** Drawn — Android Tarot Card Reading App
**Domain:** Local-only Android app for tracking and organizing tarot readings
**Researched:** 2026-04-18
**Confidence:** HIGH

## Executive Summary

This research covers three major features for Drawn v1.1: custom card decks, reading tags, and backup/restore functionality. The existing Kotlin + Jetpack Compose + Room stack fully supports all three features with only one version update required (Kotlinx Serialization 1.8.0 → 1.9.0 for JSON export). Custom decks require updating the existing card selection UI to be deck-aware, which is the primary blocking dependency. Tags are low-complexity with no dependencies. Backup/restore builds on custom decks and uses standard Android file picker APIs.

Key risks identified: image storage limits must be enforced to prevent app crashes, tags must use relational tables (not JSON columns) for query performance, and backups must include media files to avoid data loss. All three features integrate through incremental schema additions without requiring refactoring of existing v1.0 components.

## Key Findings

### Recommended Stack

The v1.0 stack remains valid with one dependency update. Kotlinx Serialization must be bumped from 1.8.0 to 1.9.0 for backup/restore JSON export (1.10.0 requires Kotlin 2.3.0, but project uses Kotlin 2.2.21).

**Core technologies:**
- **Kotlin 2.2.21 + Jetpack Compose BOM 2025.12.00** — UI framework with Material 3
- **Room 2.8.4** — Local SQLite with Flow support, handles all new entities
- **Hilt 2.59.2** — Dependency injection with compile-time safety
- **Coil 3.4.0** — Image loading for bundled and custom card images
- **Navigation Compose 3 (1.0.1)** — Type-safe routing with @Serializable
- **Kotlinx Serialization 1.9.0** — JSON backup export/import (UPDATED)

No new external dependencies required for any v1.1 feature.

### Expected Features

**Must have (table stakes):**
- **Custom Decks:** Deck editor with name, cards, images, meanings. Card selection UI must be deck-aware before editor can function.
- **Reading Tags:** Tag CRUD, tag assignment to readings, tag filtering in reading list. Uses many-to-many relationship.
- **Backup/Restore:** JSON export/import via Storage Access Framework. Full backup includes readings, custom decks, tags, and photos.

**Should have (competitive):**
- Deck duplication (clone existing deck)
- Tag colors for visual organization
- Deck import/export (share between users)
- Merge on import (combine with existing data)

**Defer (v2+):**
- Cloud backup — violates local-only constraint
- Mixed deck reading (draw from multiple decks)
- Encrypted backup
- Deck marketplace/in-app store

### Architecture Approach

The architecture follows Clean Architecture with UI → ViewModel → Repository → DAO layers. v1.1 adds 4 new tables (decks, custom_cards, tags, reading_tags junction) via Room auto-migration from version 1 to 2. New repositories (DeckRepository, TagRepository, BackupRepository) wrap DAOs. Existing CardPickerViewModel must be refactored to merge RWS + custom card sources.

**Major components:**
1. **Data Layer:** 4 new entities, 4 new DAOs, updated AppDatabase (version 2)
2. **Repository Layer:** DeckRepository, TagRepository, BackupRepository
3. **UI Layer:** 4 new screens (DeckListScreen, DeckEditorScreen, TagManagerScreen, BackupScreen), modified CardPicker and ReadingEntry screens
4. **Backup Service:** JSON serialization via Kotlinx Serialization, file I/O via ActivityResultContracts

### Critical Pitfalls

1. **Custom Deck Image Storage Without Size Limits** — Users import large images causing OOM crashes. Must enforce max 1200px dimension, 500KB compression, generate 200px thumbnails.

2. **Tag System Using JSON Column** — Storing tags as JSON text causes 400-500ms queries at scale. Must use relational many-to-many schema with indices — benchmark shows <1.5ms vs 400ms+.

3. **Backup Without Media Files** — JSON export only captures database, missing photos in app storage. Backup must include media directory or users lose data on restore.

4. **Restore Creates Duplicate Readings** — No deduplication logic causes readings to multiply on repeated restores. Must use UUID preservation and offer merge/replace choice.

5. **Deck Card Count Validation Missing** — User creates deck with too few cards for selected spread, app crashes. Must validate minCardCount against spread requirements.

6. **Tag UI Clutter With Many Tags** — 15+ tags overflow container, tags outside visible area become untappable. Must implement wrap layout with "+N more" collapse.

7. **Deck Image Path Not Portable on Restore** — Absolute paths break across devices. Must use relative paths in database and include images in backup archive.

## Implications for Roadmap

Based on research, suggested phase structure:

### Phase 1: Database Schema & Data Layer
**Rationale:** All features depend on correct schema design. Tags and custom decks both require new tables. This phase establishes the foundation for all subsequent work.

**Delivers:**
- 4 new Room entities (DeckEntity, CustomCardEntity, TagEntity, ReadingTagEntity)
- isFavorite column added to ReadingEntity
- 4 new DAOs with CRUD operations
- Domain models (Deck, CustomCard, Tag)

**Addresses:**
- Custom Decks (table stakes) — entities and basic CRUD
- Reading Tags (table stakes) — many-to-many relationship setup

**Avoids:**
- Pitfall: JSON column for tags (use relational schema with indices)
- Pitfall: Image storage without limits (enforce in data layer)
- Pitfall: Deck card count validation (store minCardCount on entity)

### Phase 2: Repository Layer
**Rationale:** UI components depend on repositories for data access. Must complete before ViewModels can be implemented.

**Delivers:**
- DeckRepository (custom deck CRUD, card management)
- TagRepository (tag CRUD, reading association)
- BackupRepository (JSON export/import logic)
- Refactored CardRepository to merge RWS + custom cards
- Updated ReadingRepository with favorite methods

**Implements:**
- Architecture pattern: Dual card source in CardPicker
- Architecture pattern: Tag multi-select data flow

### Phase 3: ViewModel Layer
**Rationale:** ViewModels provide state management for UI. Depends on repository layer.

**Delivers:**
- DeckListViewModel, DeckEditorViewModel
- TagManagerViewModel
- BackupViewModel with export/import progress
- Modified CardPickerViewModel (deck filtering)
- Modified ReadingEntryViewModel (tag selection)
- Modified ReadingListViewModel (tag/favorite filtering)

### Phase 4: UI Implementation
**Rationale:** Final UI layer builds on ViewModels. This is the largest phase by line count.

**Delivers:**
- DeckListScreen, DeckEditorScreen with card editor
- TagManagerScreen
- BackupScreen with file picker integration
- Modified ReadingEntryScreen (tag selector chips)
- Modified ReadingListScreen (filter chips, favorites)
- New navigation routes

**Avoids:**
- Pitfall: Tag UI clutter (implement wrap layout + collapse)
- Pitfall: Deck selection in wizard forgets selection (persist in ViewModel)

### Phase 5: Backup Media & Validation
**Rationale:** Must include photos in backup to prevent data loss. Critical feature that requires integration with file system.

**Delivers:**
- Media directory backup (copy photos to backup archive)
- Relative path handling for restore portability
- Backup manifest/validation
- Restore preview (show what will be imported)

**Avoids:**
- Pitfall: Backup missing media files
- Pitfall: Restore creates duplicates
- Pitfall: Deck image path not portable on restore

### Phase Ordering Rationale

- **Reading Tags first:** Lowest complexity, no dependencies, establishes data model patterns
- **Custom Decks second:** Medium complexity, requires card selection UI update (blocking dependency)
- **Backup third:** Depends on data model, implement after custom decks to include deck images
- **Backup Media last:** Critical for data integrity, builds on basic backup

### Research Flags

Phases likely needing deeper research during planning:
- **Phase 4 (UI Implementation):** Card editor UI patterns for custom decks — limited reference material
- **Phase 5 (Backup Media):** Android scoped storage edge cases on Android 11+ — complex platform behavior

Phases with standard patterns (skip research-phase):
- **Phase 1 (Schema):** Room relationships are well-documented
- **Phase 2 (Repositories):** Standard repository pattern
- **Phase 3 (ViewModels):** Standard MVVM with Compose

## Confidence Assessment

| Area | Confidence | Notes |
|------|------------|-------|
| Stack | HIGH | All versions from official sources, single version update identified |
| Features | HIGH | Reference apps (Uni Tarot, Paper Tape Tarot, Deckible) confirm feasibility |
| Architecture | HIGH | Clean integration via incremental schema, well-documented patterns |
| Pitfalls | MEDIUM-HIGH | Some benchmarks extrapolated from web research, Android-specific behavior verified |

**Overall confidence:** HIGH

### Gaps to Address

- **Deck import/export format:** No community standard found — proprietary JSON for v1.1, may need validation during implementation
- **Card image compression ratios:** Optimal settings not benchmarked — recommend testing with sample images during Phase 1
- **Large reading count performance:** No data at 1000+ readings — may need pagination if issues arise in testing

## Sources

### Primary (HIGH confidence)
- Jetpack Compose December '25 Release — UI framework version
- Room 2.8.4 on Maven Repository — Database version
- Kotlinx Serialization 1.9.0 Release — JSON serialization
- Android Navigation 3 Stable Announcement — Type-safe navigation
- Room Auto-Migration documentation — Schema migration

### Secondary (MEDIUM confidence)
- Simon Willison SQLite Tags Benchmark — Relational vs JSON performance
- Tarot Journal app reviews — Tag UI overflow issues
- Stack Overflow community patterns — Backup/restore implementations
- Hilt vs Koin 2025 Comparison — DI framework selection

### Tertiary (LOW confidence)
- Arcana Land Deck Spec — Custom deck file structure (not adopted, reference only)
- Galaxy Tarot backup feature — APK analysis, not official docs

---
*Research completed: 2026-04-18*
*Ready for roadmap: yes*