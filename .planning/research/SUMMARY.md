# Project Research Summary

**Project:** Drawn — Android Tarot Card Reading App
**Domain:** Local-only Android tarot reading tracker / journal
**Researched:** 2026-04-03
**Confidence:** HIGH

## Executive Summary

Drawn is a **tarot reading tracker** — not a reading generator, not an AI interpreter. Users perform physical tarot readings with real cards, then record the results digitally for posterity and pattern-tracking. The competitive landscape is sparse: existing apps are either basic journals (Tarot Journal, 4.6★) or abandoned pioneers (Golden Thread Tarot). The strongest differentiators are custom deck support and reading statistics, but the core value proposition is a polished, privacy-first recording loop.

The recommended approach is a modern, Compose-only Android app using Kotlin 2.2.x, Navigation Compose 3, Room 2.8.x, and Hilt for DI. No network layer, no cloud sync, no accounts — all data stays local in a Room database. The architecture follows Google's three-layer pattern (UI → Domain → Data) with MVVM, StateFlow-based reactive streams, and sealed UI state classes. This is a well-documented, mainstream Android stack with strong official support.

The key risks are concentrated in three areas: (1) Room database migration corruption — adding fields to embedded objects can silently destroy user data, requiring tested migrations and staged rollouts; (2) Compose anti-patterns — self-cancelling LaunchedEffects, event-vs-state confusion, and mutable collection mutations are the most common production bugs; (3) image handling — bundled card images and camera photos can cause OOM crashes or orientation issues without proper downsampling and EXIF handling. All three have well-documented prevention strategies.

## Key Findings

### Recommended Stack

A modern, Compose-first Android stack with all dependencies at stable, production-ready versions. The stack is intentionally local-only — no Retrofit, OkHttp, WorkManager, or Paging 3 needed.

**Core technologies:**
- **Kotlin 2.2.21** — language — latest stable with mature tooling and full Compose compiler support; 2.3.x exists but 2.2.x has broader library compatibility
- **Jetpack Compose BOM 2025.12.00** — UI framework — use the BOM to manage all Compose versions consistently, prevents conflicts
- **Navigation Compose 3 (1.0.1)** — type-safe routing — stable since Nov 2025, eliminates string-based route bugs with `@Serializable` types
- **Room 2.8.4 + KSP** — local database — Kotlin-first API with suspend functions and Flow support; KSP replaces deprecated kapt for 2x faster builds
- **Hilt 2.59.2** — dependency injection — compile-time safety, official Compose/ViewModel integration, essential for 80% test coverage mandate
- **Coil 3.4.0** — image loading — unified API for bundled drawables, camera photos, and gallery images with automatic caching
- **Material 3 (1.4.0)** — design system — supports Material You dynamic color and dark theme out of the box
- **JUnit 5 + MockK + Turbine** — testing stack — modern testing with Kotlin-native mocking and Flow testing

### Expected Features

Drawn is a reading tracker, not a reading generator. This distinction shapes the entire feature set.

**Must have (table stakes):**
- Record reading with spread + card assignment — core use case, form-style entry
- Browse reading history — chronological list of past readings
- View reading details — full context with spread layout, cards, notes, photos
- Text notes on readings — freeform interpretation field
- Photo attachments — camera capture + gallery picker, multiple per reading
- Standard 78-card RWS deck — bundled images, visual card picker
- Common spreads (3-5) — Celtic Cross, Three Card, Past/Present/Future
- Search past readings — full-text search across notes, card names, dates
- Edit/delete readings — correction and privacy control

**Should have (competitive differentiators):**
- Custom card decks — oracle decks, alternative tarot decks; makes Drawn the go-to tracker for serious practitioners
- Reading statistics — card frequency analysis, suit distribution, Major vs Minor Arcana ratios
- Pin/favorite readings — quick access to profoundly meaningful readings
- Folder/tag organization — group readings by theme, deck, or intention
- Reversed card support — toggle per reading or global setting
- Spread library — curated catalog beyond the basics
- Export reading — PDF/image export for sharing with querents or study groups
- Dark mystical theme — dark purples, golds, starry aesthetics; core to brand identity

**Defer (v2+):**
- Custom card decks — high complexity, not needed for initial validation
- Reading statistics — needs sufficient data to be meaningful
- Spread library — 3-5 built-in spreads suffice for v1
- Export — nice-to-have, not core to the recording loop
- Tags/folders — organization becomes valuable after ~50+ readings

### Architecture Approach

Three-layer architecture (UI → Domain → Data) with MVVM pattern and unidirectional data flow. For a local-only app, this simplifies cleanly without network layers. The domain layer is optional for v1 but recommended for testability.

**Major components:**
1. **UI Layer** — Compose screens, Navigation Graph, Theme System, reusable components (CardGrid, SpreadLayout, PhotoViewer)
2. **ViewModel Layer** — One ViewModel per screen (ReadingListVM, ReadingDetailVM, ReadingEntryVM, CardPickerVM, DeckEditorVM), each exposing StateFlow<UiState>
3. **Domain Layer** — Pure Kotlin data classes (Reading, Card, Spread, Deck), optional use cases for complex operations
4. **Data Layer** — Repositories (single source of truth), DAOs (Room operations), Room Database (SQLite abstraction), PhotoManager (camera/gallery)

**Key patterns:** Repository as single source of truth, entity-domain model separation, Flow-based reactive queries, sealed UI state classes, Hilt dependency injection.

### Critical Pitfalls

1. **Room Database Migration Corruption** — Adding non-null fields to `@Embedded` classes silently corrupts existing user data. Prevention: write tested migrations with `MigrationTestHelper`, only update rows where parent object was already non-null, use staged rollouts.
2. **LaunchedEffect Self-Cancellation** — Mutating a LaunchedEffect key inside its own body cancels the coroutine mid-execution. Prevention: use `LaunchedEffect(Unit)` with `snapshotFlow {}` to observe state changes.
3. **Mutable Collection Mutation Without Recomposition** — Mutating `mutableListOf` inside `mutableStateOf` doesn't trigger recomposition. Prevention: use `mutableStateListOf<T>()` or create new list references.
4. **Events Treated as State** — Using `mutableStateOf` for one-time events causes duplicate dialogs/snackbars on rotation. Prevention: use `Channel<UiEvent>` for one-time events.
5. **Bundled Asset Images Causing OOM** — Full-resolution tarot card images exhaust heap. Prevention: Coil 3 for automatic downsampling, WebP format, pre-scale to screen resolution, use `res/drawable-nodpi/`.

## Implications for Roadmap

Based on combined research, suggested phase structure:

### Phase 1: Foundation — Core Data Model & Recording Loop
**Rationale:** Everything depends on the database schema, domain models, and the ability to record a reading. This is the irreducible core.
**Delivers:** Room database with schema (readings, cards, spreads, photos tables), domain models, DAOs, repositories, ReadingEntry screen with spread picker and card assignment, ReadingList screen, basic ReadingDetail screen
**Addresses:** Record reading with spread + cards, browse history, view details, standard 78-card RWS deck, common spreads (3-5)
**Avoids:** Pre-populated database overwrite (use Room `createFromAsset()`), direct DAO access from ViewModel (repository pattern), blocking main thread (suspend functions)

### Phase 2: Enrichment — Photos, Search, Polish
**Rationale:** Builds on the recording loop with features that make it production-ready. Photo handling and search are the most complex remaining table-stakes features.
**Delivers:** PhotoManager with camera/gallery integration, search functionality, edit/delete readings, reversed card support, dark mystical theme system, Navigation Compose 3 graph
**Addresses:** Photo attachments, search readings, edit/delete, reversed cards, dark mystical theme, date/time auto-capture
**Avoids:** ACTION_IMAGE_CAPTURE inconsistency (use Photo Picker + CameraX), OOM from images (Coil + WebP), LaunchedEffect self-cancellation, event-vs-state confusion (Channels)

### Phase 3: Power User — Statistics, Organization, Export
**Rationale:** These features require sufficient reading data to be meaningful and depend on the foundation being solid. They're differentiators but not blockers.
**Delivers:** Reading statistics (card frequency, suit distribution), pin/favorite readings, tag/folder organization, spread library, export to PDF/image, custom deck editor
**Addresses:** Custom card decks, reading statistics, pin readings, folder/tag organization, spread library, export reading, querent name field
**Avoids:** Mutable collection mutation in statistics views, recomposition jank in card lists (push state reads low), APK bloat from custom deck images (WebP + on-demand)

### Phase 4: Testing & Quality — Coverage, CI, Polish
**Rationale:** 80% test coverage mandate requires dedicated effort. Testing infrastructure should be built alongside features but comprehensive coverage is a final gate.
**Delivers:** Unit tests for DAOs (in-memory Room), repositories (MockK + Turbine), ViewModels (MockK + Turbine), Compose UI tests, Robolectric tests, Ktlint + Detekt in CI
**Addresses:** All layers — 80% coverage target
**Avoids:** God ViewModel anti-pattern (one VM per screen), passing entities to UI layer, memory leaks from Bitmap references

### Phase Ordering Rationale

- **Data before UI:** The architecture mandates building bottom-up — domain models → entities → DAOs → repositories → ViewModels → screens. This is standard Android architecture and well-documented.
- **Recording before enrichment:** The core loop ("do reading → record it → feel satisfied") must work before any differentiators matter. Features like statistics need data to analyze.
- **Photos before statistics:** Photo handling is table stakes; statistics are a differentiator. Photos also introduce the most complex external integrations (camera, gallery, EXIF).
- **Testing as continuous but Phase 4 as gate:** Testing infrastructure is built alongside each phase, but comprehensive 80% coverage is a release gate.

### Research Flags

Phases likely needing deeper research during planning:
- **Phase 3 (Custom Decks):** Deck editor UI, image import pipeline, and custom deck storage strategy are high-complexity features with less established patterns. Consider `/gsd-research-phase` for custom deck architecture.
- **Phase 3 (Export):** PDF/image export of card layouts with proper typography and card rendering needs investigation — no single established Android pattern for this.

Phases with standard patterns (skip research-phase):
- **Phase 1 (Foundation):** Room + Repository + ViewModel is the most well-documented Android pattern. Official Google codelabs and Now in Android reference app provide complete guidance.
- **Phase 2 (Enrichment):** Photo Picker, Coil, Compose theming, and search are all standard with excellent documentation.
- **Phase 4 (Testing):** JUnit 5 + MockK + Turbine is a well-established Kotlin testing stack with abundant resources.

## Confidence Assessment

| Area | Confidence | Notes |
|------|------------|-------|
| Stack | HIGH | All technologies verified against official sources (Google blogs, Maven Central, Coil docs). Version numbers current as of April 2026. Alternatives considered with clear rationale. |
| Features | HIGH | Competitive analysis based on direct competitor data (Play Store, App Store, official sites). Feature dependencies mapped. Anti-features clearly defined. |
| Architecture | HIGH | Based on official Google architecture guidance, Now in Android reference app, and official codelabs. Pattern examples included for every major pattern. |
| Pitfalls | HIGH | Critical pitfalls backed by real post-mortems (Unrushed Apps), Droidcon talks, and CommonsWare analysis. Prevention strategies with code examples provided. |

**Overall confidence:** HIGH

### Gaps to Address

- **EmbeddedPhotoPicker vs full-screen Photo Picker:** Research recommends EmbeddedPhotoPicker for inline UX but marks it as MEDIUM confidence. Decision should be made during Phase 2 planning based on actual implementation complexity.
- **Room 3.0 migration timeline:** Room 3.0 is alpha (March 2026, KMP-focused). The research recommends waiting for stable, but the migration path from 2.8.x to 3.x is unknown. Monitor Room 3.0 stable release for potential future migration.
- **Export format decision:** PDF vs image export for readings — PDF provides better typography but is more complex to implement. Image export is simpler but less flexible. Decision needed during Phase 3 planning.
- **Custom deck image storage:** How to store user-imported card images (internal storage vs MediaStore vs app-specific directory) needs investigation during Phase 3.

## Sources

### Primary (HIGH confidence)
- [Jetpack Compose December '25 Release](https://www.googblogs.com/whats-new-in-the-jetpack-compose-december-25-release/) — Compose BOM versioning
- [Jetpack Navigation 3 Stable Announcement](https://android-developers.googleblog.com/2025/11/jetpack-navigation-3-is-stable.html) — Navigation Compose 3
- [Android Guide to App Architecture](https://developer.android.com/topic/architecture) — Three-layer architecture
- [Persist Data with Room Codelab](https://developer.android.com/codelabs/basic-android-kotlin-compose-persisting-data-room) — Room patterns
- [Now in Android Reference App](https://github.com/android/nowinandroid) — Production reference
- [Coil 3.4.0 Getting Started](https://coil-kt.github.io/coil/getting_started/) — Image loading
- [Room Database Migration Post-Mortem — Unrushed Apps](https://unrushedapps.com/blog/post-mortem-database-migration-error) — Migration pitfalls
- [Compose Performance Anti-Patterns — Adit Lal, Droidcon India 2025](https://aditlal.dev/compose-bottleneck-antipatterns-performance/) — Compose pitfalls
- [The ACTION_IMAGE_CAPTURE Fallacy — CommonsWare](https://commonsware.com/blog/2015/06/08/action-image-capture-fallacy.html) — Camera pitfalls
- [Android Developers: Prepopulate Room database](https://developer.android.com/training/data-storage/room/prepopulate) — Database prepopulation

### Secondary (MEDIUM confidence)
- [Tarot Journal (Google Play)](https://play.google.com/store/apps/details?id=com.tarot_journal) — Direct competitor analysis
- [Hilt vs Koin 2025 Comparison (droidcon)](https://www.droidcon.com/2025/11/26/hilt-vs-koin-the-hidden-cost-of-runtime-injection-and-why-compile-time-di-wins/) — DI comparison
- [Best Tarot Apps 2026 — TarotLingo](https://tarotlingo.com/best-tarot-apps) — Competitive landscape
- [Local DB Design Patterns — Room + Repository + ViewModel](https://dev.to/myougatheaxo/local-db-design-patterns-room-repository-viewmodel-architecture-43bo) — Architecture patterns

### Tertiary (LOW confidence)
- [Android largeHeap discussion — LinkedIn](https://www.linkedin.com/posts/amrutha-k-l-a1b37088_large-heaps-in-android-am-i-fixing-the-activity-7429957170177409025-FJ8P) — Memory management (social media source)

---
*Research completed: 2026-04-03*
*Ready for roadmap: yes*
