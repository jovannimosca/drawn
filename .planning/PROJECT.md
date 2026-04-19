# Drawn

## What This Is

A local-only Android app for tarot practitioners who perform physical readings with real cards and want to record, organize, and browse their readings on their phone. Supports custom card decks, reading tags, and device backup/restore.

## Current State (v1.1 in progress)

Android app with Kotlin + Jetpack Compose, Room database, 78-card RWS deck, 3 built-in spreads, 80% test coverage, GitHub Actions CI/CD with automated releases. Adding custom card decks, reading tags, and backup/restore.

## Core Value

Users can record a complete tarot reading — spread, cards, notes, and photos — and browse their reading history, all stored locally on their device.

## Requirements

### Validated

- ✓ Record new readings by selecting a spread and assigning cards to positions — v1.0 (Phase 2)
- ✓ Add text notes to readings — v1.0 (Phase 2)
- ✓ Browse and search past reading history — v1.0 (Phase 2, 3)
- ✓ View reading details including spread layout, assigned cards, notes, and photos — v1.0 (Phase 2, 3)
- ✓ Edit existing readings — v1.0 (Phase 3)
- ✓ Delete readings — v1.0 (Phase 3)
- ✓ Select from 3 built-in spreads (Celtic Cross, Three Card, Past/Present/Future) — v1.0 (Phase 2)
- ✓ Browse and select cards from 78-card Rider-Waite-Smith deck — v1.0 (Phase 2)
- ✓ Card images bundled in app (offline) — v1.0 (Phase 2)
- ✓ Toggle reversed/upright orientation for cards — v1.0 (Phase 3)
- ✓ Attach photos to readings (camera or gallery) — v1.0 (Phase 3)
- ✓ Dark mystical theme — v1.0 (Phase 3, 6)
- ✓ All code unit tested with 80% coverage — v1.0 (Phase 4)
- ✓ CI/CD pipeline with automated tests and security scans — v1.0 (Phase 1, 4)
- ✓ GitHub Actions release workflow with APK builds — v1.0 (Phase 5)

### Active (v1.1)

- [ ] Custom card decks with full editor (name, description, image, keywords, categories, meanings)
- [ ] Tag readings with custom labels
- [ ] Backup/restore readings to local device storage
- [ ] Pin/favorite readings for quick access
- [ ] View reading statistics (card frequency, suit distribution)
- [ ] Expanded spread library beyond 3 built-in spreads
- [ ] Export readings as PDF or image

### Out of Scope (v1.1)

- Cloud sync or OAuth — local backup only for v1.1
- F-Droid publication — deferred

### Out of Scope

- F-Droid publication — deferred
- Random/shuffle card draw — v1 is freeform entry only
- OAuth or cloud sync — local-only by design
- iOS or cross-platform — Android-first with Kotlin
- Video or non-photo attachments — photos only for v1

## Current Milestone: v1.1 Organization & Custom Decks

**Goal:** Enable users to organize readings with tags and favorites, use custom/oracle card decks, and backup/restore their data.

**Target features:**
- Custom card decks with full editor (name, description, image, keywords, categories, meanings)
- Tag readings with custom labels
- Backup/restore readings to local device storage (JSON export/import)
- Pin/favorite readings for quick access

## Context

- **Platform:** Android (min SDK 26), Kotlin 2.2 + Jetpack Compose
- **Storage:** Room Database (SQLite), local-only
- **Card imagery:** 78 RWS card images bundled in app
- **Design:** Dark mystical theme — dark purples, golds, starry aesthetics
- **Testing:** 80% coverage minimum, Kover enforcement in CI
- **Distribution:** GitHub Releases with signed APKs
- **LOC:** ~2500 Kotlin (from Phase 1-7 development)

## Constraints

- **Tech stack**: Kotlin + Jetpack Compose — best fit for native Android and F-Droid compatibility
- **Storage**: Room Database — standard Android local storage, no cloud dependencies
- **Testing**: 80% code coverage minimum — enforced in CI/CD pipeline
- **Distribution**: F-Droid requirements must be considered if pursuing in future

## Key Decisions

| Decision | Rationale | Outcome |
|----------|-----------|---------|
| Freeform entry over guided draw | Users record readings after physical sessions | ✓ Implemented |
| Tap from grid/list for card selection | Simpler than shuffle/draw for retrospective entry | ✓ Implemented |
| Card images bundled in app | No network dependency, works offline | ✓ Implemented |
| 3 built-in spreads for v1 | Start focused, expand later | ✓ Implemented |
| GitHub Actions releases | Simple, free, works for personal repo | ✓ Implemented |
| Phase 7 gap closure | Audit found animation gaps, closed in Phase 7 | ✓ Fixed |

## Evolution

This document evolves at phase transitions and milestone boundaries.

**After each phase transition** (via `/gsd-transition`):
1. Requirements invalidated? → Move to Out of Scope with reason
2. Requirements validated? → Move to Validated with phase reference
3. New requirements emerged? → Add to Active
4. Decisions to log? → Add to Key Decisions
5. "What This Is" still accurate? → Update if drifted

**After each milestone** (via `/gsd-complete-milestone`):
1. Full review of all sections
2. Core Value check — still the right priority?
3. Audit Out of Scope — reasons still valid?
4. Update Context with current state

---

*Last updated: 2026-04-18 started v1.1 milestone*
