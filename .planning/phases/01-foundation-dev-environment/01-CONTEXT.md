# Phase 1: Foundation & Dev Environment - Context

**Gathered:** 2026-04-03
**Status:** Ready for planning

<domain>
## Phase Boundary

Set up the local Android development environment, project scaffolding with full production dependencies, and the core Room database layer. The developer should be able to build, run, and preview the app on a physical device with a functioning empty reading list screen and a pre-populated database schema. This is the foundation that all subsequent phases build on.

</domain>

<decisions>
## Implementation Decisions

### Project Structure
- **D-01:** Layer-based package organization — `ui/`, `data/`, `domain/` packages at the top level, matching Google's official Android architecture guide
- **D-02:** This makes it easier for new contributors to navigate and aligns with the Now in Android reference app

### Database Pre-population
- **D-03:** Use Room's `createFromAsset()` to pre-populate the database with the 78 RWS cards on first launch
- **D-04:** No programmatic seed data insertion needed — the pre-populated DB file contains all card data (name, number, arcana type, description)
- **D-05:** User data tables (readings, reading_cards, photos) start empty — only the card catalog is pre-populated

### Initial Screen
- **D-06:** The app launches directly to the reading list screen
- **D-07:** Empty state shows a friendly message — "No readings yet, tap + to start" — with a visual cue for the add action
- **D-08:** No separate welcome/onboarding screen in v1

### Build Configuration
- **D-09:** Full production scaffolding from day one — not minimum viable
- **D-10:** Include: Kotlin 2.2.21, AGP 9.1.0, Compose BOM 2025.12.00, Hilt 2.59.2, KSP, Navigation Compose 3 (1.0.1), Room 2.8.4, Material 3 (1.4.0), Coil 3.4.0
- **D-11:** CI config, Ktlint, and Detekt set up in this phase (TEST-02, TEST-03 groundwork)
- **D-12:** JUnit 5 + MockK + Turbine test dependencies included, even if tests are written in Phase 4

### the agent's Discretion
- Exact Gradle version catalog naming conventions
- Specific drawable resource names for card images
- Exact empty state illustration/copy wording
- Room database version number and migration strategy details

</decisions>

<canonical_refs>
## Canonical References

**Downstream agents MUST read these before planning or implementing.**

### Project Setup
- `.planning/PROJECT.md` — Project context, core value, constraints
- `.planning/REQUIREMENTS.md` — DEV-01, DEV-02 requirements
- `.planning/ROADMAP.md` — Phase 1 goal and success criteria
- `.planning/research/STACK.md` — Complete technology recommendations with versions
- `.planning/research/ARCHITECTURE.md` — Architecture patterns, component boundaries, build order
- `.planning/research/PITFALLS.md` — Room migration corruption, pre-populated database copying pitfalls

### No external specs
No external specs — requirements fully captured in decisions above

</canonical_refs>

<code_context>
## Existing Code Insights

### Reusable Assets
- No existing code — greenfield project

### Established Patterns
- No established patterns yet — this phase establishes them

### Integration Points
- This phase creates the integration points that all subsequent phases connect to:
  - Room database instance (singleton via Hilt)
  - Navigation graph root
  - Theme/composition root
  - Repository interfaces

</code_context>

<specifics>
## Specific Ideas

- "Layer-based structure like Google's architecture guide" — not feature-based
- Empty reading list should feel inviting, not broken — "No readings yet, tap + to start"
- Full production scaffolding — don't defer DI, CI, or lint setup

</specifics>

<deferred>
## Deferred Ideas

None — discussion stayed within phase scope

</deferred>

---

*Phase: 01-foundation-dev-environment*
*Context gathered: 2026-04-03*
