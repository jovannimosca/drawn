# Phase 11: UI Implementation - Context

**Gathered:** 2026-04-19
**Status:** Ready for planning

<domain>
## Phase Boundary

Build UI for v1.1 features:
- Bottom navigation (Readings, Decks, Settings tabs)
- Deck management screens (list, detail, card editor)
- Tag management screens (list, assignment, filtering)
- Integrate with existing ViewModels from Phase 10

**Depends on:** Phase 10 (ViewModel Layer)
**UI hint:** yes

</domain>

<decisions>
## Implementation Decisions

### Navigation Architecture
- **D-01:** Bottom navigation replaces root — each tab has its own back stack
- **D-02:** Uses Navigation 3 (existing) — adapt DrawnNavHost for bottom nav
- **D-03:** 3 tabs: Readings, Decks, Settings

### Deck Screens
- **D-04:** List → detail flow pattern
- **D-05:** Cards integrated in deck detail: name, image, keywords, meaning, reversedMeaning
- **D-06:** Create/edit: inline or modal dialog

### Tag Screens
- **D-07:** List → modal pattern (create/edit)
- **D-08:** Tag assignment: chips in reading detail with add/remove
- **D-09:** Filter sheet (not full screen)

### Settings Screen
- **D-10:** App version display
- **D-11:** Link to backup/restore (Phase 12)

### Existing Components to Reuse
- DeckViewModel (Phase 10)
- CardManagementViewModel (Phase 10)
- TagViewModel (Phase 10) with PRESET_COLORS
- ReadingListViewModel with filtering (Phase 10)

### the agent's Discretion
- Exact filter sheet layout (chips vs dropdown)
- Settings screen detail (exactly which items)
- Transition animations

</decisions>

<canonical_refs>
## Canonical References

**Downstream agents MUST read these before planning or implementing.**

### Existing Code Patterns
- `app/src/main/kotlin/com/example/drawn/ui/navigation/DrawnNavHost.kt` — Navigation 3 pattern
- `app/src/main/kotlin/com/example/drawn/ui/readinglist/ReadingListViewModel.kt` — stateIn() pattern
- `app/src/main/kotlin/com/example/drawn/ui/deck/DeckViewModel.kt` — Phase 10 deck ViewModel
- `app/src/main/kotlin/com/example/drawn/ui/deck/CardManagementViewModel.kt` — Phase 10 card ViewModel
- `app/src/main/kotlin/com/example/drawn/ui/tag/TagViewModel.kt` — PRESET_COLORS

### Phase Dependencies
- Phase 10 (ViewModel Layer) — provides ViewModels consumed by UI
- Previous phases provide: existing screens, navigation, theme

</canonical_refs>

陈区
## Existing Code Insights

### Reusable Assets
- **DrawnNavHost:** Navigation 3 with backStack — adapt for bottom nav with tabs
- **ReadingListScreen:** Reuse search bar, list item patterns
- **Material 3:** Dark theme components ready

### Established Patterns
- Stack-based navigation per destination
- Modal dialogs for create/edit
- Loading/Success/Error UI states from ViewModels

### Integration Points
- Reading list filters consume ReadingListViewModel
- Deck list uses DeckViewModel
- Card management uses CardManagementViewModel
- Tag assignment uses ReadingTagRepository

</code_context>

<specifics>
## Specific Ideas

- Tag colors: PRESET_COLORS from TagViewModel (#9B59B6, #F39C12, #1ABC9C, etc.)
- Bottom nav: Material 3 NavigationBar or custom

</specifics>

<deferred>
## Deferred Ideas

None — discussion stayed within phase scope.

</deferred>

---

*Phase: 11-ui-implementation*
*Context gathered: 2026-04-19*