# Phase 8: Database Schema - Context

**Gathered:** 2026-04-18
**Status:** Ready for planning

<domain>
## Phase Boundary

Establish database foundation for custom decks, tags, and favorites with relational schema.

This phase adds new entities for:
- Custom card deck (extends DeckEntity)
- Custom cards within decks (new CustomCardEntity)
- Tags (new TagEntity)
- Reading-Tag many-to-many relationship (ReadingTagCrossRef)
- isFavorite on ReadingEntity

</domain>

<decisions>
## Implementation Decisions

### Custom Deck Fields (D-01)
- **Standard fields per custom card:** name, image path, uprightMeaning, reversedMeaning, sortOrder
- Extends existing DeckEntity structure

### Tag Relationship (D-02)
- Tag entity: id, name, color (hex string)
- Many-to-many via Junction table (ReadingTagCrossRef)
- Query performance: Room @Junction for indexed lookups (<1.5ms vs 400ms+)

### Favorites Storage (D-03)
- isFavorite: Boolean on ReadingEntity
- Simple column for filtering (not separate table)
- Filter by: WHERE isFavorite = 1

### Schema Migration (D-04)
- Auto-migration via Room's ExportSchema
- Export schema to JSON for version tracking
- Database version bumps from 1 → 2

</decisions>

<canonical_refs>
## Canonical References

**Downstream agents MUST read these before planning or implementing.**

### Existing Code
- `app/src/main/kotlin/com/example/drawn/data/database/entity/DeckEntity.kt` — existing deck structure
- `app/src/main/kotlin/com/example/drawn/data/database/entity/ReadingEntity.kt` — reading entity to modify
- `app/src/main/kotlin/com/example/drawn/data/database/dao/` — existing DAOs

### Room Documentation
- [Room Many-to-Many Relationships](https://developer.android.com/training/data-storage/room/relationships/many-to-many) — official docs
- [Room Auto-Migration](https://developer.android.com/training/data-storage/room/migrating-db-versions) — migration docs

</canonical_refs>

<code_context>
## Existing Code Insights

### Reusable Assets
- DeckEntity: Already exists (id, name, description, isCustom, createdAt)
- DeckDao: Already has basic CRUD methods
- Room database setup in AppDatabase.kt

### Integration Points
- New entities connect via existing DAOs or new DAOs
- Domain models in `domain/model/` need CustomCard, Tag models
- Repository layer wraps DAOs

</code_context>

<specifics>
## Specific Ideas

No additional specifics — standard Room patterns apply.

**Code location for new entities:**
`app/src/main/kotlin/com/example/drawn/data/database/entity/`

**Code location for new DAOs:**
`app/src/main/kotlin/com/example/drawn/data/database/dao/`

</specifics>

<deferred>
## Deferred Ideas

None — discussion stayed within phase scope.

</deferred>

---

*Phase: 08-database-schema*
*Context gathered: 2026-04-18*