# Migration Guide: `io.github.appspiriment.kolt` → `io.github.keltstone.kolt`

This guide explains how to migrate existing consumer and multiplatform projects from legacy `appspiriment` coordinates to **Kolt** (`io.github.keltstone.kolt`).

---

## 1. Quick Resolution: "Plugin not found" Error

If your project fails to sync or build with:
```
Plugin [id: 'io.github.appspiriment.kolt.kmp.application'] was not found in any of the following sources:
```
(or any other `io.github.appspiriment.kolt.*` plugin id), it means the project is requesting the retired Gradle plugin coordinates.

### Fix
Open the corresponding `build.gradle.kts` file and update the plugin declaration:

```kotlin
// BEFORE (broken)
plugins {
    id("io.github.appspiriment.kolt.kmp.application")
}

// AFTER (fixed)
plugins {
    id("io.github.keltstone.kolt.kmp.application")
    // or via version catalog: alias(kmplibs.plugins.kmp.application)
}
```

---

## 2. Full Convention Plugin ID Mapping

| Old Legacy Plugin ID | New Kolt Plugin ID | Catalog Accessor |
|---|---|---|
| `io.github.appspiriment.kolt.kmp.application` | `io.github.keltstone.kolt.kmp.application` | `alias(kmplibs.plugins.kmp.application)` |
| `io.github.appspiriment.kolt.kmp.library` | `io.github.keltstone.kolt.kmp.library` | `alias(kmplibs.plugins.kmp.library)` |
| `io.github.appspiriment.kolt.kmp.library-compose` | `io.github.keltstone.kolt.kmp.library-compose` | `alias(kmplibs.plugins.kmp.library.compose)` |
| `io.github.appspiriment.kolt.kmp.library-koin` | `io.github.keltstone.kolt.kmp.library-koin` | `alias(kmplibs.plugins.kmp.library.koin)` |
| `io.github.appspiriment.kolt.kmp.library-koin-compose` | `io.github.keltstone.kolt.kmp.library-koin-compose` | `alias(kmplibs.plugins.kmp.library.koin.compose)` |
| `io.github.appspiriment.kolt.kmp.data` | `io.github.keltstone.kolt.kmp.data` | `alias(kmplibs.plugins.kmp.data)` |
| `io.github.appspiriment.kolt.application` | `io.github.keltstone.kolt.application` | `alias(koltlibs.plugins.kolt.application)` |
| `io.github.appspiriment.kolt.library` | `io.github.keltstone.kolt.library` | `alias(koltlibs.plugins.kolt.library)` |
| `io.github.appspiriment.kolt.library-compose` | `io.github.keltstone.kolt.library-compose` | `alias(koltlibs.plugins.kolt.library.compose)` |
| `io.github.appspiriment.kolt.library-hilt` | `io.github.keltstone.kolt.library-hilt` | `alias(koltlibs.plugins.kolt.library.hilt)` |
| `io.github.appspiriment.kolt.library-hilt-compose` | `io.github.keltstone.kolt.library-hilt-compose` | `alias(koltlibs.plugins.kolt.library.hilt.compose)` |
| `io.github.appspiriment.kolt.data` | `io.github.keltstone.kolt.data` | `alias(koltlibs.plugins.kolt.data)` |
| `io.github.appspiriment.kolt.publish` | `io.github.keltstone.kolt.publish` | — |

---

## 3. Version Catalog Configuration

In `settings.gradle.kts`:

```kotlin
dependencyResolutionManagement {
    versionCatalogs {
        create("koltlibs") {
            from("io.github.keltstone.kolt:kolt-catalog:<version>")
        }
        create("kmplibs") {
            from("io.github.keltstone.kolt:kmp-catalog:<version>")
        }
    }
}
```

> **Note:** For compatibility with older project files referencing `koltlibs.*`, you can also define:
> ```kotlin
> create("koltlibs") {
>     from("io.github.keltstone.kolt:kolt-catalog:<version>")
> }
> ```

---

## 4. Bill of Materials (BOM) & Runtime Libraries

```kotlin
// BEFORE
dependencies {
    implementation(platform("io.github.appspiriment.kolt:kolt-bom:<version>"))
    implementation("io.github.appspiriment.kolt:utils")
    implementation("io.github.appspiriment.kolt:logutils")
    implementation("io.github.appspiriment.kolt:compose-kmp")
}

// AFTER
dependencies {
    implementation(platform("io.github.keltstone.kolt:kolt-bom:<version>"))
    implementation("io.github.keltstone.kolt:utils")
    implementation("io.github.keltstone.kolt:logutils")
    implementation("io.github.keltstone.kolt:compose-kmp")
}
```

---

## 5. Package Names & Imports

Update your Kotlin source imports across all source sets:

- `io.github.appspiriment.kolt.utils.*` → `kolt.utils.*`
- `io.github.appspiriment.kolt.logutils.*` → `kolt.logutils.*`
- `io.github.appspiriment.kolt.composekmp.*` → `kolt.composekmp.*`
- `io.github.appspiriment.kolt.composeutils.*` → `kolt.composeutils.*`
- `io.github.appspiriment.kolt.location.*` → `kolt.location.*`
- `io.github.appspiriment.kolt.locationpicker.*` → `kolt.locationpicker.*`
- `io.github.appspiriment.kolt.updateutils.*` → `kolt.updateutils.*`

---

## 6. UI Components & Theme Renames

All UI components in `compose-kmp` and `compose-utils` have been updated to the unified `Kolt*` brand prefix:

| Legacy Symbol | Kolt Symbol |
|---|---|
| `AppsButton` | `KoltButton` |
| `AppsPageScaffold` | `KoltPageScaffold` |
| `AppsTopBar` | `KoltTopBar` |
| `AppsCard` | `KoltCard` |
| `AppsAccordion` | `KoltAccordion` |
| `AppsBadge` | `KoltBadge` |
| `AppsTooltip` | `KoltTooltip` |
| `AppsStatusTag` | `KoltStatusTag` |
| `AppsSlider` | `KoltSlider` |
| `AppsRatingBar` | `KoltRatingBar` |
| `AppsDivider` | `KoltDivider` |
| `AppsBottomSheet` | `KoltBottomSheet` |
| `AppsDialog` | `KoltDialog` |
| `AppsBanner` | `KoltBanner` |
| `AppsImageText` | `KoltImageText` |
| `AppspirimentText` | `KoltText` |
| `AppspirimentTheme` | `KoltTheme` |
| `kolt { ... }` DSL | `koltx { ... }` DSL (legacy alias retained) |

---

## 7. Scaffolding Tasks

If your project was using Gradle tasks to scaffold theme resources or documentation:
- `./gradlew scaffoldKoltResources` (generates `kolt_colors.xml` and `kolt_dimens.xml`)
- `./gradlew scaffoldKoltDocs` (generates `docs/KOLT.md`, `docs/ARCHITECTURE.md`, etc.)

---

## 8. compose-kmp 0.2.1.dev-02: new type-scale slots (no migration needed)

Additive only. New slots, appended at the end of each constructor:

| sp | `Sizes` / `UiSizes` | `BaseTextStyles` | Android dimen |
|---|---|---|---|
| 17 | `fontSizeMediumLargeMid` | `textMediumLargeMid` | `font_size_medium_large_mid` |
| 22 | `fontSizeXLargeMid` | `textXLargeMid` | `font_size_xlarge_mid` |

No existing slot was renamed, reordered or re-valued, and the Material3 aliases
are unchanged. Optional cleanup: replace hand-written
`TextStyle(fontSize = 17.sp)` / `TextStyle(fontSize = 22.sp)` with
`Kolt.typography.body.mediumLargeMid` / `Kolt.typography.body.xLargeMid`.

Also in `0.2.1.dev-02`:

- **Title / body sets** — `Kolt.typography.title` and `Kolt.typography.body` each hold the whole size scale (`title.xLarge`, `body.xSmall`, …); weight stays an extension (`.bold`). `CompositionBaseProvider` now also fills `MaterialTheme.typography` from the Kolt scale, so Material components inside it switch from the M3 default typography to the Kolt font and sizes. If you want your own M3 typography, wrap your own `MaterialTheme(typography = …)` inside `CompositionBaseProvider`.
- **Deprecated aliases** — `Kolt.typography.bodyXXXSmall`, `bodyXSmall`, `bodySmall`, `bodyMedium`, `bodyMediumLarge`, `bodyLarge`, `labelSmall`, `labelMedium`, `labelLarge`, `titleSmall`, `titleMedium`, `titleLarge`, `headlineSmall`, `headlineMedium`, `displaySmall`. They still work. The IDE quick-fix swaps in the identical `body.*` style (no visual change).
- **Deprecated `text*` styles** — `Kolt.typography.textMinimum` … `textGiant`, `textMediumLargeMid`, `textXLargeMid`. Use `Kolt.typography.body.<size>` (or `title.<size>` for titles); the quick-fix gives the identical style. They're removed in 0.3.0; `BaseTextStyles(textX = …)` constructor args go with them.
- **iOS / wasmJs** — `Kolt.sizes`, `Kolt.uiSizes` and `Kolt.typography` now have the desktop default values instead of all-`Unspecified`. If you override `LocalSizes`/`LocalTypography`, nothing changes for you.
- **`UiSizes.fontSizeXBig`** (36 sp) added, appended last.

---

## 9. Package rename: `io.github.keltstone.kolt.*` → `kolt.*`

Kotlin package roots across all Kolt libraries have moved from `io.github.keltstone.kolt.*` to `kolt.*` to decouple Kotlin source code from any future Maven group coordinate or organizational shifts. This is a breaking change requiring a major version bump for all published libraries. Maven publication group coordinates (`io.github.koltalabs.kolt`) and Gradle plugin IDs (`io.github.keltstone.kolt.*`) remain unchanged.

### Package Mapping

| Old Package Prefix | New Package Prefix |
|---|---|
| `io.github.keltstone.kolt.utils` | `kolt.utils` |
| `io.github.keltstone.kolt.logutils` | `kolt.logutils` |
| `io.github.keltstone.kolt.composekmp` | `kolt.composekmp` |
| `io.github.keltstone.kolt.composeutils` | `kolt.composeutils` |
| `io.github.keltstone.kolt.crashlogs` | `kolt.crashlogs` |
| `io.github.keltstone.kolt.crashlogs.ui` | `kolt.crashlogs.ui` |
| `io.github.keltstone.kolt.crashlogs.noop` | `kolt.crashlogs.noop` |
| `io.github.keltstone.kolt.location` | `kolt.location` |
| `io.github.keltstone.kolt.locationpicker` | `kolt.locationpicker` |
| `io.github.keltstone.kolt.updateutils` | `kolt.updateutils` |
| `io.github.keltstone.kolt.detekt` | `kolt.detekt` |
| `io.github.keltstone.kolt.conventions` | `kolt.conventions` |
| `io.github.keltstone.kolt.intellij` | `kolt.intellij` |
| `io.github.keltstone.kolt.demo` | `kolt.demo` |

### One-line Consumer Find-Replace

To migrate consumer source code without touching Gradle plugin IDs or Maven dependency coordinates, run this command in your project root (or use the equivalent IDE find-and-replace scoped to `*.kt` / `*.java` files):

```bash
find . -type f \( -name "*.kt" -o -name "*.java" \) -exec sed -i '' -E 's/(package|import) io\.github\.koltalabs\.kolt\./\1 kolt./g' {} +
```

---

## 10. Consumers: composite-build setup

When consuming Kolt as an `includeBuild` composite build, avoid repeating individual module substitution blocks across consumer projects. KoltLibs provides a shared settings script `gradle/kolt-consumer.settings.gradle.kts`.

### 1. Configure `gradle.properties`
Define the relative path to `KoltLibs`:
```properties
koltLibsDir=../KoltLibs
```

### 2. Configure `settings.gradle.kts`
Include `build-logic` in `pluginManagement {}`, place any other chained builds first, and apply the shared script last:

```kotlin
pluginManagement {
    includeBuild("${settings.providers.gradleProperty("koltLibsDir").get()}/build-logic")
    repositories {
        google()
        gradlePluginPortal()
        mavenCentral()
        mavenLocal()
    }
}

val koltLibsDir = providers.gradleProperty("koltLibsDir").get()

// (Chained builds go here, if any)

// chained builds first, Kolt last (hard rule, see kolt-libs.md)
apply(from = "$koltLibsDir/gradle/kolt-consumer.settings.gradle.kts")
```

---

## 10. Rebrand: Kolta Labs → Keltstone

In this release, the publishing organization and Maven group have transitioned from **Kolta Labs** (`io.github.koltalabs.kolt`) to **Keltstone** (`io.github.keltstone.kolt`).

### Coordinate & Plugin ID Mapping

| Old (`koltalabs`) | New (`keltstone`) | Type |
|---|---|---|
| `io.github.koltalabs.kolt` | `io.github.keltstone.kolt` | Maven Group |
| `https://github.com/kolta-labs/Kolt` | `https://github.com/keltstone/Kolt` | GitHub Repository |
| `kolta-labs` | `keltstone` | Developer / Publisher ID |
| `io.github.koltalabs.kolt.kmp.application` | `io.github.keltstone.kolt.kmp.application` | Gradle Plugin ID |
| `io.github.koltalabs.kolt.kmp.library` | `io.github.keltstone.kolt.kmp.library` | Gradle Plugin ID |
| `io.github.koltalabs.kolt.kmp.library-compose` | `io.github.keltstone.kolt.kmp.library-compose` | Gradle Plugin ID |
| `io.github.koltalabs.kolt.kmp.library-koin` | `io.github.keltstone.kolt.kmp.library-koin` | Gradle Plugin ID |
| `io.github.koltalabs.kolt.kmp.library-koin-compose` | `io.github.keltstone.kolt.kmp.library-koin-compose` | Gradle Plugin ID |
| `io.github.koltalabs.kolt.kmp.data` | `io.github.keltstone.kolt.kmp.data` | Gradle Plugin ID |
| `io.github.koltalabs.kolt.application` | `io.github.keltstone.kolt.application` | Gradle Plugin ID |
| `io.github.koltalabs.kolt.library` | `io.github.keltstone.kolt.library` | Gradle Plugin ID |
| `io.github.koltalabs.kolt.library-compose` | `io.github.keltstone.kolt.library-compose` | Gradle Plugin ID |
| `io.github.koltalabs.kolt.library-hilt` | `io.github.keltstone.kolt.library-hilt` | Gradle Plugin ID |
| `io.github.koltalabs.kolt.library-hilt-compose` | `io.github.keltstone.kolt.library-hilt-compose` | Gradle Plugin ID |
| `io.github.koltalabs.kolt.data` | `io.github.keltstone.kolt.data` | Gradle Plugin ID |
| `io.github.koltalabs.kolt.publish` | `io.github.keltstone.kolt.publish` | Gradle Plugin ID |

### Consumer Checklist
1. **Plugin IDs:** Update all `id("io.github.koltalabs.kolt.<name>")` to `id("io.github.keltstone.kolt.<name>")` in `plugins {}` blocks.
2. **Version Catalogs:** Update catalog imports in `settings.gradle.kts`:
   - `from("io.github.koltalabs.kolt:kolt-catalog:<version>")` → `from("io.github.keltstone.kolt:kolt-catalog:<version>")`
   - `from("io.github.koltalabs.kolt:kmp-catalog:<version>")` → `from("io.github.keltstone.kolt:kmp-catalog:<version>")`
3. **Dependencies:** Update BOM and individual library coordinates:
   - `implementation(platform("io.github.keltstone.kolt:kolt-bom:<version>"))`
   - `implementation("io.github.keltstone.kolt:<artifact>:<version>")`
4. **Imports:** Unaffected! Kotlin package imports remain `kolt.*`.
