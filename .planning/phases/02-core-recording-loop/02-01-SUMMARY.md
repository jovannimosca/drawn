---
phase: 02-core-recording-loop
plan: 01
subsystem: ui-navigation
tags: [navigation, wizard, state-management, viewmodel]
dependencies:
  requires: []
  provides: [add-reading-route, wizard-state, add-reading-viewmodel]
  affects: [DrawnNavHost]
tech-stack:
  added: [kotlinx-serialization, hilt-viewmodel, stateflow, coroutines]
  patterns: [sealed-interface-ui-state, mutable-stateflow, hilt-di]
key-files:
  created:
    - app/src/main/kotlin/com/example/drawn/ui/addreading/AddReadingState.kt
    - app/src/main/kotlin/com/example/drawn/ui/addreading/AddReadingViewModel.kt
  modified:
    - app/src/main/kotlin/com/example/drawn/ui/navigation/DrawnDestinations.kt
decisions:
  - Used MutableStateFlow instead of derived stateIn for uiState — wizard actions mutate state imperatively, derived flows can't support this
  - selectSpread() auto-advances to CardAssignment step — reduces user friction after spread selection
  - init block guards against overwriting user state — combine flow only updates when in Loading state
metrics:
  duration_minutes: 5
  completed_date: "2026-04-03T22:53:00Z"
---

# Phase 02 Plan 01: AddReading Route and Wizard ViewModel Summary

**One-liner:** AddReading navigation route with 3-step wizard state types and Hilt-injected ViewModel managing spread selection, card assignment, and reading persistence.

## Tasks Completed

| Task | Name | Commit | Files |
|------|------|--------|-------|
| 1 | Add AddReading route and wizard state types | a7708b9 | DrawnDestinations.kt, AddReadingState.kt |
| 2 | Create AddReadingViewModel with wizard logic | 4570255 | AddReadingViewModel.kt |

## What Was Built

### Navigation Route
- `@Serializable object AddReading` added to DrawnDestinations.kt following existing ReadingList pattern
- Ready for integration into DrawnNavHost entryProvider (TODO placeholder exists at line 30)

### Wizard State Types (AddReadingState.kt)
- `AddReadingStep` enum: SpreadPicker → CardAssignment → NotesAndSave
- `AddReadingState` data class with all fields: currentStep, spreads, selectedSpread, cards, assignedCards, title, notes, isLoading, error
- `canProceedToNext()` helper: Step 1 requires spread selected, Step 2 requires all positions assigned, Step 3 always proceeds
- `AddReadingUiState` sealed interface: Loading, Ready(state), Saving, Saved(readingId), Error(message)
- `generateTitle(spreadName)` function: "{Spread Name} — {MMM d, yyyy}" format per D-20

### ViewModel (AddReadingViewModel.kt)
- `@HiltViewModel` with SpreadRepository, CardRepository, ReadingRepository injection
- MutableStateFlow-based uiState with Loading initial state
- combine() of spreads/cards flows in init, guarded to not overwrite user-modified state
- All wizard actions: selectSpread, assignCard, unassignCard, goToNextStep, goToPreviousStep, updateTitle, updateNotes, saveReading, retry
- saveReading() builds Reading + List<ReadingCard> from state, calls readingRepository.createReading()

## Deviations from Plan

### [Rule 2 - Missing Functionality] Used MutableStateFlow instead of derived stateIn

**Found during:** Task 2
**Issue:** Plan specified "Follow the StateFlow + catch + stateIn pattern from ReadingListViewModel" — but ReadingListViewModel uses a derived read-only flow, which can't support imperative state mutations needed for wizard actions (selectSpread, assignCard, etc.)
**Fix:** Used MutableStateFlow internally with asStateFlow() for the public API. Init block combines repository flows to seed initial data, then guards against overwriting user-modified state.
**Files modified:** AddReadingViewModel.kt
**Commit:** 4570255

### [Rule 3 - Blocking] Missing local.properties for Android SDK

**Found during:** Task 1 compilation check
**Issue:** ANDROID_HOME not set, local.properties missing — gradle couldn't find SDK
**Fix:** Created local.properties with sdk.dir pointing to /home/jovanni/Android/Sdk
**Files modified:** local.properties (not committed — local config)

## Auth Gates

None.

## Known Stubs

- `onAddReading` callback in DrawnNavHost.kt line 30 still has TODO comment — navigation to AddReading not yet wired (will be addressed in a subsequent plan when screens are built)

## Self-Check: PASSED
