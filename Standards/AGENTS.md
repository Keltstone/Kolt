# Agent Steering — pick the project type first

Read this before generating any code in a KMP, Android Compose, or pure Swift (macOS) project.
These rules are mandatory, not suggestions — an AI agent (Claude, Gemini, or
otherwise) must follow them by default without being re-asked per task.

`CLAUDE.md` and `GEMINI.md` at repo root should be symlinks to this file:

```
ln -s AGENTS.md CLAUDE.md
ln -s AGENTS.md GEMINI.md
```

This repo holds three full, independent steering sets. Detect which one
applies to *this* project, then load only that one — don't read both, and
don't read either speculatively before you're about to write code:

| Set | Applies when | Load |
|---|---|---|
| **KMP** | `commonMain`/`iosMain` (or another non-JVM target) exists, or `build.gradle.kts`/`settings.gradle.kts` applies `kotlin("multiplatform")` / `org.jetbrains.kotlin.multiplatform` | [steering/kmp/AGENTS.md](steering/kmp/AGENTS.md) |
| **Android-only** | Only Android Gradle modules (`com.android.application`/`com.android.library`), no `commonMain`/`iosMain`, no multiplatform plugin | [steering/android/AGENTS.md](steering/android/AGENTS.md) |
| **Swift (macOS)** | `Package.swift` or `*.xcodeproj` at the project root and **no** Gradle build (`settings.gradle*`/`build.gradle*`) — a KMP/Android repo's `iosApp` Xcode project does *not* make it Swift | [steering/swift/AGENTS.md](steering/swift/AGENTS.md) |

Detection is a repo check, not a question to default on: look for
`Package.swift`/`*.xcodeproj` without Gradle (Swift), then at
`settings.gradle.kts`'s included modules and the root/app `build.gradle.kts`
plugins block, or just check whether an `iosMain`/`commonMain` source
directory exists anywhere. If the repo is empty/brand new and genuinely
ambiguous (no code to inspect yet), ask once which target this project is —
don't guess and don't load both sets "to be safe."

The KMP and Android sets share the same shape (clean architecture, MVI, Navigation 3,
theming, testing, Kolt reuse) and the same non-negotiables philosophy, but
differ on anything the KMP boundary actually changes — DI (Koin vs Hilt),
data-layer originators (Ktor/Room-KMP/SQLDelight vs Retrofit/Room), test
tooling (`kotlin.test`+Mokkery vs JUnit+MockK), `expect`/`actual` vs none,
and how much of `Kolt/libs` is safe to reuse. Each set is fully
self-contained — once you've picked one, everything you need is under that
one directory.

The Swift set keeps the same architecture and non-negotiables philosophy
(clean layers, unidirectional MVVM with `@Observable`, theming, testing,
exposure check) in Swift/SwiftUI terms, adds macOS-specific rules
(`Process`+`Pipe`, `NSStatusItem`, `LSUIElement`, notarization), and has no
Kolt reuse — Kolt is Kotlin.
