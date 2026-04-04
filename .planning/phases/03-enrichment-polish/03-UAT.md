---
status: testing
phase: 03-enrichment-polish
source: [03-01-SUMMARY.md, 03-02-SUMMARY.md, 03-03-SUMMARY.md, 03-04-SUMMARY.md, 03-05-SUMMARY.md]
started: 2026-04-03T22:30:00Z
updated: 2026-04-03T22:30:00Z
---

## Current Test

number: 1
name: Dark Theme Foundation
expected: |
  All screens display with consistent dark mystical theme. Typography uses 4-size scale (57/24/16/14). Cards have subtle elevation. Reading list has subtle radial gradient background. All text uses proper color hierarchy (onSurface → onSurfaceVariant → onSurfaceVariant @ 0.7 alpha).
awaiting: user response

## Tests

### 1. Dark Theme Foundation
expected: All screens display with consistent dark mystical theme. Typography uses 4-size scale (57/24/16/14). Cards have subtle elevation. Reading list has subtle radial gradient background. All text uses proper color hierarchy.
result: [pending]

### 2. Search Bar in Reading List
expected: Reading list shows a search bar (either in TopAppBar or below it). Typing filters readings in real-time by title, notes, card names, and date. Clear button resets search. "No readings match your search" shown when no results.
result: [pending]

### 3. Edit Reading
expected: Reading detail screen has an edit (pencil) icon in TopAppBar. Tapping it makes title and notes editable (OutlinedTextField). Save Changes and Discard Changes buttons appear. Saving updates the reading. Discarding restores original values.
result: [pending]

### 4. Delete Reading
expected: Reading detail screen has a delete (trash) icon in TopAppBar. Tapping it shows confirmation dialog: "Delete reading?" with "This will permanently delete this reading and all attached photos. This action cannot be undone." Delete button is error-colored. Cancel button says "Keep Reading". After delete, navigates back to reading list.
result: [pending]

### 5. Reversed Card Toggle
expected: Tapping an assigned card in a position slot toggles between upright and reversed. Reversed cards display rotated 180° (upside-down) with a small gold "R" badge in the top-right corner. Toggle works in both the wizard (Step 2) and reading detail (edit mode).
result: [pending]

### 6. Photo Attachments - Add
expected: Reading detail screen has a "Photos" section with a "+" button. Tapping it shows options: "Take Photo" (camera) and "Choose from Gallery" (Photo Picker). Adding a photo shows it in the thumbnail grid.
result: [pending]

### 7. Photo Attachments - View
expected: Tapping a photo thumbnail opens a full-screen viewer with the photo centered. Close button in top-right. If multiple photos, swipe horizontally to navigate between them.
result: [pending]

### 8. Photo Attachments - Delete
expected: Long-pressing a photo shows "Remove photo?" confirmation dialog. Confirming removes the photo from the reading and the grid updates.
result: [pending]

## Summary

total: 8
passed: 0
issues: 0
pending: 8
skipped: 0

## Gaps

[none yet]
