# Phase 5: Continuous Deployment - Context

**Gathered:** 2026-04-04
**Status:** Ready for planning

<domain>
## Phase Boundary

Set up a GitHub Actions release workflow that triggers on every merge to `main`, builds a signed production APK with auto-incremented version, generates release notes from commits, and publishes a GitHub Release with the APK and R8 mapping files as artifacts.

This phase adds:
- Release signing key stored in GitHub Secrets
- Auto-incremented versionCode and semver versionName
- GitHub Actions workflow triggered on push to main
- R8/ProGuard mapping file generation
- Auto-generated release notes from commit messages
- GitHub Release creation with APK + mapping artifacts

This phase does NOT add:
- F-Droid publishing (separate future phase)
- Play Store publishing (out of scope)
- Beta/alpha channels (single release track)
- Changelog file management (release notes only in GitHub Releases)

</domain>

<decisions>
## Implementation Decisions

### Versioning
- **D-82:** Auto-increment versionCode on each merge to main (reads current value, increments by 1)
- **D-83:** versionName follows semver — major.minor.patch, auto-incremented patch on each merge
- **D-84:** Version bump happens in the CI workflow before building (not committed back to repo)
- **D-85:** Current version: versionCode=1, versionName="1.0.0" (from build.gradle.kts)

### Release Trigger
- **D-86:** Every merge/push to `main` triggers a release
- **D-87:** Workflow uses `on: push: branches: [main]`
- **D-88:** Skips release if commit message contains `[skip release]`

### Release Notes
- **D-89:** Auto-generated from commit messages since last release tag
- **D-90:** Uses `github-script` or `actions/github-script` to query commits and format notes
- **D-91:** Release tag format: `v{versionName}` (e.g., `v1.0.1`)

### Signing
- **D-92:** Release APK signed with upload key
- **D-93:** Keystore stored as base64-encoded secret in GitHub Secrets (`RELEASE_KEYSTORE`)
- **D-94:** Keystore password, key alias, and key password stored as separate secrets
- **D-95:** Release signing config in build.gradle.kts reads from environment variables

### Artifacts
- **D-96:** Published artifacts: signed APK + R8 mapping file (`mapping.txt`)
- **D-97:** APK filename pattern: `drawn-{versionName}.apk`
- **D-98:** GitHub Release created with tag, auto-generated notes, and attached artifacts

### Build Configuration
- **D-99:** Production build uses `assembleRelease` with R8 enabled
- **D-100:** `isMinifyEnabled = true` for release build type
- **D-101:** `isShrinkResources = true` for release build type
- **D-102:** ProGuard/R8 rules file at `app/proguard-rules.pro` (already exists)

### Architecture Patterns (carried from prior phases)
- **D-103:** Existing CI workflow (ci.yml) handles tests + lint on PRs — release workflow is separate
- **D-104:** Release workflow reuses the same Gradle commands but with release signing config
- **D-105:** No changes to app code — purely CI/CD infrastructure

### the agent's Discretion
- Exact GitHub Actions workflow structure (single job vs. multi-job)
- Specific action versions for setup-java, gradle actions, etc.
- Whether to use `gradle/gradle-build-action` or raw `./gradlew`
- Exact release notes formatting style
- Whether to use `softprops/action-gh-release` or GitHub API directly

</decisions>

<canonical_refs>
## Canonical References

**Downstream agents MUST read these before planning or implementing.**

### Project Setup
- `.planning/PROJECT.md` — Project context, core value, constraints
- `.planning/ROADMAP.md` — Phase 5 goal and success criteria
- `.planning/REQUIREMENTS.md` — No existing requirements (new phase)

### Existing Code (must read before planning)
- `app/build.gradle.kts` — Current build config, version info, signing setup
- `.github/workflows/ci.yml` — Existing CI workflow to integrate with
- `.github/workflows/quality.yml` — Existing quality workflow
- `app/proguard-rules.pro` — Existing ProGuard rules

### No external specs
No external specs — requirements fully captured in decisions above

</canonical_refs>

<code_context>
## Existing Code Insights

### Reusable Assets
- **CI workflow** — `.github/workflows/ci.yml` already runs tests and lint
- **Quality workflow** — `.github/workflows/quality.yml` already runs ktlint + detekt
- **ProGuard rules** — `app/proguard-rules.pro` already exists
- **Version info** — `versionCode = 1`, `versionName = "1.0"` in build.gradle.kts

### Established Patterns
- GitHub Actions with Gradle wrapper
- Android SDK setup via `actions/setup-java`
- Kover coverage reporting
- ktlint + detekt quality gates

### Integration Points
- Phase 5 connects to:
  - Existing CI workflows — release workflow is separate but shares setup patterns
  - build.gradle.kts — needs release signing config
  - GitHub Secrets — needs keystore and password secrets

</code_context>

<specifics>
## Specific Ideas

- Every merge to main should feel like shipping — automatic, reliable, zero manual steps
- Release notes should be useful — group commits by type (feat, fix, chore)
- Version should be traceable — tag + release + APK filename all match
- Signing should be secure — keystore in secrets, never in repo

</specifics>

<deferred>
## Deferred Ideas

- F-Droid publishing — separate future phase
- Play Store publishing — out of scope
- Beta/alpha channels — single release track for now
- Changelog.md file — release notes live in GitHub Releases only
- Multiple artifact formats (AAB) — APK only for now

</deferred>

---

*Phase: 05-continuous-deployment*
*Context gathered: 2026-04-04*
