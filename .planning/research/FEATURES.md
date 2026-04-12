# Feature Landscape

**Domain:** Tarot card reading tracker / journal (Android, local-only)
**Researched:** 2026-04-03

## Context

Drawn is **not** a tarot reading app (no AI interpretations, no random draws). It is a **reading tracker** — users do physical readings with real cards, then record the results digitally for posterity and pattern-tracking. This distinction is critical: the competitive set is tarot journals and logging tools, not reading generators.

Key competitors in this space:
- **Tarot Journal** (Google Play, 4.6★, 5K+ downloads) — closest direct competitor
- **Oracle Journal** (upcoming) — tarot/oracle digital journal with photos + notes
- **Tarot Journal by Steve Godfrey** (iOS, 63K+ downloads, 4.6★) — professional workspace
- **Labyrinthos** — includes a reading journal as a secondary feature
- **Golden Thread Tarot** — abandoned but pioneered the journal + pattern-tracking combo

---

## Table Stakes

Features users expect in any tarot reading tracker. Missing = product feels incomplete.

| Feature | Why Expected | Complexity | Notes |
|---------|--------------|------------|-------|
| **Record reading with spread + cards** | Core use case — users need to log what cards appeared in which positions | Medium | Form-style entry (freeform, not shuffle). Spread picker → card assignment per position |
| **Browse reading history** | Users need to revisit past readings | Low | Chronological list, pagination or lazy loading |
| **View reading details** | Full context of a past reading | Low | Display spread layout, assigned cards, notes, photos |
| **Text notes on readings** | Users record interpretations, feelings, outcomes | Low | Freeform text field per reading |
| **Photo attachments** | Users photograph their physical card layouts | Medium | Camera capture + gallery picker. Multiple photos per reading |
| **Standard 78-card RWS deck** | The universal reference deck | Low | Bundled images, visual card picker (grid/list) |
| **Common spreads (3-5)** | Celtic Cross, Three Card, Past/Present/Future are the most-used spreads | Low | Pre-built spread definitions with named positions |
| **Search past readings** | Users need to find specific readings by keyword | Medium | Full-text search across notes, card names, dates |
| **Edit existing readings** | Users make mistakes or add retrospective notes | Low | Same form as creation, pre-populated |
| **Delete readings** | Privacy control — users may want to remove readings | Low | With confirmation dialog |

---

## Differentiators

Features that set Drawn apart from basic tarot journals. Not expected, but valued.

| Feature | Value Proposition | Complexity | Notes |
|---------|-------------------|------------|-------|
| **Custom card decks** | Users own oracle decks, alternative tarot decks (Thoth, indie creators). Supporting these makes Drawn the go-to tracker for serious practitioners | High | Full deck editor: name, description, card images, keywords, categories, meanings. Import via camera/gallery |
| **Reading statistics** | "What cards appear most often?" — pattern recognition is the #1 reason people journal tarot | Medium | Card frequency analysis, suit distribution, Major vs Minor Arcana ratios, date-based trends |
| **Pin/favorite readings** | Some readings are profoundly meaningful — users want quick access to them | Low | Boolean flag, surfaced at top of history |
| **Folder/tag organization** | Users group readings by theme (love, career, shadow work), deck used, or intention | Medium | Many-to-many tagging system. Filter history by tag |
| **Querent name field** | Professional readers track readings for specific clients | Low | Optional text field per reading |
| **Date/time auto-capture** | Readings are time-sensitive — users want automatic timestamping | Low | Auto-set on creation, editable |
| **Reversed card support** | Many readers use reversals; the app should reflect this | Low | Toggle per reading or global setting. Visual indicator on card display |
| **Spread library** | Curated collection of spreads beyond the basics, with position descriptions | Medium | Browseable catalog with spread diagrams and position meanings |
| **Export reading** | Users want to share readings (with querents, on social media, in study groups) | Medium | PDF or image export of a single reading with card layout |
| **Dark mystical theme** | Tarot community expects atmospheric, immersive design — not a sterile white UI | Medium | Dark purples, golds, starry aesthetics. Core to the brand identity |

---

## Anti-Features

Features to explicitly NOT build. These conflict with Drawn's philosophy or target audience.

| Anti-Feature | Why Avoid | What to Do Instead |
|--------------|-----------|-------------------|
| **AI-generated readings** | Drawn is a tracker, not an interpreter. AI readings shift the product category and violate the "record your own readings" philosophy. Users who want AI have dedicated apps (Taroscoper, aimag.me) | Provide rich card reference data (meanings, keywords) so users can interpret themselves |
| **Random/shuffle card draw** | Users do physical readings. A digital draw feature encourages screen-based reading instead of physical practice, which the tarot community actively resists | Freeform entry only — the app records, it doesn't read |
| **Cloud sync / accounts** | Violates the local-only, privacy-first design. Tarot readings are deeply personal; many users explicitly avoid apps that upload their data | Local Room database. Future: optional export/import for manual backup |
| **Social features / sharing readings publicly** | Tarot readings are private. Social features (leaderboards, shared feeds) feel invasive and misaligned with the introspective nature of the practice | Private export (PDF/image) for user-initiated sharing only |
| **Live reader marketplace** | Connects users to human readers for paid sessions. This is a completely different business model (Tarot Life, Keen, Sanctuary) | Not applicable — Drawn is a personal tool, not a service platform |
| **Daily horoscope / astrology integration** | Feature bloat. Users who want astrology have Co-Star, The Pattern, Sanctuary. Dilutes Drawn's focused value proposition | Stay focused on tarot tracking |
| **Gamification / streaks / points** | Turns introspection into a chore. Tarot journaling is reflective, not competitive. Labyrinthos uses gamification for learning, but that's a different product category | Let intrinsic motivation drive usage |
| **Push notifications for "daily draw"** | Would require implementing a draw feature (anti-feature above) and feels pushy for a journal app | Users open the app when they have a reading to record |
| **Ads or subscription paywalls** | F-Droid distribution requires no proprietary dependencies. The tarot community strongly resents paywalled journal features (see Galaxy Tarot backlash) | Free and open source. No monetization in v1 |

---

## Feature Dependencies

```
Record reading → requires → Spread selection
Record reading → requires → Card assignment to positions
Record reading → optional → Text notes
Record reading → optional → Photo attachments
Record reading → optional → Querent name
Record reading → optional → Tags/folders

Browse history → requires → Reading records exist
Search readings → requires → Reading records exist
View reading details → requires → Reading records exist

Reading statistics → requires → Multiple reading records
Pin readings → requires → Reading records exist
Export reading → requires → Reading record with cards

Custom decks → optional → Reading card selection (can use custom deck instead of RWS)
Spread library → optional → Spread selection (extends pre-built spreads)
```

### Dependency Graph (ordered by build priority)

```
Phase 1: Foundation
  └── Standard 78-card RWS deck (bundled)
  └── Common spreads (3-5 pre-built)
  └── Record reading (spread + cards + notes)
  └── Browse reading history
  └── View reading details

Phase 2: Enrichment
  └── Photo attachments (depends on: Record reading)
  └── Search readings (depends on: Browse history)
  └── Edit/delete readings (depends on: Record reading)
  └── Reversed card support (depends on: Record reading)
  └── Dark mystical theme (cross-cutting)

Phase 3: Power User
  └── Custom card decks (depends on: Card selection)
  └── Reading statistics (depends on: Multiple reading records)
  └── Pin/favorite readings (depends on: Browse history)
  └── Folder/tag organization (depends on: Record reading)
  └── Spread library (depends on: Spread selection)
  └── Export reading (depends on: View reading details)
```

---

## MVP Recommendation

**Ship this first:**
1. Record readings with spread + card assignment (RWS deck, 3 common spreads)
2. Browse and search reading history
3. View reading details with notes
4. Photo attachments (camera + gallery)
5. Dark mystical theme

**Defer to post-MVP:**
- Custom card decks — high complexity, not needed for initial validation
- Reading statistics — needs sufficient data to be meaningful
- Spread library — 3-5 built-in spreads is enough for v1
- Export — nice-to-have, not core to the recording loop
- Tags/folders — organization becomes valuable after ~50+ readings

**Rationale:** The core loop is "do a physical reading → open app → record it → feel satisfied it's preserved." Everything else optimizes around that loop. Custom decks and statistics are the strongest differentiators but require the foundation to be solid first.

---

## Sources

- [Tarot Journal (Google Play) — 4.6★, 5K+ downloads](https://play.google.com/store/apps/details?id=com.tarot_journal) — HIGH confidence (direct competitor, active)
- [Oracle Journal — upcoming tarot/oracle journal app](https://oraclejournal.app/) — HIGH confidence (official site)
- [Tarot Journal by Steve Godfrey (iOS) — 63K+ downloads](https://mwm.ai/apps/tarot-journal/1271120458) — HIGH confidence (app store data)
- [Best Tarot Apps 2026 — TarotLingo comparison](https://tarotlingo.com/best-tarot-apps) — MEDIUM confidence (review site)
- [Best Tarot Apps & Sites 2026 — Taroscoper](https://www.taroscoper.com/guides/best-tarot-apps-and-sites-compared) — MEDIUM confidence (review site)
- [7 Best Tarot Apps 2026 — aimag.me](https://aimag.me/blog/best-tarot-apps) — MEDIUM confidence (review by app builder, disclosed bias)
- [Best Tarot Apps — Stoic Tarot](https://stoictarot.com/2025/12/21/best-tarot-apps-comparison/) — MEDIUM confidence (review site)
- [Tarot Forums — "Your Favorite Tarot Reading App?"](https://forum.thetarot.guru/t/your-favorite-tarot-reading-app/339) — MEDIUM confidence (community discussion, July 2025)
- [Labyrinthos App Store listing](https://apps.apple.com/us/app/labyrinthos-tarot-reading/id1155180220) — HIGH confidence (official listing)
- [Golden Thread Tarot — abandoned journaling pioneer](https://goldenthreadtarot.com) — HIGH confidence (abandoned but historically significant)
