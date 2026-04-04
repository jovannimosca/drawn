---
status: testing
phase: 02-core-recording-loop
source: [02-01-SUMMARY.md, 02-02-SUMMARY.md, 02-03-SUMMARY.md, 02-04-SUMMARY.md]
started: 2026-04-03T00:30:00Z
updated: 2026-04-03T22:00:00Z
---

## Current Test

number: 2
name: Open AddReading Wizard from FAB
expected: |
  Tapping the FAB (+) opens the AddReading wizard showing Step 1 (Spread selection) with a TopAppBar titled "New Reading", a 3-step indicator (Spread → Cards → Notes), and a stacked list of available spreads (One-Card Spread, Past/Present/Future, Celtic Cross, Six Card Pentagram)
awaiting: user response

## Tests

### 1. Build and Launch App
expected: App builds successfully and launches to ReadingListScreen with "Drawn" title, empty state "No readings yet", and FAB (+) button
result: pass
notes: Issues found and fixed during test: (1) Schema version mismatch — incremented from 1→2 with proper migration, (2) Error UI improved with ErrorCard component, (3) Fixed Icons.Default.ErrorOutline → Icons.Default.Warning

### 2. Open AddReading Wizard from FAB
expected: Tapping the FAB (+) opens the AddReading wizard showing Step 1 (Spread selection) with a TopAppBar titled "New Reading", a 3-step indicator (Spread → Cards → Notes), and a stacked list of available spreads
result: pass

### 3. Select a Spread in Step 1
expected: Tapping a spread card highlights it with the purple accent color. The "Next" button at the bottom becomes enabled. Auto-advances to Step 2 (Card Assignment).
result: pass

### 4. Card Assignment Step (Step 2)
expected: Shows all spread positions as tappable slots. Each unassigned slot has a dashed border with a placeholder icon and "Tap to select a card" hint. A progress indicator shows "0 of X positions assigned". The "Back" and "Next" buttons appear (Next disabled until all positions assigned).
result: pass

### 5. Card Picker Bottom Sheet
expected: Tapping a position slot opens a ModalBottomSheet with a sectioned grid of all 78 cards. Sections appear in order: Major Arcana, Wands, Cups, Swords, Pentacles. Each card shows a thumbnail image and name. Tapping a card assigns it to the position and closes the sheet.
result: pass
notes: Card images are Pamela Coleman Smith RWS imagery from sacred-texts.com

### 6. Complete Card Assignment and Advance
expected: After assigning cards to all positions, the "Next" button becomes enabled. Tapping Next advances to Step 3 (Notes and Save).
result: pass

### 7. Notes and Save Step (Step 3)
expected: Shows an editable title field pre-populated with "{Spread Name} — {Date}" (e.g., "Celtic Cross — Apr 3, 2026"). Below it is a multiline notes text field with placeholder "Write your thoughts about this reading...". A full-width "Save Reading" button in purple accent appears at the bottom.
result: pass

### 8. Save Reading and Return to List
expected: Tapping "Save Reading" shows a loading state, then returns to the ReadingListScreen. The new reading appears in the list with its title and notes preview.
result: pass
notes: Known bug: brief flash of wizard Step 1 before returning to list on save. See Gaps section.

### 9. View Reading Detail
expected: Tapping a reading in the list opens ReadingDetailScreen showing the reading title, spread name, assigned cards in position order, notes (if present), and formatted date. A back arrow in the TopAppBar returns to the reading list.
result: pass

## Summary

total: 9
passed: 9
issues: 0
pending: 0
skipped: 0

## Gaps

### Known Bug: FAB + Save Flash
**Description:** After saving a reading and returning to the list, tapping the FAB again shows a brief flash of the wizard's Step 1 (spread picker) before displaying the fresh wizard. This is a race condition between `SideEffect` navigation and ViewModel state reset.

**Root cause:** `SideEffect` runs synchronously during composition, so `reset()` sets state to `Ready` (Step 1) which renders before `onNavigateBack()` completes. Reordering the calls doesn't help — `reset()` must happen for subsequent FAB taps to work, but any state change before navigation causes a flash.

**Potential fixes to investigate:**
1. Use `LaunchedEffect` with a `Saved` state that doesn't render any UI (empty composable)
2. Use `SnapshotMutationPolicy` to batch the reset + navigation
3. Clear the backstack entry before resetting state
4. Use a separate `Flow<NavigationEvent>` instead of state for navigation signals

**Priority:** Low — cosmetic only, doesn't affect functionality
