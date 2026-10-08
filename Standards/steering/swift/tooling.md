# Enforcement tooling

Rules that can fail a build must fail a build. Don't leave them to review.

## Compiler

- Swift 6.2+ per-target settings (see [architecture.md](architecture.md#concurrency-model)):
  `SWIFT_STRICT_CONCURRENCY=complete`, upcoming features
  `NonisolatedNonsendingByDefault` and `InferIsolatedConformances`; default
  `MainActor` isolation on UI targets only.
- Swift 6 language mode.
- Warnings as errors in CI and release: `swift build -Xswiftc -warnings-as-errors`
  (or `SWIFT_TREAT_WARNINGS_AS_ERRORS = YES` in Xcode).
- Per-target `dependencies:` in `Package.swift` make illegal layer imports a
  compile error — preferred enforcement for layer rules.

## SwiftLint (`.swiftlint.yml`)

Enable `opt_in_rules` for: `force_unwrapping`, `force_try`, `explicit_acl`
(optional), `unused_import`, `redundant_type_annotation`, `closure_body_length`.
Add `custom_rules` for project bans:

```yaml
custom_rules:
  no_observable_object:
    regex: '\b(ObservableObject|@Published|@StateObject|@ObservedObject|@EnvironmentObject)\b'
    message: "Use @Observable (Observation), see presentation.md"
    severity: error
  no_shell_out:
    regex: '\b(system|popen)\s*\(|/bin/(ba)?sh'
    message: "Use Process + Pipe with an argv array, see macos.md"
    severity: error
  no_dispatch_queue:
    regex: 'DispatchQueue\.'
    message: "Use async/await; only wrap legacy APIs"
    severity: warning
  no_direct_userdefaults:
    regex: 'UserDefaults\.standard|@AppStorage'
    message: "Go through SettingsService"
    severity: error
    excluded: '.*/Data/.*SettingsService.*'
  no_hardcoded_color:
    regex: '\bColor\((red|white|\.sRGB)|\bColor\(\s*"|NSColor\(red'
    message: "Use semantic colors / theme tokens"
    severity: error
    excluded: '.*/DesignSystem/.*'
```

Domain isolation: a `custom_rule` flagging `import (SwiftUI|AppKit|Combine)`
in `Sources/Domain/**` (use `included:`).

## Architecture tests

A small Swift Testing target that scans `Sources/**` source text (or uses
SwiftSyntax) to assert: no same-layer dependencies by constructor param type,
`Domain` imports only Foundation, one `Contract.swift` per screen folder.
Cheap text/regex checks are enough to start.

## swift-format

Use `swift-format` (or SwiftFormat) with a committed config; format is
checked, not argued about, in CI.

## Wiring

A single script (`scripts/check.sh`) runs: `swift build` (warnings-as-errors)
→ `swiftlint --strict` → `swift test`. That is the "`check`" of this project;
it must pass before commit.

## Keep failure output cheap

Run `swiftlint --quiet --reporter emoji`/`xcbeautify` to trim noise. Don't run
the full build after every small edit: build the touched target
(`swift build --target X`), run the nearest test (`swift test --filter Y`),
and run the full `check` before commit.
