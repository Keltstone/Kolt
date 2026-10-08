# Theme / Design System

A dedicated `DesignSystem` target (or folder in a small project) owns every
visual constant and shared component. Everything else references it.

## Shape

```
DesignSystem/
  Tokens/      Colors.swift  Spacing.swift  Typography.swift  Radius.swift
  Components/  reusable views + ViewModifiers
  Resources/   Assets.xcassets (color sets, SF-symbol-less images), Localizable.xcstrings
  Theme.swift  Environment key exposing the tokens
```

Expose tokens through the SwiftUI environment:

```swift
struct Theme { var colors: AppColors; var spacing: AppSpacing; var type: AppTypography }
extension EnvironmentValues { @Entry var theme = Theme.standard }
// usage: @Environment(\.theme) private var theme
```

## Rules

1. **No hardcoded colors, sizes, fonts or user-facing strings** outside the
   DesignSystem. Use semantic system colors (`.primary`, `.secondary`,
   `Color(nsColor: .windowBackgroundColor)`) or tokens; spacing/radius from
   tokens; text styles (`.font(.body)`) or typography tokens — not
   `.font(.system(size: 13))`.
2. Colors come from asset-catalog color sets with Light/Dark (and
   High-Contrast) variants — never branch on `colorScheme` to pick RGB.
3. Strings live in `Localizable.xcstrings` (String Catalog); use
   `LocalizedStringResource`/`String(localized:)`. No string literals in views
   for user-visible text.
4. Support Dark Mode, increased contrast, reduce transparency/motion
   (`accessibilityReduceMotion`, `accessibilityReduceTransparency`).
5. Prefer system materials and SF Symbols; tint with semantic colors, always
   with an accessibility label.
6. Shared components are small, stateless, `#Preview`-able, and take only
   values + closures.
7. Menu-bar template images: `isTemplate = true` so they follow the menu bar
   appearance.
