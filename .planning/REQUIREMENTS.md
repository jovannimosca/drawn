# Requirements: Drawn v1.1

**Milestone:** v1.1 Organization & Custom Decks
**Generated:** 2026-04-18

---

## v1.1 Requirements

### Navigation & UI Structure

- [ ] **NAV-01**: User can navigate between 3 main sections via bottom navigation bar
- [ ] **NAV-02**: Bottom navigation shows Readings, Decks, Settings tabs
- [ ] **NAV-03**: Settings displays app version and about information
- [ ] **NAV-04**: Backup/Restore accessible from Settings tab

### Custom Card Decks

- [ ] **DECK-01**: User can create a new custom deck with name and description
- [ ] **DECK-02**: User can add cards to custom deck with name, image, keywords, meaning
- [ ] **DECK-03**: User can edit existing custom deck and its cards
- [ ] **DECK-04**: User can delete custom deck
- [ ] **DECK-05**: User can select custom deck when recording a reading

### Reading Tags

- [ ] **TAG-01**: User can create custom tags with name and optional color
- [ ] **TAG-02**: User can assign multiple tags to a reading
- [ ] **TAG-03**: User can filter reading list by tags
- [ ] **TAG-04**: User can remove tags from reading
- [ ] **TAG-05**: User can delete unused tags
- [ ] **TAG-06**: User can mark readings as favorites
- [ ] **TAG-07**: User can filter favorites in reading list

### Backup/Restore

- [ ] **BACKUP-01**: User can export all readings to JSON file
- [ ] **BACKUP-02**: Export includes photos (media files)
- [ ] **BACKUP-03**: User can import backup file (restore)
- [ ] **BACKUP-04**: Duplicate handling (merge or replace choice)
- [ ] **BACKUP-05**: User can backup custom decks too

---

## Future Requirements (Deferred)

- Card frequency statistics
- Expanded spread library (more than 3 built-in spreads)
- PDF export
- Cloud backup/sync
- Theme toggle (dark/light)

---

## Out of Scope

- Cloud sync — local-only by design
- F-Droid publication — deferred
- Random/shuffle card draw — freeform entry only

---

## Traceability

| Requirement | Phase | Status |
|--------------|-------|--------|
| NAV-01 through NAV-04 | 11 - UI Implementation | Not started |
| DECK-01 through DECK-05 | 8-11 | Not started |
| TAG-01 through TAG-07 | 8-11 | Not started |
| BACKUP-01 through BACKUP-05 | 12 - Backup/Restore | Not started |

---

*Requirements defined: 2026-04-18*