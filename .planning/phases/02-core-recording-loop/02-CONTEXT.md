# Phase 2: Core Recording Loop - Context

**Gathered:** 2026-04-03
**Status:** Ready for planning

<domain>
## Phase Boundary

Build the complete user-facing recording loop: users can select a spread, assign cards to each position via a visual grid picker, add notes, save the reading, and browse/view their reading history. This is the core value delivery — transforming the scaffolding from Phase 1 into a functional tarot tracker.

This phase adds:
- AddReading flow (multi-step wizard)
- ReadingDetailScreen (showing spread layout with assigned cards)
- Navigation between reading list, add reading, and reading detail
- All ViewModels for the new screens

This phase does NOT add:
- Photo attachments (Phase 3)
- Search/filter readings (Phase 3)
- Edit/delete readings (Phase 3)
- Reversed card toggles (Phase 3)
- Dark theme polish (Phase 3)
- Unit tests (Phase 4)

</domain>

<decisions>
## Implementation Decisions

### Add Reading Flow
- **D-13:** Multi-step wizard — not a single scrollable screen
- **D-14:** Step 1: Pick spread → Step 2: Assign cards to each position → Step 3: Add notes and save
- **D-15:** This matches the mental model of "doing a reading" — choose your spread first, then fill it in

### Card Selection UX
- **D-16:** Tap from visual grid — each spread position shows a card slot; tap to open card picker
- **D-17:** Card picker is a bottom sheet or full-screen dialog showing all 78 cards
- **D-18:** Cards are sectioned by arcana type: Major Arcana first, then Minor Arcana grouped by suit (Wands, Cups, Swords, Pentacles)
- **D-19:** Within each section, cards are ordered by number (Ace through King)

### Reading Title
- **D-20:** Auto-generated from spread name + date, e.g. "Celtic Cross — Apr 3, 2026"
- **D-21:** Title is pre-populated but editable — user can modify before saving
- **D-22:** Title field appears in Step 3 (notes + save), shown above the notes field

### Interpretations
- **D-23:** Notes only at the end — one freeform text field for the whole reading
- **D-24:** No per-position interpretation fields in v1 — keeps the UI focused and simple

### Navigation
- **D-25:** Add new navigation route `AddReading` — the wizard is its own destination
- **D-26:** ReadingDetailScreen is already routed (`ReadingDetail(readingId)`) — just needs implementation
- **D-27:** The wizard steps are managed internally within AddReadingScreen (not separate nav destinations)

### Architecture Patterns (carried from Phase 1)
- **D-28:** Layer-based structure continues — new screens go in `ui/`, new ViewModels alongside screens
- **D-29:** Repositories from Phase 1 are reused — no new data layer code needed for core flow
- **D-30:** `ReadingRepository.createReading()` already handles the transaction (reading + cards)
- **D-31:** `ReadingRepository.observeReadingWithDetails()` already provides the detail data

### the agent's Discretion
- Exact wizard step UI (horizontal pager vs. sequential screens with next/back buttons)
- Card grid layout specifics (columns, card size, image placeholder handling)
- Bottom sheet vs. full-screen for card picker
- Exact date format for auto-generated titles
- Empty/unassigned position visual treatment

</decisions>

<canonical_refs>
## Canonical References

**Downstream agents MUST read these before planning or implementing.**

### Project Setup
- `.planning/PROJECT.md` — Project context, core value, constraints
- `.planning/REQUIREMENTS.md` — READ-01, READ-02, READ-03, READ-04, SPRD-01, SPRD-02, SPRD-03, SPRD-04, CARD-01, CARD-02, CARD-03
- `.planning/ROADMAP.md` — Phase 2 goal and success criteria
- `.planning/phases/01-foundation-dev-environment/01-CONTEXT.md` — Phase 1 decisions (layer structure, DI patterns, navigation approach)

### Existing Code (must read before planning)
- `app/src/main/kotlin/com/example/drawn/data/repository/ReadingRepository.kt` — Already has `createReading()`, `observeReadingWithDetails()`, `observeAllReadings()`
- `app/src/main/kotlin/com/example/drawn/data/repository/CardRepository.kt` — Already has `observeAllCards()`, `observeCardsByDeck()`
- `app/src/main/kotlin/com/example/drawn/data/repository/SpreadRepository.kt` — Already has `observeAllSpreads()`, `getSpreadById()`
- `app/src/main/kotlin/com/example/drawn/ui/navigation/DrawnNavHost.kt` — Current nav host with TODO placeholders
- `app/src/main/kotlin/com/example/drawn/ui/navigation/DrawnDestinations.kt` — Current routes (ReadingList, ReadingDetail)
- `app/src/main/kotlin/com/example/drawn/ui/readinglist/ReadingListScreen.kt` — Existing screen pattern to follow
- `app/src/main/kotlin/com/example/drawn/ui/readinglist/ReadingListViewModel.kt` — Existing ViewModel pattern to follow
- `app/src/main/kotlin/com/example/drawn/domain/model/` — All domain models (Reading, Card, Spread, ReadingCard, ReadingDetail)
- `app/src/main/kotlin/com/example/drawn/ui/theme/` — Existing theme (dark purple/gold)

### No external specs
No external specs — requirements fully captured in decisions above

</canonical_refs>

<code_context>
## Existing Code Insights

### Reusable Assets
- **ReadingRepository** — `createReading(reading, cards)` handles the full save transaction
- **ReadingRepository** — `observeReadingWithDetails(id)` returns ReadingDetail with cards + photos
- **CardRepository** — `observeAllCards()` returns Flow of all 78 cards
- **SpreadRepository** — `observeAllSpreads()` returns Flow of 3 pre-loaded spreads
- **SpreadRepository** — `getSpreadById(id)` returns Spread with positions
- **ReadingDetail** domain model — aggregates Reading + List<ReadingCard> + List<ReadingPhoto>
- **ReadingCard** domain model — has positionName, positionOrder, interpretation, isReversed fields
- **DrawnTheme** — Dark color scheme already defined (purple/gold)
- **Navigation Compose 3** — `NavDisplay` with `mutableStateListOf` backstack pattern established

### Established Patterns
- ViewModel pattern: `StateFlow<UiState>` with Loading/Success/Error sealed interface
- Repository pattern: Flow-based observation, suspend functions for mutations
- Entity-domain mapping: `toDomain()` and `toEntity()` extension functions
- Hilt: `@HiltViewModel` + `hiltViewModel()` in Compose
- UI pattern: Scaffold + TopAppBar + content area with Loading/Error/Success states

### Integration Points
- Phase 2 connects to:
  - `ReadingRepository.createReading()` — save new readings
  - `ReadingRepository.observeReadingWithDetails()` — load reading detail
  - `SpreadRepository.observeAllSpreads()` — populate spread picker
  - `CardRepository.observeAllCards()` — populate card picker
  - Navigation: Add `AddReading` route, implement `ReadingDetail` screen

</code_context>

<specifics>
## Specific Ideas

- Multi-step wizard matches the mental model of "doing a reading" — choose spread, then fill it in
- Card picker should feel like browsing a physical deck — sectioned by Major/Minor, then by suit
- Auto-generated titles reduce friction — user just wants to record their reading quickly
- Single notes field at the end keeps the UI focused — per-position notes can come later if needed
- The data layer is already complete — Phase 2 is purely UI + ViewModels

</specifics>

<deferred>
## Deferred Ideas

- Photo attachments — Phase 3
- Search/filter readings — Phase 3
- Edit/delete readings — Phase 3
- Reversed card toggles — Phase 3
- Per-position interpretation notes — deferred, may revisit based on user feedback
- Custom deck selection for readings — Phase 2 only uses the built-in RWS deck
- Random/shuffle card draw — explicitly out of scope (freeform entry only)

</deferred>

---

*Phase: 02-core-recording-loop*
*Context gathered: 2026-04-03*
