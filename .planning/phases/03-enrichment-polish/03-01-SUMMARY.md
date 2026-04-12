---
phase: 03-enrichment-polish
plan: 01
subsystem: theme
tags: [theme, typography, colors, elevation]
dependency_graph:
  requires: []
  provides: [THEME-01, THEME-02]
  affects: [all-screens]
tech-stack:
  added: []
  patterns: [M3 color scheme, typography scale, card elevation]
key-files:
  created: []
  modified:
    - app/src/main/kotlin/com/example/drawn/ui/theme/Type.kt
    - app/src/main/kotlin/com/example/drawn/ui/theme/Color.kt
    - app/src/main/kotlin/com/example/drawn/ui/theme/Theme.kt
decisions:
  - "DrawnCardElevation made @Composable function instead of val due to CardDefaults.cardElevation() being @Composable"
  - "All hardcoded color values replaced with named constants from Color.kt"
metrics:
  duration: "5 minutes"
  completed_date: "2026-04-03"
---

# Phase 03 Plan 01: Theme Foundation Summary

**One-liner:** Defined complete dark mystical theme foundation with 7-style typography scale (weights 400/500 only), expanded M3 color scheme to all 25 slots with 24 named color values, and standardized card elevation at 1.dp/3.dp.

## Completed Tasks

| Task | Name | Commit | Files |
|------|------|--------|-------|
| 1 | Define typography scale in Type.kt | a8d5bda | Type.kt |
| 2 | Polish color scheme and add missing M3 color slots | 239d863 | Color.kt |
| 3 | Add card elevation defaults and verify theme compilation | 51a2ff2 | Theme.kt |

## Task Details

### Task 1: Typography Scale
- **File:** `app/src/main/kotlin/com/example/drawn/ui/theme/Type.kt`
- **Changes:** Replaced empty `Typography()` with 7 custom TextStyle definitions
- **Styles defined:** displayLarge (57.sp), headlineSmall (24.sp), titleMedium (16.sp), bodyLarge (16.sp), bodyMedium (14.sp), labelLarge (14.sp/500 weight), labelSmall (11.sp)
- **Weights:** Only 400 (regular) and 500 (medium) — no bold/semibold per UI-SPEC
- **Verification:** `./gradlew :app:compileDebugKotlin` succeeded

### Task 2: Color Scheme Expansion
- **File:** `app/src/main/kotlin/com/example/drawn/ui/theme/Color.kt`
- **Changes:** Added 13 new color values for complete M3 palette
- **New colors:** DarkOutline, DarkOnSurface, DarkOnSurfaceVariant, DarkErrorContainer, DarkOnErrorContainer, DarkPrimaryContainer, DarkOnPrimaryContainer, DarkInverseSurface, DarkInverseOnSurface, DarkInversePrimary, DarkSurfaceTint, DarkOutlineVariant, DarkScrim
- **Total color values:** 24 (11 existing + 13 new)
- **Verification:** `./gradlew :app:compileDebugKotlin` succeeded

### Task 3: Color Scheme Application + Card Elevation
- **File:** `app/src/main/kotlin/com/example/drawn/ui/theme/Theme.kt`
- **Changes:** 
  - Expanded DarkColorScheme to all 25 M3 color slots
  - Replaced hardcoded `Color(0xFFE0E0E0)` and `Color(0xFFB0B0B0)` with named constants
  - Added `DrawnCardElevation()` composable with 1.dp default / 3.dp dragged elevation
- **Deviation:** Made DrawnCardElevation a `@Composable` function instead of a `val` because `CardDefaults.cardElevation()` is marked `@Composable` in this Compose version
- **Verification:** `./gradlew :app:assembleDebug` BUILD SUCCESSFUL

## Deviations from Plan

### Auto-fixed Issues

**1. [Rule 1 - Bug] CardDefaults.cardElevation() is @Composable**
- **Found during:** Task 3
- **Issue:** `CardDefaults.cardElevation()` is marked `@Composable` in the current Compose BOM version, so it cannot be assigned to a top-level `val`
- **Fix:** Changed from `val DrawnCardElevation = CardDefaults.cardElevation(...)` to `@Composable fun DrawnCardElevation() = CardDefaults.cardElevation(...)`
- **Files modified:** Theme.kt
- **Commit:** 51a2ff2

## Verification Results

- ✅ Type.kt exports DrawnTypography with 7 custom TextStyle definitions
- ✅ Only weights 400 and 500 used (no bold/semibold)
- ✅ Color.kt has 24 color values
- ✅ Theme.kt DarkColorScheme includes all 25 M3 color slots using named constants
- ✅ DrawnCardElevation() defined for consistent card shadows
- ✅ Full debug build succeeds (`./gradlew :app:assembleDebug` — BUILD SUCCESSFUL)

## Known Stubs

None — this plan establishes theme infrastructure only, no UI stubs created.

## Self-Check: PASSED

- ✅ Type.kt exists with DrawnTypography definition
- ✅ Color.kt exists with 24 color values
- ✅ Theme.kt exists with complete DarkColorScheme and DrawnCardElevation
- ✅ Commits verified: a8d5bda, 239d863, 51a2ff2
