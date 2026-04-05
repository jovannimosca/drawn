# Phase 6: Refine UI theming and screens - Context

**Gathered:** 2026-04-05
**Status:** Ready for planning

<domain>
## Phase Boundary

Elevate the visual polish, interaction feel, and component states of the existing app beyond the Phase 3 foundation. Phase 3 established the dark theme and core screens; this phase focuses on the details that make it feel premium: animations, transitions, empty states, visual depth, layout density, and hierarchy.

This phase adds:
- Screen-to-screen transitions (shared element transitions)
- Horizontal pager swipe for wizard step navigation
- Animated card flip for reversed/upright toggle
- Starry/nebula background treatment on main screens
- Gold accent dividers between sections
- Gold shimmer effect on save confirmation
- Illustrated empty states for reading list and search results
- Themed error handling
- Compact reading list cards with date + spread metadata
- Spread-accurate card layout in reading detail
- Component state refinement (loading, empty, error)

This phase does NOT add:
- New screens or features (no new capabilities)
- Custom deck support (v2)
- Statistics or organization features (v2)
- Light theme support

</domain>

<decisions>
## Implementation Decisions

### Animation & Transitions
- **D-01:** Screen transitions use shared element transitions — tapped reading card expands into detail view, creating continuity between list and detail
- **D-02:** Wizard steps use horizontal pager swipe — users swipe left/right between Spread → Cards → Notes steps
- **D-03:** Reversed/upright card toggle uses 180° Y-axis flip animation — visually satisfying feedback (agent discretion on exact implementation)
- **D-04:** Micro-interactions use default M3 ripple only — no custom scale or press animations beyond Material 3 defaults
- **D-05:** Reading list uses subtle fade-in when loading data — no skeleton placeholders needed (local Room DB is fast)

### Component States & Edge Cases
- **D-06:** Reading list empty state uses illustrated empty state — themed illustration with text like "No readings yet — tap + to record your first reading"
- **D-07:** Card picker empty state (no search results) uses themed empty state — centered mystical aesthetic message
- **D-08:** Database errors use themed error handling — agent discretion on exact implementation (themed snackbar or inline banner)
- **D-09:** Long reading notes in list card preview — agent discretion on truncation approach
- **D-10:** Loading state remains centered CircularProgressIndicator — no skeleton needed for local data

### Visual Depth & Aesthetic Polish
- **D-11:** Main screens use starry/nebula background treatment — subtle star pattern or nebula texture as background element for mystical aesthetic
- **D-12:** Icons use Material 3 icons tinted with gold accent — consistent, no custom SVG assets needed
- **D-13:** Section dividers use gold accent dividers — thin gold/amber lines between sections reinforcing the mystical aesthetic
- **D-14:** Save confirmation uses gold shimmer effect — brief gold shimmer or sparkle when reading is saved, satisfying feedback

### Layout Density & Hierarchy
- **D-15:** Reading list cards are compact — show title, date, and spread name as metadata, plus notes preview (max 2 lines)
- **D-16:** Reading detail screen card layout uses spread-accurate geometry — cards arranged to match physical spread layout (e.g., Celtic Cross cross shape) rather than simple grid
- **D-17:** Reading detail screen info priority — agent discretion on ordering (spread overview → cards → notes → photos)
- **D-18:** Typography scale stays at fixed 4-step scale — existing 7 M3 styles (displayLarge, headlineSmall, titleMedium, bodyLarge, bodyMedium, labelLarge, labelSmall) with 400/500 weights only

### Architecture Patterns (carried from prior phases)
- **D-19:** Layer-based structure continues — new shared components should go in `ui/components/` (currently doesn't exist)
- **D-20:** ViewModel pattern: `StateFlow<UiState>` with Loading/Success/Error sealed interface
- **D-21:** Navigation Compose 3 with `@Serializable` route types
- **D-22:** Hilt: `@HiltViewModel` + `hiltViewModel()` in Compose

### the agent's Discretion
- Exact shared element transition implementation (which elements are shared between screens)
- Card flip animation duration and easing curve
- Starry/nebula background implementation (custom drawable vs. canvas drawing vs. image asset)
- Gold shimmer save animation duration and visual treatment
- Error state exact implementation (themed snackbar vs. inline banner)
- Long text truncation approach in reading list cards
- Reading detail screen information hierarchy ordering
- Spread-accurate layout implementation for complex spreads (Celtic Cross cross+staff geometry)

</decisions>

<canonical_refs>
## Canonical References

**Downstream agents MUST read these before planning or implementing.**

### Project Setup
- `.planning/PROJECT.md` — Project context, core value, constraints
- `.planning/REQUIREMENTS.md` — THEME-01, THEME-02 (already complete, this phase refines further)
- `.planning/ROADMAP.md` — Phase 6 goal and scope
- `.planning/phases/03-enrichment-polish/03-CONTEXT.md` — Phase 3 decisions (theme foundation, existing components, patterns)

### Existing Code (must read before planning)
- `app/src/main/kotlin/com/example/drawn/ui/theme/Color.kt` — Current color tokens (purple/gold palette)
- `app/src/main/kotlin/com/example/drawn/ui/theme/Type.kt` — Current typography scale (7 of 15 M3 styles defined)
- `app/src/main/kotlin/com/example/drawn/ui/theme/Theme.kt` — Theme entry point, DrawnCardElevation
- `app/src/main/kotlin/com/example/drawn/ui/readinglist/ReadingListScreen.kt` — Current list screen (has inline EmptyState, ErrorCard, ReadingListItem)
- `app/src/main/kotlin/com/example/drawn/ui/readingdetail/ReadingDetailScreen.kt` — Current detail screen (has ReadingDetailCardItem in 3-column grid)
- `app/src/main/kotlin/com/example/drawn/ui/addreading/AddReadingScreen.kt` — Wizard screen (has WizardBottomBar, needs pager migration)
- `app/src/main/kotlin/com/example/drawn/ui/addreading/WizardStepIndicator.kt` — Step indicator composable
- `app/src/main/kotlin/com/example/drawn/ui/addreading/PositionSlot.kt` — Position slot with static reversed rotation
- `app/src/main/kotlin/com/example/drawn/ui/addreading/CardThumbnail.kt` — Card image display, cardDrawableRes() mapping
- `app/src/main/kotlin/com/example/drawn/ui/navigation/DrawnNavHost.kt` — Navigation host (needs shared element transition support)
- `app/src/main/kotlin/com/example/drawn/ui/navigation/DrawnDestinations.kt` — Route definitions

### No external specs
No external specs — requirements fully captured in decisions above

</canonical_refs>

<code_context>
## Existing Code Insights

### Reusable Assets
- **CardThumbnail** — Reusable composable for displaying tarot card images with fallback
- **PositionSlot** — Position slot with assigned/unassigned states, double-click reversed toggle (needs animation upgrade)
- **WizardStepIndicator** — 3-step canvas-based indicator (circles for progress)
- **SpreadCard** — Selectable spread card from Phase 2
- **ReadingListItem** — Current compact card in reading list (title + notes preview, 2-line max)
- **ReadingDetailCardItem** — Card thumbnail in 3-column grid (needs spread-accurate layout)
- **DrawnCardElevation** — Composable function for card elevation (1dp default, 3dp dragged)
- **ic_launcher_foreground.xml** — Eight-pointed mystical star vector, reusable as decorative element

### Established Patterns
- All screens use Scaffold + TopAppBar + content area pattern
- Loading: centered CircularProgressIndicator
- Empty states: inline private composables (not shared)
- Error states: inline error cards with errorContainer colors
- ViewModel: StateFlow<SealedInterface> with Loading/Success/Error
- Repository: Flow-based observation, suspend functions for mutations
- No animations exist in codebase — zero Compose Animation API usage

### Integration Points
- Phase 6 connects to:
  - `DrawnNavHost.kt` — shared element transitions between list and detail
  - `AddReadingScreen.kt` — migrate from step swap to HorizontalPager
  - `PositionSlot.kt` — add Y-axis flip animation to reversed toggle
  - `ReadingListScreen.kt` — add fade-in loading, illustrated empty state
  - `ReadingDetailScreen.kt` — spread-accurate card layout, nebula background
  - `Theme.kt` — potential new shapes, background composable
  - `Type.kt` — missing 8 M3 typography styles if needed

### Key Constraint
- No `ui/components/` directory exists — shared components (EmptyState, LoadingState) are currently duplicated as private composables in each screen
- Typography scale only defines 7 of 15 M3 styles — may need expansion for refined hierarchy

</code_context>

<specifics>
## Specific Ideas

- Shared element transitions should feel like the reading card "opens up" into the detail view — continuity between list and detail
- Horizontal pager swipe for wizard feels more natural and modern than tap-based navigation
- Starry/nebula background should be subtle — not overwhelming the content, just adding atmosphere
- Gold shimmer on save should be brief and satisfying — a moment of celebration for completing a reading
- Compact reading list cards should show more readings per screen — users want to scan their history quickly
- Spread-accurate layout means the Celtic Cross should look like a Celtic Cross, not a flat list
- Material icons with gold tint keeps the app lightweight — no need for custom SVG icon sets
- Illustrated empty states should match the mystical aesthetic — sparkle emoji or star motif works

</specifics>

<deferred>
## Deferred Ideas

- Custom deck UI support — v2 requirement (CDECK-01 through CDECK-04)
- Statistics and organization features — v2 (ORG-01 through ORG-04)
- Light theme support — not requested, dark-only is fine for v1
- Per-position interpretation notes — deferred from Phase 2, still deferred
- Random/shuffle card draw — explicitly out of scope

### Reviewed Todos (not folded)
None — analysis stayed within phase scope

</deferred>

---

*Phase: 06-refine-ui-theming-and-screens*
*Context gathered: 2026-04-05*
