---
phase: quick
plan: 260418-h7k
subsystem: CI/CD
tags: [workflow, github-actions, release, ci-optimization]
dependency_graph:
  requires: []
  provides:
    - Enhanced release workflow with path filters and manual trigger
  affects:
    - .github/workflows/release.yml
tech_stack:
  added: []
  patterns:
    - GitHub Actions workflow_dispatch
    - Path filtering for CI optimization
key_files:
  created: []
  modified:
    - .github/workflows/release.yml
decisions:
  - Keep client-side conventional commit parsing (simple, no extra dependencies)
  - Use workflow_dispatch for manual override instead of external action
metrics:
  duration: <5 minutes
  completed_date: 2026-04-18
  tasks: 3
  files: 1
---

# Quick Task 260418-h7k: Enhance GitHub Release Workflow Summary

**One-liner:** Enhanced release workflow with path filters, manual dispatch, and version override options.

## Completed Tasks

| Task | Name | Commit | Files |
|------|------|--------|-------|
| 1 | Add path filters to trigger | b6dd4b4 | .github/workflows/release.yml |
| 2 | Add workflow_dispatch for manual trigger | b6dd4b4 | .github/workflows/release.yml |
| 3 | Add conventional commits validation research | b6dd4b4 | .github/workflows/release.yml |

## Changes Made

### Path Filters
Added paths filter to push trigger to only run when relevant files change:
- `app/**` - Android app code
- `*.gradle*` - Gradle build files
- `gradle/**` - Gradle wrapper
- `.github/workflows/**` - GitHub Actions workflows

Excludes: markdown, documentation, and non-code files from triggering builds.

### Manual Dispatch
Added `workflow_dispatch` trigger with inputs:
- `skip_release`: boolean - Explicit skip option (build only)
- `version_override`: string - Manual version name (e.g., "1.2.3")
- `version_code_override`: number - Manual version code

### Version Override Logic
Updated bump version step to check for manual overrides first. If `version_override` is provided, use it directly; otherwise fall back to auto-bump logic.

### Skip Logic
Updated job condition to check both:
- Commit message `[skip release]` (existing)
- Input parameter `skip_release` (new)

## Deviations from Plan

None - plan executed exactly as written.

## Known Stubs

None.

## Self-Check: PASSED

- Path filters present: ✅
- workflow_dispatch with skip_release: ✅
- Version override logic: ✅