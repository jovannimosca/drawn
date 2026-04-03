# Drawn

## What This Is

Drawn is a local-only Android app for tracking and organizing tarot card readings. Users can record readings by selecting a spread, assigning cards to positions, adding notes, and attaching photos of their physical layout. It ships with the standard 78-card Rider-Waite-Smith deck and common spreads, while supporting custom card decks (oracle, alternative imagery). Built with Kotlin and Jetpack Compose, designed for personal use first but structured as open source so the tarot community can benefit.

## Core Value

Users can record a complete tarot reading — spread, cards, notes, and photos — and browse their reading history, all stored locally on their device.

## Requirements

### Validated

(None yet — ship to validate)

### Active

- [ ] Record new readings by selecting a spread and assigning cards to positions
- [ ] Browse and search past reading history
- [ ] View reading details including spread layout, assigned cards, notes, and photos
- [ ] Select from 3-5 pre-built common spreads (Celtic Cross, Three Card, Past/Present/Future, etc.)
- [ ] Browse and select cards from the standard 78-card Rider-Waite-Smith deck (22 Major Arcana, 56 Minor Arcana)
- [ ] Add custom card decks with full editor (name, description, image, keywords, categories, meanings)
- [ ] Attach photos to readings (camera or gallery)
- [ ] Add text notes to readings
- [ ] All code unit tested with 80% coverage
- [ ] Local dev environment set up for preview and development
- [ ] CI/CD pipeline with automated unit tests and security scans on PRs (GitHub Actions)
- [ ] Mystical/dark visual theme

### Out of Scope

- F-Droid publication/automated deployment — deferred until deployment strategy is figured out
- Random/shuffle card draw — v1 is freeform entry only
- OAuth or cloud sync — local-only by design
- iOS or cross-platform — Android-first with Kotlin
- Video or non-photo attachments — photos only for v1

## Context

- **Platform:** Android (min SDK TBD), Kotlin + Jetpack Compose
- **Storage:** Room Database (SQLite), local-only
- **Card imagery:** 78 RWS card images bundled in app
- **Design direction:** Mystical/dark theme — dark purples, golds, starry aesthetics
- **Distribution:** F-Droid targeted eventually, but v1 focuses on CI/CD quality gates only
- **Testing:** Unit tests required, 80% coverage minimum

## Constraints

- **Tech stack**: Kotlin + Jetpack Compose — best fit for native Android and F-Droid compatibility
- **Storage**: Room Database — standard Android local storage, no cloud dependencies
- **Testing**: 80% code coverage minimum — enforced in CI/CD pipeline
- **Distribution**: F-Droid requirements (reproducible builds, no proprietary dependencies) must be considered even if deployment is deferred

## Key Decisions

| Decision | Rationale | Outcome |
|----------|-----------|---------|
| Freeform entry over guided draw | Users record readings after physical sessions, not during | Freeform form-style entry |
| Tap from grid/list for card selection | Simpler than shuffle/draw for retrospective entry | Visual card picker |
| Card images bundled in app | No network dependency, works offline | Increases app size but ensures reliability |
| F-Droid CI/CD deferred to future phase | Focus v1 on quality gates (tests, scans) rather than deployment | GitHub Actions for tests + security scans only |
| 3-5 common spreads for v1 | Start focused, expand later | Celtic Cross, Three Card, Past/Present/Future at minimum |

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
*Last updated: 2026-04-03 after initialization*
