# Pitfalls Research

**Domain:** Tarot Reading App — Custom Card Decks, Reading Tags, Backup/Restore
**Researched:** 2026-04-18
**Confidence:** MEDIUM-HIGH

## Critical Pitfalls

### Pitfall 1: Custom Deck Image Storage Without Size Limits

**What goes wrong:**
Users import custom deck images (sometimes 100MB+ per image), causing storage bloat and app crashes with "Canvas: trying to draw too large bitmap" errors. The app becomes unusable on devices with limited storage.

**Why it happens:**
- No image compression or resizing on import
- No maximum file size validation
- No thumbnail generation for deck preview
- Users import high-resolution scans meant for print

**How to avoid:**
- Enforce max image dimension (e.g., 1200px) on import with automatic resizing
- Compress JPEG images to max 500KB
- Generate thumbnails (200px) for deck picker UI
- Store original vs thumbnail separately
- Validate file size before import, reject with clear message

**Warning signs:**
- Deck editor slow to load previews
- Storage settings show app using excessive space
- Bitmap too large errors in logcat

**Phase to address:** Phase 1 — Database Schema & Models (deck editor data layer)

---

### Pitfall 2: Tag System Using JSON Column Instead of Relational Tables

**What goes wrong:**
Storing tags as a JSON array in a text column (e.g., `"[\"love\", \"career\"]"`). Query performance degrades to 400-500ms for 100K rows. Full table scans required for tag filtering. User experience becomes unacceptable.

**Why it happens:**
- Simpler initial implementation (no join tables)
- Seemed fine during development with small test data
- Android/Room documentation doesn't emphasize performance for tag queries
- LIKE-based search on JSON strings performs terribly

**How to avoid:**
- Use proper many-to-many relational schema: `ReadingTag` entity + `ReadingTagCrossRef` junction table
- Add indices on `tag_id` and `reading_id` in junction table
- Use JOIN queries instead of LIKE matching
- Reference: Simon Willison's research shows indexed many-to-many handles 100K rows in <1.5ms vs 400ms+ for JSON

**Warning signs:**
- Tag filter queries take >200ms
- Tag list UI feels sluggish
- Scroll performance drops as reading count grows

**Phase to address:** Phase 1 — Database Schema & Models (tags data layer)

---

### Pitfall 3: Backup Without Media Files

**What goes wrong:**
Backup exports only Room database (JSON), but readings have attached photos stored in app-specific storage. Users restore expecting photos but they are missing. Users lose valuable data.

**Why it happens:**
- Database backup is straightforward (Room + JSON export)
- Media files require different handling (copy files, not database records)
- Photos are in scoped storage, harder to access programmatically
- Oversight: "backup" seemed complete during testing (no photos in test data)

**How to avoid:**
- Include media directory in backup (copy photos to backup zip)
- Store relative paths in database, reconstruct on restore
- Validate backup completeness before restore (manifest file)
- Handle missing media gracefully during restore (show placeholder, don't crash)
- Test backup/restore with real photos attached to readings

**Warning signs:**
- Backup file suspiciously small for large reading history
- Restore completes but photos don't appear
- Users report "incomplete backup"

**Phase to address:** Phase 2 — Backup/Restore Implementation

---

### Pitfall 4: Restore Creates Duplicate Readings

**What goes wrong:**
User restores a backup, then accidentally restores again or restores on top of existing data. All readings appear twice (or more). No deduplication logic handles this scenario.

**Why it happens:**
- No unique identifier strategy for imported readings
- No duplicate detection on restore
- No "merge" vs "replace" choice for user
- Assumption: restore only happens on fresh install

**How to avoid:**
- Use UUIDs for reading IDs (generated at creation), preserve on restore
- Check for existing readings with same timestamp + cards before import
- Offer user choice: "Merge with existing" vs "Replace all"
- Show preview of what will be imported before confirming

**Warning signs:**
- Reading count doubles after restore
- Same reading appears multiple times in history

**Phase to address:** Phase 2 — Backup/Restore Implementation

---

### Pitfall 5: Deck Editor Missing Card Count Validation

**What goes wrong:**
User creates a "custom oracle deck" with only 20 cards, then tries to use it with a Celtic Cross spread (10 cards). App crashes or shows confusing error. Or: user creates deck with gaps in numbering causing display issues.

**Why it happens:**
- No validation that deck has minimum cards for available spreads
- No spread-specific card count requirements checked
- Card numbering assumes consecutive sequence

**How to avoid:**
- Store `minCardCount` and `maxCardCount` on deck entity
- Validate deck has enough cards when selected for reading
- Show clear error: "Deck has X cards but Celtic Cross requires 10"
- Handle variable-length decks gracefully in UI

**Warning signs:**
- User reports "app crashes when I select my deck"
- Custom deck appears available but isn't usable

**Phase to address:** Phase 1 — Database Schema & Models (deck validation)

---

### Pitfall 6: Tag UI Clutter With Many Tags

**What goes wrong:**
Reading has 15+ tags. In list view, tags overflow container. On reading detail, tags take up entire screen. User can't tap tags outside visible area. From Tarot Journal Play review: "if I have a large tagging system, the list will just go off-screen, so I can't click on any tags that are outside the box area".

**Why it happens:**
- Tags rendered as full-width chips
- No horizontal scrolling for tag container
- No "show more" collapse for excessive tags
- Material 3 chips don't wrap nicely in constrained widths

**How to avoid:**
- Wrap tags (flexbox-style) not horizontal scroll
- Show max 5 tags, "+N more" button expands full list
- In detail view, use lazy column for tags
- Test with 20+ tags to verify UI handles it

**Warning signs:**
- Tags cut off on small screens
- User can't access tags on edge of container

**Phase to address:** Phase 1 — Reading Tags UI

---

### Pitfall 7: Deck Image Path Not Updated on Restore

**What goes wrong:**
User backs up on device A, restores on device B. Custom deck images fail to load because paths point to device A's storage. Or: custom deck images lost after app reinstall.

**Why it happens:**
- Image paths stored as absolute paths (e.g., `/data/user/0/com.drawn/files/decks/...`)
- Backup doesn't include media files
- New install has different internal storage path

**How to avoid:**
- Store images in app-specific directory only (not external)
- Use relative paths in database (e.g., `decks/uuid/image.png`)
- On restore, map relative paths to new app directory
- Include images in backup archive

**Warning signs:**
- Custom deck images blank after restore
- "File not found" errors in logcat after restore

**Phase to address:** Phase 2 — Backup/Restore Implementation

---

## Technical Debt Patterns

| Shortcut | Immediate Benefit | Long-term Cost | When Acceptable |
|----------|-------------------|----------------|-----------------|
| Store tags as JSON string | No migration needed, simple schema | Slow queries at scale, no indexing | Never — properly benchmark shows massive difference |
| Skip image compression | Preserve original quality | Storage bloat, OOM crashes | Only for archive/export feature, not production |
| Skip backup media files | Simpler implementation | Data loss on restore | Never — incomplete backup is worse than no backup |
| Use absolute file paths | Easier to debug | Breaks on restore, device-to-device | Never — relative paths always |
| Skip deck validation | Faster initial feature | Confusing errors, crashes | Only if deck editor disabled after creation |

---

## Integration Gotchas

| Feature | Common Mistake | Correct Approach |
|---------|----------------|------------------|
| Photo Picker | Using READ_EXTERNAL_STORAGE (deprecated) | Use system Photo Picker API (no permissions needed) |
| Scoped Storage | Trying to access app-specific files directly on Android 11+ | Use MediaStore for external, app-specific for internal |
| Coil Image Loading | Not handling missing images gracefully | Show placeholder, don't crash on null |
| Room Migration | Backup created with newer schema than app | Validate schema version before restore, show clear error |

---

## Performance Traps

| Trap | Symptoms | Prevention | When It Breaks |
|------|----------|------------|----------------|
| JSON tag queries | Tag filter takes 500ms+ | Relational tables + indices | At ~1000 readings with 5+ tags each |
| Large deck image loading | App freezes on deck selection | Thumbnail generation, lazy loading | With decks >20 cards, full-size images |
| Backup with uncompressed media | Backup takes 10+ minutes, huge file | Compress images in backup | With readings containing multiple photos |
| N+1 query for tags on reading list | Each reading triggers separate tag query | Single JOIN query or batch load | At ~50+ readings displayed |

---

## Security Mistakes

| Mistake | Risk | Prevention |
|---------|------|------------|
| Backup stored in public directory | Other apps can read user data | Use app-private external storage or encrypted internal |
| No backup validation | Corrupted backup corrupts database | Validate JSON structure before restore, check schema version |
| Export includes image paths to absolute system paths | Information leak about device | Sanitize paths, don't expose absolute filesystem paths |

---

## UX Pitfalls

| Pitfall | User Impact | Better Approach |
|---------|-------------|-----------------|
| No way to search by tag in list | User can't find readings by tag | Add filter chips to reading list, tap to filter |
| Deck selection in wizard forgets selection | User frustrated, must re-select | Persist selection in ViewModel during wizard flow |
| Backup doesn't show what's included | Users don't know what they're restoring | Show preview: "This backup includes 47 readings, 23 photos" |
| Restore fails silently on partial data | User thinks backup worked, data missing | Clear success/failure messages, show what succeeded vs failed |
| Tag creation requires too many taps | Users don't tag readings | Quick-add from reading detail with suggestions |

---

## "Looks Done But Isn't" Checklist

- [ ] **Custom Deck:** Images stored — but are they included in backup?
- [ ] **Custom Deck:** Deck shows in picker — but does it have enough cards for spreads?
- [ ] **Tags:** Tags save to database — but are queries fast enough with 500+ readings?
- [ ] **Tags:** Tag filter UI works — but does it handle 20 tags without overflow?
- [ ] **Backup:** JSON exports successfully — but are photos included?
- [ ] **Restore:** Data loads into database — but are there now duplicates?
- [ ] **Deck Editor:** User can add cards — but can they re-order or delete cards?
- [ ] **Reading:** Tags display — but can user filter history by tag?

---

## Recovery Strategies

| Pitfall | Recovery Cost | Recovery Steps |
|---------|---------------|----------------|
| Duplicate readings after restore | MEDIUM | Delete duplicates by timestamp+card combination, add deduplication on next restore |
| Missing photos after restore | LOW | Re-attach photos manually, or use corrupted backup as reference |
| Slow tag queries | LOW | Add indices to junction table, benchmark shows 100x improvement |
| Custom deck images broken | MEDIUM | Ask user to re-import deck, or attempt to relocate by matching names |

---

## Pitfall-to-Phase Mapping

How roadmap phases should address these pitfalls.

| Pitfall | Prevention Phase | Verification |
|---------|------------------|--------------|
| Image storage without limits | Phase 1: Database Schema & Models | Import 10MB image, verify compression to <500KB |
| JSON tag storage vs relational | Phase 1: Database Schema & Models | Query 100 readings with tags, verify <50ms |
| Backup missing media | Phase 2: Backup/Restore | Backup reading with photo, restore, verify photo present |
| Restore duplicates | Phase 2: Backup/Restore | Restore twice, verify no duplicates |
| Deck card count validation | Phase 1: Database Schema & Models | Create 5-card deck, select Celtic Cross, verify clear error |
| Tag UI clutter | Phase 1: Reading Tags UI | Add 20 tags to reading, verify no overflow on narrow screen |
| Deck image paths not portable | Phase 2: Backup/Restore | Backup to device B, restore, verify images load |

---

## Sources

- **Simon Willison (2026):** SQLite Tags Benchmark comparing 5 strategies — indexed many-to-many <1.5ms vs JSON full table scan 400-500ms
- **AnkiDroid Issues:** Image handling bugs (#6184 - 100MB image crash, #18744 - gallery attachment crash)
- **Card-Forge/Android:** Scoped storage issues on Android 11+ (2024)
- **Tarot Journal Play Store Reviews:** Tag UI overflow issues reported by users
- **Arcana Land Deck Spec:** Custom deck file structure recommendations (deck.toml format)
- **Room Documentation:** Many-to-many relationship patterns and query optimization
- **Android Photo Picker:** System API documentation for permissions-free photo selection

---
*Pitfalls research for: Tarot Reading App — Custom Card Decks, Reading Tags, Backup/Restore*
*Researched: 2026-04-18*