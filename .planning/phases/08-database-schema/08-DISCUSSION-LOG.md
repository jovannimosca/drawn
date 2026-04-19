# Phase 8: Database Schema - Discussion Log

> **Audit trail only.** Do not use as input to planning, research, or execution agents.
> Decisions are captured in CONTEXT.md — this log preserves the alternatives considered.

**Date:** 2026-04-18
**Phase:** 8-database-schema
**Areas discussed:** Custom deck fields, Tag relationship, Favorites storage, Schema migration

---

## Custom Deck Fields

| Option | Description | Selected |
|--------|-------------|----------|
| Standard | name, image, uprightMeaning, reversedMeaning, sortOrder | ✓ |
| Full | name, image, uprightMeaning, reversedMeaning, keywords, category | |
| Minimal | Just name and image | |

**User's choice:** Standard (recommended)
**Notes:** Standard covers most use cases without over-complicating

---

## Tag Relationship

| Option | Description | Selected |
|--------|-------------|----------|
| With colors | Tag has name + color (hex) | ✓ |
| Name only | Just name — simpler | |

**User's choice:** With colors
**Notes:** User can color-code tags for organization

---

## Favorites Storage

| Option | Description | Selected |
|--------|-------------|----------|
| isFavorite only | Boolean on ReadingEntity | ✓ |
| Favorite + pinned | Both favorite and pinned | |

**User's choice:** isFavorite only
**Notes:** Simple boolean for filtering

---

## Schema Migration

| Option | Description | Selected |
|--------|-------------|----------|
| Auto-migration | Room detects changes via ExportSchema | ✓ |
| Manual | Manual Migration objects | |

**User's choice:** Auto-migration (recommended)
**Notes:** Works for adding tables + one column

---

## the agent's Discretion

None — all decisions made by user.

## Deferred Ideas

None — discussion stayed within phase scope.