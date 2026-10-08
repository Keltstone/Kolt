# Presentation: State / Action / Effect (MVVM, unidirectional)

SwiftUI + the Observation framework (`@Observable`). Never `ObservableObject`,
`@Published`, `@StateObject`, `@ObservedObject`, or `@EnvironmentObject`.

## Contract

All three in one `Contract.swift` per screen:

```swift
struct DevicesState: Equatable {
    var devices: [DeviceItem] = []
    var isLoading = false
    var errorMessage: String?
}

enum DevicesAction {
    case appear
    case select(DeviceID)
    case retry
}

enum DevicesEffect {          // one-shot: navigation, toast, copy-to-clipboard
    case openDetail(DeviceID)
}
```

- `State` is a `struct` of plain values; the single source of truth for the UI.
- `Action` is an `enum` of everything the user/system can do.
- `Effect` is for one-shot events only. Prefer modeling things as `State`
  when they can be (current Apple/SwiftUI guidance); use `Effect` for
  navigation and fire-and-forget actions.

## View model

```swift
@MainActor @Observable
final class DevicesViewModel {
    private(set) var state = DevicesState()
    let effects: AsyncStream<DevicesEffect>
    private let continuation: AsyncStream<DevicesEffect>.Continuation
    private let listDevices: ListDevices

    init(listDevices: ListDevices) {
        self.listDevices = listDevices
        (effects, continuation) = AsyncStream.makeStream()
    }

    func send(_ action: DevicesAction) async { /* reduce → state */ }
}
```

- `@MainActor`, `final`, one `send(_:)` entry point, `private(set)` state.
- Depends on UseCases (protocol or concrete struct) — never on a repository,
  `Process`, `URLSession`, `UserDefaults`.
- Owns its `Task`s; cancels them in `deinit`-safe fashion (store `Task?`,
  cancel on stop). Prefer views driving work with `.task { }` so SwiftUI
  cancels it on disappear.
- Reusable logic: compose small helper types the VM holds, don't subclass.

## No logic in views

Views are dumb, passive, state-hoisted: layout and bindings only. No
filtering, mapping, formatting decisions or business rules in `body` — those
belong in the view model or Domain.

## Wiring: Route owns the view model; content view is stateless

```swift
struct DevicesRoute: View {
    @State private var viewModel: DevicesViewModel
    let onOpenDetail: (DeviceID) -> Void

    init(container: AppContainer, onOpenDetail: @escaping (DeviceID) -> Void) {
        _viewModel = State(initialValue: container.makeDevicesViewModel())
        self.onOpenDetail = onOpenDetail
    }

    var body: some View {
        DevicesView(state: viewModel.state, onAction: { a in Task { await viewModel.send(a) } })
            .task { await viewModel.send(.appear) }
            .task {
                for await effect in viewModel.effects {
                    if case .openDetail(let id) = effect { onOpenDetail(id) }
                }
            }
    }
}
```

- The Route is the **only** consumer of the effect stream. It resolves
  navigation itself and forwards the rest as plain state/closures.
- The content view takes `state`, `onAction`, and navigation closures only —
  never the view model, container, or effect stream. It must render in
  `#Preview` with fake data alone.

## Property wrappers (Observation)

- Owned by the view: `@State private var viewModel`. (Replaces `@StateObject`.)
- Injected shared model: `@Environment(Type.self)`. (Replaces `@EnvironmentObject`.)
- Need `$` bindings into an observable model: `@Bindable var vm`. (Replaces
  `@ObservedObject` where bindings are needed; otherwise pass the model as a
  plain `let`.) Don't confuse with `@Binding`, which reads/writes a value
  owned elsewhere.
- `@State` has no autoclosure like `@StateObject` did: the initial value
  expression runs every time the parent re-evaluates `init`, though SwiftUI
  keeps only the first instance. So a view model's `init` must be **cheap and
  side-effect-free** — no I/O, no starting tasks. Start work from `.task`.

## Collections and performance

- `@Observable` tracks **property** access. Because the view reads
  `viewModel.state` (one property), any mutation of `state` invalidates every
  view that read any part of it. That's fine for small screens; for hot
  paths (high-rate logs, large lists) split the hot data into its own
  observable property or child view model so unrelated views don't re-evaluate.
  Keep `State` `Equatable` and avoid `[String: Any]`-style blobs.
- Big lists: `LazyVStack`/`List` with stable `Identifiable` ids; never
  `id: \.self` on non-stable values.
- Streams that arrive faster than the UI can draw (logcat): batch/throttle in
  the view model before mutating `state`, cap retained lines.

## Accessibility

- `.accessibilityLabel()` on every meaningful control/image; `.accessibilityHidden(true)` on decorative ones.
- Everything reachable and operable by keyboard; set `.keyboardShortcut` for primary actions where the platform expects it.
- Respect Dynamic Type / user text size; no fixed-height containers that clip text.

## Checklist for any new screen

- [ ] `Contract.swift` with State/Action/Effect
- [ ] `@MainActor @Observable` view model, `send(_:)` only entry point
- [ ] Route owns VM + effect stream; content view stateless
- [ ] `#Preview` with fake state
- [ ] Accessibility labels, strings in String Catalog, tokens not literals
- [ ] Tasks cancelled on disappear/stop
