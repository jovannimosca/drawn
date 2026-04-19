# Feature Landscape: Custom Card Decks, Reading Tags, and Backup/Restore

**Project:** Drawn v1.1
**Researched:** 2026-04-18
**Overall confidence:** HIGH

## Executive Summary

This research covers three new feature areas for Drawn v1.1: custom card decks, reading tags, and backup/restore. The research draws from existing tarot apps in the ecosystem (Uni Tarot, Paper Tape Tarot, Deckible, Tarot Journal) to identify table stakes, differentiators, and anti-features. All three features are achievable with the existing Kotlin + Jetpack Compose + Room stack. Key dependencies are identified: custom decks require the existing card selection UI to be deck-aware; tags require the metadata model to be extended; backup/restore requires structured JSON serialization (already planned via Kotlinx Serialization).

## Key Findings Summary

| Feature | Table Stakes | Complexity | Dependencies |
|---------|-------------|------------|-------------|
| Custom Card Decks | Deck editor with name, cards, images, meanings | Medium | Card selection UI, image picker |
| Reading Tags | Tag CRUD, filter readings by tag | Low | Reading model extension |
| Backup/Restore | JSON export/import to device storage | Medium | File picker, serialization |

---

## Table of Contents

1. [Custom Card Decks](#1-custom-card-decks)
2. [Reading Tags](#2-reading-tags)
3. [Backup/Restore](#3-backuprestore)

---

## 1. Custom Card Decks

### What Users Expect (Table Stakes)

The following features are expected in any tarot app that supports custom decks. Missing any of these results in an incomplete product feel.

| Feature | Description | Why Expected | Complexity |
|---------|-------------|-------------|------------|
| Deck name and description | Name displayed in deck selector, description for context | Users need to identify which deck they are using | Low |
| Add/remove cards | CRUD operations for individual cards within a deck | Custom decks may have non-standard card counts (22, 36, 79 cards per Uni Tarot) | Medium |
| Card image upload | Capture or select image for each card | Core to tarot — users want their own artwork/photography | Medium |
| Card title/name | Editable name for each card | Different decks use different names | Low |
| Card meaning/description | Editable text explaining card interpretation | Users create their own guidebook | Low |
| Reversed meaning | Optional separate text for reversed orientation | Standard tarot practice (per Paper Tape Tarot, Uni Tarot) | Low |
| Search cards | Find cards by name, description, keywords | Decks with 78+ cards need searchability | Low |
| Deck activation toggle | Enable/disable custom deck without deletion | Users may have multiple decks | Low |

### What Differentiates (Differentiators)

These features add value but are not strictly required. They set the product apart.

| Feature | Value Proposition | Complexity | Notes |
|---------|-------------------|------------|-------|
| Card keywords/tags | Label cards with keywords for cross-deck search | Low | Enables finding "love" cards across all decks |
| Deck duplication | Clone existing deck as starting point | Low | Template from RWS for modifications |
| Category grouping | Group cards by Major Arcana, suits, elemental categories | Low | Aligns with standard tarot structure |
| Mixed deck reading | Draw from multiple custom decks in one reading | High | Premium feature in Deckible, complex UI |
| Deck image/cover | Custom thumbnail for deck selector | Low | Visual personalization |
| Deck import/export | Share decks with other users | Medium | Requires file format definition |

### Anti-Features (Explicitly NOT Build)

| Anti-Feature | Why Avoid | What to Do Instead |
|--------------|-----------|------------------|
| In-app deck store/marketplace | Adds complexity, backend requirements, curation burden | Focus on local-only custom deck editing |
| Deck version control | Over-engineering for personal app | Simple overwrite, no need for version history |
| Cloud deck sync | Violates local-only constraint | Manual backup/restore serves this need |
| AI-generated card meanings | Unnecessary, unreliable | User provides their own meanings |

### Feature Dependencies

```
Card Selection Screen
    ↓ (must be deck-aware)
Deck Selector
    ↓
Deck Editor
    ↓
    ├── Card Editor (image, name, meaning, reversed)
    ├── Deck Settings (name, description, activation)
    └── Card List (search, filter, reorder)
```

**Critical dependency:** The existing card selection UI (from v1.0) must be updated to support deck selection before the deck editor can be used. The card selection flow currently assumes a single hardcoded RWS deck. This is the blocking dependency.

### Implementation Notes

- **Image storage:** Custom card images should be stored in app-internal storage (`context.getExternalFilesDir()` or app-specific directory) to avoid MediaStore exposure. Room stores file paths.
- **Image format:** Support JPEG and PNG. Resize to consistent dimensions (e.g., 600x1000) on import to manage storage.
- **Deck data model:** Needs `Deck` entity with `id`, `name`, `description`, `isActive`, `createdAt`, `updatedAt`. Card entity links to `deckId`.
- **Default deck:** RWS deck remains as built-in "system" deck that cannot be deleted, only hidden.

### MVP Recommendation

Prioritize:

1. Deck name and activation toggle (table stakes)
2. Card image upload via camera/gallery (table stakes — core value)
3. Card title and meaning editor (table stakes)
4. Deck selector in card selection flow (dependency enabler)

Defer: Mixed deck reading, deck import/export — these add significant complexity without blocking core use case.

---

## 2. Reading Tags

### What Users Expect (Table Stakes)

Reading tags are a simple organizational feature. Users expect basic tagging capability.

| Feature | Description | Why Expected | Complexity |
|---------|-------------|-------------|------------|
| Tag creation | Create new tag with name | Users define their own organization scheme | Low |
| Tag assignment | Apply tags to readings | Core function — tag reading at creation or edit time | Low |
| Tag removal | Remove tag from reading | Correct mistakes | Low |
| Tag filtering | Filter reading list by tag | Find readings by topic (e.g., all "love" readings) | Low |
| Tag deletion | Delete unused tags | Clean up stale tags | Low |

### What Differentiates (Differentiators)

| Feature | Value Proposition | Complexity | Notes |
|---------|-------------------|------------|-------|
| Tag colors | Color-code tags for visual organization | Low | Visual grouping without creating categories |
| Tag suggestions | Suggest tags based on past usage | Low | Reduces friction, common pattern |
| Multi-tag filter | Filter by multiple tags (AND/OR) | Medium | Find readings that match multiple criteria |
| Reading count per tag | Show reading count next to tag | Low | Helps prioritize, shows usage patterns |
| Quick filter chips | Filter with tap on tag chips in reading list | Low | Common UX pattern |

### Anti-Features

| Anti-Feature | Why Avoid | What to Do Instead |
|--------------|-----------|------------------|
| Hierarchical tags/categories | Adds complexity, conflicts with flat tag simplicity | Use naming conventions (e.g., "area:career") |
| Nested tags | Over-engineering for personal app | Flat tag list suffices |
| Tag sharing/sync | Violates local-only constraint | Backup/restore handles转移 |

### Feature Dependencies

```
Reading List Screen
    ↓
Reading Detail/Edit Screen
    ↓
Tag Selector (multi-select)
    ↓
Tag Entity (many-to-many with Reading)
```

**No blocking dependencies.** Tags can be implemented independently without changes to other features.

### Data Model

Tags are a many-to-many relationship with readings. This avoids duplicating tag names and enables querying readings by tag.

```
ReadingEntity (existing)
    ↓ (many-to-many)
ReadingTagEntity: readingId, tagId
    ↓
TagEntity: id, name, color (optional), createdAt
```

### MVP Recommendation

Prioritize:

1. Tag creation and assignment (table stakes)
2. Tag filtering in reading list (table stakes — enables organization)
3. Tag deletion (cleanup)

Defer: Tag colors, multi-tag filter — useful but not blocking.

---

## 3. Backup/Restore

### What Users Expect (Table Stakes)

Local-only apps need local backup. Users expect to export their data and restore it if needed.

| Feature | Description | Why Expected | Complexity |
|---------|-------------|-------------|------------|
| Export to JSON | Export all readings to JSON file | Standard backup expectation | Medium |
| Import from JSON | Restore readings from JSON file | Standard restore expectation | Medium |
| Export to device storage | Save to Downloads/Documents folder | User control over backup location | Medium |
| Import from file picker | Select file to restore | User control over restore source | Medium |
| Clear backup destination indicator | Show where file was saved | User knows where to find backup | Low |

### What Differentiates (Differentiators)

| Feature | Value Proposition | Complexity | Notes |
|---------|-------------------|------------|-------|
| Selective export | Export only tagged readings | Filter what gets backed up | Medium |
| Backup timestamp in filename | Auto-name files with date | Easier file management | Low |
| Export progress indicator | Show progress for large exports | Better UX for many readings | Low |
| Merge on import | Combine with existing readings | Avoid data loss | Medium |
| Custom card images in backup | Include custom deck images | Full backup for custom decks | High |
| Backup verification | Validate JSON before import | Catch errors early | Low |

### Anti-Features

| Anti-Feature | Why Avoid | What to Do Instead |
|--------------|-----------|------------------|
| Cloud backup (Google Drive, etc.) | Violates local-only constraint | Export to local storage, user manages cloud sync |
| Auto-backup scheduling | Adds background processing, complexity | Manual export is sufficient |
| Encrypted backup | Unnecessary for personal local app | JSON is human-readable, user can manage encryption |
| Incremental backup | Over-engineering | Full export is simple and reliable |

### JSON Export Format

The export should be a self-contained JSON file with all reading data.

```json
{
  "version": "1.1",
  "exportedAt": "2026-04-18T12:00:00Z",
  "readings": [
    {
      "id": "uuid",
      "date": "2026-04-15T10:30:00Z",
      "spreadId": "celtic-cross",
      "deckId": "rws",
      "deckName": "Rider-Waite-Smith",
      "question": "What should I know about my career?",
      "cards": [
        {
          "position": 1,
          "cardId": "major_01_high_priestess",
          "reversed": false,
          "note": "The High Priestess represents intuition"
        }
      ],
      "notes": "Key insight: trust my inner voice",
      "tags": ["career", "intuition"],
      "photos": []
    }
  ],
  "tags": [
    { "id": "uuid", "name": "career", "color": "#FF5722" }
  ],
  "customDecks": [
    {
      "id": "uuid",
      "name": "My Oracle Deck",
      "description": "Custom 36-card deck",
      "cards": [
        {
          "id": "uuid",
          "name": "New Beginning",
          "imagePath": "cards/my_oracle_01.jpg",
          "meaning": "A fresh start"
        }
      ]
    }
  ]
}
```

### Restore Behavior

| Scenario | Behavior | Notes |
|----------|---------|-------|
| Import when readings exist | Merge with existing readings | Generate new UUIDs to avoid conflicts |
| Import when tag already exists | Reuse existing tag | Match by name, case-insensitive |
| Import when custom deck exists | Prompt user: skip, replace, or create new |三种选项 |
| Import with missing card images | Log warning, continue import | User can re-add images later |
| Import corrupted JSON | Show error, do not modify data | Validate before import |

### Feature Dependencies

```
Backup/Restore Settings
    ↓
    ├── Export Flow: JSON serialization → File picker → Save to Downloads
    └── Import Flow: File picker → JSON parsing → Validation → Merge
```

**No blocking dependencies.** Uses existing Kotlinx Serialization (already in stack) and platform file picker APIs.

### MVP Recommendation

Prioritize:

1. Full export to JSON (table stakes)
2. Full import from JSON (table stakes)
3. File picker integration (enables user control)

Defer: Selective export, merge options — simple full export/import suffices for MVP.

---

## Cross-Feature Dependencies

### Summary Dependency Graph

```
Custom Decks (v1.1)
    │
    ├── Requires: Card Selection UI → Deck Selector
    │   (blocking — update existing UI)
    │
    └── Enables: Card images in backup
        │
        └── Backup/Restore (v1.1)
            │
            ├── Requires: JSON serialization
            │   (Kotlinx Serialization — already in stack)
            │
            └── Enables: Full deck restore
                │
                └── Reading Tags (v1.1)
                    │
                    └── No dependencies — can implement independently
```

### Implementation Order Recommendation

1. **Reading Tags** — Lowest complexity, no dependencies. Implement first to establish data model patterns.

2. **Custom Card Decks** — Medium complexity, requires UI changes. Needs card selection flow update before editor.

3. **Backup/Restore** — Medium complexity, depends on data model. Implement after custom decks to include deck images.

---

## Confidence Assessment

| Feature Area | Confidence | Reason |
|-------------|------------|--------|
| Custom Card Decks | HIGH | Multiple reference apps (Uni Tarot, Paper Tape Tarot) demonstrate feasibility |
| Reading Tags | HIGH | Standard pattern in journaling apps, simple data model |
| Backup/Restore | HIGH | JSON export is standard pattern, Room + Kotlinx Serialization supports it |

---

## Sources

- Uni Tarot app features (Google Play) — https://play.google.com/store/apps/details?id=com.ucdevs.utarot
- Paper Tape Tarot app (App Store) — https://apps.apple.com/us/app/paper-tape-tarot/id6463463569
- Deckible digital deck publishing — https://cards.deckible.com/
- Tarot Journal app (Google Play) — https://play.google.com/store/apps/details?id=com.tarot_journal
- Galaxy Tarot backup feature — https://galaxy-tarot.apk.gold/
- Digital tarot journaling practices — https://www.astrologyjuno.com/your-ultimate-guide-to-tarot-journaling-track-your-readings/

---

## Gaps to Address

- **Deck import/export format:** Research did not find a standard community format for sharing tarot decks. Consider proprietary JSON for v1.1.
- **Card image compression:** No research on optimal compression ratios for tarot card images. Recommend testing on storage usage.
- **Large reading count performance:** No data on performance at scale (1000+ readings). May need pagination if issues arise in testing.