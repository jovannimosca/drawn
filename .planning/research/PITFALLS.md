# Domain Pitfalls: Android Tarot Card Reading App

**Domain:** Local-only Android app with bundled assets and photo attachments
**Researched:** 2026-04-03

## Critical Pitfalls

Mistakes that cause rewrites, data loss, or major user-facing issues.

### Pitfall 1: Room Database Migration Corruption
**What goes wrong:** Adding fields to embedded objects in Room and running migrations corrupts existing user data. When you add a non-null field to an `@Embedded` class and populate it with default values for ALL rows, Room interprets any non-null field as meaning the entire embedded object should be instantiated — converting null sibling fields to their Kotlin defaults (0, false, null). This silently corrupts data for users who never used the feature.

**Why it happens:** Room flattens embedded objects into columns but has no concept of the Kotlin object structure. If ANY column of an embedded object is non-null, Room instantiates the whole object, using Kotlin defaults for the remaining null columns.

**Consequences:** User data silently corrupted — settings reset to defaults, readings lost, photos orphaned. Real post-mortem from a solo dev who lost trust with 26 users in a single rollout. ([Unrushed Apps post-mortem](https://unrushedapps.com/blog/post-mortem-database-migration-error))

**Prevention:**
- Only update migration columns for rows where the parent embedded object was already non-null (check a sibling column)
- Use `fallbackToDestructiveMigration()` ONLY for pre-release apps — never in production
- Write and test migrations with Room's `MigrationTestHelper` before shipping
- Use staged rollouts and halt capability on Play Console to limit blast radius
- Consider making feature-gated data optional at the model level, not just the DB level

**Detection:** Add data integrity checks on app launch after migration. Log warnings if expected relationships are broken.

### Pitfall 2: LaunchedEffect Self-Cancellation in Compose
**What goes wrong:** A `LaunchedEffect` with a state key gets cancelled when that same state changes inside the effect body. This is the most common Compose bug that ships to production.

**Why it happens:** Changing a `LaunchedEffect` key schedules recomposition. The old coroutine is cancelled at the next suspension point (like `delay()`). If you mutate the key state inside the effect, you cancel yourself.

**Consequences:** OTP verification never completes, card readings never save, animations freeze mid-flight. Users see infinite spinners. ([Adit Lal, Droidcon India 2025](https://aditlal.dev/compose-bottleneck-antipatterns-performance/))

**Prevention:**
- Use `LaunchedEffect(Unit)` with `snapshotFlow {}` to observe state changes without restarting the effect
- Never mutate a LaunchedEffect key inside its own body
- If you must use a state key, ensure the effect completes before the key changes

```kotlin
// WRONG — self-cancels
LaunchedEffect(wasAutoFilled) {
    if (wasAutoFilled) {
        wasAutoFilled = false  // Key changes → effect cancelled
        delay(300)             // Never reaches here
        saveReading()
    }
}

// RIGHT — snapshotFlow decouples observation from lifecycle
LaunchedEffect(Unit) {
    snapshotFlow { wasAutoFilled }
        .filter { it }
        .collect {
            wasAutoFilled = false  // Safe — just emits to flow
            delay(300)
            saveReading()          // Actually executes
        }
}
```

### Pitfall 3: Mutable Collection Mutation Without Recomposition
**What goes wrong:** Mutating a `mutableListOf` inside `mutableStateOf` doesn't trigger recomposition because Compose uses reference equality.

**Why it happens:** `cartItems.add(item)` mutates the same list object. Compose sees the same reference and skips recomposition.

**Consequences:** Card readings added to a list don't appear in the UI. The database has the data but the screen shows nothing.

**Prevention:**
- Use `mutableStateListOf<T>()` for observable lists
- Or create new list references: `items = items + newItem`
- Never mutate collections held in `mutableStateOf`

### Pitfall 4: Events Treated as State (Duplicate Snackbars/Dialogs)
**What goes wrong:** Using `mutableStateOf<String?>` for one-time events (errors, confirmations) causes them to re-fire on configuration changes.

**Why it happens:** ViewModel survives configuration change. The state value persists. When the composable recreates, `LaunchedEffect` sees the non-null value and fires again.

**Consequences:** Error dialogs show repeatedly on rotation. "Card saved" confirmation appears multiple times. User thinks something is broken.

**Prevention:**
- Use `Channel<UiEvent>` for one-time events
- Consume events via `Flow` collection in `LaunchedEffect(Unit)`
- Never use `mutableStateOf` for events that should fire once

### Pitfall 5: Bundled Asset Images Causing OOM
**What goes wrong:** Loading full-resolution tarot card images from `assets/` or `res/drawable` directly into memory causes `OutOfMemoryError`. A typical Rider-Waite tarot deck has 78 cards — if each image is 1-2MB, loading even a few simultaneously can exhaust the heap.

**Why it happens:** Android decodes bitmaps at their native resolution. A 2000x3000px image at ARGB_8888 uses ~24MB of RAM. Three cards = 72MB on a 256MB heap.

**Consequences:** App crashes on card reveal, especially on low-end devices or when flipping between cards rapidly.

**Prevention:**
- Use Coil 3.x for image loading — it handles downsampling, caching, and memory management automatically
- Pre-scale bundled images to display size during build, not at runtime
- Use `res/drawable-nodpi/` for assets that shouldn't be density-scaled (tarot cards are typically the same physical size regardless of screen density)
- Consider WebP format for bundled card images — 25-34% smaller than PNG with same quality
- For card flip animations, preload the next card's image before the flip starts

### Pitfall 6: ACTION_IMAGE_CAPTURE Inconsistency
**What goes wrong:** Relying on `ACTION_IMAGE_CAPTURE` intent to take photos produces inconsistent results across camera apps — different orientations, mirroring, thumbnail sizes, and file handling.

**Why it happens:** The Android contract for `ACTION_IMAGE_CAPTURE` is underspecified. Camera app implementations vary wildly in how they handle EXIF rotation, front-camera mirroring, `EXTRA_OUTPUT` URIs, and thumbnail sizing. (CommonsWare called this "The ACTION_IMAGE_CAPTURE Fallacy" — it's been a known issue since 2015 and remains unresolved.)

**Consequences:** User's attached photo appears rotated 90°, mirrored, or as a tiny thumbnail. EXIF orientation header is ignored by `BitmapFactory`.

**Prevention:**
- Use Android Photo Picker (`PickVisualMedia`) for selecting existing photos — it returns a content URI with proper permissions
- For camera capture, use `CameraX` library for consistent behavior instead of `ACTION_IMAGE_CAPTURE`
- Always read and apply EXIF orientation when loading captured images
- Use `ActivityResultContracts.TakePicture()` with a pre-created `content://` URI via `FileProvider`
- Test on multiple OEM camera apps (Samsung, Google, Xiaomi handle this differently)

### Pitfall 7: APK Size Bloat from Unoptimized Assets
**What goes wrong:** Bundling 78 tarot card images at full resolution makes the APK 50-100MB+, causing users to skip downloading, especially in emerging markets.

**Why it happens:** Developers bundle images at print resolution when screen resolution is sufficient. No resource shrinking configured.

**Consequences:** Low install conversion rate. Google Play may flag the app as unusually large for its category.

**Prevention:**
- Use Android App Bundle (`.aab`) — Play Store delivers only resources needed for each device configuration
- Enable resource shrinking: `isMinifyEnabled = true` and `isShrinkResources = true` in release build
- Use WebP format for card images (lossless or lossy at 80-90% quality)
- Target screen-appropriate resolutions — 1080x1620 is sufficient for even the largest phone screens
- Consider shipping a base set of cards and downloading additional decks on demand (but this contradicts local-only requirement)
- Use `res/raw/` for the pre-populated Room database instead of `assets/` — Room's `createFromAsset()` handles it natively

## Moderate Pitfalls

### Pitfall 8: State Read Too High in Compose Tree
**What goes wrong:** Reading scroll state or animation state at the screen level causes the entire screen to recompose on every frame during scroll.

**Why it happens:** Compose recomposes any composable that reads changed state. Reading `scrollState.firstVisibleItemIndex` at the screen scope means every child recomposes on every scroll event.

**Consequences:** Janky scrolling, dropped frames, battery drain. Card list feels sluggish.

**Prevention:**
- Push state reads as low as possible in the tree
- Use `derivedStateOf {}` to reduce recomposition frequency
- Wrap scroll-aware UI in its own composable that reads the state

### Pitfall 9: Flip Animation Z-Order and Click Targeting
**What goes wrong:** In a two-view flip card layout, the "hidden" back view still intercepts touch events even when fully transparent (alpha = 0).

**Why it happens:** Alpha visibility doesn't affect touch handling. The back view sits on top in the view hierarchy and captures all taps.

**Consequences:** User taps the visible front card but nothing happens — the invisible back view consumed the touch.

**Prevention:**
- Toggle `isClickable` and `isFocusable` based on which side is visible
- Call `bringToFront()` on the visible view after flip completes
- Or use a single-view flip with content swap at 90° rotation

### Pitfall 10: Configuration Change Resets Animation State
**What goes wrong:** Device rotation or multi-window mode resets card flip state to default, leaving cards half-flipped or showing the wrong side.

**Why it happens:** Activity recreation destroys view state. Animation state (`isFrontShowing`, current rotation) is lost.

**Consequences:** Card appears half-rotated after rotation. User loses their place in a reading.

**Prevention:**
- Save flip state in `onSaveInstanceState` / `rememberSaveable`
- Restore initial state in `onCreate` by setting alpha and rotation explicitly
- For Compose, use `rememberSaveable { mutableStateOf(isFrontShowing) }`

### Pitfall 11: Pre-populated Database Overwrites User Data
**What goes wrong:** Using `createFromAsset()` or copying a database from assets on every app launch overwrites user-saved readings and photo attachments.

**Why it happens:** Developer copies the pre-packaged database on each `onCreate` instead of only on first launch, or doesn't check if the database already exists.

**Consequences:** User's reading history, saved cards, and attached photos are wiped on every app restart.

**Prevention:**
- Use Room's `createFromAsset("database.db")` — it only copies on first launch
- Or check `File(databasePath).exists()` before copying from assets
- Separate the pre-populated data (tarot card meanings) from user data (readings, photos) into different tables or databases

### Pitfall 12: RecyclerView/ LazyList Half-Flipped Cards
**What goes wrong:** In a scrollable list of card readings, recycled ViewHolders or Compose items show half-flipped cards from previous items.

**Why it happens:** View recycling reuses the view in whatever animation state it was left in. If a card was mid-flip when scrolled off-screen, it appears half-rotated when recycled for a new item.

**Consequences:** Card list shows garbled, partially-rotated cards. Looks broken.

**Prevention:**
- Always reset alpha and rotation to known values in `onBindViewHolder` or Compose item
- Cancel ongoing animations when view is recycled
- Store flip state in the data model, not in the view

### Pitfall 13: Memory Leaks from Bitmap References
**What goes wrong:** Holding `Bitmap` references in long-lived objects (ViewModel, singleton, static fields) prevents garbage collection.

**Why it happens:** Bitmaps are large objects. A single tarot card bitmap can be 10-24MB. Holding references means the GC can't reclaim them.

**Consequences:** Gradual memory growth until OOM crash. App works fine for 10 minutes then crashes.

**Prevention:**
- Never hold Bitmap references in ViewModel or singleton
- Use Coil's memory cache — it uses LRU eviction with proper size limits
- Call `bitmap.recycle()` only if manually managing bitmaps (Coil handles this)
- Use LeakCanary in debug builds to catch leaks early

## Minor Pitfalls

### Pitfall 14: `largeHeap="true"` as a Fix
**What goes wrong:** Adding `android:largeHeap="true"` to the manifest to avoid OOM errors.

**Why it happens:** It's the first "fix" found online for OOM. It requests a larger heap from the system but doesn't address the root cause.

**Consequences:** App uses more memory than necessary, gets killed more aggressively by the system, and still OOMs on low-end devices.

**Prevention:** Fix the actual memory issue (downsample images, use proper caching) instead of requesting more heap.

### Pitfall 15: Missing Accessibility on Card Flip
**What goes wrong:** Screen readers announce the wrong side of the card after a flip, or don't announce the flip at all.

**Why it happens:** `contentDescription` isn't updated when the card flips. Both views have static descriptions.

**Consequences:** Visually impaired users get confused about which card side they're viewing.

**Prevention:**
- Update `contentDescription` on the visible side after each flip
- Use `announceForAccessibility()` for automatic flips
- Set `importantForAccessibility="yes"` on both sides

### Pitfall 16: Data Loss on App Uninstall
**What goes wrong:** Users uninstall and reinstall the app, losing all reading history and photo attachments.

**Why it happens:** Local-only apps store everything in app-private storage, which is wiped on uninstall.

**Consequences:** User loses their entire reading history. Negative reviews.

**Prevention:**
- This is inherent to local-only design — make it clear to users that data is device-local
- Consider offering export functionality (JSON/CSV export of readings)
- Use Android's auto-backup framework (`android:allowBackup="true"`) to preserve data across reinstalls on the same device

## Phase-Specific Warnings

| Phase Topic | Likely Pitfall | Mitigation |
|-------------|---------------|------------|
| **Bundling tarot card images** | APK bloat, OOM on load | WebP format, pre-scale to screen resolution, use Coil |
| **Pre-populating card meanings DB** | Overwriting user data on launch | Use Room `createFromAsset()`, separate card data from user data |
| **Card flip animation** | Z-order issues, half-flip on rotation | Toggle `isClickable`, save state in `rememberSaveable` |
| **Photo attachment from camera** | Rotated/mirrored images, URI permission loss | Use CameraX or Photo Picker, read EXIF orientation |
| **Saving readings to Room** | Migration corruption on schema change | Write migrations with tests, only update non-null parent rows |
| **Card reading history list** | Recomposition jank, recycled view corruption | Push state reads low, reset view state in onBind |
| **Compose state management** | Self-cancelling effects, duplicate events | Use `snapshotFlow`, Channels for one-time events |

## Sources

- [Room Database Migration Post-Mortem — Unrushed Apps](https://unrushedapps.com/blog/post-mortem-database-migration-error) (HIGH — real production incident)
- [Compose Performance Anti-Patterns — Adit Lal, Droidcon India 2025](https://aditlal.dev/compose-bottleneck-antipatterns-performance/) (HIGH — production experience)
- [The ACTION_IMAGE_CAPTURE Fallacy — CommonsWare](https://commonsware.com/blog/2015/06/08/action-image-capture-fallacy.html) (HIGH — authoritative, still relevant in 2026)
- [Flip Card Animation in Android — TheLinuxCode](https://thelinuxcode.com/flip-card-animation-in-android-kotlin-property-animations-and-production-ready-patterns/) (MEDIUM — comprehensive guide)
- [Android Storage in 2026 — TheLinuxCode](https://thelinuxcode.com/picking-the-right-android-storage-in-2026-internal-files-shared-storage-preferences-and-databases-with-real-kotlin-examples/) (MEDIUM)
- [Android Developers: Prepopulate Room database](https://developer.android.com/training/data-storage/room/prepopulate) (HIGH — official docs)
- [Android Developers: Migrate Room database](https://developer.android.com/training/data-storage/room/migrating-db-versions) (HIGH — official docs)
- [Android Developers: Compose Best Practices](https://developer.android.com/develop/ui/compose/performance/bestpractices) (HIGH — official docs)
- [Coil Performance Optimizations PR](https://github.com/coil-kt/coil/pull/2795) (HIGH — library source)
- [Bitmap Memory Management — MoldStud](https://moldstud.com/articles/p-effective-bitmap-memory-management-in-android-prevent-outofmemory-errors) (MEDIUM)
- [Android largeHeap discussion — LinkedIn](https://www.linkedin.com/posts/amrutha-k-l-a1b37088_large-heaps-in-android-am-i-fixing-the-activity-7429957170177409025-FJ8P) (LOW — social media)
