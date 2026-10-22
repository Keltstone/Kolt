# package-rename — decouple Kotlin packages from Maven group / Gradle plugin IDs

Goal: company name keeps changing. Freeze brand-only Kotlin package root (`kolt`). Make Maven group + Gradle plugin IDs cheap to change later (1 line / 1 file, zero source edits, no consumer import breakage).

Three independent namespaces (never derive one from another):
- Kotlin package — in consumer source. Frozen forever.
- Maven group (+ artifact IDs) — consumer `build.gradle` dep lines. Changeable.
- Gradle plugin IDs — consumer `plugins {}`. Changeable (alias overlap).

Decisions (confirm before start):
- Package root: `kolt` (placeholder; user to confirm). Map `io.github.koltalabs.kolt.X` -> `kolt.X`; sub-packages kept (`kolt.composekmp`, `kolt.utils`, `kolt.crashlogs`, ...). Build-logic internal package -> `kolt.conventions`.
- Group + plugin IDs: unchanged in this change (still `io.github.koltalabs.kolt`). Only made single-source.
- IntelliJ plugin `<id>` (`...kolt.project-wizard`) NOT changed: Marketplace ID immutable. Its Kotlin package moves like the rest.
- Out of scope: GitHub org/repo rename, `Kolta Labs` text, `support@koltalabs.com`, `kolta-labs` URLs (~59 hits) — separate step, GitHub redirects old URL.
- Demo `applicationId` (`...kolt.demo`): demo only, may change; separate decision (new app ID = reinstall).

## Step 0 — single-source the coordinates (no behaviour change; do first, build green)
Group hardcoded today in: `gradle.properties` (`GROUP`), root `build.gradle.kts` (`allprojects.group`), `build-logic/conventions/build.gradle.kts` (`group=`), `KmpLibraryCoordinates.kt` (`GROUP_KOLT`), `Dependencies.kt` (BOM + utils/logutils/crashlogs/compose notations), `libs/bom/build.gradle.kts` (`api("...:utils:..")` x9), `intellij-plugin/build.gradle.kts`, `settings.gradle.kts` dependency substitution, `project-templates/**`, `scripts/new-project.sh`.
- Root `gradle.properties GROUP` = only source. Everything else reads it (root `allprojects.group`, build-logic via property/const, bom via `project.group`). Build-logic strings built as `"$KOLT_GROUP:utils"`.
- Plugin IDs: one const/list in `build-logic/conventions/build.gradle.kts` `gradlePlugin {}`; version catalogs `koltlibs`/`kmplibs` toml are the only other place. When ID changes later: register BOTH old + new ID -> same `implementationClass` for one release (alias), old marked deprecated in docs.
- Done when: `./gradlew check` identical to before; changing `GROUP` locally to a dummy value builds with zero other edits.

## Step 1 — package sweep (script, one commit)
Use `git mv` per dir (history kept), then rewrite. Rules, by string shape — never one blanket sed:
- `package ...` / `import ...` / FQCN in `*.kt` under `src/**`: `io.github.koltalabs.kolt.` -> `kolt.`
- Maven coordinate = `io.github.koltalabs.kolt:` (has colon) -> leave (group, Step 0).
- Plugin ID = `io.github.koltalabs.kolt.(kmp.|library|application|data|publish)...` -> leave (plugin IDs). Must be excluded from the sweep or consumers break.
- Android `namespace = ` in each `libs/*/build.gradle.kts` -> new package (keep namespace == root package of module; R/BuildConfig move with it).
- `packageOfResClass` (location-picker) -> `kolt.locationpicker.generated.resources`. Pin it explicitly for ANY module using `compose.resources`: default is derived from group+module, so a later group change would silently move `Res` and break imports.
- Hidden string couplings (grep each): `AndroidManifest.xml` class names + `androidx.startup` `InitializationProvider` meta-data (7 xml), crashlogs intent action `io.github.koltalabs.kolt.crashlogs.VIEW` (lib + ui module, change both together; -> `kolt.crashlogs.VIEW`), `META-INF/services/...RuleSetProvider` file content in detekt-rules, any `-keep`/consumer ProGuard rules, `implementationClass` strings in `gradlePlugin {}`, `mainClass` in demo (`...demo.MainKt`).
- Docs/steering/tests touched same commit: `Standards/steering/**`, `docs/**`, `README.md`, `AGENTS.md`, `demo-web/docs/**` (html/js), `project-templates/**` imports, Konsist/Detekt rule fixtures + `KoltRulesTest`. Konsist rules asserting package prefixes must be updated or `check` fails.
- Do not touch: generated `build/`, `kotlin-js-store`.

## Step 2 — verify
- `./gradlew check` all modules + build-logic tests + detekt-rules tests.
- Leftover scan: `grep -r 'io\.github\.koltalabs\.kolt\.' --include=*.kt --include=*.xml` must be empty except plugin IDs/group in allowlist (build files, toml, plugin.xml `<id>`).
- `publishToMavenLocal`; unzip one jar/aar/klib per lib: package dirs = `kolt/...`, no old dirs, manifest namespace correct.
- Consumer smoke: generate project from `project-templates` (kmp + android), resolve from mavenLocal, compile, run demo-app (desktop + android) + demo-web build.
- Flip test (proves goal): set `GROUP` to dummy, `publishToMavenLocal`, confirm a consumer compiles with ONLY its dep line changed (imports untouched), then revert.

## Later: when group / plugin IDs change
- Edit `GROUP` + plugin ID list. Zero Kotlin edits.
- Maven Central: new group = new namespace verification (domain TXT / GitHub). Old versions stay forever. Publish final old-group release with a POM `<relocation>` to new coordinates. Update `kolt-bom` first; consumers using BOM change one line.
- Plugins: register old + new ID for one release; remove old next major.
- Native klib `unique_name` derives from group — changes harmlessly (Gradle resolves by coordinates); only matters if two klibs share a name.
- Breaking for consumers, so version bump: this package change is a MAJOR for every lib; add `docs/MIGRATION.md` entry (old->new import map, one `sed` line for consumers).

## Risks
- Largest: ~424 files reference old string (337 kt). Mechanical but wide; one commit, easy `git revert`.
- Silent breakage candidates: plugin IDs swept by mistake, Compose `Res` package, manifest/intent strings, ServiceLoader file. All covered by Step 2.
- Package clash: confirm no other dep on classpath uses top-level `kolt` before shipping.

Where: `libs/*`, `build-logic/**`, `intellij-plugin/**`, `demo-app`, `demo-web`, `project-templates`, `Standards/steering/**`, `docs/**`, `gradle.properties`, `settings.gradle.kts`, `gradle/*.versions.toml`.

Status: Executed in working tree (uncommitted). Steps 0 & 1 complete: package root `kolt.*` moved across all modules, build-logic, intellij-plugin, demo, templates, and docs. GROUP single-sourced from root gradle.properties. Verified: leftover scan categorized & clean; build-logic check passed; all module unit/desktop tests & lint passed; publishToMavenLocal verified via artifact unzip; consumer smoke project compiled; flip test passed & reverted. Unverified: iOS binary linking (no Xcode CLI) and wasmJsBrowserTest (no ChromeHeadless).
