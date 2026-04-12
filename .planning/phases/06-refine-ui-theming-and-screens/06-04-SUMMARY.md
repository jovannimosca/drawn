# 06-04: HorizontalPager swipe navigation for AddReading wizard

**Status:** Complete
**Started:** 2026-04-05
**Completed:** 2026-04-05

## Objective

Migrate AddReading wizard from step-based switching to HorizontalPager swipe navigation.

## What Was Built

Replaced `when(state.currentStep)` block with `HorizontalPager`:
- `rememberPagerState` initialized with `state.currentStep.ordinal`, page count = 3
- `LaunchedEffect(state.currentStep)` syncs pager when ViewModel changes the step (from button clicks)
- Each page renders the correct step: SpreadPickerStep, CardAssignmentStep, NotesAndSaveStep
- Back/Next buttons in WizardBottomBar call ViewModel methods AND animate pager via `coroutineScope.launch { pagerState.animateScrollToPage(...) }`
- Swipe navigation enabled (`userScrollEnabled = true`)
- Removed the 24dp Spacer between WizardStepIndicator and step content (pager provides natural spacing)

## Key Files Created/Modified

| File | Action | Purpose |
|------|--------|---------|
| `ui/addreading/AddReadingScreen.kt` | Modified | Replaced `when` step switching with HorizontalPager |

## Verification

- `./gradlew :app:compileDebugKotlin` — BUILD SUCCESSFUL
- HorizontalPager with 3 pages (SpreadPicker, CardAssignment, NotesAndSave)
- Swipe navigation enabled between all steps
- Back/Next buttons animate pager to correct page
- WizardStepIndicator reflects current page via LaunchedEffect sync
- coroutineScope.launch used for animated scroll

## Deviations

None.

## Self-Check: PASSED
