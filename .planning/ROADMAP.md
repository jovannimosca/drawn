# Roadmap: Drawn

## Overview

Drawn evolves from an empty Android project to a polished, locally-stored tarot reading tracker. The journey begins with dev environment setup, builds the core recording loop (spread selection → card assignment → notes → save), enriches it with photos/search/edit/theme, and closes with a testing/CI quality gate ensuring 80% coverage. All data stays on-device — no cloud, no accounts, no network.

## Phases

**Phase Numbering:**
- Integer phases (1, 2, 3): Planned milestone work
- Decimal phases (2.1, 2.2): Urgent insertions (marked with INSERTED)

Decimal phases appear between their surrounding integers in numeric order.

- [ ] **Phase 1: Foundation & Dev Environment** - Set up local dev environment, project scaffolding, and core data layer
- [ ] **Phase 2: Core Recording Loop** - Users can record complete readings with spreads, cards, and notes, and browse history
- [x] **Phase 3: Enrichment & Polish** - Photos, search, edit/delete, reversed cards, and dark mystical theme (completed 2026-04-04)
- [x] **Phase 4: Testing & CI** - 80% test coverage with automated CI quality gates (completed 2026-04-04)
- [x] **Phase 5: Continuous Deployment** - Automated release pipeline: build production APK and publish GitHub Release on merge to main (completed 2026-04-05)

## Phase Details

### Phase 1: Foundation & Dev Environment
**Goal**: Developer can build, run, and preview the app with core data layer in place
**Depends on**: Nothing (first phase)
**Requirements**: DEV-01, DEV-02
**Success Criteria** (what must be TRUE):
  1. Developer can build and run the app on a physical Android device
  2. App launches with a functioning main screen (even if placeholder)
  3. Room database schema is defined with core tables (readings, cards, spreads, reading_cards, photos)
**Plans**: 5 plans

Plans:
- [ ] 01-01-PLAN.md — Gradle project structure with version catalog and all dependencies
- [ ] 01-02-PLAN.md — CI/CD pipeline with Ktlint, Detekt, and GitHub Actions
- [ ] 01-03-PLAN.md — Room database schema with domain models, entities, DAOs, and pre-population
- [ ] 01-04-PLAN.md — Hilt DI modules and repository layer with entity-domain mapping
- [ ] 01-05-PLAN.md — UI layer with dark theme, Navigation Compose 3, and ReadingListScreen

### Phase 2: Core Recording Loop
**Goal**: Users can record a complete tarot reading and browse their reading history
**Depends on**: Phase 1
**Requirements**: READ-01, READ-02, READ-03, READ-04, SPRD-01, SPRD-02, SPRD-03, SPRD-04, CARD-01, CARD-02, CARD-03
**Success Criteria** (what must be TRUE):
  1. User can select a spread (Celtic Cross, Three Card, Past/Present/Future) and see position meanings when assigning cards
  2. User can browse and select cards from the full 78-card RWS deck to assign to spread positions
  3. User can add freeform text notes to a reading and save it
  4. User can view a chronological list of past readings
  5. User can tap a reading to view full details including spread layout, assigned cards, and notes
**Plans**: 4 plans

Plans:
- [x] 02-01-PLAN.md — AddReading route, wizard state types, and AddReadingViewModel with save logic
- [x] 02-02-PLAN.md — AddReadingScreen wizard container, SpreadPickerStep, NotesAndSaveStep, and step indicator
- [x] 02-03-PLAN.md — CardAssignmentStep with position slots, CardPickerBottomSheet, and CardThumbnail
- [x] 02-04-PLAN.md — Wire AddReading navigation, implement ReadingDetailScreen with ViewModel

### Phase 3: Enrichment & Polish
**Goal**: Users can fully manage readings with photos, search, editing, and a polished dark theme
**Depends on**: Phase 2
**Requirements**: READ-05, READ-06, READ-07, CARD-04, PHOTO-01, PHOTO-02, PHOTO-03, PHOTO-04, THEME-01, THEME-02
**Success Criteria** (what must be TRUE):
  1. User can attach photos from gallery or camera to a reading, view them full-screen, and remove them
  2. User can edit and delete existing readings
  3. User can search past readings by notes, card names, or date
  4. User can toggle reversed/upright orientation for each card in a reading
  5. All screens display with a consistent dark mystical theme (dark purples, golds, starry aesthetics)
**Plans**: 5 plans

Plans:
- [x] 03-01-PLAN.md — Dark theme foundation: typography scale, complete M3 color scheme, card elevation
- [x] 03-02-PLAN.md — Search bar in ReadingListScreen with real-time filtering by title, notes, spread name
- [x] 03-03-PLAN.md — Inline edit and delete on ReadingDetailScreen with confirmation dialogs
- [x] 03-04-PLAN.md — Reversed card toggle on position slots with 180° rotation and gold "R" badge
- [x] 03-05-PLAN.md — Photo attachments: gallery/camera picker, grid display, full-screen viewer, delete

### Phase 4: Testing & CI
**Goal**: Codebase meets 80% test coverage with automated CI quality gates on every PR
**Depends on**: Phase 3
**Requirements**: TEST-01, TEST-02, TEST-03, TEST-04
**Success Criteria** (what must be TRUE):
  1. All code has unit tests achieving minimum 80% coverage
  2. CI pipeline automatically runs unit tests on every pull request
  3. CI pipeline runs security and lint scans on every pull request
  4. CI pipeline blocks merge when coverage drops below 80%
**Plans**: 3 plans

Plans:
- [x] 04-01-PLAN.md — Kover coverage config + ViewModel unit tests (AddReading, ReadingList, ReadingDetail)
- [x] 04-02-PLAN.md — DAO tests (Room in-memory) + Repository tests (mocked DAOs)
- [x] 04-03-PLAN.md — Compose UI tests + CI workflow with coverage gates

### Phase 5: Continuous Deployment
**Goal**: Merging to main automatically builds a production APK and publishes a GitHub Release
**Depends on**: Phase 4
**Requirements**: DEPLOY-01, DEPLOY-02, DEPLOY-03
**Success Criteria** (what must be TRUE):
  1. Merging to `main` triggers a release workflow that builds a signed production APK
  2. Release workflow auto-generates a version tag (semver) and creates a GitHub Release with the APK as an artifact
  3. Release notes are auto-generated from commit messages since the last release
**Plans**: 1 plan

Plans:
- [x] 05-01-PLAN.md — Release signing config in build.gradle.kts + GitHub Actions release workflow with version bump, signed APK build, and GitHub Release publish

## Progress

**Execution Order:**
Phases execute in numeric order: 1 → 2 → 3 → 4 → 5

| Phase | Plans Complete | Status | Completed |
|-------|----------------|--------|-----------|
| 1. Foundation & Dev Environment | 5/5 | Complete   | 2026-04-03 |
| 2. Core Recording Loop | 4/4 | Complete   | 2026-04-04 |
| 3. Enrichment & Polish | 5/5 | Complete   | 2026-04-04 |
| 4. Testing & CI | 3/3 | Complete   | 2026-04-04 |
| 5. Continuous Deployment | 1/1 | Complete   | 2026-04-05 |

### Phase 6: Refine UI theming and screens

**Goal:** Elevate visual polish with animations, transitions, shared components, spread-accurate layouts, and mystical aesthetic refinements
**Requirements**: THEME-01, THEME-02 (refinement beyond Phase 3 foundation)
**Depends on:** Phase 5
**Plans:** 6/6 plans complete

Plans:
- [x] 06-01-PLAN.md — Shared UI components (EmptyState, ErrorBanner, GoldDivider) + migrate ReadingListScreen
- [x] 06-02-PLAN.md — Compact reading list cards with metadata (date, spread) + fade-in animation
- [x] 06-03-PLAN.md — NebulaBackground composable applied to main screens
- [x] 06-04-PLAN.md — HorizontalPager swipe navigation for AddReading wizard
- [x] 06-05-PLAN.md — Y-axis flip animation for reversed card toggles + gold dividers in detail screen
- [x] 06-06-PLAN.md — Spread-accurate card layout (Celtic Cross cross+staff, row layouts)

### Phase 7: Polish Bug Fixes
**Goal:** Fix remaining implementation gaps from Phase 6 audit
**Depends on:** Phase 6
**Gap Closure:** Closes gaps from v1.0-MILESTONE-AUDIT.md

Plans:
- [ ] 07-01-PLAN.md — Fix AnimatedVisibility fade-in to trigger on state transition
- [ ] 07-02-PLAN.md — Implement shared element transitions for list→detail navigation
