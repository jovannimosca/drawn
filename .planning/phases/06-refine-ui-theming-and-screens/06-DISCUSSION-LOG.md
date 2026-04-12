# Phase 6: Refine UI theming and screens - Discussion Log

> **Audit trail only.** Do not use as input to planning, research, or execution agents.
> Decisions are captured in CONTEXT.md — this log preserves the alternatives considered.

**Date:** 2026-04-05
**Phase:** 06-refine-ui-theming-and-screens
**Areas discussed:** Animation & Transitions, Component States & Edge Cases, Visual Depth & Aesthetic Polish, Layout Density & Hierarchy

---

## Animation & Transitions

| Option | Description | Selected |
|--------|-------------|----------|
| Default Compose animations | Standard slide/fade transitions | |
| Custom slide + fade | Intentional slide directions per screen | |
| Shared element transitions | Tapped card expands into detail view | ✓ |

**User's choice:** Shared element transitions for screen-to-screen navigation

---

| Option | Description | Selected |
|--------|-------------|----------|
| Horizontal pager swipe | Swipe left/right between wizard steps | ✓ |
| Slide + fade with buttons | Guided Next/Back navigation | |
| No animation | Instant step swap | |

**User's choice:** Horizontal pager swipe for wizard steps

---

| Option | Description | Selected |
|--------|-------------|----------|
| 180° Y-axis flip animation | Visual card flip for reversed toggle | ✓ (agent discretion) |
| Instant 180° rotation | Static rotation | |

**User's choice:** You decide — agent picks 180° Y-axis flip animation

---

| Option | Description | Selected |
|--------|-------------|----------|
| Subtle scale on tap | 0.97x scale on press | |
| Ripple only (default M3) | Material 3 ripple effect | ✓ |

**User's choice:** Ripple only (default M3) for micro-interactions

---

| Option | Description | Selected |
|--------|-------------|----------|
| Subtle fade-in | Fade-in when list loads | ✓ |
| Skeleton placeholder | Skeleton layout matching card shapes | |
| No indicator needed | Local DB is fast enough | |

**User's choice:** Subtle fade-in for reading list loading

---

## Component States & Edge Cases

| Option | Description | Selected |
|--------|-------------|----------|
| Illustrated empty state | Themed illustration with call-to-action text | ✓ |
| Minimal text only | Simple centered text | |

**User's choice:** Illustrated empty state for reading list (no readings yet)

---

| Option | Description | Selected |
|--------|-------------|----------|
| Themed empty state | Mystical aesthetic message | ✓ |
| You decide | Agent picks | |

**User's choice:** Themed empty state for card picker (no search results)

---

| Option | Description | Selected |
|--------|-------------|----------|
| Themed error banner | Custom themed snackbar/inline message | |
| M3 Snackbar | Standard Material 3 snackbar | |
| You decide | Agent picks | ✓ |

**User's choice:** You decide — agent picks themed error handling approach

---

| Option | Description | Selected |
|--------|-------------|----------|
| You decide | Agent picks truncation approach | ✓ |

**User's choice:** You decide — agent picks long text handling in list cards

---

## Visual Depth & Aesthetic Polish

| Option | Description | Selected |
|--------|-------------|----------|
| Subtle gradient backgrounds | Dark purple-to-black gradients | |
| Solid colors only | Keep solid dark backgrounds | |
| Starry/nebula background | Subtle star pattern or nebula texture | ✓ |

**User's choice:** Starry/nebula background treatment on main screens

---

| Option | Description | Selected |
|--------|-------------|----------|
| Custom mystical icons | Custom SVG icons for suits/spreads | |
| Material icons with tint | M3 icons tinted with gold accent | ✓ |

**User's choice:** Material icons with gold tint across the app

---

| Option | Description | Selected |
|--------|-------------|----------|
| Gold accent dividers | Thin gold/amber lines between sections | ✓ |
| Subtle gradient dividers | Faint gradient dividers fading at edges | |
| Spacing-only separators | No visible dividers, spacing only | |

**User's choice:** Gold accent dividers between sections

---

| Option | Description | Selected |
|--------|-------------|----------|
| Gold shimmer on save | Brief gold shimmer/sparkle on save | ✓ |
| Simple success toast | Standard M3 snackbar | |
| No visual confirmation | Just navigate back | |

**User's choice:** Gold shimmer effect on save confirmation

---

## Layout Density & Hierarchy

| Option | Description | Selected |
|--------|-------------|----------|
| Compact cards (show more) | Title, date, spread, first line of notes | ✓ |
| Spacious cards (show less) | Larger cards with breathing room | |

**User's choice:** Compact cards for reading list

---

| Option | Description | Selected |
|--------|-------------|----------|
| Cards → Positions → Notes → Photos | Cards at top, most important first | |
| Spread overview → Cards → Notes → Photos | Spread name and meanings first | |
| You decide | Agent picks | ✓ |

**User's choice:** You decide — agent picks reading detail screen priority

---

| Option | Description | Selected |
|--------|-------------|----------|
| 2-column card grid | Compact and scannable | |
| Single column list | Clearer but more vertical space | |
| Spread-accurate layout | Cards match physical spread geometry | ✓ |

**User's choice:** Spread-accurate layout for reading detail screen

---

| Option | Description | Selected |
|--------|-------------|----------|
| Fixed 4-step scale | Standard M3 scale with custom weights | ✓ |
| Custom tarot scale | Larger body text for low-light reading | |

**User's choice:** Fixed 4-step typography scale (existing 7 M3 styles)

---

## the agent's Discretion

- Card flip animation duration and easing curve
- Starry/nebula background implementation approach
- Gold shimmer save animation specifics
- Error state exact implementation
- Long text truncation in reading list cards
- Reading detail screen information hierarchy ordering
- Spread-accurate layout implementation for complex spreads

## Deferred Ideas

- Custom deck UI support — v2 requirement
- Statistics and organization features — v2
- Light theme support — not requested
- Per-position interpretation notes — deferred from Phase 2
- Random/shuffle card draw — explicitly out of scope
