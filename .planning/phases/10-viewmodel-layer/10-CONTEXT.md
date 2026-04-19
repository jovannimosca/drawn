# Phase 10: ViewModel Layer - Context

**Gathered:** 2026-04-19
**Status:** Ready for planning

<domain>
## Phase Boundary

Provide ViewModels with reactive state management for all v1.1 feature UIs:
- Deck management (CRUD + card management within decks)
- Tag management (CRUD)
- Reading list filtering (tags, favorites)

**Depends on:** Phase 9 (Repository Layer)

</domain>

<decisions>
## Implementation Decisions

### State Management Pattern
- **D-01:** Use `stateIn()` pattern for reactive UI updates — matching existing `ReadingListViewModel` approach
- **Rationale:** Reactive, concise, automatic updates. Best for read-only or simple collections.
- **Does NOT apply:** Complex multi-step forms (those use manual MutableStateFlow like AddReadingViewModel)

### Deck ViewModel
- **D-02:** Full CRUD + Card management within deck
  - Create deck (name, description)
  - Edit deck (name, description)
  - Delete deck
  - List all decks (custom decks)
  - Manage cards within deck (add card, edit card, remove card)
- **D-03:** Card model includes: name, image path, keywords, meaning

### Tag ViewModel
- **D-04:** Full CRUD operations
  - Create tag (name, optional color)
  - Edit tag (name, color)
  - Delete tag
  - List all tags
- **D-05:** Tag colors: Preset palette (8-12 predefined colors)
  - Simpler, consistent UI — user picks from palette
  - Color is optional (can be null)

### Reading List Filtering
- **D-06:** Filter by tags — multi-select tag filter
  - Filter readings that have ANY of selected tags (OR) or ALL of selected tags (AND) — default: OR
- **D-07:** Filter by favorites — toggle filter for favorites-only
- **Integration:** Extend existing ReadingListViewModel with filter state

### the agent's Discretion
- Filter logic: AND vs OR for multi-tag selection (recommend OR for simplicity)
- Color palette exact colors (recommend 8-10 mystical colors: purple, gold, teal, rose, etc.)

</decisions>

<canonical_refs>
## Canonical References

**Downstream agents MUST read these before planning or implementing.**

### Existing Code Patterns
- `app/src/main/kotlin/com/example/drawn/ui/readinglist/ReadingListViewModel.kt` — stateIn() pattern reference
- `app/src/main/kotlin/com/example/drawn/ui/addreading/AddReadingViewModel.kt` — MutableStateFlow pattern for complex forms
- `app/src/main/kotlin/com/example/drawn/data/repository/DeckRepository.kt` — existing deck Flow operations
- `app/src/main/kotlin/com/example/drawn/data/repository/TagRepository.kt` — existing tag Flow operations
- `app/src/main/kotlin/com/example/drawn/data/repository/ReadingRepository.kt` — existing reading operations

### Data Entities
- `app/src/main/kotlin/com/example/drawn/data/database/entity/DeckEntity.kt` — deck data model
- `app/src/main/kotlin/com/example/drawn/data/database/entity/CardEntity.kt` — card data model
- `app/src/main/kotlin/com/example/drawn/data/database/entity/TagEntity.kt` — tag data model
- `app/src/main/kotlin/com/example/drawn/data/database/entity/ReadingTagEntity.kt` — reading-tag relationship

### Phase Dependencies
- Phase 9 (Repository Layer) — provides repositories with Flow-based observe methods
- Previous phases provide: Room database schema, Hilt DI setup, existing ViewModel patterns

</canonical_refs>

<code_context>
## Existing Code Insights

### Reusable Assets
- **ReadingListViewModel:** Uses `stateIn(SharingStarted.WhileSubscribed(5_000), initialValue = Loading` — reuse this pattern
- **DeckRepository:** Has `observeAllDecks()`, `observeCustomDecks()`, `createDeck()`, `updateDeck()`, `deleteDeck()`
- **TagRepository:** Has `observeAllTags()`, `create()`, `update()`, `delete()`
- **ReadingRepository:** Extend with `observeReadingsByTags()`, `observeFavoriteReadings()` — verify Phase 9-02 implemented

### Established Patterns
- State pattern: `stateIn()` for reactive flows, `MutableStateFlow` for complex forms
- UI state: `Sealed interface` with Loading/Success/Error states
- ViewModel injection: `@HiltViewModel` + constructor injection

### Integration Points
- Phase 11 (UI Implementation) consumes these ViewModels
- Navigation: bottom nav tabs (Readings, Decks, Settings)
- Card picker when recording reading uses selected deck

</code_context>

<specifics>
## Specific Ideas

No specific "I want it like X" moments — standard ViewModel patterns acceptable.

</specifics>

<deferred>
## Deferred Ideas

None — discussion stayed within phase scope.

</deferred>

---

*Phase: 10-viewmodel-layer*
*Context gathered: 2026-04-19*