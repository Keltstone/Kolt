# Navigation (macOS SwiftUI)

## Choose the structure by app shape

| App shape | Use |
|---|---|
| Document/utility window with sidebar → detail | `NavigationSplitView` |
| Linear drill-down inside one pane | `NavigationStack` with a typed path |
| Menu-bar / status-item agent | `NSStatusItem` + `NSMenu`; windows opened on demand via `Window`/`openWindow` |
| Secondary tool surfaces | `Window(id:)` / `Settings { }` scenes |

Navigation state is a **value you own**, not implicit: a `[Route]` /
`NavigationPath` held in a `@State`/`@Observable` router at the root. Routes
are `Hashable` enums with associated values.

```swift
enum AppRoute: Hashable { case devices; case detail(DeviceID) }

NavigationStack(path: $router.path) {
    DevicesRoute(...)
        .navigationDestination(for: AppRoute.self) { route in /* build Route views */ }
}
```

## Rules

1. One navigation root per window. Features contribute routes/destinations;
   they don't create their own competing stacks.
2. Navigation is **closure-only** outside the Route view: content views get
   `onOpenDetail: (DeviceID) -> Void`, never a router or the path.
3. The Route view resolves `Effect`s into router calls (see
   [presentation.md](presentation.md)); view models never touch the router.
4. Deep links: handle with `.onOpenURL` / `NSAppleEventManager` at the root
   and translate to a route; validate and ignore unknown URLs.
5. Sheets/popovers for ≤2 inputs, a separate `Window` or full pane for more;
   alerts/`confirmationDialog` for confirm/cancel only (rule 20 in `AGENTS.md`).
6. Menu-bar apps: `NSMenu` structure (including nested per-device submenus)
   is built from state in the `AppDelegate`/menu controller; menu actions
   call the same UseCases/view-model actions as the UI — no parallel logic.
7. Restore state with `@SceneStorage` only for UI position (selection, sidebar
   width), never for business data.
