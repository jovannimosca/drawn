---
phase: 05-continuous-deployment
plan: 01
subsystem: ci-cd
tags: [release, github-actions, signing, r8, apk]
dependency_graph:
  requires: []
  provides:
    - "Signed release APK build pipeline"
    - "Auto-incremented versioning"
    - "GitHub Release automation"
  affects:
    - "app/build.gradle.kts"
    - ".github/workflows/release.yml"
tech_stack:
  added:
    - "softprops/action-gh-release@v2"
    - "actions/github-script@v7"
  patterns:
    - "Environment variable-based signing config"
    - "Base64-encoded keystore decoding"
    - "Semver patch auto-increment"
    - "Conventional commit release notes"
key_files:
  created:
    - ".github/workflows/release.yml"
  modified:
    - "app/build.gradle.kts"
decisions:
  - "Used dummy.jks fallback for local builds without secrets"
  - "Single-job workflow (no multi-job split needed for release)"
  - "Version bump not committed back to repo (CI-only increment)"
  - "Release notes grouped by conventional commit type (feat, fix, chore, other)"
metrics:
  duration: ~5min
  completed: "2026-04-05"
---

# Phase 05 Plan 01: Release Signing & GitHub Actions Workflow Summary

**One-liner:** Production release signing config with R8 minification and a GitHub Actions workflow that auto-builds signed APKs and publishes GitHub Releases on every merge to main.

## Tasks Completed

| Task | Name | Commit | Files |
| ---- | ---- | ------ | ----- |
| 1 | Add release signing config and enable R8 in build.gradle.kts | `724b399` | `app/build.gradle.kts` |
| 2 | Create GitHub Actions release workflow | `3055f49` | `.github/workflows/release.yml` |

## Task 1: Release Signing Config & R8

**Changes to `app/build.gradle.kts`:**

1. **Added `signingConfigs` block** — creates a "release" signing config that reads from environment variables:
   - `RELEASE_KEYSTORE_PATH` — path to decoded keystore file (from workflow)
   - `RELEASE_KEYSTORE_PASSWORD` — keystore password
   - `RELEASE_KEY_ALIAS` — key alias
   - `RELEASE_KEY_PASSWORD` — key password
   - Falls back to `dummy.jks` / empty strings for local builds without secrets

2. **Enabled R8/minification** on release build type:
   - `isMinifyEnabled = true` (was `false`)
   - `isShrinkResources = true` (new)
   - `signingConfig = signingConfigs.getByName("release")`

3. **Configured APK output filename** — `applicationVariants.all` block renames release APK to `drawn-{versionName}.apk`

## Task 2: GitHub Actions Release Workflow

**Created `.github/workflows/release.yml`** with:

1. **Trigger:** `on: push: branches: [main]` with `[skip release]` conditional skip
2. **Setup:** JDK 17, Android SDK 36, Gradle cache (reuses ci.yml patterns)
3. **Keystore decoding:** Base64-decodes `RELEASE_KEYSTORE` secret to temp file, sets `RELEASE_KEYSTORE_PATH` env var
4. **Version bump:** Reads current versionCode/versionName from build.gradle.kts, increments versionCode by 1, increments semver patch (1.0 → 1.0.0, 1.0.0 → 1.0.1), sets env vars — does NOT commit back to repo
5. **Build:** Runs `./gradlew :app:assembleRelease --no-daemon` with signing env vars
6. **Release notes:** Uses `actions/github-script@v7` to query commits since last release tag, groups by conventional commit type (feat/fix/chore/other)
7. **GitHub Release:** Uses `softprops/action-gh-release@v2` to create tagged release with APK + mapping.txt artifacts

## Deviations from Plan

None - plan executed exactly as written.

## Requirements Addressed

- **DEPLOY-01:** Signed APK — signingConfigs block with env vars, release buildType applies signing
- **DEPLOY-02:** Version tag + release — auto-incremented versionCode/versionName, GitHub Release with v{versionName} tag
- **DEPLOY-03:** Release notes — conventional commit grouping via github-script

## Known Stubs

None.

## Self-Check

- [x] `app/build.gradle.kts` — contains signingConfigs, isMinifyEnabled=true, isShrinkResources=true, applicationVariants
- [x] `.github/workflows/release.yml` — exists with push trigger, keystore decoding, version bump, assembleRelease, action-gh-release
- [x] Commit `724b399` — Task 1 committed
- [x] Commit `3055f49` — Task 2 committed

## Self-Check: PASSED
