# Phase 10: ViewModel Layer - Discussion Log

> **Audit trail only.** Do not use as input to planning, research, or execution agents.
> Decisions are captured in CONTEXT.md — this log preserves the alternatives considered.

**Date:** 2026-04-19
**Phase:** 10-viewmodel-layer
**Areas discussed:** State pattern, Deck operations, Tag operations, Reading list filtering

---

## State Pattern

| Option | Description | Selected |
|--------|------------|----------|
| Consistent stateIn() | Reactive, concise, automatic updates — matches ReadingListViewModel | ✓ |
| Manual MutableStateFlow | More control for complex forms — matches AddReadingViewModel | |
| You decide | Standard pattern | |

**User's choice:** Consistent stateIn() (Recommended)
**Notes:** Pattern matching existing ReadingListViewModel for reactive UI updates.

---

## Deck Operations

| Option | Description | Selected |
|--------|------------|----------|
| Create + Edit + Delete | Full CRUD, list decks | |
| List + Create only | Just list and create | |
| Full CRUD + Card management | Also manage cards within deck | ✓ |

**User's choice:** Full CRUD + Card management
**Notes:** Includes creating, editing, deleting decks and managing cards within decks.

---

## Tag Operations

| Option | Description | Selected |
|--------|------------|----------|
| Full CRUD | Create, edit, delete, list tags | ✓ |
| Create + List only | Just create and list | |
| Assign only | Just assign/remove from readings | |

**User's choice:** Full CRUD (Recommended)
**Notes:** Same pattern as Deck operations.

---

## Tag Colors

| Option | Description | Selected |
|--------|------------|----------|
| Preset palette | 8-12 predefined colors | ✓ |
| Any color picker | Full color picker | |
| No colors | Tags have no colors | |

**User's choice:** Preset palette (Recommended)
**Notes:** Simpler, consistent UI — user picks from palette, color is optional.

---

## Reading List Filtering

| Option | Description | Selected |
|--------|------------|----------|
| Tag + Favorite filters | Filter by tags, filter favorites | ✓ |
| Tag filter only | Just filter by tag | |
| You decide | Standard approach | |

**User's choice:** Tag + Favorite filters
**Notes:** Integrate tag and favorite filters with existing ReadingListViewModel.

---

## the agent's Discretion

- Filter logic: AND vs OR for multi-tag selection (recommend OR)
- Color palette exact colors (8-10 mystical colors)
- Any additional state management details not explicitly discussed