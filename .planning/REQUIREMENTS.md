# Requirements: Drawn

**Defined:** 2026-04-03
**Core Value:** Users can record a complete tarot reading — spread, cards, notes, and photos — and browse their reading history, all stored locally on their device.

## v1 Requirements

Requirements for initial release. Each maps to roadmap phases.

### Reading Management

- [x] **READ-01**: User can create a new reading by selecting a spread and assigning cards to positions
- [ ] **READ-02**: User can add freeform text notes to a reading
- [ ] **READ-03**: User can view a chronological list of past readings
- [ ] **READ-04**: User can view full reading details including spread layout, assigned cards, notes, and photos
- [ ] **READ-05**: User can edit an existing reading
- [ ] **READ-06**: User can delete a reading
- [ ] **READ-07**: User can search past readings by notes, card names, or date

### Spread Management

- [x] **SPRD-01**: App includes Celtic Cross spread (10 positions)
- [x] **SPRD-02**: App includes Three Card spread (3 positions)
- [x] **SPRD-03**: App includes Past/Present/Future spread (3 positions)
- [x] **SPRD-04**: User can see spread name and position meanings when selecting cards

### Card Management

- [ ] **CARD-01**: App includes all 78 Rider-Waite-Smith cards (22 Major Arcana, 56 Minor Arcana)
- [ ] **CARD-02**: User can browse and select cards from a visual grid/list when assigning to positions
- [ ] **CARD-03**: Card images are bundled in the app and work offline
- [ ] **CARD-04**: User can toggle reversed/upright orientation for each card in a reading

### Photo Attachments

- [ ] **PHOTO-01**: User can attach photos from device gallery to a reading
- [ ] **PHOTO-02**: User can capture photos with camera and attach to a reading
- [ ] **PHOTO-03**: User can view attached photos in full-screen within reading details
- [ ] **PHOTO-04**: User can remove attached photos from a reading

### Theme & Visual

- [ ] **THEME-01**: App uses a dark mystical theme (dark purples, golds, starry aesthetics)
- [ ] **THEME-02**: All screens are visually consistent with the theme

### Testing & Quality

- [ ] **TEST-01**: All code has unit tests with minimum 80% coverage
- [ ] **TEST-02**: CI pipeline runs unit tests on every pull request
- [ ] **TEST-03**: CI pipeline runs security/lint scans on every pull request
- [ ] **TEST-04**: CI pipeline blocks merge if coverage drops below 80%

### Development Environment

- [ ] **DEV-01**: Local dev environment is set up for building and previewing the app
- [ ] **DEV-02**: App can be installed and run on a physical Android device

## v2 Requirements

Deferred to future release. Tracked but not in current roadmap.

### Custom Decks

- **CDECK-01**: User can create a custom card deck with name and description
- **CDECK-02**: User can add custom cards with name, description, image, keywords, categories, and meanings
- **CDECK-03**: User can select from custom decks when creating a reading
- **CDECK-04**: User can edit and delete custom decks

### Organization & Statistics

- **ORG-01**: User can pin/favorite readings for quick access
- **ORG-02**: User can tag readings with custom labels
- **ORG-03**: User can view reading statistics (card frequency, suit distribution, Major vs Minor Arcana ratios)
- **ORG-04**: User can export a reading as PDF or image

### Spread Library

- **SLIB-01**: App includes expanded spread library beyond the 3 built-in spreads
- **SLIB-02**: User can browse available spreads by category or number of positions

## Out of Scope

Explicitly excluded. Documented to prevent scope creep.

| Feature | Reason |
|---------|--------|
| Random/shuffle card draw | v1 is freeform entry only — users record physical readings |
| Cloud sync or accounts | Local-only by design, privacy-first |
| iOS or cross-platform | Android-first with Kotlin |
| F-Droid automated deployment | Deferred until deployment strategy is finalized |
| AI-generated readings | Anti-feature — this is a tracker, not a generator |
| Video or non-photo attachments | Photos only for v1 |
| OAuth login | No accounts, no cloud |
| Gamification | Tarot is reflective, not competitive |

## Traceability

Which phases cover which requirements. Updated during roadmap creation.

| Requirement | Phase | Status |
|-------------|-------|--------|
| READ-01 | Phase 2 | Complete |
| READ-02 | Phase 2 | Pending |
| READ-03 | Phase 2 | Pending |
| READ-04 | Phase 2 | Pending |
| READ-05 | Phase 3 | Pending |
| READ-06 | Phase 3 | Pending |
| READ-07 | Phase 3 | Pending |
| SPRD-01 | Phase 2 | Complete |
| SPRD-02 | Phase 2 | Complete |
| SPRD-03 | Phase 2 | Complete |
| SPRD-04 | Phase 2 | Complete |
| CARD-01 | Phase 2 | Pending |
| CARD-02 | Phase 2 | Pending |
| CARD-03 | Phase 2 | Pending |
| CARD-04 | Phase 3 | Pending |
| PHOTO-01 | Phase 3 | Pending |
| PHOTO-02 | Phase 3 | Pending |
| PHOTO-03 | Phase 3 | Pending |
| PHOTO-04 | Phase 3 | Pending |
| THEME-01 | Phase 3 | Pending |
| THEME-02 | Phase 3 | Pending |
| TEST-01 | Phase 4 | Pending |
| TEST-02 | Phase 4 | Pending |
| TEST-03 | Phase 4 | Pending |
| TEST-04 | Phase 4 | Pending |
| DEV-01 | Phase 1 | Pending |
| DEV-02 | Phase 1 | Pending |

**Coverage:**
- v1 requirements: 26 total
- Mapped to phases: 26
- Unmapped: 0 ✓

---
*Requirements defined: 2026-04-03*
*Last updated: 2026-04-03 after initial definition*
