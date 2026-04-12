# Phase 7: Polish Bug Fixes - Context

**Gathered:** 2026-04-11
**Status:** Ready for planning
**Source:** Gap closure from v1.0-MILESTONE-AUDIT.md

<domain>
## Phase Boundary

This phase fixes implementation gaps identified in the v1.0 milestone audit. Two specific bugs from Phase 6 need fixing:

1. **AnimatedVisibility fade-in not triggering** — The reading list should fade in when data loads, but `visible = true` is hardcoded instead of tracking state
2. **Shared element transitions not implemented** — Decision D-01 from Phase 6 context specified screen transitions should use shared element transitions, but this was not implemented

This phase does NOT add:
- New features or capabilities
- New screens or UI elements beyond the existing two fixes
- Any architectural changes

</domain>

<decisions>
## Implementation Decisions

### Gap Fixes
- **D-01:** ReadingListScreen fade-in animation must trigger on state transition — bind AnimatedVisibility `visible` parameter to loading state variable that transitions false→true when data loads
- **D-02:** Navigation from list→detail uses shared element transitions — implement SharedTransitionScope with sharedElement modifier so tapped reading card expands into detail view

### the agent's Discretion
- Exact AnimatedVisibility implementation (which state variable triggers fade-in)
- Which elements participate in shared element transition (card image, title, or both)
- Transition duration and easing for shared element animation

</decisions>

<canonical_refs>
## Canonical References

**Downstream agents MUST read these before planning or implementing.**

### Project Context
- `.planning/PROJECT.md` — Project context, core value, constraints
- `.planning/v1.0-MILESTONE-AUDIT.md` — Contains the specific gaps being closed
- `.planning/phases/06-refine-ui-theming-and-screens/06-CONTEXT.md` — Original D-01 decision for shared element transitions

### Existing Code (must read before implementing)
- `app/src/main/kotlin/com/example/drawn/ui/readinglist/ReadingListScreen.kt` — Line ~161 has AnimatedVisibility with hardcoded visible=true
- `app/src/main/kotlin/com/example/drawn/ui/navigation/DrawnNavHost.kt` — Navigation host for adding shared element transition support

### No external specs
Gap closure — fixes specific implementation bugs, no external requirements

</canonical_refs>

<code_context>
## Existing Code Insights

### ReadingListScreen.kt Structure (Line ~161)
```kotlin
AnimatedVisibility(
    visible = true,  // BUG: should track state
    enter = fadeIn(),
    exit = fadeOut()
) {
    // reading list content
}
```

Current pattern:
- Uses Loading/Success/Error sealed interface in ViewModel
- `isLoading` StateFlow<Boolean> exists but isn't connected to AnimatedVisibility

### Navigation Pattern
- Uses Navigation Compose 3 with type-safe routes
- No SharedTransitionScope currently implemented
- DrawnNavHost.kt defines destinations with NavType.SerializableType

### ViewModel Pattern
- StateFlow<UiState> with Loading/Success/Error
- isLoading: StateFlow<Boolean> available in ReadingListViewModel

</code_context>

<specifics>
## Specific Ideas

- Fade-in should be subtle — standard fadeIn() animation, 300ms default
- Shared element should feel like the card "opens up" into detail view
- Both fixes are non-breaking — just fixing existing intended behavior

</specifics>

<deferred>
## Deferred Ideas

None — this is a focused gap closure phase with only 2 specific fixes

</deferred>

---

*Phase: 07-polish-bug-fixes*
*Context gathered: 2026-04-11 via gap audit*