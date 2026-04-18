---
phase: quick
plan: 01
subsystem: CI/CD
tags: [release, semantic-release, github-actions]
dependency_graph:
  requires: []
  provides: [semantic-release-workflow]
  affects: [.github/workflows/release.yml]
tech_stack:
  added: [cycjimmy/semantic-release-action]
  patterns: [conventional-commits, auto-versioning, auto-release-notes]
key_files:
  created: []
  modified: [.github/workflows/release.yml]
decisions:
  - "Migrated from client-side version bumping to semantic-release for automatic versioning based on conventional commits"
  - "Removed workflow_dispatch version_override inputs since semantic-release handles versioning automatically"
  - "Added separate build-only job for skip_release scenarios"
metrics:
  duration: "~1 minute"
  completed_date: "2026-04-18"
---

# Quick Task 260418-h7x: Semantic Release Migration Summary

Migrated the GitHub Actions release workflow from client-side script parsing to semantic-release for automatic versioning based on conventional commits.

## Execution Summary

| Task | Name | Commit | Files |
| ---- | ---- | ------ | ----- |
| 1 | Migrate release workflow to semantic-release | `a6ef464` | `.github/workflows/release.yml` |

## What Was Done

- **Added semantic-release action**: Integrated `cycjimmy/semantic-release-action@v4` with plugins:
  - `@semantic-release/commit-analyzer` — analyzes commits for version bumps
  - `@semantic-release/release-notes-generator` — auto-generates release notes
  - `@semantic-release/github` — creates GitHub release with proper tags

- **Removed legacy elements**:
  - Manual version parsing/bumping in "Bump version" step
  - Manual release note generation via GitHub Script
  - `workflow_dispatch` version_override inputs (semantic-release handles this)

- **Kept**:
  - Path filters for CI optimization (app/, gradle files)
  - `workflow_dispatch` with skip_release option for build-only scenarios
  - Keystore decoding and build steps

- **Added separate build-only job**: For scenarios where skip_release is true, a dedicated job builds the APK without creating a release

## Verification

- [x] Release workflow exists at `.github/workflows/release.yml`
- [x] semantic-release action is configured
- [x] Manual version bumping removed
- [x] Manual release note generation removed

## Self-Check: PASSED

- Commit `a6ef464` exists and contains the migration changes
- File `.github/workflows/release.yml` modified with semantic-release

## Deviations from Plan

None — plan executed exactly as written.

## Threat Flags

None — this is a CI/CD workflow change with no security-relevant surface added.