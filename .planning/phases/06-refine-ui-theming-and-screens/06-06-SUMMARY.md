# 06-06: Spread-accurate card layout (Celtic Cross cross+staff)

**Status:** Complete
**Started:** 2026-04-05
**Completed:** 2026-04-05

## Objective

Implement spread-accurate card layout in ReadingDetailScreen — Celtic Cross shows cross + staff geometry, simple spreads show row layout.

## What Was Built

**SpreadAccurateLayout composable:**
- Signature: `SpreadAccurateLayout(spreadName, cards, cardContent, modifier)`
- `cardByPosition(order: Int)` helper to find cards by position order
- Layout routing by spread name:
  - **"Celtic Cross"** → CrossAndStaffLayout
  - **"Three Card"**, **"Past/Present/Future"** → RowLayout
  - **Anything else** → FallbackGridLayout (3-column chunked grid)

**CrossAndStaffLayout:**
- Left side: Cross shape with nested Rows/Columns
  - Position 5 (Crown) at top
  - Position 4 (Left), Position 1 (Center), Position 6 (Right) in middle row
  - Position 3 (Foundation) at bottom
  - Position 2 (Crossing) overlays Position 1 with 90° rotation and slight offset
- Right side: Staff column with "Advice" header
  - Positions 7, 8, 9, 10 stacked vertically

**RowLayout:**
- Single Row with `Arrangement.spacedBy(12.dp)`
- Cards sorted by positionOrder

**FallbackGridLayout:**
- Same 3-column chunked grid as before
- Used for unrecognized spread names

**ReadingDetailScreen migration:**
- Replaced `cards.chunked(columns).forEach` with `SpreadAccurateLayout`
- `cardContent` lambda wraps `ReadingDetailCardItem`

## Key Files Created/Modified

| File | Action | Purpose |
|------|--------|---------|
| `ui/readingdetail/SpreadAccurateLayout.kt` | Created | Spread-accurate layout with Celtic Cross cross+staff, row, and fallback |
| `ui/readingdetail/ReadingDetailScreen.kt` | Modified | Replaced flat grid with SpreadAccurateLayout |

## Verification

- `./gradlew :app:compileDebugKotlin` — BUILD SUCCESSFUL
- SpreadAccurateLayout exports correct signature with `cardContent` lambda
- `when (spreadName)` routes to correct layout
- Celtic Cross renders cross shape + staff column
- Three Card and Past/Present/Future use horizontal row
- Unknown spreads fall back to 3-column grid
- `cardByPosition` helper present

## Deviations

None.

## Self-Check: PASSED
