# Swift + SwiftUI (macOS) Agent Steering

This is the **pure Swift** steering set — the project is a Swift Package /
Xcode project (`Package.swift` or `*.xcodeproj`) with **no** Gradle/Kotlin
build. If a `build.gradle(.kts)` or `settings.gradle(.kts)` exists, stop and
read `steering/kmp/AGENTS.md` or `steering/android/AGENTS.md` instead — see
the root `AGENTS.md` for the detection rule.

Kotlin/Gradle/Compose/Kolt rules do **not** apply here. Read this before
generating any code. These rules are mandatory, not suggestions — an AI agent
(Claude, Gemini, or otherwise) must follow them by default without being
re-asked per task.

## Load steering files on demand, not upfront

The tl;dr below is enough to keep code compliant in general. Don't read any
file in this directory speculatively — each one is only worth its tokens
right before you touch the area it covers:

| Read this file... | ...right before you | Skip if |
|---|---|---|
| [architecture.md](architecture.md) | create/restructure a target or package, place a type in `Domain`/`Data`/`Presentation`, write a UseCase/Repository/Service, wire dependencies, or you're about to write a second/third copy of some logic | you're only editing inside an existing correctly-placed file |
| [presentation.md](presentation.md) | write/edit an `@Observable` view model, its `State`/`Action`, or a SwiftUI view that talks to one | the change is view-only inside an already-compliant screen |
| [navigation.md](navigation.md) | add a window/scene/screen, touch `NavigationStack`/`NavigationSplitView`/sheets/deep links (`onOpenURL`) | navigation isn't part of the task |
| [theming.md](theming.md) | add a color/size/font/user-facing string or asset, or touch the design-system target | you're just referencing an existing token |
| [macos.md](macos.md) | touch ADB/`Process`, `NSStatusItem`/`NSMenu`, `AppDelegate`, `Info.plist`/entitlements/sandbox, notifications, or `UserDefaults` | the task is pure domain or pure SwiftUI view code |
| [testing.md](testing.md) | write a unit test for a UseCase/view model/service, or reach for a mocking approach | the task has no test-writing in it |
| [tooling.md](tooling.md) | set up or edit SwiftLint/swift-format/strict-concurrency config, or `swift build`/lint failures need fixing | not touching lint/arch-test config |
| [release.md](release.md) | prepare a build for release: version bump, sign, notarize, distribute | the task is ordinary feature/bug work |

There is no `kolt-libs.md` here: `Kolt/libs` is Kotlin and not reusable from
Swift. Do not look for it, and do not port Kolt modules.

Keep it cheap: don't re-read a file already loaded this session unless you
edited it; follow a link only when the tl;dr's one line doesn't settle the
question. If this list keeps growing past ~20, split it into "every time" vs.
"on demand" tiers instead of appending.

If this file was reached through a `.standards` symlink in a consuming repo
(see `scripts/link-standards.sh`), the table links resolve to
`.standards/<file>.md` from that repo's root.

## Working rules — always apply (any stack)

Read [rules.md](rules.md) once at the start of every session, before the first
action, and keep it cached in context/memory (don't re-read) — it is short and applies to *every* task, not just code. Headlines:
Caveman ultra + Ponytail ultra stay on, and follow the stack's best current
standards; run
`git status` before any git state change and never lose uncommitted work;
commit at logical boundaries (when asked); **never delete a file or run a
destructive command without explicit confirmation**; stay in scope; verify and
report honestly. Docs: everything lives under `docs/` — compact recovery
specs in `docs/specs/` (commit locally *before* a new feature or major behaviour
change; updated only then), user docs in `docs/user/` (updated only when the
user asks). Unlike the other files here, this one is not load-on-demand.

## Non-negotiables (tl;dr)

1. **Swift 6 language mode, complete strict concurrency.** UI-bound types are `@MainActor`; cross-isolation values are `Sendable`; shared mutable state lives in an `actor`. No `@unchecked Sendable` or `nonisolated(unsafe)` without a one-line justification comment. On Swift 6.2+, default `MainActor` isolation is allowed **only in the App/Presentation/DesignSystem targets** — `Domain` and `Data` stay nonisolated (explicit isolation), and off-main work is marked `@concurrent`. See [architecture.md](architecture.md#concurrency-model).
2. Clean architecture, strict call chain, no skipped layers: `View → ViewModel → UseCase → Repository (protocol, Domain) → RepositoryImpl → DataSource/Service → originator (Process/URLSession/CoreData/UserDefaults)`. `UseCase` depends only on `Repository` protocols; `RepositoryImpl` depends only on its `DataSource`(s). Domain imports Foundation only — no SwiftUI/AppKit/Combine. Platform errors are mapped to a domain `Error` enum at the `DataSource` boundary — never caught raw above it. See [architecture.md](architecture.md).
3. No same-layer dependencies: a `UseCase` never depends on another `UseCase`; a `RepositoryImpl` never on another `Repository`; a `DataSource` never on another `DataSource`. Shared logic between peers is a plain, non-suffixed function or type.
4. **MVVM with unidirectional flow:** one `@Observable` view model per screen exposing a single `State` struct and one `send(_ action: Action)` entry point; one-shot work is an `Effect` the view consumes. `State`, `Action`, `Effect` live together in one `Contract.swift`. Never `ObservableObject` / `@Published` / `@StateObject` / `@ObservedObject`. See [presentation.md](presentation.md).
5. One folder per screen under `Presentation/` (e.g. `Presentation/Devices/`) holding that screen's `Contract.swift`, `ViewModel.swift`, `Route.swift`, and `View.swift` — never a flat `Presentation/` shared by every screen.
6. The view model is created and owned by the **Route view** (`@State private var viewModel`) — never inside the stateless content view, never above the navigation root. The stateless content view receives only `state`, `onAction: (Action) -> Void`, and resolved navigation closures; it is `#Preview`-able with fake data alone.
7. `async/await` and `AsyncStream`/`AsyncSequence` for all async work — never `DispatchQueue` callbacks, `Combine`, or completion handlers unless strictly wrapping a legacy API. Every long-lived `Task` (streams, observers) is stored and **cancelled** when the stream stops or the view disappears (`.task {}` is preferred — it cancels automatically).
8. **No shelling out.** Subprocess I/O (ADB, etc.) uses Foundation `Process` + `Pipe` with explicit argument arrays — never `system()`, `popen()`, `/bin/sh -c`, or string-concatenated commands. Binary output (e.g. screenshots) is read as raw `Data` from the pipe; no temp files. See [macos.md](macos.md#process--pipe).
9. **Settings** live in `UserDefaults` behind a single `SettingsService` (protocol + impl). No `UserDefaults`/`@AppStorage` access from views or view models directly; they depend on the protocol.
10. Dependencies are injected through initializers (protocol-typed), composed in one root (`AppContainer`). No singletons beyond that root, no service locators, no global mutable state. SwiftUI `@Environment` is for design tokens and the container, not for hiding service dependencies.
11. App lifecycle for AppKit-shaped apps (menu bar, status item) is owned by an `AppDelegate` via `@NSApplicationDelegateAdaptor`. Menu-bar apps use `NSStatusItem` + `NSMenu` when nested/dynamic submenus are needed (`MenuBarExtra` can't do them). `LSUIElement = YES` stays in `Info.plist` for agent apps — never remove it or set it to `NO`.
12. Notifications use `UNUserNotificationCenter`; request authorization at first use, not at launch. Logging uses `os.Logger` (subsystem = bundle id, a category per area), never `print`; interpolated user data is `privacy: .private` by default.
13. Theme lives in its own target, exposed via `Environment`. **No hardcoded colors/sizes/fonts/user-facing strings** outside the theme/resources: semantic SwiftUI colors or the project's token constants, `Localizable.xcstrings` (String Catalog) for text.
14. Accessibility is required: `.accessibilityLabel()` on every meaningful interactive control and image (`.accessibilityHidden(true)` if decorative), full keyboard operability, no layout that breaks at larger Dynamic Type / text size settings.
15. Follow the [Swift API Design Guidelines](https://www.swift.org/documentation/api-design-guidelines/) (clarity at the call site, grammatical names, no abbreviations). Prefer value types: `struct` + `enum` for state/models, `final class` only for view models, services and reference-identity needs. Mark types `final` and `private` by default; no force-unwraps (`!`), `try!`, or `fatalError` in non-test code outside a documented invariant.
16. Extract a helper/component once real duplication shows up (rule of three — two if error-prone) — not speculatively, and not left unused after extraction.
17. Unit tests use **Swift Testing** (`import Testing`, `@Test`, `#expect`); XCTest only for UI/performance tests. Fakes over mocks by default. See [testing.md](testing.md).
18. The architecture rules (2, 3, 4, 6, 7, 8, 9) are lint/test-enforced where tooling exists — `swift build` with warnings-as-errors, SwiftLint and the architecture tests must pass. Don't hand-wave a violation as "will fix in review". Only 14 (partly), 15, 16 and 21 stay judgment calls. See [tooling.md](tooling.md).
19. Menu-bar commands and keyboard shortcuts: every app action reachable from the UI is also a menu command with a standard shortcut where HIG defines one (`⌘,` Settings, `⌘W`, `⌘Q`, `⌘C/V`…); preferences use the `Settings` scene. Follow the macOS Human Interface Guidelines for controls, window behavior and terminology before inventing custom ones.
20. Input UX: a screen that collects input uses a **popover/sheet** for at most 2 data points, a **separate window or full pane** for more — unless the user specified otherwise. **Alerts/confirmation dialogs** are for messages and confirm/cancel choices only, never data entry.
21. If a task is ambiguous, the right scope is unclear, or you're several attempts deep retrying variations of the same fix — stop and ask the user. Don't expand scope, add speculative abstraction, or keep guessing to route around a blocker.
22. **Pre-commit exposure check — every commit, no exceptions.** Before `git commit`/`git push`, scan the staged diff and any new untracked files for: (a) internal/local paths (`/Users/…`, home dirs, internal hostnames/URLs, project or client folder names); (b) secrets (API keys, tokens, passwords, signing certificates/`.p12`/provisioning profiles, notarization credentials, `.env`); (c) personal data (emails, phone numbers, real names, device serials/IDs — including real ADB device serials in fixtures/logs). If anything matches: **stop, list each item (`file:line`), warn the user, and commit only with their express approval for each item** — a blanket "commit it" does not cover them. Prefer redacting, or gitignoring + untracking, over approval. Once pushed it is public — removing it later needs a history rewrite + force-push.
