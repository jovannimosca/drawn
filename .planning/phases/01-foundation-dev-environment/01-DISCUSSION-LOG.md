# Phase 1: Foundation & Dev Environment - Discussion Log

> **Audit trail only.** Do not use as input to planning, research, or execution agents.
> Decisions are captured in CONTEXT.md — this log preserves the alternatives considered.

**Date:** 2026-04-03
**Phase:** 01-foundation-dev-environment
**Areas discussed:** Project structure, Database pre-population, Initial screen content, Build config scope

---

## Project Structure

| Option | Description | Selected |
|--------|-------------|----------|
| Layer-based | ui/, data/, domain/ packages — matches Google's architecture guide | ✓ |
| Feature-based | feature/reading/, feature/card/, feature/spread/ — scales for large teams | |
| Hybrid | Layer-based top level, feature-based within layers | |

**User's choice:** Layer-based
**Notes:** Aligns with Google's official architecture and Now in Android reference app

## Database Pre-population

| Option | Description | Selected |
|--------|-------------|----------|
| createFromAsset() | Room copies pre-populated DB on first launch | ✓ |
| Insert on first launch | App inserts 78 cards programmatically | |

**User's choice:** createFromAsset()
**Notes:** Cards are ready immediately, no seed data code needed

## Initial Screen Content

| Option | Description | Selected |
|--------|-------------|----------|
| Empty reading list | Reading list screen with "No readings yet, tap + to start" | ✓ |
| Placeholder/welcome | Welcome screen with app name and intro | |

**User's choice:** Empty reading list
**Notes:** No separate welcome/onboarding screen in v1

## Build Config Scope

| Option | Description | Selected |
|--------|-------------|----------|
| Full production scaffolding | Hilt, KSP, Compose BOM, Nav 3, Room, M3, CI, lint/detekt | ✓ |
| Minimum viable | Just enough to build and run | |

**User's choice:** Full production scaffolding
**Notes:** Include CI config, Ktlint, Detekt groundwork even if tests come in Phase 4

## the agent's Discretion

- Exact Gradle version catalog naming conventions
- Specific drawable resource names for card images
- Exact empty state illustration/copy wording
- Room database version number and migration strategy details

## Deferred Ideas

None — all discussed areas were within Phase 1 scope
