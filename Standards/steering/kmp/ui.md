# UI Layout & Components

Mandatory defaults for every screen, unless the user's request says otherwise:

- **Edge-to-edge, always.** Content draws behind the system bars
  (`enableEdgeToEdge()` on Android, full-bleed root on iOS). Never a letterboxed
  or opaque-bar-bounded window.
- **System bar insets, always.** Edge-to-edge is not an excuse to clip: pad for
  status bar, navigation bar, cutout and IME (`WindowInsets.systemBars` /
  `safeDrawing`, `Modifier.windowInsetsPadding`, or the insets the Scaffold
  hands to content). Scrolling lists pad their `contentPadding` instead, so
  content scrolls under the bars but its last item stays reachable.
- **App bar only when needed.** If a screen has no title/actions to show, it
  has no app bar. When one is needed, use `KoltTopBar` (via `KoltPageScaffold`)
  with a normal back/up nav button (`NavigationMode`) and plain action buttons.
  No bootstrapped/default app bar, no collapsing/large/custom variant, and no
  non-standard nav control (hamburger, custom glyph) unless the user explicitly
  asks.
- **Kolt components only.** Every component comes from Kolt and follows the
  Kolt theme (`Kolt.colors` / `Kolt.sizes` / `Kolt.typography`) — text fields
  (`KoltValidatedTextField`, `KoltSearchTextField`, `KoltPasswordTextField`),
  dropdowns (`KoltDropdown`), buttons, text, sheets, switches, checkboxes,
  progress, and so on. Never raw Material3 (`TextField`, `OutlinedTextField`,
  `ExposedDropdownMenuBox`, `Button`, `Switch`, `TopAppBar`, …) when Kolt has
  an equivalent. Check `libs/compose-kmp` before reaching for Material; raw
  Material is only for what Kolt lacks (e.g. `Scaffold`, `Dialog`).
  Exception: an app that deliberately ships its own separate theme/design
  system uses that system's components, consistently.
- **Hairline dividers.** Separate rows/sections with `KoltDivider` (1 dp,
  Kolt's divider/cell-border color `Kolt.colors.dividerColor`) — not 2 dp+
  lines, not a raw `HorizontalDivider`/`Divider`, not a hand-picked color.
  Cell/card borders use the same color at 1 dp.
