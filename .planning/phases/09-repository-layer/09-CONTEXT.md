# Phase 9: Repository Layer - Context

## Decisions Made

| Decision | Value |
|----------|-------|
| Tag filter logic | ANY (OR) — returns readings with any selected tag |
| Favorites format | Same as `observeAllReadingsWithSpread()` for consistency |
| Tag operations | All 3: addTag, removeTag, replaceTags |
| Complex queries | Use `combine()` pattern for joins |
| Error handling | Defer to future phase |

## Repository Changes

### New Repositories (3)
- `CustomCardRepository` — custom cards per deck
- `TagRepository` — tag management
- `ReadingTagRepository` — reading ↔ tag associations

### Extensions to Existing (2)
- `ReadingRepository` — `observeFavorites()`, `observeReadingsByTags()`
- `DeckRepository` — `observeCustomDecks()`

## Implementation Notes

- All `observe*()` return `Flow` with `distinctUntilChanged()`
- All mutations are `suspend` functions
- Domain model mapping in repository layer
- Tag operations: add, remove, bulk replace
- Favorites toggle method added