<!-- GSD:project-start source:PROJECT.md -->
## Project

**Drawn**

Drawn is a local-only Android app for tracking and organizing tarot card readings. Users can record readings by selecting a spread, assigning cards to positions, adding notes, and attaching photos of their physical layout. It ships with the standard 78-card Rider-Waite-Smith deck and common spreads, while supporting custom card decks (oracle, alternative imagery). Built with Kotlin and Jetpack Compose, designed for personal use first but structured as open source so the tarot community can benefit.

**Core Value:** Users can record a complete tarot reading — spread, cards, notes, and photos — and browse their reading history, all stored locally on their device.

### Constraints

- **Tech stack**: Kotlin + Jetpack Compose — best fit for native Android and F-Droid compatibility
- **Storage**: Room Database — standard Android local storage, no cloud dependencies
- **Testing**: 80% code coverage minimum — enforced in CI/CD pipeline
- **Distribution**: F-Droid requirements (reproducible builds, no proprietary dependencies) must be considered even if deployment is deferred
<!-- GSD:project-end -->

<!-- GSD:stack-start source:research/STACK.md -->
## Technology Stack

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
| Kotlinx Serialization | 1.8.0 | Data serialization | Type-safe serialization for Navigation Compose 3 route parameters and any JSON needs. Official Kotlin library with Compose Navigation integration. | HIGH |
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
## Complete Dependency Block
## Bundled Assets Strategy
| Approach | Recommendation | Why |
|----------|---------------|-----|
| `res/drawable-nodpi/` | ✅ Use this | Place card images here with descriptive names (e.g., `card_major_00_fool.png`). `nodpi` prevents Android from scaling them. Access via `painterResource(R.drawable.card_major_00_fool)`. |
| `assets/` folder | ❌ Avoid | Requires `AssetManager` API, not Compose-native. No compile-time safety. |
| Compose Multiplatform resources | ❌ Avoid | This is Android-only — no need for CMP resource system. |
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
## Sources
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
<!-- GSD:stack-end -->

<!-- GSD:conventions-start source:CONVENTIONS.md -->
## Conventions

Conventions not yet established. Will populate as patterns emerge during development.
<!-- GSD:conventions-end -->

<!-- GSD:architecture-start source:ARCHITECTURE.md -->
## Architecture

Architecture not yet mapped. Follow existing patterns found in the codebase.
<!-- GSD:architecture-end -->

<!-- GSD:workflow-start source:GSD defaults -->
## GSD Workflow Enforcement

Before using Edit, Write, or other file-changing tools, start work through a GSD command so planning artifacts and execution context stay in sync.

Use these entry points:
- `/gsd:quick` for small fixes, doc updates, and ad-hoc tasks
- `/gsd:debug` for investigation and bug fixing
- `/gsd:execute-phase` for planned phase work

Do not make direct repo edits outside a GSD workflow unless the user explicitly asks to bypass it.
<!-- GSD:workflow-end -->



<!-- GSD:profile-start -->
## Developer Profile

> Profile not yet configured. Run `/gsd:profile-user` to generate your developer profile.
> This section is managed by `generate-claude-profile` -- do not edit manually.
<!-- GSD:profile-end -->
