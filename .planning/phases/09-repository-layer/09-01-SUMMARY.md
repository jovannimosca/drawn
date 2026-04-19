---
phase: 09-repository-layer
plan: "01"
subsystem: data
tags: [repository, flow, custom-cards, tags]
dependency_graph:
  requires: [08-db-schema]
  provides: [CustomCardRepository, TagRepository, ReadingTagRepository]
  affects: [ui-layer, domain-layer]
tech_stack:
  added: [CustomCardRepository, TagRepository, ReadingTagRepository]
  patterns: [Flow-based reactive data access, Room DAO mapping]
key_files:
  created:
    - app/src/main/kotlin/com/example/drawn/data/repository/CustomCardRepository.kt
    - app/src/main/kotlin/com/example/drawn/data/repository/TagRepository.kt
    - app/src/main/kotlin/com/example/drawn/data/repository/ReadingTagRepository.kt
  modified:
    - app/src/main/kotlin/com/example/drawn/data/repository/DeckRepository.kt
decisions:
  - "[Task 4 - Pre-existing] DeckRepository.observeCustomDecks() already exists, DeckDao already has observeCustomDecks query - no changes needed"
metrics:
  started: "2026-04-19T17:43:19Z"
  completed: "2026-04-19T17:45:00Z"
  tasks: 4
  files_created: 3
  files_modified: 1
---

# Phase 09 Plan 01: Repository Layer - Custom Cards, Tags, and Deck Favorites

## Summary

Created three new repository classes for Flow-based data access: CustomCardRepository for managing custom tarot cards, TagRepository for tag CRUD operations, and ReadingTagRepository for reading-tag associations. DeckRepository already had observeCustomDecks() implemented.

## Completed Tasks

### Task 1: CustomCardRepository
- **Status:** Complete
- **Commit:** 55e7598
- **Created:** `app/src/main/kotlin/com/example/drawn/data/repository/CustomCardRepository.kt`
- **Methods:**
  - `observeCardsForDeck(deckId: Long): Flow<List<CustomCard>>` - observes cards for a specific deck
  - `insert(card: CustomCard): Long` - inserts new custom card
  - `update(card: CustomCard)` - updates existing custom card
  - `delete(card: CustomCard)` - deletes custom card

### Task 2: TagRepository
- **Status:** Complete
- **Commit:** 55e7598
- **Created:** `app/src/main/kotlin/com/example/drawn/data/repository/TagRepository.kt`
- **Methods:**
  - `observeAllTags(): Flow<List<Tag>>` - observes all tags
  - `create(tag: Tag): Long` - creates new tag
  - `update(tag: Tag)` - updates existing tag
  - `delete(tagId: Long)` - deletes tag by ID

### Task 3: ReadingTagRepository
- **Status:** Complete
- **Commit:** 55e7598
- **Created:** `app/src/main/kotlin/com/example/drawn/data/repository/ReadingTagRepository.kt`
- **Methods:**
  - `observeTagsForReading(readingId: Long): Flow<List<Tag>>` - observes tags for a reading (uses ANY/OR logic per D-01)
  - `addTag(readingId: Long, tagId: Long)` - adds tag to reading
  - `removeTag(readingId: Long, tagId: Long)` - removes tag from reading
  - `replaceTags(readingId: Long, tagIds: List<Long>)` - replaces all tags for a reading

### Task 4: DeckRepository.observeCustomDecks()
- **Status:** Already complete (pre-existing)
- **Commit:** N/A - existed before this plan
- **Method:** `observeCustomDecks(): Flow<List<Deck>>` - observes custom (user-created) decks

## Deviations from Plan

### Auto-fixed Issues

None - plan executed exactly as written.

## Known Stubs

None.

## Threat Flags

None.

## Self-Check: PASSED

All 4 repository files exist and compile:
- CustomCardRepository: ✅ Found
- TagRepository: ✅ Found
- ReadingTagRepository: ✅ Found
- DeckRepository.observeCustomDecks: ✅ Found (pre-existing)

Build verification: ✅ Compilation successful