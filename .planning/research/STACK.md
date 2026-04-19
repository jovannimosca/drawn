# Technology Stack

**Project:** Drawn — Android Tarot Card Reading App
**Researched:** 2026-04-18
**For Milestone:** v1.1 — Custom Card Decks, Reading Tags, Backup/Restore

## Recommended Stack

### Core Platform
| Technology | Version | Purpose | Why | Confidence |
|------------|---------|---------|-----|------------|
| Kotlin | 2.2.21 | Language | Latest stable with mature tooling, full Compose compiler support. Kotlin 2.3.x exists (March 2026) but 2.2.x has broader library compatibility for a new project. | HIGH |
| Android Gradle Plugin | 9.1.0 | Build system | Latest stable (March 2026). Supports API level 36.1, includes Compose compiler plugin built-in for Kotlin 2.0+. | HIGH |
| minSdk | 26 (Android 8.0) | Minimum Android version | Covers ~95% of active devices. Required for Photo Picker compatibility and modern APIs. | MEDIUM |
| targetSdk | 36 | Target Android version | Latest API level. Required for Play Store / F-Droid compliance. | HIGH |

### UI Layer
| Technology | Version | Purpose | Why | Confidence |
|------------|---------|---------|-----|------------|
| Jetpack Compose BOM | 2025.12.00 (Compose 1.10.x) | UI framework BOM | December 2025 stable release. Use the BOM to manage all Compose artifact versions consistently — prevents version conflicts between Compose modules. | HIGH |
| Material 3 | 1.4.0 | Design system | Stable since Sep 2025. Supports Material You dynamic color, dark theme out of the box — aligns with the mystical/dark aesthetic without custom theming overhead. | HIGH |
| Compose Foundation | (via BOM) | Core Compose primitives | Layout, input, and drawing primitives. Required for custom card layouts and gesture handling. | HIGH |
| Compose Animation | (via BOM) | Animations | For card flip animations, spread layout transitions, and UI polish. Native Compose animations are performant and integrate seamlessly with the composition. | HIGH |

### Navigation
| Technology | Version | Purpose | Why | Confidence |
|------------|---------|---------|-----|------------|
| Navigation Compose 3 | 1.0.1 | Type-safe navigation | Stable since Nov 2025. Built specifically for Compose with type-safe route definitions using `@Serializable` Kotlin types — eliminates string-based route bugs and provides compile-time safety for navigation arguments. Replaces the older `androidx.navigation:navigation-compose` (v2.x). | HIGH |

### Data Layer
| Technology | Version | Purpose | Why | Confidence |
|------------|---------|---------|-----|------------|
| Room | 2.8.4 | Local SQLite database | Latest stable (Nov 2025). Kotlin-first API with suspend functions and Flow support — integrates naturally with Compose's reactive model. Room 3.0 is alpha (March 2026, KMP-focused) — too early for production. | HIGH |
| Room Compiler (KSP) | 2.8.4 | Annotation processing | Use KSP instead of kapt — 2x faster compilation, Google's recommended path. KSP is the future-proof choice as kapt is deprecated. | HIGH |
| Kotlinx Serialization | 1.10.0 | Data serialization | **Updated from 1.8.0** — Required for backup/restore JSON export. Version 1.10.0 (Jan 2026) uses Kotlin 2.3.0 but compatible with Kotlin 2.2.x via version 1.9.0. Use 1.9.0 for Kotlin 2.2.x. | HIGH |

### Dependency Injection
| Technology | Version | Purpose | Why | Confidence |
|------------|---------|---------|-----|------------|
| Hilt | 2.59.2 | Dependency injection | Google's official DI for Android. Compile-time safety catches injection errors at build time, not runtime. First-class Compose integration (`@HiltViewModel`, `hiltViewModel()`), ViewModel support. Koin is lighter but Hilt's compile-time guarantees and official Compose/ViewModel integration make it the right choice for a maintainable codebase with 80% test coverage requirements. | HIGH |
| Hilt Navigation Compose | 1.2.0 | Hilt + Navigation integration | Bridges Hilt ViewModel injection with Navigation Compose 3. Required for injecting ViewModels into Compose destinations. | HIGH |

### Image Loading
| Technology | Version | Purpose | Why | Confidence |
|------------|---------|---------|-----|------------|
| Coil 3 | 3.4.0 | Image loading and display | Latest stable (Feb 2026). Full Compose Multiplatform support, `AsyncImage` composable, memory/disk caching. Handles bundled drawable resources, camera photos, and gallery images with a single unified API — no need for separate image loading strategies. | HIGH |
| Coil Compose | 3.4.0 | Compose integration | `AsyncImage` and `rememberAsyncImagePainter` for Compose UI. Native Compose integration means no View interoperability overhead. | HIGH |

### Photo Capture & Selection
| Technology | Version | Purpose | Why | Confidence |
|------------|---------|---------|-----|------------|
| Android Photo Picker (system) | Platform API | Gallery photo selection | System-level photo picker — no storage permissions needed, privacy-friendly. Works on Android 13+ natively, backward compatible via Google Play Services. Eliminates the need for READ_EXTERNAL_STORAGE permission. | HIGH |
| EmbeddedPhotoPicker | androidx.photopicker:photopicker-compose | In-app photo picker | Embedded version renders inside your Compose UI hierarchy (SurfaceView-based). Use if you want the picker inline rather than full-screen — better UX for attaching photos to readings without leaving the reading screen. | MEDIUM |
| MediaStore + Camera Intent | Platform API | Camera capture | Standard `ACTION_IMAGE_CAPTURE` intent for camera photos. No custom camera implementation needed — keeps scope focused and avoids maintaining camera code. | HIGH |

### Architecture & State
| Technology | Version | Purpose | Why | Confidence |
|------------|---------|---------|-----|------------|
| ViewModel | 2.8.7 (via lifecycle) | UI state holder | Survives configuration changes, integrates with Hilt and Compose. Standard Android architecture component for holding UI state. | HIGH |
| Kotlinx Coroutines | 1.10.1 | Async operations | Structured concurrency for database operations, image processing. Kotlin's official async library with Flow integration. | HIGH |
| Kotlinx Flow | (via coroutines) | Reactive streams | Room returns `Flow<List<T>>` natively. Compose collects via `collectAsStateWithLifecycle()`. Unidirectional data flow pattern. | HIGH |
| Lifecycle Runtime Compose | 2.8.7 | Lifecycle-aware composition | `collectAsStateWithLifecycle()` — only collects flows when the Compose UI is visible, preventing wasted work and memory leaks. | HIGH |

### Testing
| Technology | Version | Purpose | Why | Confidence |
|------------|---------|---------|-----|------------|
| JUnit 5 | 5.11.4 | Unit test framework | Modern testing with parameterized tests, nested tests, and better assertions. Required for 80% coverage mandate. JUnit 4 is legacy. | HIGH |
| MockK | 1.13.16 | Mocking library | Kotlin-native mocking — handles coroutines, sealed classes, and objects cleanly. Superior to Mockito for Kotlin codebases because it doesn't require workarounds for Kotlin-specific features. | HIGH |
| Turbine | 1.2.0 | Flow testing | Purpose-built for testing Kotlin Flows. Simple `test { ... }` API for verifying stream emissions. Essential for testing Room DAO flows and ViewModel state streams. | HIGH |
| Compose UI Test | (via BOM) | Compose UI testing | `createComposeRule()` for semantic tree assertions on Compose UI. Official Compose testing library. | HIGH |
| Room Testing | 2.8.4 (in-memory) | Database testing | Room supports in-memory databases for fast, isolated DAO tests. No need for real SQLite files during unit tests. | HIGH |
| Robolectric | 4.14.1 | JVM-based Android tests | Run Android framework tests on JVM without emulator — faster CI feedback. Essential for testing Android-specific code without device/emulator overhead. | MEDIUM |

### Build & Quality
| Technology | Version | Purpose | Why | Confidence |
|------------|---------|---------|-----|------------|
| KSP | 2.2.21-2.0.0 | Annotation processing | Replaces kapt for Room and Hilt. Significantly faster builds. KSP is the future-proof choice as kapt is deprecated. | HIGH |
| Ktlint | 1.5.0+ | Code formatting | Kotlin idiomatic style enforcement. Enforce in CI to maintain consistent code style across contributors. | MEDIUM |
| Detekt | 1.23.7+ | Static analysis | Kotlin-specific lint rules — catches complexity, style, and potential bugs. Complements Ktlint with deeper code quality checks. | MEDIUM |

---

## v1.1 Feature Additions

### Custom Card Deck Editor

**What's needed:**

| Technology | Already in Stack | New Requirement | Notes |
|------------|----------------|---------------|----------|
| Room Entities | Yes (Room 2.8.4) | New entities | Create `Deck`, `DeckCard`, `CardKeyword`, `CardCategory` tables |
| Image storage | Yes (Coil 3 + Photo Picker) | No new library | Reuse existing system Photo Picker for custom card images |
| Image display | Yes (Coil 3) | No new library | Reuse Coil for loading custom deck card images |
| File storage | Android internal storage | No new library | Store custom card images in app-specific directory |

**No new dependencies required.** The existing stack already supports:
- Room for deck/card/keyword/category entities
- System Photo Picker for selecting custom card images
- Coil for loading and displaying images
- Internal app storage for custom image files

**Database entities needed:**

```
Deck (id, name, description, coverImagePath, isBuiltIn, createdAt, updatedAt)
DeckCard (id, deckId, name, description, uprightMeaning, reversedMeaning, imagePath, sortOrder)
CardKeyword (id, name)
CardCategory (id, name)
DeckCardKeyword (deckCardId, keywordId) — junction
DeckCardCategory (deckCardId, categoryId) — junction
```

---

### Reading Tags (Many-to-Many)

**What's needed:**

| Technology | Already in Stack | New Requirement | Notes |
|------------|----------------|---------------|----------|
| Room Junction | Yes (Room 2.8.4) | Use @Junction | Many-to-many via junction table |
| Tag entity | Yes (Room) | New entity | Create `Tag` table |
| ReadingTagCrossRef | Yes (Room) | New entity | Junction table for Reading ↔ Tag |

**No new dependencies required.** Room 2.8.4 already supports `@Junction` annotation for many-to-many relationships (see official docs).

**Database entities needed:**

```
Tag (id, name, color, createdAt)
ReadingTagCrossRef (readingId, tagId) — junction table with composite primary key
```

**Query pattern using @Junction:**

```kotlin
data class ReadingWithTags(
    @Embedded val reading: Reading,
    @Relation(
        parentColumn = "id",
        entityColumn = "id",
        associateBy = Junction(ReadingTagCrossRef::class)
    )
    val tags: List<Tag>
)
```

---

### Backup/Restore (JSON Export/Import)

**What's needed:**

| Technology | Already in Stack | New Requirement | Notes |
|------------|----------------|---------------|----------|
| Kotlinx Serialization | Yes (update to 1.9.0+) | Version bump | 1.8.0 → 1.9.0 or 1.10.0 for JSON export/import |
| ActivityResultContracts | Yes (Android Platform) | No new library | Use `CreateDocument` and `OpenDocument` for file picker |
| File I/O | Yes (Android Platform) | No new library | Use ContentResolver to read/write to scoped storage |

**Version update required:**

| Library | Current | Recommended | Why |
|---------|---------|------------|-----|
| Kotlinx Serialization | 1.8.0 | 1.9.0+ | Stable JSON APIs, Instant serializers, better error messages. Use 1.9.0 for Kotlin 2.2.x compatibility (1.10.0 requires Kotlin 2.3.0). |

**No new external dependencies required.** Use:
- Kotlinx Serialization (update version) for JSON ↔ Room entity mapping
- Android ActivityResultContracts for file picker UI
- ContentResolver.openOutputStream() / openInputStream() for file I/O

**Export flow:**

```kotlin
// Use ActivityResultContracts.CreateDocument to let user choose save location
val exportLauncher = registerForActivityResult(
    ActivityResultContracts.CreateDocument("application/json")
) { uri ->
    uri?.let { saveBackupToUri(it) }
}

fun saveBackupToUri(uri: Uri) {
    val json = Json.encodeToString(BackupData.serializer(), backupData)
    contentResolver.openOutputStream(uri)?.bufferedWriter()?.write(json)
}
```

**Import flow:**

```kotlin
// Use ActivityResultContracts.OpenDocument to let user select backup file
val importLauncher = registerForActivityResult(
    ActivityResultContracts.OpenDocument()
) { uri ->
    uri?.let { restoreBackupFromUri(it) }
}

fun restoreBackupFromUri(uri: Uri) {
    val json = contentResolver.openInputStream(uri)?.bufferedReader()?.readText()
    val backupData = Json.decodeFromString(BackupData.serializer(), json)
    // Insert into Room database
}
```

---

## Complete Dependency Block

```kotlin
// build.gradle.kts (app level)

dependencies {
    // === Compose (managed by BOM) ===
    val composeBom = platform("androidx.compose:compose-bom:2025.12.00")
    implementation(composeBom)
    androidTestImplementation(composeBom)
    
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-graphics")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.material3:material3:1.4.0")
    debugImplementation("androidx.compose.ui:ui-tooling")
    debugImplementation("androidx.compose.ui:ui-test-manifest")
    
    // === Navigation ===
    implementation("androidx.navigation3:navigation3-runtime:1.0.1")
    implementation("androidx.navigation3:navigation3-ui:1.0.1")
    
    // === Lifecycle + ViewModel ===
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.8.7")
    implementation("androidx.lifecycle:lifecycle-runtime-compose:2.8.7")
    
    // === Room Database ===
    implementation("androidx.room:room-runtime:2.8.4")
    implementation("androidx.room:room-ktx:2.8.4")
    ksp("androidx.room:room-compiler:2.8.4")
    
    // === Dependency Injection ===
    implementation("com.google.dagger:hilt-android:2.59.2")
    ksp("com.google.dagger:hilt-compiler:2.59.2")
    implementation("androidx.hilt:hilt-navigation-compose:1.2.0")
    
    // === Image Loading ===
    implementation("io.coil-kt.coil3:coil-compose:3.4.0")
    // Note: coil-network-okhttp NOT needed for local-only app
    
    // === Serialization (UPDATED for backup/restore) ===
    // Use 1.9.0 for Kotlin 2.2.x (1.10.0 requires Kotlin 2.3.0)
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.9.0")
    
    // === Coroutines ===
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.10.1")
    
    // === Testing ===
    testImplementation("org.junit.jupiter:junit-jupiter:5.11.4")
    testImplementation("io.mockk:mockk:1.13.16")
    testImplementation("app.cash.turbine:turbine:1.2.0")
    testImplementation("androidx.room:room-testing:2.8.4")
    testImplementation("org.robolectric:robolectric:4.14.1")
    
    androidTestImplementation("androidx.compose.ui:ui-test-junit4")
    androidTestImplementation("androidx.test.ext:junit:1.2.1")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.6.1")
}
```

---

## Bundled Assets Strategy

For the 78 Rider-Waite-Smith card images:

| Approach | Recommendation | Why |
|----------|---------------|-----|
| `res/drawable-nodpi/` | ✅ Use this | Place card images here with descriptive names (e.g., `card_major_00_fool.png`). `nodpi` prevents Android from scaling them. Access via `painterResource(R.drawable.card_major_00_fool)`. |
| `assets/` folder | ❌ Avoid | Requires `AssetManager` API, not Compose-native. No compile-time safety. |
| Compose Multiplatform resources | ❌ Avoid | This is Android-only — no need for CMP resource system. |

**Image format recommendation:** WebP with lossless compression. ~78 cards at ~50-100KB each = ~4-8 MB total APK increase. WebP is natively supported on Android 4.0+ and significantly smaller than PNG.

**Custom card images:** Store in app-internal files directory (no permissions needed):

```kotlin
// Save custom card image
fun saveCustomCardImage(context: Context, uri: Uri, cardId: Long): String {
    val file = File(context.filesDir, "custom_cards/$cardId.jpg")
    context.contentResolver.openInputStream(uri)?.use { input ->
        file.outputStream().use { output ->
            input.copyTo(output)
        }
    }
    return file.absolutePath
}
```

---

## Alternatives Considered

| Category | Recommended | Alternative | Why Not |
|----------|-------------|-------------|---------|
| DI | Hilt | Koin | Koin is simpler but runtime-only — errors surface at runtime instead of compile time. Hilt's official Compose/ViewModel integration and compile-time safety justify the boilerplate for a maintainable project. |
| DI | Hilt | Manual DI / Service Locator | No DI framework adds zero dependencies but creates tight coupling and makes testing painful. The 80% coverage requirement demands testable architecture. |
| Navigation | Navigation Compose 3 | Voyager / Decompose | Third-party navigation libs add external dependencies and may conflict with F-Droid reproducibility requirements. Navigation Compose 3 is Google's official Compose-native solution. |
| Image Loading | Coil 3 | Glide | Glide is heavier, View-centric, and has a more complex API. Coil is Kotlin-first, Compose-native, and has a simpler API. |
| Image Loading | Coil 3 | painterResource only | Works for bundled images but can't handle camera/gallery photos. Coil handles both with one API. |
| Database | Room | SQLDelight | SQLDelight is excellent but has a steeper learning curve and less Android ecosystem integration. Room's Flow integration and Google backing make it the standard choice. |
| Database | Room | DataStore | DataStore is for key-value preferences, not relational data. Readings have complex relationships (spread → positions → cards → photos). |
| Testing | JUnit 5 | JUnit 4 | JUnit 4 is legacy. JUnit 5 has better parameterized tests, nested tests, and extension model. |
| Testing | MockK | Mockito-Kotlin | Mockito's Kotlin integration is a second-class citizen. MockK is built for Kotlin — handles coroutines, objects, and sealed classes natively. |
| Photo Picker | System Photo Picker | Custom gallery | Building a custom gallery requires READ_EXTERNAL_STORAGE permission (deprecated on Android 13+), reinvents the wheel, and has privacy implications. The system Photo Picker needs no permissions. |
| Backup format | JSON (kotlinx-serialization) | Manual string parsing | Kotlinx Serialization provides type-safe JSON mapping with compile-time guarantees. Manual parsing is error-prone. |
| Backup storage | User-selected file | Internal app storage | User-selected via ActivityResultContracts.CreateDocument allows user to save to Downloads, Drive, or other location. |
| Backup encryption | None (v1.1) | AES encryption | Defer to v1.2. Plain JSON is sufficient for personal use backup. |

---

## What NOT to Use

| Technology | Why Avoid |
|-----------|-----------|
| **kapt** | Replaced by KSP. 2x slower compilation, deprecated path. |
| **LiveData** | Legacy architecture component. Flow is the modern standard with better Compose integration. |
| **XML layouts / View system** | Project is Compose-only. Mixing systems adds complexity and bundle size. |
| **Retrofit / OkHttp** | Local-only app — no network calls needed. Coil's network module is also unnecessary. |
| **WorkManager** | No background sync or scheduled tasks needed for local-only app. |
| **Paging 3** | Reading history for personal use won't hit pagination scale. Simple `LIMIT` queries suffice. |
| **Room 3.0 (alpha)** | KMP-focused breaking changes, not stable. Wait for stable release before migrating. |
| **Material 2** | Deprecated. Material 3 is the current standard with better dark theme support. |
| **Gson** | Use Kotlinx Serialization instead — Kotlin-native, type-safe, no runtime reflection for serializable classes. |
| **RoomDatabaseBackup library** | External dependency for simple JSON backup. Use manual JSON export with ActivityResultContracts — no extra dependency, user controls file location. |

---

## Sources

### v1.0 Stack (unchanged)
- [Jetpack Compose December '25 Release (1.10.x)](https://www.googblogs.com/whats-new-in-the-jetpack-compose-december-25-release/) — HIGH confidence
- [Jetpack Navigation 3 Stable Announcement](https://android-developers.googleblog.com/2025/11/jetpack-navigation-3-is-stable.html) — HIGH confidence
- [Room 2.8.4 on Maven Repository](https://mvnrepository.com/artifact/androidx.room/room-runtime) — HIGH confidence
- [Coil 3.4.0 Getting Started](https://coil-kt.github.io/coil/getting_started/) — HIGH confidence
- [Hilt 2.59.2 on Maven Central](https://central.sonatype.com/artifact/com.google.dagger/hilt-core/2.59.2) — HIGH confidence
- [Material 3 1.4.0 on Maven Repository](https://mvnrepository.com/artifact/androidx.compose.material3/material3) — HIGH confidence
- [Kotlin 2.2.21 Release](https://github.com/JetBrains/kotlin/releases/tag/v2.2.21) — HIGH confidence
- [AGP 9.1.0 Release Notes](https://developer.android.com/build/releases/agp-9-1-0-release-notes) — HIGH confidence
- [Embedded Photo Picker Documentation](https://developer.android.com/training/data-storage/shared/photo-picker/embedded) — HIGH confidence
- [Hilt vs Koin 2025 Comparison (droidcon)](https://www.droidcon.com/2025/11/26/hilt-vs-koin-the-hidden-cost-of-runtime-injection-and-why-compile-time-di-wins/) — MEDIUM confidence

### v1.1 Additions (NEW)
- [Room Many-to-Many Relationships](https://developer.android.com/training/data-storage/room/relationships/many-to-many) — HIGH confidence
- [Kotlinx Serialization 1.10.0 Release](https://github.com/Kotlin/kotlinx.serialization/releases/tag/v1.10.0) — HIGH confidence
- [Kotlinx Serialization 1.9.0 for Kotlin 2.2.x](https://github.com/Kotlin/kotlinx.serialization/releases/tag/v1.9.0) — HIGH confidence
- [Export/Import Room to JSON (Stack Overflow)](https://stackoverflow.com/questions/77649547/how-to-backup-and-restore-kotlin) — MEDIUM confidence
- [Android Room Database Backup Library](https://github.com/rafi0101/Android-Room-Database-Backup) — MEDIUM confidence (for reference, not recommending)