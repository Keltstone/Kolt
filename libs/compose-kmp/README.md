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

### Themes

Kolt themes: `KoltTheme` (Material 3) and `AppleTheme` (Apple HIG). Both wrap
`CompositionBaseProvider`, so `Kolt.colors` / `sizes` / `typography` / `flags` keep working, and add
one token group, `Kolt.layout` (`KoltLayout`): `screenSide`, `topToTitle`, `titleToSubtitle`,
`headerToContent`, `ctaGap`, `ctaBottom` (add the nav-bar / safe-area inset yourself),
`ctaPrimaryHeight`, `ctaSecondaryHeight`, `minTouchTarget`, text styles `screenTitle` /
`screenSubtitle` / `body` / `section` (`sectionAllCaps`), and style values `titleStyle`
(Standard/Large), `fieldStyle` (Outlined/Inset), `listStyle` (Plain/GroupedInset) with
`fieldRadius`, `fieldOutline`, `fieldFocusOutline`, `listRadius`, `listRowMinHeight`,
`listRowPadding`. Defaults are Material 3 compact (16 margin, 56 CTA and row, outlined 4dp field).

**Theme your own app (3 steps, no token layer, no copied components):**

```kotlin
KoltTheme(                                   // or AppleTheme(tint = …)
    colorScheme = myScheme,                  // 1. palette: colorScheme > seed > useWallpaper > Kolt palette
    customize = {                            // 2. override ONLY what differs from the base
        it.copy(layout = it.layout.copy(screenSide = 28.dp, ctaPrimaryHeight = 56.dp))
    },
) { App() }                                  // 3. wrap the app root
```

`customize` receives the whole `KoltThemeSpec(colors, sizes, typography, layout)`; shared values
(4dp spacing, type steps, font) are inherited, so an app states only its deltas. `compactText = false`
turns off the Android <400dp text down-scaling and gives the exact M3 scale.

| | KoltTheme (M3) | AppleTheme (HIG) |
|---|---|---|
| Palette | scheme > `seed` (contrast-guarded HSL approximation of HCT) > wallpaper (Android 12+) > Kolt brand palette; custom success green | system colours, `tint` (systemBlue), accessible success green |
| Text | M3 roles (display..label) | HIG roles (Large Title 34 Regular ... Caption2 11, SF tracking), no M3 line heights |
| Shapes / ripple | M3 shapes and ripple | 6/8/10/14/20 shapes, no ripple |
| Margin / CTA / row | 16 / 56 / 56 | 16 / 50 / 44, target 44 |
| Title / field / list | standard / outlined 4dp (2dp focus) / plain | large / inset 10dp, no outline / grouped inset 10dp with inset separators |
| Font | Roboto | platform system font (SF on iOS) |

Components read the style values: `KoltTopBar` (large title), `KoltValidatedTextField` (outlined or
inset), `KoltListGroup { row { … } }` (plain or grouped, separators) and `Modifier.ctaPrimary()` /
`ctaSecondary()`. There is no `GoogleButton` / `AppleButton`. `GoogleTheme` is deprecated: use
`KoltTheme(colorScheme = M3Schemes.blue(dark), customize = …)`. `KoltIosButton` is deprecated: use
`KoltButton` under `AppleTheme`.

### Theme rules (standard)

- **One theme per platform look, customised not forked.** Material look = `KoltTheme`, Apple look =
  `AppleTheme`. An app (Calendar, KeelPass, ...) never adds a third provider, a parallel
  `Screen*`/`*Dp`/`*Sp` token layer, or a copied component: it passes a palette and a `customize` lambda.
- **Follow the platform spec; prototype numbers are app overrides.** Kolt's presets hold Material 3 /
  Apple HIG values only. A value from a design prototype that differs from the spec (28dp margin, 30sp
  title, 52dp CTA, 12dp field radius) lives in the app's `customize`, not in Kolt.
- **Type:** use spec steps. 13sp and 15sp body are not M3 steps (14, 16) and not HIG body (17, 15
  subhead, 13 footnote only for footnote/section). Readable text is never below 12sp (11 only for
  captions/labels, per M3 labelSmall / HIG Caption 2). Sizes under 11 are decoration only.
- **Contrast:** text >= 4.5:1, non-text and outlines >= 3:1, light and dark. A generated palette
  (seed) must be contrast-checked, not trusted; Apple system colours that miss 4.5:1 as text (green)
  are darkened for text use.
- **Targets:** >= 48dp (Material) / 44pt (Apple). **Dark mode:** read the theme's dark flag, never
  `isSystemInDarkTheme()` inside a component.
- **Android narrow screens:** text is down-scaled on <400dp by default; set `compactText = false` for the exact scale.
- **Material leakage:** an Apple screen must not get M3 text roles, shapes or ripple (the Apple preset replaces them).
- **Steps:** add a scale step only when a spec value is missing (28dp = M3 extra-large shape was); a
  one-theme value stays in `Kolt.layout`/`customize`.

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
