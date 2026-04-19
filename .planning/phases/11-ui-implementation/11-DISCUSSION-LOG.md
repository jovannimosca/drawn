# Phase 11: UI Implementation - Discussion Log

> **Audit trail only.** Do not use as input to planning, research, or execution agents.
> Decisions are captured in CONTEXT.md — this log preserves the alternatives considered.

**Date:** 2026-04-19
**Phase:** 11-ui-implementation
**Areas discussed:** Navigation architecture, Deck screens, Tag screens, Filter UI

---

## Navigation Architecture

| Option | Description | Selected |
|--------|------------|----------|
| Bottom nav replaces root | Each tab has own back stack | ✓ |
| Bottom nav wraps existing | Tabs go to root screens | |

**User's choice:** Bottom nav replaces root (Recommended)
**Notes:** Navigation 3 adapts for bottom nav with tabs

---

## Deck UI Pattern

| Option | Description | Selected |
|--------|------------|----------|
| List → detail flow | Simple list, tap opens detail | ✓ |
| Full-screen wizard | Multi-step form | |

**User's choice:** List → detail flow (Recommended)

---

## Card Management

| Option | Description | Selected |
|--------|------------|----------|
| Integrated in deck detail | Card section in detail screen | ✓ |
| Separate screen | Different screen for cards | |

**User's choice:** Integrated in deck detail (Recommended)

---

## Card Fields

| Option | Description | Selected |
|--------|------------|----------|
| Name + image | Simple | |
| Name + image + keywords + meaning | Full | ✓ |
| You decide | Agent decides | |

**User's choice:** Name + image + keywords + meaning + reversedMeaning

---

## Tag UI Pattern

| Option | Description | Selected |
|--------|------------|----------|
| List → modal | Create/edit inline | ✓ |
| Full-screen editor | Full-screen form | |

**User's choice:** List → modal (Recommended)

---

## Tag Assignment

| Option | Description | Selected |
|--------|------------|----------|
| Chips in detail | Chips with add/remove | ✓ |
| Full-screen selector | Full selection screen | |

**User's choice:** Chips in detail (Recommended)

---

## Filter UI

| Option | Description | Selected |
|--------|------------|----------|
| Filter sheet | FAB/icon opens sheet | ✓ |
| Full filter screen | Full screen | |

**User's choice:** Filter sheet

---

## the agent's Discretion

- Exact filter sheet layout details
- Settings screen items beyond version
- Transition animations
- Deck form field order