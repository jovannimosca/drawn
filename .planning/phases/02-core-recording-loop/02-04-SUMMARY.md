---
phase: 02-core-recording-loop
plan: 04
subsystem: ui
tags: [compose, navigation, reading-detail, viewmodel, hilt]

# Dependency graph
requires:
  - phase: 02-core-recording-loop
    provides: AddReadingScreen (Plans 01-03), ReadingRepository.observeReadingWithDetails, domain models
provides:
  - Complete navigation graph: ReadingList → AddReading → back
  - ReadingDetailScreen with ViewModel for viewing saved readings
  - FAB navigation wired on ReadingListScreen
affects: [03-01 photo attachments, 03-02 search/filter]

# Tech tracking
tech-stack:
  added: [SavedStateHandle for ViewModel injection]
  patterns:
    - ViewModel with SavedStateHandle for route parameters
    - ReadingDetailScreen follows same Loading/Error/Success pattern as ReadingListScreen

key-files:
  created:
    - app/src/main/kotlin/com/example/drawn/ui/readingdetail/ReadingDetailScreen.kt
    - app/src/main/kotlin/com/example/drawn/ui/readingdetail/ReadingDetailViewModel.kt
  modified:
    - app/src/main/kotlin/com/example/drawn/ui/navigation/DrawnNavHost.kt
    - app/src/main/kotlin/com/example/drawn/ui/addreading/AddReadingScreen.kt

key-decisions:
  - "ReadingDetailViewModel uses SavedStateHandle to get readingId from navigation parameters"
  - "Card display shows cardId text only (not thumbnail) since ReadingCard only has cardId, not full Card object"

patterns-established:
  - "SavedStateHandle pattern for extracting route parameters in ViewModels"
  - "ReadingDetailScreen Loading/Error/Success state pattern consistent with ReadingListScreen"

requirements-completed: [READ-03, READ-04]

# Metrics
duration: 5min
completed: 2026-04-03
---

# Phase 02 Plan 04: Navigation Wiring and Reading Detail Summary

**Complete user journey: FAB → wizard → save → list → detail view with all navigation stubs resolved**

## Performance

- **Duration:** 5 min
- **Started:** 2026-04-03T00:05:00Z
- **Completed:** 2026-04-03T00:10:00Z
- **Tasks:** 2
- **Files modified:** 4

## Accomplishments
- DrawnNavHost fully wired: ReadingList → AddReading (FAB), AddReading → ReadingList (back arrow or save), ReadingList → ReadingDetail (item tap), ReadingDetail → ReadingList (back arrow)
- ReadingDetailViewModel with SavedStateHandle for readingId, Loading/Success/Error states via observeReadingWithDetails
- ReadingDetailScreen showing reading title, cards in position order, notes (if present), and formatted date
- AddReadingScreen CardAssignment placeholder replaced with actual CardAssignmentStep from Plan 02-03
- All TODO stubs from Phase 1 in DrawnNavHost resolved

## Task Commits

Each task was committed atomically:

1. **Task 1: Wire AddReading navigation and implement ReadingDetailScreen** - `bd2015a` (feat)
2. **Task 2: Wire ReadingDetail in NavHost and verify full flow** - `bd2015a` (feat)

**Plan metadata:** `bd2015a` (docs: complete plan)

## Files Created/Modified
- `app/src/main/kotlin/com/example/drawn/ui/navigation/DrawnNavHost.kt` - Wired all 3 routes (ReadingList, AddReading, ReadingDetail) with proper navigation callbacks
- `app/src/main/kotlin/com/example/drawn/ui/addreading/AddReadingScreen.kt` - Replaced CardAssignment placeholder with CardAssignmentStep component
- `app/src/main/kotlin/com/example/drawn/ui/readingdetail/ReadingDetailScreen.kt` - Full reading detail view with scrollable content, cards, notes, date
- `app/src/main/kotlin/com/example/drawn/ui/readingdetail/ReadingDetailViewModel.kt` - ViewModel loading ReadingDetail via observeReadingWithDetails

## Decisions Made
- ReadingDetailViewModel uses `SavedStateHandle` to extract `readingId` from navigation parameters — standard Android pattern for parameterized ViewModels
- Card display in detail view shows `cardId` as text rather than thumbnail — `ReadingCard` only has `cardId`, not the full `Card` object. To show thumbnails would require joining with Card table. Deferred to Phase 3 when photo attachments are implemented.

## Deviations from Plan

None - plan executed exactly as written.

## Issues Encountered
None

## User Setup Required
None - no external service configuration required.

## Next Phase Readiness
- Complete user journey functional: create reading via wizard → see in list → view details
- Phase 02 (core-recording-loop) is now complete — all 4 plans finished
- Ready for Phase 03: photo attachments, search/filter, edit/delete, reversed card toggles
- Card thumbnails in detail view deferred to Phase 3 (needs Card lookup by cardId)

---
*Phase: 02-core-recording-loop*
*Completed: 2026-04-03*
