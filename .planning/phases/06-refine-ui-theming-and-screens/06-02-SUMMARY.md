# 06-02: Compact reading list cards with metadata + fade-in

**Status:** Complete
**Started:** 2026-04-05
**Completed:** 2026-04-05

## Objective

Compact reading list cards with metadata (date + spread name) and fade-in loading animation.

## What Was Built

**Data layer additions:**
- `ReadingWithSpread` data class — lightweight model with `spreadName` field from JOIN query
- `ReadingDao.observeAllReadingsWithSpread()` — new JOIN query: `readings INNER JOIN spreads`
- `ReadingRepository.observeAllReadingsWithSpread()` — new method exposing the JOIN flow
- `ReadingListViewModel` — switched from `observeAllReadings()` to `observeAllReadingsWithSpread()`
- Search now also matches against `spreadName` (previously only title + notes)

**UI updates (ReadingListItem):**
- **Row 1:** Title (titleMedium, bold) + formatted date (labelSmall, onSurfaceVariant, right-aligned) using "MMM d, yyyy" pattern
- **Row 2:** Spread name (labelMedium, gold/DarkSecondary color)
- **Row 3:** Notes preview (bodyMedium, onSurfaceVariant, maxLines=2, TextOverflow.Ellipsis)
- Compact padding: 12.dp horizontal (reduced from 16.dp), 4.dp vertical on Card, 2.dp between rows
- `AnimatedVisibility` with `fadeIn(300ms)` wraps the ReadingList

## Key Files Created/Modified

| File | Action | Purpose |
|------|--------|---------|
| `data/database/entity/ReadingEntity.kt` | Modified | Added `ReadingWithSpread` data class |
| `data/database/dao/ReadingDao.kt` | Modified | Added `observeAllReadingsWithSpread()` JOIN query |
| `data/repository/ReadingRepository.kt` | Modified | Added `observeAllReadingsWithSpread()` method |
| `ui/readinglist/ReadingListViewModel.kt` | Modified | Switched to `ReadingWithSpread`, search includes spreadName |
| `ui/readinglist/ReadingListScreen.kt` | Modified | Updated ReadingListItem with metadata + fade-in |

## Verification

- `./gradlew :app:compileDebugKotlin` — BUILD SUCCESSFUL
- ReadingListItem shows title + date on same row (date right-aligned)
- Spread name displayed in gold color below title
- Notes truncated to 2 lines with ellipsis
- Card uses 12.dp horizontal padding (compact)
- AnimatedVisibility with fadeIn wraps reading list

## Deviations

None.

## Self-Check: PASSED
