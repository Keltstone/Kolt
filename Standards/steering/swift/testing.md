# Unit Testing

**Swift Testing** (`import Testing`, `@Test`, `#expect`, `#require`) for unit
tests. XCTest only for UI and performance tests. Run with `swift test`.

## Where tests live

```
Tests/DomainTests/        UseCases (pure, fake repositories)
Tests/DataTests/          DataSources: error mapping, DTO mappers
Tests/PresentationTests/  View models: Action → State/Effect
```

Domain and presentation tests need no app host, no UI, no real `Process`.

## Domain: fakes over mocks

Hand-written fakes conforming to the Repository protocol; no mocking library
by default.

```swift
final class FakeLogcatRepository: LogcatRepository {
    var lines: [LogcatLine] = []
    func stream(device: DeviceID) -> AsyncStream<LogcatLine> {
        AsyncStream { c in lines.forEach { c.yield($0) }; c.finish() }
    }
}

@Test func collectsAllLines() async {
    let repo = FakeLogcatRepository(); repo.lines = [.sample]
    var got: [LogcatLine] = []
    for await l in ObserveLogcat(repository: repo)(device: "d1") { got.append(l) }
    #expect(got == [.sample])
}
```

## Data: inject the originator

Test a DataSource's error mapping by injecting a fake `ProcessRunning` /
`URLProtocol` stub that returns canned output or non-zero exit. Assert the
`DomainError` produced. Never hit a real `adb` or the network in unit tests.

## ViewModel: drive Actions, assert State, consume Effects

```swift
@Test @MainActor func appearLoadsDevices() async {
    let vm = DevicesViewModel(listDevices: ListDevices(repository: FakeDevices([.a])))
    await vm.send(.appear)
    #expect(vm.state.devices.count == 1)
}
```

- Inject fakes via init; no global state. Use `@MainActor` on tests that
  touch view models.
- For time, inject a `Clock`/`ContinuousClock` abstraction instead of
  sleeping; for async sequences use `confirmation` or collect with a bound.
- Cancellation: assert a stream's `Process`/handle is released when the
  consuming task is cancelled.

## UI tests (optional)

`XCUITest` for whole-journey smoke only; accessibility labels are the
locators, so they double as a11y checks.

## Swift Testing notes

- Tests run **in parallel** by default: no shared mutable globals or fixed
  temp paths; use `@Suite(.serialized)` only when unavoidable.
- Use `#require` to unwrap/stop, `@Test(arguments:)` for table-style cases,
  `confirmation` for events that must fire N times, tags to group.
- If a test target uses default-`MainActor` isolation, mark test types
  explicitly (`@MainActor`/`nonisolated`) — XCTest subclasses are the usual
  breakage; Swift Testing structs avoid it.

## Rules

1. Domain/presentation tests are Swift Testing, in the matching test target.
2. Fakes over mocks; no real process/network/disk in unit tests.
3. Every new UseCase and view-model action path gets a test; DataSource
   error mapping gets a test per mapped error.
4. No `sleep`-based timing; inject clocks.
