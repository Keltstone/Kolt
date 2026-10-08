# Clean Architecture — Swift

## Layers

```
Presentation (SwiftUI views, @Observable view models)
        │  depends on
        ▼
   Domain (UseCases, models, repository protocols — Foundation only)
        ▲  implements
        │
     Data
        ├── RepositoryImpl   (implements the Domain protocol; depends on DataSource(s) only)
        └── DataSource       (the only types allowed to touch Process/URLSession/CoreData/UserDefaults/Keychain)
```

Dependency direction is one-way: `Presentation` and `Data` depend on `Domain`.
`Domain` imports `Foundation` only — no SwiftUI, AppKit, Combine, or
framework types — so it is testable with plain `swift test`, no app host.

**The call chain is strict and every link is mandatory — no skipping a
layer, and no layer depends on another instance of its own layer:**
`ViewModel → UseCase → Repository (protocol) → RepositoryImpl → DataSource →
originator (Process / URLSession / CoreData / UserDefaults)`.

- A `UseCase` may depend on any number of `Repository` protocols — never on
  another `UseCase`.
- A `RepositoryImpl` may depend on its own `DataSource`(s) — never on another
  `Repository`, and never on `Process`, `URLSession`, `UserDefaults` directly.
- A `DataSource` wraps exactly one originator — never another `DataSource`.

Cross-repository orchestration happens one level up, in a `UseCase` that takes
both repositories as init params. That keeps every `UseCase` fully testable
with N small fakes — see [testing.md](testing.md).

A project with only one thin integration (e.g. a single ADB wrapper) may
collapse `Repository`+`DataSource` into one protocol-fronted `Service`, but
the `ViewModel → UseCase → protocol` boundary stays. Don't invent layers for
pass-through logic you don't have.

## Project layout (SPM)

```
Package.swift
Sources/
  App/            @main App, AppDelegate, AppContainer (composition root) — wiring only
  Domain/         models, UseCases, Repository protocols, DomainError
  Data/           RepositoryImpls, DataSources, DTOs + mappers
  Presentation/   one folder per screen (Contract, ViewModel, Route, View)
  DesignSystem/   tokens, shared components, Localizable.xcstrings, assets
Tests/
  DomainTests/  DataTests/  PresentationTests/
```

- `App` contains only platform wiring: entry point, `AppDelegate`, DI
  composition, `Info.plist`/entitlements. No screens, UseCases or repositories.
- Targets depend inward: `App → Presentation/Data → Domain`. `Domain` and
  `DesignSystem` depend on nothing in the app. Enforce with per-target
  `dependencies:` in `Package.swift` — an illegal import then doesn't compile.
- Start with the single-target layout (folders, not targets) and split into
  targets when a boundary needs compiler enforcement or a second consumer
  appears.

New source lives where the project says it does (e.g. `AndroidCaptureSPM/`);
legacy directories marked "reference only" are never edited.

## UseCase shape

One public action per `UseCase`, called as a function:

```swift
struct ObserveLogcat {
    private let repository: any LogcatRepository
    init(repository: any LogcatRepository) { self.repository = repository }
    func callAsFunction(device: DeviceID) -> AsyncStream<LogcatLine> {
        repository.stream(device: device)
    }
}
```

- Name by verb; suffix `UseCase` only if the project already does.
- No UI types, no `Task` creation — return `async` values or `AsyncStream`
  and let the view model own the `Task` lifetime.

## Data layer: Repository → DataSource → originator

- `DataSource` maps platform errors (`CocoaError`, `URLError`, non-zero
  process exit) to a `DomainError` enum at its boundary. Nothing above it
  catches a raw platform error.
- DTOs stay in `Data`; mappers convert to Domain models. Domain models never
  carry `Codable` keys for a wire format.
- Streams are `AsyncStream`/`AsyncThrowingStream` with `onTermination`
  cleaning up the originator (kill the `Process`, close the file handle).

## Dependency injection

Plain initializer injection, composed once in `AppContainer`
(`@MainActor final class`). No DI framework by default; add one only for a
concrete need (e.g. very large graph). Protocol-typed (`any`/`some`) params so
tests pass fakes. No singletons (`static let shared`) outside the container.

## Concurrency model

- Swift 6 language mode, complete checking. Data crossing isolation
  boundaries is `Sendable` (prefer value types).
- `Presentation`: `@MainActor` (explicit, or via the Swift 6.2 default
  `MainActor` isolation build setting for those targets only).
- `Domain`/`Data`: **not** default-`MainActor`. Mark intent explicitly —
  protocols and models `Sendable`, services that hold mutable state are
  `actor`s, CPU- or I/O-heavy async work that must leave the caller's
  executor is `@concurrent` (Swift 6.2; plain `nonisolated async` runs on the
  caller's actor under `NonisolatedNonsendingByDefault`).
- Long-lived work uses structured concurrency (`async let`, task groups) or
  a stored `Task` that is cancelled on teardown; avoid `Task.detached` and
  unstructured `Task {}` without an owner.
- Public async APIs are cancellation-aware (`try Task.checkCancellation()`,
  `withTaskCancellationHandler`) and bridge callback APIs with
  `withCheckedThrowingContinuation` (resume exactly once).
- Errors: `throws` with `DomainError`; typed throws (`throws(DomainError)`)
  where the error set is closed and it reads better.

## Build-time config

No secrets or environment values in source. Use `.xcconfig` + `Info.plist`
substitution or a git-ignored `Secrets.xcconfig`; read via `Bundle.main`
behind a `Configuration` type in `Data`.

## Reuse over duplication

Extract once real duplication shows up (rule of three — two if error-prone):
a view-model helper, a `ViewModifier`, a design-system component, a Domain
function. Prefer composition (a small struct/protocol extension) over
subclassing. Don't leave an extracted helper unused.

## Rules

1. Layer rules above are enforced by the architecture test in
   [tooling.md](tooling.md) and by target dependencies — fix violations, don't
   suppress.
2. `Domain` has zero UI/framework imports.
3. No same-layer dependencies (UseCase→UseCase, Repo→Repo, DataSource→DataSource).
4. Errors cross layers as `DomainError`, never raw platform errors.
5. Every long-lived stream is cancellable and cleans up its originator.
