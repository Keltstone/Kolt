# Changelog

All notable changes to `compose-kmp` (`io.github.keltstone.kolt:compose-kmp`) are documented
here, newest first. Format loosely follows
[Keep a Changelog](https://keepachangelog.com/en/1.1.0/).

## [Unreleased]
- Added `KoltTheme` (Material 3) and `AppleTheme` (HIG) and the `Kolt.layout` token group (`KoltLayout`, `LocalLayout`). Both wrap `CompositionBaseProvider` and take `customize: (KoltThemeSpec) -> KoltThemeSpec`, the one way for an app to override only what differs from the base. `KoltTheme` palette: `colorScheme` > `seed` (`seedColorScheme`, HSL approximation of HCT, contrast-guarded to 4.5:1) > Android 12+ wallpaper (`useWallpaper`) > Kolt palette. `compactText = false` disables the Android <400dp text down-scaling. `M3Schemes.blue(dark)` is the built-in Google-blue scheme.
- **Visual change:** `Kolt.layout` defaults are Material 3 compact (16 margin, 56 CTA/row, outlined field radius 4, 1dp outline in `outline` colour, 2dp focus). `KoltValidatedTextField` defaults read them, so fields are less rounded (was 8) with a visible outline and 2dp focus border (was 1dp, 10%-alpha). Pass `colors = KoltTextFieldDefaults.defaultColor(borderShape = …)` to keep the old look.
- `AppleTheme` is HIG-only: HIG text roles (Large Title 34 Regular … Caption 2) with SF tracking, 6–20dp shapes, no Material ripple, 16pt margins, 44pt targets, large titles, inset fields and grouped inset lists with inset separators, accessible success green.
- Deprecated `GoogleTheme` (delegates to `KoltTheme` with the built-in blue scheme; layout is now plain M3, prototype values move to `customize`) and `KoltIosButton` (use `KoltButton` under `AppleTheme`). `iosButtonColors` now follows the Kolt theme's dark flag, not `isSystemInDarkTheme()`.
- Added `Sizes.cornerRadiusXXLargeMid` / `UiSizes.cornerRadiusXXLargeMid` (28 dp, M3 extra-large shape; appended last; Android dimen `corner_radius_xxlarge_mid`).
- Fixed (Android): `cornerRadiusXXXLarge` read `corner_radius_xxlarge` (24 dp) instead of `corner_radius_xxxlarge` (32 dp, the value on all other platforms).
- Components read the theme's style values: `KoltTopBar` uses `LargeTopAppBar` when `titleStyle = Large`; `KoltValidatedTextField` takes radius/outline/focus width from `Kolt.layout` and an inset fill (no border) for `FieldStyle.Inset`. New `KoltListGroup { row { … } }` and `Modifier.ctaPrimary()` / `ctaSecondary()`.
- `BaseColors` gained `successOverride`, `successContainerOverride`, `onSuccessContainerOverride` (appended last, default Unspecified = derive as before).
- Fixed: `Sizes.iconXXSmall` (4 dp) and `Sizes.illustrationLarge` (200 dp) were never populated on any platform (`Unspecified`); now set (Android dimens `icon_xxsmall`, `illustration_large`).
- Fixed (Android): `paddingTiny` read `padding_xxsmall` (2 dp) instead of `padding_tiny` (1 dp, the value on all other platforms). **Visual change** of 1 dp wherever `paddingTiny` is used on Android.
- Tests: desktop composition tests now run (`ui-test` + `compose.desktop.currentOs` as `desktopTestImplementation`).
- Android: font sizes are now smaller on screens narrower than 400dp (e.g. Galaxy S23, ~360dp) — about ×0.88 of the previous values, rounded to whole sp (`values/dimens.xml`). Screens ≥ 400dp (Pixel-class phones, tablets, foldables) keep the previous sizes via the new `values-sw400dp/dimens.xml`. iOS, desktop and wasmJs unchanged.

## [0.2.1.dev-03] - 2026-09-24
- Added `KoltDefaults.sizes()` / `KoltDefaults.uiSizes()`: public access to the per-platform default `Sizes`/`UiSizes` (Android `dimens.xml`, shared defaults elsewhere), so an app providing its own `LocalSizes` can `copy()` a few fields instead of leaving the rest `Unspecified`. Additive only.
- Added a general element-size scale to `Sizes`/`UiSizes` (appended last): `sizeXXXSmall` 8, `sizeXXSmall` 16, `sizeXSmall` 24, `sizeSmall` 32, `sizeSmallMedium` 36, `sizeMedium` 40, `sizeMediumLarge` 48, `sizeLarge` 56, `sizeXLarge` 64, `sizeXXLarge` 80, `sizeXXXLarge` 96, `sizeGiant` 128 dp — for button/row/bar heights, avatars, dots and boxes, so non-icon dimensions stop borrowing `icon*` names. Android dimens `size_*`.
- Added an image/illustration size scale to `Sizes`/`UiSizes` (appended last): `imageXXSmall` 24, `imageXSmall` 32, `imageSmall` 40, `imageSmallMedium` 48, `imageMedium` 64, `imageMediumLarge` 80, `imageLarge` 96, `imageXLarge` 120, `imageXXLarge` 160, `imageXXXLarge` 200, `imageHuge` 240, `imageGiant` 320 dp, plus `imageSmallMediumPlus` 56 dp (Material list-item thumbnail; appended after `imageGiant`, not in scale order) — avatars, thumbnails, empty-state and hero illustrations. Android dimens `image_*`.
- `KoltBottomNavBar` / `DefaultBottomNavigationItem` gained `iconSize: Dp = Kolt.sizes.iconMedium` (default unchanged).

## [0.2.1.dev-02] - 2026-09-23
- Added type-scale slots `fontSizeMediumLargeMid` / `textMediumLargeMid` (17 sp, between `MediumLarge` 16 and `Large` 18) and `fontSizeXLargeMid` / `textXLargeMid` (22 sp, between `XLarge` 20 and `XXLarge` 24) to `Sizes`, `UiSizes` and `BaseTextStyles`, with Android dimens `font_size_medium_large_mid` / `font_size_xlarge_mid` and desktop actuals.
- Additive only: new params are appended at the end of each constructor, so named callers and existing `componentN()` are unaffected. The JVM constructor signatures of `Sizes`, `UiSizes` and `BaseTextStyles` changed — recompile consumers that call them positionally from Java or prebuilt binaries.
- Added `Kolt.typography.title` and `Kolt.typography.body`: two `TextScale` sets, each the whole size scale with short names (`title.xLarge`, `body.xSmall`, …); weight stays an extension (`.bold`). Both reference the existing `text*` styles (no copies) and are body properties of `BaseTextStyles`, so the constructor, `equals()` and `componentN()` are unchanged. `CompositionBaseProvider` now also wraps content in `MaterialTheme(typography = …)` built from the Kolt scale (color scheme and shapes still come from the enclosing `MaterialTheme`). **Visual change:** Material components (`Text` default style, `Button`, `TextField`, `TopAppBar`, …) inside the provider now use the Kolt font and sizes instead of the M3 defaults.
- `BaseTextStyles` is now remembered in `CompositionBaseProvider` instead of rebuilt on every recomposition.
- Deprecated the `BaseTextStyles` M3-style aliases (`bodyXXXSmall`…`displaySmall`); their sizes don't match M3. `ReplaceWith` points at the identical `body.*` step. Internal usages migrated with no visual change.
- Deprecated the flat `text*` styles (`Kolt.typography.textXLarge`, …) in favour of `body.*`/`title.*`; `ReplaceWith` gives the identical `body.*` style. They're still the constructor params that store the styles and go away in 0.3.0. All Kolt usages migrated, no visual change.
- `UiSizes` gained `fontSizeXBig` (36 sp), appended last, to match `Sizes`.
- iOS and wasmJs now get the same default sizes as desktop (moved to a shared `defaultSizes()`/`defaultUiSizes()`), instead of all-`Unspecified`. **Visual change** on those platforms: `Kolt.typography`/`Kolt.sizes` now have real values, where text previously fell back to the platform default size.
- Android: replaced deprecated `Resources.getColor(Int)` with `Context.getColor` in `UiColor.getColor`, and `DisplayMetrics.scaledDensity` with `density * configuration.fontScale` in `UiDimen` (same values).

## [0.2.1.dev-01] - 2026-09-21
- Added `KoltCheckBox` (gradient fill, configurable `shape`/corner rounding) to `components/core/selectors`.
- Added `KoltRadioButtonGroup` (single-selection list of `KoltRadioButton`s) to `components/core/selectors`.
- `KoltRadioButton` gained a `shape` param (defaults to `CircleShape`) for rounded-square styling.
- Resolved Material 3 deprecations: updated `TopAppBarColors`, unified tooltip position providers, and updated `menuAnchor` parameters.
- Parameterized `KoltDropdownCore` with `<T>`, eliminating unchecked cast.
- Suppressed `LocalClipboardManager` deprecation with CMP-7624 rationale.

## [0.2.1.dev-00] - 2026-08-08
- Changelog tracking starts here — see the git log for prior history.
