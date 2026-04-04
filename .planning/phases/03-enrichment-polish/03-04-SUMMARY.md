---
phase: 03-enrichment-polish
plan: 04
subsystem: ui
tags: [card-reversal, ui-wiring, position-slot]
dependencies:
  requires: ["03-03"]
  provides: ["CARD-04: reversed card toggle"]
  affects: ["reading-detail", "add-reading-wizard"]
tech-stack:
  added: []
  patterns: [combinedClickable for double-tap, graphicsLayer rotation, Box alignment for badges]
key-files:
  created: []
  modified:
    - app/src/main/kotlin/com/example/drawn/ui/addreading/PositionSlot.kt
    - app/src/main/kotlin/com/example/drawn/ui/addreading/CardAssignmentStep.kt
    - app/src/main/kotlin/com/example/drawn/ui/addreading/AddReadingState.kt
    - app/src/main/kotlin/com/example/drawn/ui/addreading/AddReadingViewModel.kt
    - app/src/main/kotlin/com/example/drawn/ui/addreading/AddReadingScreen.kt
    - app/src/main/kotlin/com/example/drawn/ui/readingdetail/ReadingDetailScreen.kt
decisions:
  - Used combinedClickable (onDoubleClick) instead of detectTapGestures to avoid conflicts with Card's onClick
  - Reversed state tracked as Set<Int> (position orders) in wizard, isReversed Boolean on ReadingCard in detail
  - Box contentAlignment used instead of horizontalAlignment for proper nested layout
metrics:
  duration: ~5min
  tasks_completed: 2
  files_modified: 6
  completed_date: "2026-04-04"
---

# Phase 03 Plan 04: Reversed Card Toggle Summary

**One-liner:** Reversed/upright card toggle wired to PositionSlot with 180° rotation + gold "R" badge, available in both wizard (CardAssignmentStep) and reading detail (ReadingDetailCardItem).

## Tasks Completed

| Task | Name | Commit | Files |
|------|------|--------|-------|
| 1 | Add reversed toggle to PositionSlot with visual indicators | f5cfc94 | PositionSlot.kt |
| 2 | Wire reversed toggle in CardAssignmentStep and ReadingDetailScreen | 12eb9d6 | CardAssignmentStep.kt, AddReadingState.kt, AddReadingViewModel.kt, AddReadingScreen.kt, ReadingDetailScreen.kt |

## Verification Results

- PositionSlot accepts `isReversed: Boolean = false` and `onToggleReversed: (() -> Unit)? = null` parameters
- Double-tap (combinedClickable onDoubleClick) triggers reversed toggle on assigned cards
- Single-tap still opens card picker via Card onClick
- Card thumbnail rotates 180° via `Modifier.graphicsLayer { rotationZ = if (isReversed) 180f else 0f }`
- Gold "R" badge (16.dp circle, DarkSecondary background, DarkOnSecondary text) shown in top-right corner
- CardAssignmentStep passes `reversedPositions: Set<Int>` and `onToggleReversed: (Int) -> Unit` to PositionSlot
- AddReadingViewModel tracks reversedPositions in state, provides `togglePositionReversed(positionOrder: Int)`
- ReadingDetailCardItem shows rotation + badge for reversed cards based on `readingCard.isReversed`
- `./gradlew :app:compileDebugKotlin` succeeds with no errors

## Deviations from Plan

### Auto-fixed Issues

**1. [Rule 1 - Bug] CombinedClickable instead of detectTapGestures**
- **Found during:** Task 1 compilation
- **Issue:** Using `pointerInput { detectTapGestures }` inside AssignedSlotContent caused "@Composable invocations can only happen from the context of a @Composable function" errors, and conflicted with Card's onClick
- **Fix:** Moved double-tap handling to Card level using `Modifier.combinedClickable(onClick = onTap, onDoubleClick = onToggleReversed)`, which cleanly separates single-tap (card picker) from double-tap (reversed toggle)
- **Files modified:** PositionSlot.kt
- **Commit:** f5cfc94

**2. [Rule 1 - Bug] Box alignment parameter**
- **Found during:** Task 1 compilation
- **Issue:** `Box(horizontalAlignment = ...)` is invalid — Box uses `contentAlignment`, not `horizontalAlignment`
- **Fix:** Changed to `Box(contentAlignment = Alignment.Center, ...)`
- **Files modified:** PositionSlot.kt
- **Commit:** f5cfc94

**3. [Rule 1 - Bug] Missing size import in ReadingDetailScreen**
- **Found during:** Task 2 compilation
- **Issue:** `Modifier.size(16.dp)` used but `size` not imported in ReadingDetailScreen
- **Fix:** Added `import androidx.compose.foundation.layout.size`
- **Files modified:** ReadingDetailScreen.kt
- **Commit:** 12eb9d6

## Known Stubs

None — all reversed functionality is fully wired.

## Self-Check: PASSED

- [x] PositionSlot.kt — 167 lines, contains isReversed, graphicsLayer, rotationZ, DarkSecondary, CircleShape
- [x] CardAssignmentStep.kt — passes reversedPositions and onToggleReversed
- [x] AddReadingState.kt — contains reversedPositions field
- [x] AddReadingViewModel.kt — contains togglePositionReversed function
- [x] AddReadingScreen.kt — passes reversed state to CardAssignmentStep
- [x] ReadingDetailScreen.kt — shows rotation + badge for reversed cards
- [x] All commits verified: f5cfc94, 12eb9d6
- [x] `./gradlew :app:compileDebugKotlin` succeeds
