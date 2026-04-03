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
- [ ] **Phase 3: Enrichment & Polish** - Photos, search, edit/delete, reversed cards, and dark mystical theme
- [ ] **Phase 4: Testing & CI** - 80% test coverage with automated CI quality gates

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
- [ ] 02-03-PLAN.md — CardAssignmentStep with position slots, CardPickerBottomSheet, and CardThumbnail
- [ ] 02-04-PLAN.md — Wire AddReading navigation, implement ReadingDetailScreen with ViewModel

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
**Plans**: TBD
**UI hint**: yes

### Phase 4: Testing & CI
**Goal**: Codebase meets 80% test coverage with automated CI quality gates on every PR
**Depends on**: Phase 3
**Requirements**: TEST-01, TEST-02, TEST-03, TEST-04
**Success Criteria** (what must be TRUE):
  1. All code has unit tests achieving minimum 80% coverage
  2. CI pipeline automatically runs unit tests on every pull request
  3. CI pipeline runs security and lint scans on every pull request
  4. CI pipeline blocks merge when coverage drops below 80%
**Plans**: TBD

## Progress

**Execution Order:**
Phases execute in numeric order: 1 → 2 → 3 → 4

| Phase | Plans Complete | Status | Completed |
|-------|----------------|--------|-----------|
| 1. Foundation & Dev Environment | 0/0 | Not started | - |
| 2. Core Recording Loop | 1/4 | In Progress|  |
| 3. Enrichment & Polish | 0/0 | Not started | - |
| 4. Testing & CI | 0/0 | Not started | - |
