# Phase 3: Enrichment & Polish - Context

**Gathered:** 2026-04-03
**Status:** Ready for planning

<domain>
## Phase Boundary

Add photo attachments, search/filter, edit/delete readings, reversed card toggles, and a full dark theme polish pass. This phase transforms the functional recording loop from Phase 2 into a polished, complete tarot tracker.

This phase adds:
- Photo attachment inline in reading detail (gallery + camera)
- Full-screen photo viewer with delete
- Inline editing on reading detail screen
- Delete reading with confirmation
- Search bar integrated into reading list (real-time filter by notes, card names, date)
- Reversed/upright toggle on position slots (tap assigned card to toggle)
- Full dark theme polish across all screens

This phase does NOT add:
- Custom decks (Phase 2 deferred, still out of scope)
- Statistics/organization features (v2)
- Unit tests (Phase 4)
- F-Droid deployment (future)

</domain>

<decisions>
## Implementation Decisions

### Photo Attachments
- **D-32:** Photos live inline in the reading detail screen — not a separate screen
- **D-33:** Photos section appears below the notes in reading detail
- **D-34:** Tap "+" to add photos — system Photo Picker for gallery, Camera Intent for camera
- **D-35:** Tap a photo to view full-screen (bottom sheet or full-screen dialog)
- **D-36:** Long-press or swipe-to-delete for removing photos
- **D-37:** No storage permissions needed — use Android Photo Picker API

### Edit Flow
- **D-38:** Inline editing on the reading detail screen — NOT re-using the wizard
- **D-39:** Edit button on reading detail TopAppBar
- **D-40:** Tapping edit makes title, notes, and card assignments editable inline
- **D-41:** Save/Cancel buttons appear when in edit mode
- **D-42:** Card reassignment in edit mode uses the same CardPickerBottomSheet from Phase 2

### Delete Reading
- **D-43:** Delete button (trash icon) on reading detail TopAppBar
- **D-44:** Confirmation dialog before delete
- **D-45:** Delete navigates back to reading list after confirmation

### Search
- **D-46:** Search bar integrated into the reading list — not a separate screen
- **D-47:** Search filters in real-time as user types
- **D-48:** Search matches against: reading title, notes, assigned card names, and date
- **D-49:** Search clears when user navigates away or taps clear button
- **D-50:** Search is implemented in the ReadingListViewModel — no new DAO needed (filter existing Flow)

### Reversed Card Toggle
- **D-51:** Toggle lives on the position slot — tap an assigned card to toggle upright/reversed
- **D-52:** Visual indicator shows reversed state (card image flipped vertically or "R" badge)
- **D-53:** Toggle is available in both the wizard (Step 2) and reading detail (edit mode)
- **D-54:** `isReversed` field already exists in ReadingCard entity — just needs UI wiring

### Theme Polish
- **D-55:** Full theme pass — not just consistency, but visual polish
- **D-56:** Custom typography scale with appropriate sizes for tarot context
- **D-57:** Card shadows and elevation for depth
- **D-58:** Subtle gradients or starry background elements where appropriate
- **D-59:** Consistent spacing, rounded corners, and visual hierarchy across all screens
- **D-60:** Dark theme colors already defined (purple/gold) — polish is about application, not new colors

### Architecture Patterns (carried from prior phases)
- **D-61:** Layer-based structure continues
- **D-62:** Photo URIs stored in Room, actual files managed by MediaStore
- **D-63:** Search implemented as Flow transformation in ViewModel — no new repository methods needed
- **D-64:** Edit mode uses same repositories — `ReadingRepository.updateReading()` already exists

### the agent's Discretion
- Exact photo viewer implementation (bottom sheet vs. full-screen activity)
- Specific typography scale values
- Exact search match algorithm (contains, fuzzy, etc.)
- Visual treatment of reversed cards (flip animation vs. static indicator)
- Gradient/star background implementation details

</decisions>

<canonical_refs>
## Canonical References

**Downstream agents MUST read these before planning or implementing.**

### Project Setup
- `.planning/PROJECT.md` — Project context, core value, constraints
- `.planning/REQUIREMENTS.md` — READ-05, READ-06, READ-07, CARD-04, PHOTO-01 through PHOTO-04, THEME-01, THEME-02
- `.planning/ROADMAP.md` — Phase 3 goal and success criteria
- `.planning/phases/02-core-recording-loop/02-CONTEXT.md` — Phase 2 decisions (wizard patterns, card picker, navigation)
- `.planning/phases/02-core-recording-loop/02-UAT.md` — Known bugs (FAB save flash)

### Existing Code (must read before planning)
- `app/src/main/kotlin/com/example/drawn/data/repository/ReadingRepository.kt` — Has `updateReading()`, `deleteReading()`, `observeReadingWithDetails()`
- `app/src/main/kotlin/com/example/drawn/data/repository/CardRepository.kt` — Has `observeAllCards()`
- `app/src/main/kotlin/com/example/drawn/data/database/entity/ReadingPhotoEntity.kt` — Already defined
- `app/src/main/kotlin/com/example/drawn/data/database/entity/ReadingCardEntity.kt` — Has `isReversed` field
- `app/src/main/kotlin/com/example/drawn/data/database/dao/ReadingPhotoDao.kt` — Already has insert/delete/observe methods
- `app/src/main/kotlin/com/example/drawn/ui/readingdetail/ReadingDetailScreen.kt` — Current detail screen (needs photo section, edit, delete)
- `app/src/main/kotlin/com/example/drawn/ui/readinglist/ReadingListScreen.kt` — Current list screen (needs search bar)
- `app/src/main/kotlin/com/example/drawn/ui/readinglist/ReadingListViewModel.kt` — Current ViewModel (needs search filter)
- `app/src/main/kotlin/com/example/drawn/ui/theme/` — Current theme (needs polish)

### No external specs
No external specs — requirements fully captured in decisions above

</canonical_refs>

<code_context>
## Existing Code Insights

### Reusable Assets
- **CardPickerBottomSheet** — Reusable for card reassignment in edit mode
- **PositionSlot** — Needs reversed state visual treatment
- **ReadingRepository.updateReading()** — Already handles reading + cards transaction
- **ReadingRepository.deleteReading()** — Already exists
- **ReadingPhotoDao** — Has `insert()`, `delete()`, `observePhotosForReading()`
- **ReadingPhotoEntity** — Has id, readingId, photoUri, caption fields
- **ReadingDetail domain model** — Already includes photos list

### Established Patterns
- ViewModel pattern: `StateFlow<UiState>` with Loading/Success/Error
- Repository pattern: Flow-based observation, suspend functions for mutations
- Hilt: `@HiltViewModel` + `hiltViewModel()` in Compose
- UI pattern: Scaffold + TopAppBar + content area
- Navigation: Navigation Compose 3 with `mutableStateListOf` backstack

### Integration Points
- Phase 3 connects to:
  - ReadingPhotoDao — photo CRUD
  - ReadingRepository.updateReading() — edit flow
  - ReadingRepository.deleteReading() — delete flow
  - ReadingListViewModel — search filter
  - CardPickerBottomSheet — card reassignment in edit mode
  - PositionSlot — reversed state display

</code_context>

<specifics>
## Specific Ideas

- Photos inline in reading detail keeps the flow natural — view reading, see photos, add more
- Inline editing on detail screen is faster than re-opening the wizard for small changes
- Search bar in reading list is the most discoverable pattern — no hidden search screen
- Reversed toggle on position slot is intuitive — tap the card you want to flip
- Full theme pass means every screen should feel cohesive and polished — not just "dark mode works"

</specifics>

<deferred>
## Deferred Ideas

- Custom deck creation and selection — still out of scope
- Statistics/organization features — v2
- Per-position interpretation notes — deferred from Phase 2, still deferred
- Random/shuffle card draw — explicitly out of scope
- F-Droid deployment — future phase
- Unit tests — Phase 4

</deferred>

---

*Phase: 03-enrichment-polish*
*Context gathered: 2026-04-03*
