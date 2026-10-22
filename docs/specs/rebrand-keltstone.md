# rebrand-keltstone — Rebrand Kolt monorepo from Kolta Labs to Keltstone

Rebrand the entire KoltLibs monorepo and its convention plugins from "Kolta Labs" to "Keltstone".

## Decisions
- GitHub org/handle: `keltstone`. Repository name remains `Kolt`. URL base: `https://github.com/keltstone/Kolt` (developer URL: `https://github.com/keltstone/`).
- Maven group: `io.github.keltstone.kolt` (replaces `io.github.koltalabs.kolt`).
- Gradle plugin IDs: prefix updated to `io.github.keltstone.kolt.<suffix>` (suffixes unchanged: `kmp.library`, `kmp.library-compose`, `kmp.library-koin`, `kmp.library-koin-compose`, `kmp.data`, `kmp.application`, `library`, `library-compose`, `library-hilt`, `library-hilt-compose`, `application`, `data`, `publish`).
- Hard cut: no deprecated plugin aliases or coordinates retained.
- Brand text: `Kolta Labs` / `KoltaLabs` / `kolta labs` → `Keltstone`. Developer ID `kolta-labs` → `keltstone`.
- Kotlin package root: remains `kolt.*` (frozen in prior package-rename step).
- Defaults applied:
  - `SUPPORT_EMAIL`: `email` attribute removed from IntelliJ `plugin.xml` `<vendor>` tag; example code references replaced with RFC 2606 example address / repo URL.
  - `LICENSE_HOLDER`: `Copyright 2025 Kolta Labs` preserved pending legal decision.
  - `INTELLIJ_PLUGIN_PUBLISHED_TO_MARKETPLACE`: `io.github.koltalabs.kolt.project-wizard` preserved on ALLOWLIST.

## Allowlist
- `docs/MIGRATION.md`: contains the migration mapping table and consumer checklist.
- `LICENSE`: legal holder line preserved.
- `intellij-plugin/src/main/resources/META-INF/plugin.xml`: immutable marketplace plugin `<id>` preserved.

## Verification
- Scan with P1 (`koltalabs`), P2 (`kolta[-_ ]labs`), P3 (`\bkolta\b`), P4 (file/dir names).
- Kotlin class names unchanged (no edits to `Kolt*` identifiers).
- Gradle builds: root `./gradlew help`, `./gradlew check`, `build-logic` checks.
- `publishToMavenLocal` verified under `~/.m2/repository/io/github/keltstone/kolt/`.
- Offline composite-build proof verified.
- Local consumer projects (DialexAI) updated and verified.

Status: Executed in working tree.
