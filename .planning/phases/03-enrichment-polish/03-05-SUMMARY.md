---
phase: 03-enrichment-polish
plan: 05
subsystem: photo-attachments
tags: [photos, camera, gallery, fileprovider, coil, photo-picker]
dependency_graph:
  requires: ["03-03"]
  provides: ["PHOTO-01", "PHOTO-02", "PHOTO-03", "PHOTO-04"]
  affects: ["ReadingDetailScreen", "ReadingDetailViewModel", "ReadingRepository", "ReadingPhotoDao"]
tech_stack:
  added: ["FileProvider", "ActivityResultContracts.PickVisualMedia", "ActivityResultContracts.TakePicture", "Coil AsyncImage", "LazyHorizontalGrid", "ModalBottomSheet", "Dialog"]
  patterns: ["Photo Picker (no permissions)", "FileProvider for camera URIs", "Flow-based photo observation via existing observeReadingWithDetails"]
key_files:
  created:
    - app/src/main/res/xml/file_paths.xml
  modified:
    - app/src/main/kotlin/com/example/drawn/ui/readingdetail/ReadingDetailScreen.kt
    - app/src/main/kotlin/com/example/drawn/ui/readingdetail/ReadingDetailViewModel.kt
    - app/src/main/kotlin/com/example/drawn/data/repository/ReadingRepository.kt
    - app/src/main/kotlin/com/example/drawn/data/database/dao/ReadingPhotoDao.kt
    - app/src/main/AndroidManifest.xml
decisions:
  - Used combinedClickable (onLongClick) instead of detectLongPressGestures — detectLongPressGestures was not available in the Compose Foundation version; combinedClickable is the idiomatic Compose way to handle long-press on clickable elements
  - Used androidx.compose.ui.window.Dialog instead of material3.Dialog — material3.Dialog doesn't exist; Dialog is in ui.window package
  - Photo section always visible (not gated by edit mode) — photos can be added/viewed at any time, not just during editing
  - Camera uses cache-path FileProvider — avoids external storage permissions, files cleaned up with app cache
metrics:
  duration: ~15 minutes
  tasks_completed: 2
  files_created: 1
  files_modified: 5
  completed_date: "2026-04-04"
---

# Phase 03 Plan 05: Photo Attachments Summary

**One-liner:** Photo attachment system with camera/gallery capture, LazyHorizontalGrid thumbnail display, full-screen viewer Dialog, and long-press delete — all wired through existing ReadingPhotoDao Flow observation so UI updates automatically.

## Tasks Completed

| Task | Name | Commit | Key Files |
|------|------|--------|-----------|
| 1 | Add photo CRUD to repository and ViewModel | `5cf9191` | ReadingPhotoDao.kt, ReadingRepository.kt, ReadingDetailViewModel.kt |
| 2 | Add photo grid, picker, viewer, and delete to ReadingDetailScreen | `2f191d0` | ReadingDetailScreen.kt, AndroidManifest.xml, file_paths.xml |

## What Was Built

### Task 1: Photo CRUD Layer
- **ReadingPhotoDao**: Added `getPhotoById(photoId: Long)` suspend query
- **ReadingRepository**: Added `addPhotoToReading(readingId, photoUri)` and `removePhotoFromReading(photoId)` suspend functions
- **ReadingDetailViewModel**: Added `addPhoto(photoUri: String)` and `deletePhoto(photoId: Long)` methods using viewModelScope.launch
- UI auto-updates because `observeReadingWithDetails` already combines `readingPhotoDao.observePhotosForReading(id)` — adding/deleting photos triggers Flow emission

### Task 2: Photo UI
- **Photo grid**: `LazyHorizontalGrid` with 80.dp thumbnails, 8.dp spacing, "+" button as first item
- **Add photo flow**: `ModalBottomSheet` with "Take Photo" (camera) and "Choose from Gallery" (Photo Picker) options
- **Camera**: `ActivityResultContracts.TakePicture()` with FileProvider-generated cache URI
- **Gallery**: `ActivityResultContracts.PickVisualMedia(ImageOnly)` — no storage permissions needed
- **Full-screen viewer**: `Dialog` with black background, `AsyncImage` (Coil) with `ContentScale.Fit`, close button
- **Delete**: Long-press photo shows `AlertDialog` confirmation, calls `viewModel.deletePhoto(photo.id)`
- **Empty state**: "Tap + to add photos" hint when no photos exist
- **FileProvider**: Added `file_paths.xml` (cache-path) and manifest `<provider>` entry

## Deviations from Plan

### Auto-fixed Issues

**1. [Rule 1 - Bug] Fixed Dialog import path**
- **Found during:** Task 2 compilation
- **Issue:** Plan referenced `androidx.compose.material3.Dialog` which doesn't exist — Dialog is in `androidx.compose.ui.window.Dialog`
- **Fix:** Changed import to `androidx.compose.ui.window.Dialog`
- **Files modified:** ReadingDetailScreen.kt
- **Commit:** 2f191d0

**2. [Rule 1 - Bug] Fixed long-press gesture detection**
- **Found during:** Task 2 compilation
- **Issue:** `detectLongPressGestures` was not resolvable in the Compose Foundation version available
- **Fix:** Used `combinedClickable` with `onLongClick` parameter instead — already imported and idiomatic for clickable elements with secondary actions
- **Files modified:** ReadingDetailScreen.kt
- **Commit:** 2f191d0

**3. [Rule 1 - Bug] Fixed cameraPhotoUri smart cast error**
- **Found during:** Task 2 compilation
- **Issue:** Kotlin can't smart cast delegated properties (`cameraPhotoUri` via `mutableStateOf`) — `cameraLauncher.launch(cameraPhotoUri)` failed compilation
- **Fix:** Created local `val uri = createTempImageUri()` before assignment and launch
- **Files modified:** ReadingDetailScreen.kt
- **Commit:** 2f191d0

**4. [Rule 2 - Missing] Removed unused imports**
- **Found during:** Task 2 cleanup
- **Issue:** `android.content.Context` and `pointerInput` imports were unused after refactoring
- **Fix:** Removed both unused imports
- **Files modified:** ReadingDetailScreen.kt
- **Commit:** 2f191d0

## Requirements Met

- **PHOTO-01:** ✅ User can attach photos from device gallery to a reading (Photo Picker)
- **PHOTO-02:** ✅ User can capture photos with camera and attach to a reading (TakePicture + FileProvider)
- **PHOTO-03:** ✅ User can view attached photos in full-screen within reading details (Dialog viewer)
- **PHOTO-04:** ✅ User can remove attached photos from a reading (long-press + confirmation dialog)

## Verification

- `./gradlew :app:compileDebugKotlin` — ✅ passes
- `./gradlew :app:ktlintCheck` (main source set) — ✅ passes
- `./gradlew :app:assembleDebug` — ✅ full build succeeds

## Known Stubs

None — all photo functionality is fully wired.

## Self-Check: PASSED

- [x] ReadingPhotoDao.kt has getPhotoById method
- [x] ReadingRepository.kt has addPhotoToReading and removePhotoFromReading
- [x] ReadingDetailViewModel.kt has addPhoto and deletePhoto
- [x] ReadingDetailScreen.kt has photo grid, bottom sheet, viewer, delete dialog
- [x] file_paths.xml created in res/xml/
- [x] AndroidManifest.xml has FileProvider entry
- [x] Both commits exist: 5cf9191, 2f191d0
- [x] Full debug build succeeds
