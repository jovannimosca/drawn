# Architecture Patterns: Drawn

**Domain:** Local-only Android app with Room Database + Jetpack Compose
**Researched:** 2026-04-03

## Recommended Architecture

Drawn follows Google's recommended **three-layer architecture** (UI → Domain → Data) with **MVVM pattern** and **unidirectional data flow**. For a local-only app, this simplifies to a clean separation without network layers.

```
┌─────────────────────────────────────────────────────────────┐
│                        UI Layer                              │
│  ┌─────────────┐  ┌──────────────┐  ┌────────────────────┐  │
│  │  Compose    │  │  Navigation  │  │  Theme/Design      │  │
│  │  Screens    │  │  Graph       │  │  System            │  │
│  └──────┬──────┘  └──────┬───────┘  └────────────────────┘  │
│         │                │                                    │
│  ┌──────▼────────────────▼───────────────────────────────┐  │
│  │              ViewModels (StateFlow)                    │  │
│  │  ReadingListVM │ ReadingDetailVM │ ReadingEntryVM     │  │
│  │  CardPickerVM  │ DeckEditorVM    │ SettingsVM         │  │
│  └──────────────────────┬────────────────────────────────┘  │
└─────────────────────────┼───────────────────────────────────┘
                          │ Events ↑  State ↓
┌─────────────────────────▼───────────────────────────────────┐
│                     Domain Layer (Optional)                   │
│  ┌──────────────────────────────────────────────────────┐   │
│  │  Use Cases: CreateReading, GetReadingHistory,        │   │
│  │             SearchReadings, ExportReading            │   │
│  └──────────────────────┬───────────────────────────────┘   │
│  ┌──────────────────────────────────────────────────────┐   │
│  │  Domain Models: Reading, Card, Spread, Deck          │   │
│  │  (pure Kotlin, no Android dependencies)              │   │
│  └──────────────────────┬───────────────────────────────┘   │
└─────────────────────────┼───────────────────────────────────┘
                          │
┌─────────────────────────▼───────────────────────────────────┐
│                      Data Layer                              │
│  ┌──────────────────────────────────────────────────────┐   │
│  │  Repositories: ReadingRepo, CardRepo, DeckRepo       │   │
│  │  - Entity ↔ Domain model mapping                     │   │
│  │  - Flow-based reactive streams                       │   │
│  └──────────────────────┬───────────────────────────────┘   │
│  ┌──────────────────────────────────────────────────────┐   │
│  │  DAOs: ReadingDao, CardDao, SpreadDao, DeckDao       │   │
│  │  - @Query, @Insert, @Update, @Delete                 │   │
│  │  - Flow for reads, suspend for writes                │   │
│  └──────────────────────┬───────────────────────────────┘   │
│  ┌──────────────────────────────────────────────────────┐   │
│  │  Room Database: AppDatabase                          │   │
│  │  - Entities: ReadingEntity, CardEntity, etc.         │   │
│  │  - TypeConverters for complex types                  │   │
│  └──────────────────────────────────────────────────────┘   │
│  ┌──────────────────────────────────────────────────────┐   │
│  │  Local Storage: PhotoManager (camera/gallery)        │   │
│  └──────────────────────────────────────────────────────┘   │
└─────────────────────────────────────────────────────────────┘
```

## Component Boundaries

### UI Layer

| Component | Responsibility | Communicates With |
|-----------|---------------|-------------------|
| **Compose Screens** | Render UI, handle user input, display state | ViewModels (observe StateFlow, send events) |
| **Navigation Graph** | Route between screens, pass arguments | Compose Screens (via NavHost) |
| **Theme System** | Dark mystical theme, typography, colors | All Compose components |
| **Reusable Components** | CardGrid, SpreadLayout, PhotoViewer, NoteEditor | Screens (composition) |

### ViewModel Layer

| ViewModel | Screen | State Exposed | Events Handled |
|-----------|--------|---------------|----------------|
| **ReadingListViewModel** | Reading list screen | `StateFlow<ReadingListUiState>` (list, loading, error) | Search, filter, delete reading |
| **ReadingDetailViewModel** | Reading detail screen | `StateFlow<ReadingDetailUiState>` (reading, cards, photos) | Navigate to edit, delete |
| **ReadingEntryViewModel** | New/edit reading screen | `StateFlow<ReadingEntryUiState>` (draft reading, selected cards) | Select spread, assign cards, save |
| **CardPickerViewModel** | Card selection dialog/screen | `StateFlow<CardPickerUiState>` (available cards, selected) | Filter by deck, select/deselect cards |
| **DeckEditorViewModel** | Custom deck management | `StateFlow<DeckEditorUiState>` (deck list, editing deck) | Create/edit/delete deck, add cards |

### Domain Layer (Optional but Recommended)

| Component | Responsibility | Communicates With |
|-----------|---------------|-------------------|
| **Domain Models** | Pure data classes (Reading, Card, Spread, Deck) | Repositories (input/output), ViewModels (consumption) |
| **Use Cases** | Single-responsibility business logic operations | Repositories (data access), ViewModels (orchestration) |

**Note:** For v1, the domain layer can be kept minimal. Domain models can double as the data transferred between Repository and ViewModel. Use cases become valuable as complexity grows (e.g., export, search with multiple filters).

### Data Layer

| Component | Responsibility | Communicates With |
|-----------|---------------|-------------------|
| **Repositories** | Single source of truth, entity↔domain mapping, Flow streams | DAOs (database), PhotoManager (storage), Domain layer |
| **DAOs** | Database operations, typed queries | Room Database (direct access) |
| **Room Database** | SQLite abstraction, schema management, migrations | DAOs (provides instances) |
| **PhotoManager** | Camera capture, gallery selection, file storage | Android MediaStore, file system |

## Data Flow

### Unidirectional Data Flow Pattern

```
User Action → Compose Screen → ViewModel Event → Repository → DAO → Room DB
                                                                         ↓
Room DB → DAO (Flow emission) → Repository (map to domain) → ViewModel (update StateFlow) → Compose Screen (recompose)
```

### Concrete Example: Saving a Reading

```
1. User taps "Save" on ReadingEntryScreen
2. Screen calls: viewModel.saveReading()
3. ViewModel validates draft, calls: repository.createReading(reading)
4. Repository maps domain Reading → ReadingEntity
5. Repository calls: readingDao.insert(readingEntity)  // suspend function
6. Room inserts row, triggers Flow emission
7. DAO emits updated reading list via Flow
8. Repository maps entities → domain models
9. ViewModel's StateFlow updates with new list
10. ReadingListScreen recomposes with updated data
```

### State Management Pattern

```kotlin
// UI State sealed class (one per screen)
sealed class ReadingListUiState {
    object Loading : ReadingListUiState()
    data class Success(val readings: List<Reading>) : ReadingListUiState()
    data class Error(val message: String) : ReadingListUiState()
}

// ViewModel exposes StateFlow
class ReadingListViewModel(
    private val repository: ReadingRepository
) : ViewModel() {
    
    val uiState: StateFlow<ReadingListUiState> = repository.observeAllReadings()
        .map { readings -> ReadingListUiState.Success(readings) }
        .catch { error -> emit(ReadingListUiState.Error(error.message ?: "Unknown error")) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = ReadingListUiState.Loading
        )
    
    fun onSearchQueryChanged(query: String) { ... }
    fun onDeleteReading(readingId: Long) { ... }
}

// Compose screen consumes StateFlow
@Composable
fun ReadingListScreen(viewModel: ReadingListViewModel = hiltViewModel()) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    
    when (uiState) {
        is ReadingListUiState.Loading -> LoadingIndicator()
        is ReadingListUiState.Success -> ReadingList((uiState as ReadingListUiState.Success).readings)
        is ReadingListUiState.Error -> ErrorMessage((uiState as ReadingListUiState.Error).message)
    }
}
```

## Suggested Build Order

Based on dependency graph, build components in this order:

### Phase 1: Foundation (No Dependencies)
1. **Domain Models** — Pure Kotlin data classes (`Reading`, `Card`, `Spread`, `Deck`)
2. **Room Entities** — Database schema (`ReadingEntity`, `CardEntity`, `SpreadEntity`, `DeckEntity`)
3. **TypeConverters** — For complex types (dates, enums, lists)

### Phase 2: Data Access (Depends on Phase 1)
4. **DAOs** — Database operations (`ReadingDao`, `CardDao`, `SpreadDao`, `DeckDao`)
5. **Room Database** — `AppDatabase` class with all entities
6. **Repositories** — Business logic layer with Flow streams

### Phase 3: ViewModel Layer (Depends on Phase 2)
7. **ViewModels** — State management for each screen
8. **Use Cases** (optional) — Complex operations if needed

### Phase 4: UI Layer (Depends on Phase 3)
9. **Theme System** — Dark mystical theme, colors, typography
10. **Navigation Graph** — App routing structure
11. **Reusable Components** — CardGrid, SpreadLayout, PhotoViewer
12. **Screens** — Compose screens consuming ViewModels

### Phase 5: Integration & Polish
13. **PhotoManager** — Camera/gallery integration
14. **Bundled Assets** — RWS card images, default spreads
15. **Testing** — Unit tests for ViewModels, Repositories, DAOs

## Patterns to Follow

### Pattern 1: Repository as Single Source of Truth
**What:** All data access goes through repositories, never directly from DAOs in ViewModels.
**When:** Always — this is the foundation of testability and separation of concerns.
**Example:**
```kotlin
class ReadingRepository(
    private val readingDao: ReadingDao,
    private val cardDao: CardDao
) {
    fun observeAllReadings(): Flow<List<Reading>> =
        readingDao.observeAllReadings()
            .map { entities -> entities.map { it.toDomain() } }
            .distinctUntilChanged()
    
    suspend fun createReading(reading: Reading): Long =
        readingDao.insert(reading.toEntity())
}
```

### Pattern 2: Entity-Domain Model Separation
**What:** Database entities (`@Entity` classes) are separate from domain models (pure data classes).
**When:** Always — prevents database schema changes from rippling through the entire app.
**Example:**
```kotlin
// Database entity
@Entity(tableName = "readings")
data class ReadingEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val spreadId: Long,
    val createdAt: Long,
    val notes: String?
)

// Domain model (pure Kotlin, no Android annotations)
data class Reading(
    val id: Long,
    val title: String,
    val spreadId: Long,
    val createdAt: Instant,
    val notes: String?
)

// Conversion extensions
fun ReadingEntity.toDomain() = Reading(
    id = id,
    title = title,
    spreadId = spreadId,
    createdAt = Instant.fromEpochMilliseconds(createdAt),
    notes = notes
)

fun Reading.toEntity() = ReadingEntity(
    id = id,
    title = title,
    spreadId = spreadId,
    createdAt = createdAt.toEpochMilliseconds(),
    notes = notes
)
```

### Pattern 3: Flow-Based Reactive Queries
**What:** DAO queries return `Flow<List<T>>` for automatic UI updates when data changes.
**When:** For all read operations that feed UI — eliminates manual refresh logic.
**Example:**
```kotlin
@Dao
interface ReadingDao {
    @Query("SELECT * FROM readings ORDER BY createdAt DESC")
    fun observeAllReadings(): Flow<List<ReadingEntity>>
    
    @Query("SELECT * FROM readings WHERE id = :id")
    fun observeReadingById(id: Long): Flow<ReadingEntity?>
    
    @Insert
    suspend fun insert(reading: ReadingEntity): Long
    
    @Update
    suspend fun update(reading: ReadingEntity)
    
    @Delete
    suspend fun delete(reading: ReadingEntity)
}
```

### Pattern 4: Sealed UI State Classes
**What:** Each screen has a sealed class representing all possible UI states.
**When:** Always — makes state handling exhaustive and prevents null checks.
**Example:** See State Management Pattern above.

### Pattern 5: Dependency Injection with Hilt
**What:** Use Hilt for dependency injection throughout the app.
**When:** Always — enables testability, reduces boilerplate, manages lifecycles.
**Example:**
```kotlin
@HiltAndroidApp
class DrawnApplication : Application()

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    
    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase =
        Room.databaseBuilder(context, AppDatabase::class.java, "drawn_database")
            .build()
    
    @Provides
    fun provideReadingDao(database: AppDatabase): ReadingDao =
        database.readingDao()
}

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {
    
    @Provides
    fun provideReadingRepository(dao: ReadingDao): ReadingRepository =
        ReadingRepository(dao)
}
```

## Anti-Patterns to Avoid

### Anti-Pattern 1: Direct DAO Access from ViewModel
**What:** Calling DAOs directly from ViewModels without a repository layer.
**Why bad:** Ties ViewModels to database implementation, makes testing harder, violates separation of concerns.
**Instead:** Always go through a repository that handles entity↔domain mapping.

### Anti-Pattern 2: Passing Entities to UI Layer
**What:** Using `@Entity` classes directly in Compose screens.
**Why bad:** Database schema changes force UI changes, entities carry Android dependencies.
**Instead:** Map entities to domain models in the repository, pass domain models to ViewModels.

### Anti-Pattern 3: Blocking Main Thread with Database Operations
**What:** Calling DAO methods without `suspend` or `Flow`.
**Why bad:** Causes ANR (Application Not Responding), crashes on main thread.
**Instead:** Use `suspend` functions for writes, `Flow` for reads. Room enforces this on main thread by default.

### Anti-Pattern 4: Mutable State in Compose Without Proper Lifecycle
**What:** Using `mutableStateOf` without considering lifecycle, or collecting flows without `collectAsStateWithLifecycle`.
**Why bad:** Memory leaks, unnecessary recompositions, crashes on configuration changes.
**Instead:** Use `collectAsStateWithLifecycle()` for Flow collection, `viewModelScope` for coroutines.

### Anti-Pattern 5: God ViewModel
**What:** One ViewModel handling all screens and all logic.
**Why bad:** Becomes untestable, hard to maintain, violates single responsibility.
**Instead:** One ViewModel per screen/feature, delegate business logic to repositories or use cases.

## Scalability Considerations

| Concern | At 100 readings | At 10K readings | At 100K readings |
|---------|-----------------|-----------------|------------------|
| **Database queries** | Simple `SELECT *` works | Add pagination with Paging 3 | Add indices, optimize queries |
| **Photo storage** | Store in app directory | Use MediaStore with thumbnails | Consider compression, lazy loading |
| **Card images** | Bundle 78 RWS images (~2-5MB) | Same — fixed deck size | Custom decks may need caching |
| **Memory usage** | Load all readings into memory | Use `Flow` with lazy loading | Implement cursor-based pagination |
| **Search** | In-memory filtering | Room `LIKE` queries with indices | FTS (Full-Text Search) extension |

## Database Schema (Proposed)

```
┌─────────────────┐     ┌─────────────────┐     ┌─────────────────┐
│   decks         │     │   spreads       │     │   readings      │
├─────────────────┤     ├─────────────────┤     ├─────────────────┤
│ id (PK)         │     │ id (PK)         │     │ id (PK)         │
│ name            │     │ name            │     │ title           │
│ description     │     │ description     │     │ spread_id (FK)  │
│ is_custom       │     │ position_count  │     │ created_at      │
│ created_at      │     │ positions_json  │     │ notes           │
└────────┬────────┘     └────────┬────────┘     └────────┬────────┘
         │                       │                       │
         │                       │         ┌─────────────┘
         │                       │         │
┌────────▼────────┐     ┌────────▼─────────▼───────┐
│   cards         │     │   reading_cards          │
├─────────────────┤     ├──────────────────────────┤
│ id (PK)         │     │ id (PK)                  │
│ deck_id (FK)    │     │ reading_id (FK)          │
│ name            │     │ card_id (FK)             │
│ arcana_type     │     │ position_name            │
│ number          │     │ position_order           │
│ image_res_id    │     │ interpretation           │
│ keywords        │     └──────────────────────────┘
│ meaning_upright │
│ meaning_reversed│     ┌──────────────────────────┐
└─────────────────┘     │   reading_photos         │
                        ├──────────────────────────┤
                        │ id (PK)                  │
                        │ reading_id (FK)          │
                        │ photo_uri                │
                        │ caption                  │
                        └──────────────────────────┘
```

## Testing Strategy

| Layer | Test Type | Tools | Coverage Target |
|-------|-----------|-------|-----------------|
| **DAOs** | Instrumented tests | Room in-memory database, JUnit | 80% |
| **Repositories** | Unit tests | Mock DAOs, Turbine for Flow testing | 80% |
| **ViewModels** | Unit tests | Mock repositories, Turbine for StateFlow | 80% |
| **Use Cases** | Unit tests | Mock repositories | 80% |
| **UI Components** | Compose tests | Compose Test Rule, semantics | As needed |

## Sources

- [Android Guide to App Architecture](https://developer.android.com/topic/architecture) — HIGH confidence (official docs)
- [Android Architecture Recommendations](https://developer.android.com/topic/architecture/recommendations) — HIGH confidence (official docs)
- [Compose UI Architecture](https://developer.android.com/develop/ui/compose/architecture) — HIGH confidence (official docs)
- [Persist Data with Room Codelab](https://developer.android.com/codelabs/basic-android-kotlin-compose-persisting-data-room) — HIGH confidence (official codelab)
- [Local DB Design Patterns — Room + Repository + ViewModel](https://dev.to/myougatheaxo/local-db-design-patterns-room-repository-viewmodel-architecture-43bo) — MEDIUM confidence (community, aligns with official guidance)
- [Room 3.0 Announcement](https://android-developers.googleblog.com/2026/03/room-30-modernizing-room.html) — HIGH confidence (official blog, March 2026)
- [Now in Android Reference App](https://github.com/android/nowinandroid) — HIGH confidence (official Google sample)
