# Roadmap: Drawn

## Milestones

- ✅ **v1.0 MVP** — Phases 1-7 (shipped 2026-04-12) — [milestones/v1.0-ROADMAP.md](milestones/v1.0-ROADMAP.md)

## Phases

- [x] **Phase 8: Database Schema** — New entities for decks, cards, tags with relational schema
- [x] **Phase 9: Repository Layer** — Data access layer with Flow-based repositories
- [ ] **Phase 10: ViewModel Layer** — State management for all v1.1 features
- [ ] **Phase 11: UI Implementation** — Bottom navigation, deck editor, tag manager screens
- [ ] **Phase 12: Backup/Restore** — JSON export/import with media handling

---

## Phase Details

### Phase 8: Database Schema

**Goal**: Establish database foundation for custom decks, tags, and favorites with relational schema

**Depends on**: Phase 7 (v1.0 completed)

**Requirements**: DECK-01, DECK-02, DECK-03, DECK-04, DECK-05, TAG-01, TAG-02, TAG-03, TAG-04, TAG-05, TAG-06, TAG-07

**Status**: ✅ COMPLETE

**Success Criteria** (what must be TRUE):

1. User can store custom decks with name, description, and card count — custom decks persist across app restarts
2. User can store custom cards with name, image path, keywords, and meaning — card data is queryable by deck
3. User can store tags with name and color — tags display with assigned colors in UI
4. User can associate multiple tags with single reading — many-to-many relationship works correctly
5. User can mark readings as favorites — favorite flag persists and is filterable

**Plans**: 1 plan

Plans:
- [x] 08-01-PLAN.md — Database entities and DAOs for custom decks, tags, and favorites

### Phase 9: Repository Layer

**Goal**: Enable data access through repositories with Flow-based reactive streams

**Depends on**: Phase 8

**Requirements**: DECK-01, DECK-02, DECK-03, DECK-04, DECK-05, TAG-01, TAG-02, TAG-03, TAG-04, TAG-05, TAG-06, TAG-07

**Status**: ✅ COMPLETE

**Success Criteria** (what must be TRUE):

1. User can query custom decks as reactive list — deck list updates when decks added/edited/deleted
2. User can query cards by deck — card picker shows deck-specific cards
3. User can query tags by reading — reading detail shows assigned tags
4. User can filter readings by tag — tag filter returns matching readings
5. User can filter favorites — favorites filter returns marked readings only

**Plans**: 2 plans

Plans:
- [x] 09-01-PLAN.md — New repositories (CustomCard, Tag, ReadingTag, extend Deck) ✅ DONE
- [ ] 09-02-PLAN.md — Extend ReadingRepository with favorites and tag filtering

### Phase 10: ViewModel Layer

**Goal**: Provide state management for all v1.1 feature UIs

**Depends on**: Phase 9

**Requirements**: DECK-01, DECK-02, DECK-03, DECK-04, DECK-05, TAG-01, TAG-02, TAG-03, TAG-04, TAG-05, TAG-06, TAG-07

**Success Criteria** (what must be TRUE):

1. User sees deck list update in real-time when deck created/edited/deleted
2. User can create and edit decks with all required fields
3. User can manage tags with create/edit/delete operations
4. User can assign/remove tags from reading in edit flow
5. User sees filtered reading list based on tag or favorite selection

**Plans**: TBD

### Phase 11: UI Implementation

**Goal**: Users can navigate between screens, manage decks, and organize readings with tags

**Depends on**: Phase 10

**Requirements**: NAV-01, NAV-02, NAV-03, NAV-04, DECK-01, DECK-02, DECK-03, DECK-04, DECK-05, TAG-01, TAG-02, TAG-03, TAG-04, TAG-05, TAG-06, TAG-07

**Success Criteria** (what must be TRUE):

1. User can tap bottom navigation tabs to switch between Readings, Decks, Settings — navigation works from any screen
2. User sees 3 tabs (Readings, Decks, Settings) in bottom nav bar — tabs are labeled and accessible
3. User can create new deck with name and description — deck appears in deck list after save
4. User can add custom cards to deck with name, image, keywords, meaning — cards persist in deck
5. User can edit and delete existing decks and their cards — changes persist after restart
6. User can select custom deck when recording reading — deck selection affects card picker
7. User can create tags with name and optional color — tag appears in tag list
8. User can assign multiple tags to reading — tags display on reading detail
9. User can filter reading list by tag — filtered list shows matching readings
10. User can mark reading as favorite — favorite indicator shows in list and detail
11. User can filter to show favorites only — favorites appear when filter active
12. User can view app version in Settings — version info displays correctly
13. User can access backup/restore from Settings — backup screen opens from nav

**Plans**: 3 plans

Plans:
- [ ] 11-01-PLAN.md — Bottom navigation with tabs
- [ ] 11-02-PLAN.md — Deck management screens
- [ ] 11-03-PLAN.md — Tag management and Settings

**UI hint**: yes

### Phase 12: Backup/Restore

**Goal**: Users can export and restore all data including custom decks and media files

**Depends on**: Phase 11

**Requirements**: BACKUP-01, BACKUP-02, BACKUP-03, BACKUP-04, BACKUP-05

**Success Criteria** (what must be TRUE):

1. User can export all readings to JSON file — file saves to user-selected location
2. Export includes all reading photos — media files are copied to backup location
3. User can import backup file — readings restore from JSON correctly
4. User can choose merge or replace for duplicates — duplicate handling works as selected
5. User can backup custom decks — deck data exports and imports correctly

**Plans**: TBD

---

## Progress

| Phase | Plans Complete | Status | Completed |
|-------|----------------|--------|-----------|
| 8. Database Schema | 1/1 | ✅ Complete | 2026-04-19 |
| 9. Repository Layer | 2/2 | ✅ Complete | 2026-04-19 |
| 10. ViewModel Layer | 1/1 | ✅ Complete | 2026-04-19 |
| 11. UI Implementation | 3/3 | 📋 Planned | - |
| 12. Backup/Restore | 0/1 | Not started | - |

---

*For previous milestone details, see [milestones/v1.0-ROADMAP.md](milestones/v1.0-ROADMAP.md)*
