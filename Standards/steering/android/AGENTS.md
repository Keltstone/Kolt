# Android + Compose Agent Steering

This is the **Android-only** steering set — the current project has no
`commonMain`/`iosMain`/`kotlin("multiplatform")` plugin, just Android
Gradle modules. If the project is Kotlin Multiplatform, stop and read
`steering/kmp/AGENTS.md` instead — see the root `AGENTS.md` for the
detection rule.

Read this before generating any code here. These rules are mandatory, not
suggestions — an AI agent (Claude, Gemini, or otherwise) must follow them by
default without being re-asked per task.

## Load steering files on demand, not upfront

The tl;dr below is enough to keep code compliant in general. Don't read any
file in this directory speculatively or "just in case" — each one is only
worth its tokens right before you touch the area it covers, and skip it
entirely otherwise:

| Read this file... | ...right before you | Skip if |
|---|---|---|
| [architecture.md](architecture.md) | create/restructure a module, place a class in `domain`/`data`/`presentation`, write a UseCase/Repository/DataSource, wire DI, or you're about to write a second/third copy of some logic | you're only editing inside an existing correctly-placed file |
| [presentation-mvi.md](presentation-mvi.md) | write/edit a `ViewModel`, `State`, `Intent`, or `Effect`, or extract ViewModel logic into a delegate | the change is UI-only inside an already-compliant screen |
| [navigation.md](navigation.md) | add a route/screen, touch `NavDisplay`/back stack/deep links, or the question is Activity-per-journey vs single-Activity | navigation isn't part of the task |
| [theming.md](theming.md) | add a color/dimension/font/user-facing string/token, or touch the theme module | you're just referencing `AppTheme.colors.x` that already exists |
| [ui.md](ui.md) | build or edit any screen UI: edge-to-edge/insets, app bar, which components to use, dividers | the change is non-visual (domain/data/ViewModel logic) |
| [kolt-libs.md](kolt-libs.md) | about to add a dependency or write a new util/component, **and** `Kolt/libs` exists in this workspace — check first, its vetted pieces are mandatory to reuse | `Kolt/libs` isn't present — the rest of this steering set doesn't need it |
| [kolt-libs-reference.md](kolt-libs-reference.md) | `kolt-libs.md`'s table already pointed you at a module (usually `location-picker`) and you need more than the one-line summary | the table's one-liner already answers it |
| [testing.md](testing.md) | write a unit test for a UseCase/ViewModel, or reach for a mocking library | the task has no test-writing in it |
| [tooling.md](tooling.md) | set up or edit Konsist/Detekt/ktlint config, or a `./gradlew check` failure needs fixing | not touching lint/architecture-test config |
| [release.md](release.md) | prepare a build for release: version bump, R8/signing, launch optimization + baseline profile, whole-journey tests, store checklist, staged rollout | the task is ordinary feature/bug work, not a release |

**Three more ways to keep this cheap, not just the table above:**

- Loaded a file already this session? Don't re-read it for a second,
  unrelated task — its content hasn't changed. Re-read only if you actually
  edited it since, or genuinely can't recall its content.
- The tl;dr's non-negotiables list is usually the whole answer. Follow a
  `[link]` into a full doc only when the tl;dr's one line doesn't settle the
  question — not as a reflex "the tl;dr told me this file exists, so I
  should read it."
- If this list of non-negotiables keeps growing past ~20, that's a signal to
  split it into "read every time" vs. "read on demand" tiers, not to keep
  appending — a tl;dr an agent skims in one pass is the whole point of it.

If this file was reached through a `.standards` symlink in a consuming app
repo (see the root `AGENTS.md`'s `scripts/link-standards.sh`), the table
links above (`kolt-libs.md`, `architecture.md`, etc.) resolve to
`.standards/<file>.md` from that app's root, not `<file>.md` — the symlinked
folder, not the app root, is what they're sibling to.

## Working rules — headlines always apply (any stack)

Caveman ultra + Ponytail ultra stay on, and follow the stack's best current
standards. `git status` before any git state change and never lose uncommitted
work; commit at logical boundaries (when asked); **never delete a file or run
a destructive command without explicit confirmation**; stay in scope; verify
and report honestly. Docs live under `docs/`: compact recovery specs in
`docs/specs/` (commit locally *before* a new feature or major behaviour
change), user docs in `docs/user/` (only when the user asks).

Read the full [rules.md](rules.md) **only when a trigger fires** — a commit,
branch/PR or other git state change; a delete or other destructive command; a
dependency or tooling change; writing/updating a spec or doc; or you're
stuck/unsure how to proceed. Not at session start; once read, don't re-read.

## Non-negotiables (tl;dr)

One line each; the linked doc holds the detail — open it only if the line doesn't settle the question.

1. Clean architecture, strict call chain, no skipped layers: `ViewModel → UseCase → Repository (interface, domain) → RepositoryImpl → DataSource → originator (Retrofit/Room/DataStore)`. Domain has zero Android/Compose imports; platform exceptions map to `DomainException` at the `DataSource` boundary. See [architecture.md](architecture.md).
2. No same-layer dependencies: `UseCase` never depends on another `UseCase`; `RepositoryImpl` never depends on another `Repository`; `DataSource` never depends on another `DataSource`. Shared logic between peers is a plain, non-`UseCase`/non-`Repository`-suffixed function or class. See [architecture.md](architecture.md#data-layer-repository--datasource--originator).
3. MVI: `State` (data class), `Intent` (sealed interface), `Effect` (sealed interface, one-shot via `Channel`), all three together in one `Contract.kt`.
4. One package per screen under `presentation/` (e.g. `presentation/list/`, `presentation/detail/`), holding that screen's `Contract.kt`, `ViewModel`, `Route`, and `Screen` — never a flat `presentation/` shared by every screen in the feature.
5. Navigation is **Navigation 3** (`androidx.navigation3`, stable since Nov 2025) — not the old `androidx.navigation:navigation-compose`. Caller-owned back stack (`NavKey` list), not a `NavController`.
6. ViewModel is instantiated inside the composable named in `entryProvider` for its route (the "Route" composable), obtained via `hiltViewModel()` — never inside the stateless screen composable, never above the back stack.
7. The Route composable is the **only** collector of the effect `Flow`: it resolves nav effects itself and forwards non-nav effects to the screen as plain lambdas/state, never a raw `Flow`. `Channel` effects are a deliberate deviation — see [presentation-mvi.md](presentation-mvi.md#known-deviation-from-current-official-guidance-channel-based-effects).
8. The stateless root composable receives only `state`, `onIntent: (Intent) -> Unit`, and resolved navigation lambdas. It never sees a `ViewModel`, a back stack, or an effect `Flow`. It must be `@Preview`-able with fake data alone.
9. Any `List`/`Map`/`Set` in `State` is `ImmutableList`/`ImmutableMap`/`ImmutableSet` (`kotlinx.collections.immutable`), never the plain `kotlin.collections` type — Compose treats plain collections as unstable and over-recomposes.
10. Single Activity, one `NavDisplay` for the whole app; feature modules contribute routes, not their own `NavDisplay`. A separate Activity per journey is opt-in, for a concrete reason only — see [navigation.md](navigation.md).
11. Theme lives in its own module, exposed via `CompositionLocal`. No hardcoded colors/dimens/fonts/user-facing strings (use `stringResource`) outside their respective modules.
12. Accessibility is required, not optional polish: `contentDescription` on every meaningful image/icon (explicit `null` if decorative), ≥48.dp touch targets, no layout that breaks at large system font scales.
13. If `Kolt/libs` is present, its vetted pieces are mandatory (don't write a new theme module/`AsyncState`/UI component that exists there), and always use `Kolt.colors`/`Kolt.sizes`/`Kolt.typography` instead of literals or parallel tokens; replace deprecated Kolt tokens when you touch the code. Android-only projects can use most of Kolt incl. `compose-utils`; what to skip: [kolt-libs.md](kolt-libs.md). Token rules: [theming.md](theming.md#rules).
14. Extract a delegate/util/component once real duplication shows up (rule of three — two if it's error-prone) — not speculatively, and not left unused after extraction. See [architecture.md](architecture.md#reuse-over-duplication).
15. Unit tests for `domain`/`presentation` are plain JVM local unit tests (JUnit4/5) — no Robolectric/instrumentation unless the test genuinely needs a real Android framework class. Fakes for small interfaces, MockK for large/volatile ones. See [testing.md](testing.md).
16. **DI is Hilt**, not Koin — no multiplatform constraint here, and Hilt is the officially recommended choice for Android apps of this shape.
17. Non-negotiables 1–11, 13, 21 are Konsist/Detekt/compiler-enforced — `./gradlew check` must pass; don't hand-wave a violation as "will fix in review". Only 12 (partly), 14, 18, 19 and 20 stay judgment calls. See [tooling.md](tooling.md).
18. Input UX: a screen that collects input uses a **bottom sheet** for at most 2 data points, a **full screen** for more — unless the user specified otherwise. **Dialogs** are for messages and CTA/confirm choices only, never for data entry.
19. **Pre-commit exposure check — every commit, no exceptions.** Before `git commit`/`git push`, scan the staged diff and any new untracked files for: (a) internal/local paths (`/Users/…`, `C:\Users\…`, home dirs, internal hostnames/URLs, project or client folder names); (b) secrets (API keys, tokens, passwords, keystores, `.env`, `google-services.json`/`GoogleService-Info.plist`, signing/`local.properties`); (c) personal data (emails, phone numbers, real names, device/account IDs). If anything matches: **stop, list each item (`file:line`), warn the user, and commit only with their express approval for each item** — a blanket "commit it" does not cover them. Prefer redacting, or gitignoring + untracking the file, over approval. Once pushed it is public — removing it later needs a history rewrite + force-push, so catch it before.
20. If a task is ambiguous, the right scope is unclear, or you're several attempts deep retrying variations of the same fix without progress — stop and ask the user. Don't expand scope, add speculative abstraction, or keep guessing to route around a blocker; a wrong guess compounds, a question doesn't.
21. **File size: soft cap ~600 lines per `.kt` file, hard cap 800.** Past the soft cap, split by responsibility before adding more — one screen section / component per file, helpers in sibling files (`private`/`internal`), same package. This is size-driven and separate from the duplication rule (14). Konsist/Detekt-enforced. See [architecture.md](architecture.md#file-size--split-by-responsibility).
