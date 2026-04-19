# Architecture Research: Drawn v1.1 — Custom Decks, Tags, Backup

**Domain:** Android local-only tarot reading app
**Researched:** 2026-04-18
**Confidence:** HIGH

## Executive Summary

The v1.1 features (custom card decks, reading tags, backup/restore) integrate cleanly with the existing Room/Hilt/Compose architecture through **incremental schema additions** and **new service layers** without requiring refactoring of existing components.

- **Custom Decks** add 2 new tables (`decks`, `custom_cards`) with new DAOs and repositories
- **Reading Tags** add 2 new tables (`tags`, `reading_tags`) with many-to-many relationship to readings
- **Backup/Restore** uses JSON export/import via repositories — no new tables needed

All new components follow the existing patterns: sealed UI state, Flow-based queries, Hilt injection, repository abstraction.

## Existing Architecture Context

The current v1.0 architecture follows Clean Architecture with three layers:

```
UI Layer (Compose) → ViewModel (StateFlow) → Repository (Flow) → DAO (Room)
```

Key existing components:
- **Room Database:** `AppDatabase` with 4 tables (`readings`, `spreads`, `cards`, `reading_cards`, `reading_photos`)
- **DAOs:** `ReadingDao`, `SpreadDao`, `CardDao`
- **Repositories:** `ReadingRepository`, `SpreadRepository`, `CardRepository`
- **ViewModels:** `ReadingListViewModel`, `ReadingDetailViewModel`, `ReadingEntryViewModel`, `CardPickerViewModel`
- **Navigation:** Navigation Compose 3 with type-safe routes

---

## Schema Integration

### Database Schema Changes

#### New Tables for v1.1

```
┌─────────────────────┐     ┌─────────────────────┐
│       decks          │     │   custom_cards     │
├─────────────────────┤     ├─────────────────────┤
│ id (PK)             │     │ id (PK)             │
│ name                │◄────│ deck_id (FK)        │
│ description         │     │ name                │
│ is_default (bool)   │     │ position_index      │
│ created_at          │     │ image_uri           │
│ image_uri (cover)   │     │ keywords            │
└─────────┬───────────┘     │ meaning_upright     │
          │                 │ meaning_reversed    │
          │                 │ category            │
          │                 │ is_reversed_allowed │
          │                 └─────────────────────┘

┌─────────────────────┐     ┌─────────────────────┐
│        tags         │     │    reading_tags      │
├─────────────────────┤     ├─────────────────────┤
│ id (PK)             │     │ reading_id (FK)     │
│ name                │◄────│ tag_id (FK)         │
│ color (hex)         │     │ (PK = composite)    │
│ created_at          │     └─────────────────────┘
└─────────────────────┘

┌─────────────────────┐
│     readings        │  (MODIFIED)
├─────────────────────┤
│ id (PK)             │
│ title               │
│ spread_id (FK)      │
│ created_at          │
│ notes               │
│ is_favorite (NEW)   │  ←── Boolean column for pin/favorite
└─────────────────────┘
```

#### Migration Strategy

The existing database version increments from **1 to 2** with **auto-migration** for simple additions:

```kotlin
@Database(
    entities = [
        ReadingEntity::class,
        SpreadEntity::class,
        CardEntity::class,
        ReadingCardEntity::class,
        ReadingPhotoEntity::class,
        // NEW v1.1 entities
        DeckEntity::class,
        CustomCardEntity::class,
        TagEntity::class,
        ReadingTagEntity::class
    ],
    version = 2,
    autoMigrations = [
        AutoMigration(from = 1, to = 2)
    ],
    exportSchema = true
)
abstract class AppDatabase : RoomDatabase()
```

Room auto-migration handles:
- Adding new tables (`decks`, `custom_cards`, `tags`, `reading_tags`)
- Adding new column (`is_favorite` to readings)

No complex schema changes = auto-migration sufficient.

---

## Component Integration

### Data Layer

#### New DAOs (add to existing DAOs)

| DAO | New Methods | Purpose |
|-----|-------------|---------|
| `DeckDao` | `getAllDecks()`, `getDeckById()`, `insertDeck()`, `updateDeck()`, `deleteDeck()` | CRUD for custom decks |
| `CustomCardDao` | `getCardsForDeck()`, `insertCard()`, `updateCard()`, `deleteCard()` | CRUD for custom cards |
| `TagDao` | `getAllTags()`, `getTagsForReading()`, `insertTag()`, `updateTag()`, `deleteTag()` | CRUD for tags |
| `ReadingTagDao` | `addTagToReading()`, `removeTagFromReading()`, `getTagsForReading()` | Many-to-many management |
| `ReadingDao` | Add `updateFavorite()`, `getFavoriteReadings()` | Pin/favorite feature |

#### New Repositories

| Repository | Wraps | New Features |
|------------|-------|---------------|
| `DeckRepository` | `DeckDao`, `CustomCardDao` | Custom deck CRUD, card management |
| `TagRepository` | `TagDao`, `ReadingTagDao` | Tag CRUD, reading association |
| `BackupRepository` | All DAOs via `AppDatabase` | JSON export/import service |

#### Existing Repository Changes

- `ReadingRepository` adds: `updateFavorite()`, `getFavoriteReadings()`
- `CardRepository` needs refactored to support dual source: bundled RWS cards + custom cards

### ViewModel Layer

#### New ViewModels

| ViewModel | Screen | New State |
|-----------|--------|-----------|
| `DeckListViewModel` | Deck management list | `decks: Flow<List<Deck>>`, `selectedDeck`, `isLoading` |
| `DeckEditorViewModel` | Create/edit deck + cards | `deck: Deck`, `cards: List<CustomCard>`, `editingCard` |
| `CardPickerViewModel` (MODIFIED) | Card selection | Add `availableDecks`, `selectedDeck` filter |
| `TagManagerViewModel` | Tag CRUD screen | `tags: Flow<List<Tag>>`, `selectedTag` |
| `ReadingEntryViewModel` (MODIFIED) | New/edit reading | Add `availableTags`, `selectedTags` multi-select |
| `ReadingListViewModel` (MODIFIED) | Reading list | Add `filterByTag`, `filterByFavorite` |
| `BackupViewModel` | Settings > Backup | `backupState: BackupState`, `exportProgress`, `importProgress` |

#### Modifications to Existing ViewModels

- **`CardPickerViewModel`**: Add `selectedDeckId` filter, merge RWS cards + custom cards into single list
- **`ReadingEntryViewModel`**: Add `selectedTags: Set<Tag>` for tagging readings
- **`ReadingListViewModel`**: Add `showFavoritesOnly`, `filterByTagId` query parameters

### UI Layer

#### New Screens

| Screen | Purpose | Navigation Route |
|--------|---------|------------------|
| `DeckListScreen` | Browse/create/delete decks | `deck-list` |
| `DeckEditorScreen` | Edit deck name/description, add cards | `deck-editor/{deckId}` |
| `CardEditorDialog` | Edit individual card details | Modal from DeckEditor |
| `TagManagerScreen` | CRUD for tags | `tag-manager` |
| `ReadingListScreen` (MODIFIED) | Add filter chips for tags, favorites | Existing |
| `ReadingEntryScreen` (MODIFIED) | Add tag selector chip row | Existing |
| `BackupScreen` | Export/import JSON | `settings/backup` |

#### Navigation Changes

Add new routes to existing NavHost:

```kotlin
// New routes
sealed class Screen(val route: String) {
    // ... existing routes
    object DeckList : Screen("deck-list")
    object DeckEditor : Screen("deck-editor/{deckId}") {
        fun createRoute(deckId: Long? = null) = "deck-editor/${deckId ?: -1}"
    }
    object TagManager : Screen("tag-manager")
    object Backup : Screen("settings/backup")
}
```

#### Shared Components

| Component | Purpose | Used By |
|-----------|---------|---------|
| `DeckCard` | Display deck with cover image | DeckListScreen, CardPickerScreen |
| `CustomCardGrid` | Grid of custom cards with edit | DeckEditorScreen |
| `TagChip` | Tag display with color | ReadingEntryScreen, ReadingListScreen |
| `TagSelector` | Multi-select tag picker | ReadingEntryScreen |
| `FavoriteToggle` | Star icon toggle | ReadingDetailScreen |

---

## Backup/Restore Data Flow

### Export Flow

```
User taps "Export" 
    ↓
BackupViewModel.exportToJson()
    ↓
BackupRepository.exportAllData()
    ↓
1. readingDao.getAllReadings() → List<ReadingEntity>
2. cardDao.getAllCards() → List<CardEntity>
3. deckDao.getAllDecks() → List<DeckEntity>
4. customCardDao.getAllCards() → List<CustomCardEntity>
5. tagDao.getAllTags() → List<TagEntity>
6. readingTagDao.getAll() → List<ReadingTagEntity>
    ↓
Convert all entities to JSON-serializable DTOs (no Room annotations)
    ↓
Kotlinx Serialization: Json.encodeToString(BackupData(...))
    ↓
Save to user-selected location via Storage Access Framework
```

### Import Flow

```
User selects backup file
    ↓
BackupViewModel.importFromJson(uri)
    ↓
BackupRepository.importAllData(jsonString)
    ↓
Json.decodeFromString<BackupData>(jsonString)
    ↓
VALIDATION:
- Check schema version compatibility
- Validate required fields not null
- Check for duplicate IDs, handle conflicts
    ↓
TRANSACTION (all-or-nothing):
1. Insert decks (or update if ID exists)
2. Insert custom_cards
3. Insert tags
4. Insert reading_tags
5. Update readings.is_favorite
    ↓
Notify DAOs to refresh Flows
    ↓
UI auto-updates via existing Flow subscriptions
```

### Backup Data Structure

```kotlin
@Serializable
data class BackupData(
    val version: Int = 1,
    val exportedAt: String, // ISO 8601
    val appVersion: String,
    val decks: List<DeckDto>,
    val customCards: List<CustomCardDto>,
    val tags: List<TagDto>,
    val readingTags: List<ReadingTagDto>,
    val readings: List<ReadingDto> // includes is_favorite
)

@Serializable
data class DeckDto(
    val id: Long,
    val name: String,
    val description: String?,
    val isDefault: Boolean,
    val coverImageUri: String?,
    val createdAt: Long
)
```

---

## New vs. Modified Components

### Components to CREATE (New)

| Layer | Component | Files |
|-------|-----------|-------|
| Data | `DeckEntity` | `entity/DeckEntity.kt` |
| Data | `CustomCardEntity` | `entity/CustomCardEntity.kt` |
| Data | `TagEntity` | `entity/TagEntity.kt` |
| Data | `ReadingTagEntity` | `entity/ReadingTagEntity.kt` |
| Data | `DeckDao` | `dao/DeckDao.kt` |
| Data | `CustomCardDao` | `dao/CustomCardDao.kt` |
| Data | `TagDao` | `dao/TagDao.kt` |
| Data | `ReadingTagDao` | `dao/ReadingTagDao.kt` |
| Data | `DeckRepository` | `repository/DeckRepository.kt` |
| Data | `TagRepository` | `repository/TagRepository.kt` |
| Data | `BackupRepository` | `repository/BackupRepository.kt` |
| Domain | `Deck` | `model/Deck.kt` |
| Domain | `CustomCard` | `model/CustomCard.kt` |
| Domain | `Tag` | `model/Tag.kt` |
| UI | `DeckListViewModel` | `viewmodel/DeckListViewModel.kt` |
| UI | `DeckEditorViewModel` | `viewmodel/DeckEditorViewModel.kt` |
| UI | `TagManagerViewModel` | `viewmodel/TagManagerViewModel.kt` |
| UI | `BackupViewModel` | `viewmodel/BackupViewModel.kt` |
| UI | `DeckListScreen` | `ui/screen/deck/DeckListScreen.kt` |
| UI | `DeckEditorScreen` | `ui/screen/deck/DeckEditorScreen.kt` |
| UI | `TagManagerScreen` | `ui/screen/tag/TagManagerScreen.kt` |
| UI | `BackupScreen` | `ui/screen/settings/BackupScreen.kt` |

### Components to MODIFY (Existing)

| Layer | Component | Change |
|-------|-----------|--------|
| Data | `ReadingEntity` | Add `isFavorite: Boolean` column |
| Data | `ReadingDao` | Add `updateFavorite()`, `getFavorites()` |
| Data | `AppDatabase` | Add new entities to `entities[]`, increment `version = 2` |
| Data | `ReadingRepository` | Add `updateFavorite()`, `getFavoriteReadings()` |
| Data | `CardRepository` | Refactor to support both RWS + custom cards |
| Domain | `Reading` | Add `isFavorite: Boolean` |
| UI | `CardPickerViewModel` | Add deck filtering, merge card sources |
| UI | `ReadingEntryViewModel` | Add tag selection |
| UI | `ReadingListViewModel` | Add filtering by tag/favorite |
| UI | `NavigationGraph` | Add new routes |

### Components Unchanged (Existing)

- `CardDao` (bundled RWS cards)
- `SpreadDao`, `SpreadRepository` (spreads unchanged)
- `ReadingDetailViewModel` (minor addition: show tags)
- `Theme` system
- `PhotoManager` (photos unaffected)

---

## Build Order & Dependencies

### Phase 1: Schema Foundation (No dependencies)

**Order:**
1. Create new entities (`DeckEntity`, `CustomCardEntity`, `TagEntity`, `ReadingTagEntity`)
2. Add `isFavorite` to `ReadingEntity`
3. Create new DAOs
4. Update `AppDatabase` with new entities and migration
5. Add domain models (`Deck`, `CustomCard`, `Tag`)

**Rationale:** All data layer changes first — no UI dependencies.

### Phase 2: Repository Layer (Depends on Phase 1)

**Order:**
1. Create `DeckRepository`, `TagRepository`
2. Update `ReadingRepository` with favorite methods
3. Create `BackupRepository` with export/import logic
4. Refactor `CardRepository` to merge RWS + custom

**Rationale:** UI needs repositories to bind against.

### Phase 3: ViewModel Layer (Depends on Phase 2)

**Order:**
1. Create `DeckListViewModel`, `DeckEditorViewModel`
2. Create `TagManagerViewModel`
3. Create `BackupViewModel`
4. Modify existing ViewModels (`CardPicker`, `ReadingEntry`, `ReadingList`)

**Rationale:** ViewModels depend on repositories.

### Phase 4: UI Layer (Depends on Phase 3)

**Order:**
1. Create reusable components (`DeckCard`, `TagChip`, `TagSelector`)
2. Create new screens (`DeckListScreen`, `DeckEditorScreen`, `TagManagerScreen`, `BackupScreen`)
3. Update existing screens with new features (tag selector, favorite toggle)
4. Update navigation graph with new routes

**Rationale:** UI depends on ViewModels.

---

## Integration Patterns

### Pattern 1: Dual Card Source in CardPicker

**Problem:** Users must choose from both RWS cards (bundled) and custom cards (user-created).

**Solution:** Merge both sources in repository layer:

```kotlin
class CardRepository(
    private val cardDao: CardDao,
    private val customCardDao: CustomCardDao,
    private val deckDao: DeckDao
) {
    fun getCardsForPicker(selectedDeckId: Long?): Flow<List<Card>> {
        return when (selectedDeckId) {
            null -> // "All" selected - show both RWS and custom
                combine(
                    cardDao.observeAllRwsCards(),
                    customCardDao.observeAllCustomCards()
                ) { rws, custom -> rws.map { it.toDomain() } + custom.map { it.toDomain() } }
            
            -1L -> // "RWS Only" 
                cardDao.observeAllRwsCards().map { it.map { c -> c.toDomain() } }
            
            else -> // Specific custom deck
                customCardDao.getCardsForDeck(selectedDeckId).map { it.map { c -> c.toDomain() } }
        }
    }
}
```

### Pattern 2: Tag Multi-Select in Reading Entry

**Problem:** Users tag readings with multiple tags.

**Solution:** Chip row with add button, bottom sheet for selection:

```kotlin
// ReadingEntryScreen
@Composable
fun TagSelector(
    availableTags: List<Tag>,
    selectedTags: Set<Tag>,
    onTagsChanged: (Set<Tag>) -> Unit
) {
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        selectedTags.forEach { tag ->
            FilterChip(
                selected = true,
                onClick = { onTagsChanged(selectedTags - tag) },
                label = { Text(tag.name) },
                leadingIcon = { Icon(Icons.Default.Close, null) }
            )
        }
        AssistChip(
            onClick = { showTagSelectorSheet() },
            label = { Text("Add Tag") },
            leadingIcon = { Icon(Icons.Default.Add, null) }
        )
    }
}
```

### Pattern 3: Backup as Service (Not UI State)

**Problem:** Backup can be large, needs background processing, must not block UI.

**Solution:** Repository handles backup as suspend function, ViewModel exposes StateFlow for progress:

```kotlin
class BackupRepository(
    private val database: AppDatabase,
    private val json: Json
) {
    suspend fun exportToJson(outputStream: OutputStream, progress: (Float) -> Unit) {
        progress(0.1f)
        val readings = database.readingDao().getAllReadingsSync()
        progress(0.2f)
        // ... more data fetching ...
        val backupData = BackupData(...)
        progress(0.8f)
        json.encodeToString(backupData)
        outputStream.write(encoded)
        progress(1.0f)
    }
}
```

---

## Anti-Patterns to Avoid

### Anti-Pattern 1: Storing Custom Card Images in Database

**What:** Storing images as BLOB in `custom_cards` table.

**Why bad:** Bloats database, memory issues, slow queries.

**Instead:** Store image URI (content:// or file://) in `custom_cards.image_uri`, use Coil to load.

### Anti-Pattern 2: Exporting Raw Database File

**What:** Copying `.db` file directly.

**Why bad:** Version-specific, includes WAL/shm files, not human-readable.

**Instead:** JSON export with Kotlinx Serialization — portable, versionable, debuggable.

### Anti-Pattern 3: Deleting Tags Without Cleaning Junction Table

**What:** Deleting from `tags` table without `ON DELETE CASCADE`.

**Why bad:** Orphan rows in `reading_tags`, data corruption.

**Instead:** Use Room's `@ForeignKey(delete = DeleteAction.CASCADE)` or explicit delete in transaction.

### Anti-Pattern 4: Backup Without Validation

**What:** Importing JSON without schema/version checks.

**Why bad:** Crashes on incompatible backups, partial imports.

**Instead:** Validate `backupData.version`, check required fields, wrap in transaction.

---

## Sources

- [Room Migration Documentation](https://developer.android.com/training/data-storage/room/migrating-db-versions) — HIGH confidence (official)
- [Room Auto-Migration](https://developer.android.com/reference/kotlin/androidx/room/AutoMigration) — HIGH confidence (official)
- [Kotlinx Serialization](https://kotlinlang.org/docs/serialization.html) — HIGH confidence (official)
- [Android Storage Access Framework](https://developer.android.com/guide/topics/providers/document-provider) — HIGH confidence (official)
- [Backup/Restore Pattern (Medium)](https://medium.com/@vaclav.oujezsky/backup-and-restore-room-database-locally-with-user-interaction-8396a040e433) — MEDIUM confidence (community)

---

*Architecture research for: Drawn v1.1 (custom decks, tags, backup)*
*Researched: 2026-04-18*