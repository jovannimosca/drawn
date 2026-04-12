# Quick Task: 260405-jyv

**Date:** 2026-04-05
**Description:** Uniform card height in Celtic Cross layout — cards were varying in height because each card measured its natural height based on name text length. Fixed by calculating a uniform card height from the image aspect ratio (2:3) + text reserve (36dp), then constraining all measurables to that height.

## Changes

| File | Change |
|------|--------|
| `SpreadAccurateLayout.kt` | Added `uniformCardHeight` calculation, replaced `actualCardHeight` in all placement logic |

## Verification

- `./gradlew :app:compileDebugKotlin` — BUILD SUCCESSFUL
