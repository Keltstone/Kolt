# libs/compose-kmp — Compose Multiplatform UI

[![Maven Central](https://img.shields.io/badge/Maven%20Central-0.2.1.dev--00-blue?style=flat-square)](https://central.sonatype.com/artifact/io.github.keltstone.kolt/compose-kmp)
[![KMP](https://img.shields.io/badge/Platform-Android%20%7C%20iOS%20%7C%20Desktop-7F52FF?style=flat-square&logo=kotlin)](https://kotlinlang.org/docs/multiplatform.html)
[![License](https://img.shields.io/badge/License-Apache%202.0-orange?style=flat-square)](../../LICENSE)
[![Changelog](https://img.shields.io/badge/Changelog-view-lightgrey?style=flat-square)](CHANGELOG.md)

Core Compose Multiplatform UI components and design-system tokens (`commonMain`, real `expect`/`actual` per platform — Android, iOS, Desktop). This is the shared foundation [`libs/compose-utils`](../compose-utils) (Android-only) builds on top of.

---

## Installation

```kotlin
dependencies {
    implementation("io.github.keltstone.kolt:compose-kmp:0.2.1.dev-00")
}
```

---

## Theming

Wrap your app root in a `CompositionBaseProvider`, then read tokens off `Kolt`:

```kotlin
CompositionBaseProvider(isDarkTheme = isSystemInDarkTheme(), font = AppFont.Roboto) {
    Text("Hello", color = Kolt.colors.onBackground, style = Kolt.typography.body.medium)
}
```

`Kolt.colors` / `Kolt.sizes` / `Kolt.typography` / `Kolt.flags` are `CompositionLocal`-backed — no hardcoded colors, dimens, or fonts outside this module (see the root steering docs' `theming.md`).

### Google and Apple themes

`GoogleTheme` (Material 3) and `AppleTheme` (Apple HIG) wrap `CompositionBaseProvider`, so
`Kolt.colors` / `sizes` / `typography` / `flags` keep working; they add one more token group,
`Kolt.layout` (`KoltLayout`): `screenSide`, `topToTitle`, `titleToSubtitle`, `headerToContent`,
`ctaGap`, `ctaBottom` (add the nav-bar / safe-area inset yourself), `ctaPrimaryHeight`,
`ctaSecondaryHeight`, `minTouchTarget`, text styles `screenTitle` / `screenSubtitle` / `body` /
`section` (`sectionAllCaps`), and style values `titleStyle` (Standard/Large), `fieldStyle`
(Outlined/Inset), `listStyle` (Plain/GroupedInset) with `fieldRadius`, `fieldOutline`,
`fieldFocusOutline`, `listRadius`, `listRowMinHeight`, `listRowPadding`. Without either theme
`Kolt.layout` is a neutral default and existing screens look exactly as before.

```kotlin
GoogleTheme(isDarkTheme = null, seed = Color(0xFF0B57D0)) { App() }   // or colorScheme = …, useWallpaper = true
AppleTheme(isDarkTheme = null, tint = null /* systemBlue */) { App() }
```

| | Google | Apple |
|---|---|---|
| Palette | `colorScheme` > `seed` > wallpaper (`useWallpaper`, Android 12+) > built-in Google blue | system colours, `tint` (default systemBlue), grouped backgrounds |
| Font | Roboto (default) | platform system font (SF on iOS) |
| Side / CTA | 28 / 56+52 | 20 / 50+50 |
| Title / body | 30 bold / 15 | 34 bold large title / 17 |
| Fields | outlined, 12 radius, 1 (2 focused) | inset fill, 10 radius, no outline |
| Lists | plain | grouped inset, 10 radius, 44 rows |

Presets are `KoltThemeSpec(colors, sizes, typography, layout)` built from one base with
`copy()`, so shared values are defined once. Components read the style values — there is no
`GoogleButton` / `AppleButton`: `KoltTopBar` (large title), `KoltValidatedTextField`
(outlined / inset), `KoltListGroup` + `KoltListRow` (plain / grouped) and
`Modifier.ctaPrimary()` / `ctaSecondary()` render both. `BaseColors` has optional
`successOverride` / `successContainerOverride` / `onSuccessContainerOverride`; both themes set
them. `seed` uses an HSL approximation of Material's HCT (close, not identical to wallpaper colours).

---

## Component inventory

| Package | Contents |
|---|---|
| `components/core/buttons` | `KoltButton`, `KoltOutlinedButton`, `KoltTextButton`, `KoltTonalButton`, `KoltLinkButton`, `KoltIosButton`, `KoltIconButton`, `KoltIconTextButton`, `KoltImageButton`, `KoltCircularButton` |
| `components/core/text` | `KoltText`, `KoltExpandableText`, `KoltHighlightText`, `KoltCopyableText`, `KoltImageText`, `KeyValuePairText`, `PrefixedText` |
| `components/core/text/textfield` | `KoltValidatedTextField` (stateful, `ValidatedTextFieldState`), `KoltPasswordTextField`, `KoltSearchTextField`, `InputTransformations` |
| `components/core/dropdowns` | `KoltDropDown`, `KoltTextDropDown`, `KoltIconDropDown`, `ChipDropDown`, `DropDownSpinner`, `TextDropDown`, `IconDropDown` |
| `components/core/messages` | `MessageDialog`, `KoltBanner`, `KoltSnackbar`, `KoltBottomSheet` |
| `components/core/image` | `KoltImage`, `KoltAvatar`, `KoltIcon`, `CircleIconBox` |
| `components/core/progress` | `KoltProgressIndicator`, `FullscreenLoader` |
| `components/core/selectors` | `KoltSelectableText`, `KoltCheckBox`, `KoltRadioButton`, `KoltRadioButtonGroup`, `KoltSwitch` |
| `components/core/modifiers`, `components/modifiers` | `CircleBackground`, `ModifierExtensions`, `ShimmerEffect` |
| `components/core` (misc) | `KoltAccordion`, `KoltDivider`, `KoltEmptyState`, `KoltRatingBar`, `KoltSlider`, `KoltStatusTag`, `KoltStepper`, `KoltTooltip`, `AsyncStateBox`, `ShimmerBox`, `Spacers` |
| `components/containers` | `PageScaffold`, `KoltTopBar`, `KoltDrawerScaffold`, `PullToRefreshBox`, `TitledCardView`, `bottomnavigation/KoltBottomNavigation` |
| `components/containers/swipeactionbox` | `SwipeableActionsBox` and its `SwipeAction`/`ActionFinder`/`SwipeableActionsState` support |
| `wrappers` | `UiText`, `UiColor`, `UiDimen`, `UiImage`, `FieldError` — platform-agnostic wrappers for string resources, colors, dimens, and images across Android/iOS/Desktop |

`AsyncStateBox` requires `AsyncState` from [`libs/utils`](../utils), which this module depends on directly.

---

## Not ported here

Anything built on `androidx.navigation` (`NavHost`, `NavGraphBuilder`, nav-scoped `AnimatedContent`) stays Android-only in `compose-utils` — this repo's steering standardizes on Navigation 3's caller-owned back stack instead of the old Navigation-Compose API, so those components weren't force-abstracted into commonMain.
