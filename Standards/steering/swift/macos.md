# macOS platform rules

Read before touching subprocesses, the status item, `AppDelegate`,
entitlements, notifications, or settings.

## Process + Pipe

All external tool I/O (ADB, etc.) goes through one `ProcessRunner` DataSource
using Foundation `Process` + `Pipe`. Never `system()`, `popen()`, `sh -c`, or
string-built command lines.

```swift
let p = Process()
p.executableURL = adbURL                 // resolved, absolute
p.arguments = ["-s", serial, "exec-out", "screencap", "-p"]   // argv array, no shell
let out = Pipe(); p.standardOutput = out
try p.run()
let data = try out.fileHandleForReading.readToEnd() ?? Data()   // raw Data
p.waitUntilExit()                                               // only off the main actor
```

In app code wrap `terminationHandler` in `withCheckedThrowingContinuation`
instead of blocking on `waitUntilExit()`, and run it from a `Data`-layer
actor/`@concurrent` function — never on the main actor.

- Arguments are an array; user-controlled values (serials, paths) are never
  interpolated into a shell string.
- Read stdout/stderr concurrently (async `readabilityHandler` or
  `FileHandle.bytes`) to avoid pipe-buffer deadlock on large output.
- Binary output (screenshots) is piped as raw `Data` — no temp files, no
  on-device temp files (`exec-out`, not `shell` + `pull`).
- Streams (logcat) return `AsyncStream`; `onTermination` terminates the
  `Process` and closes handles. Cancel the owning `Task` when the stream
  stops or the view disappears.
- Non-zero exit / launch failure → map to `DomainError` in the DataSource.
- Resolve the tool path from settings or a known location; never assume
  `PATH` is inherited in a GUI app.

## Status item / menu bar

- `NSStatusItem` + `NSMenu` (not `MenuBarExtra`) when nested or dynamic
  submenus are required. The menu controller lives in/behind `AppDelegate`
  (`@NSApplicationDelegateAdaptor`).
- Rebuild menu items from state (`menuNeedsUpdate`/state observation); menu
  actions call the same UseCases as the UI.
- Keep `LSUIElement = YES` in `Info.plist` for agent apps (no Dock icon).
  Never remove it or set it to `NO`. Open regular windows with
  `NSApp.activate` after `openWindow`, since agent apps don't auto-activate.

## Settings

All settings go through `SettingsService` (protocol; `UserDefaults`-backed
impl in `Data`). Views and view models depend on the protocol — no
`@AppStorage`/`UserDefaults.standard` in them. Expose changes as an
`AsyncStream` or observable property, not `NotificationCenter` strings.
Secrets (tokens) belong in the Keychain, never `UserDefaults`.

## Notifications

`UNUserNotificationCenter`. Request authorization lazily at first use (not
launch), handle denied state gracefully, and keep notification creation
behind a `NotificationService` protocol.

## Sandbox, entitlements, hardened runtime

- Enable Hardened Runtime for distribution.
- **Sandbox vs. launching tools:** a sandboxed app cannot `Process`-launch an
  arbitrary binary (e.g. `adb` in `/usr/local/bin` fails with "launch path not
  accessible"). Options, in preference order: (1) distribute outside the Mac
  App Store with Developer ID + Hardened Runtime + notarization, unsandboxed;
  (2) bundle the tool as a helper signed with exactly
  `com.apple.security.app-sandbox` + `com.apple.security.inherit` (no other
  sandbox entitlements); (3) `NSUserUnixTask` with scripts the user installs
  in the app's Scripts folder. Decide this with the user before writing
  process code — it shapes distribution.
- Request the minimum entitlements; each added one is called out in the PR.
- Never write outside `Application Support`/`Caches`/user-chosen locations
  (`NSOpenPanel`/`NSSavePanel`).

## QR codes and images

Generate QR codes with CoreImage `CIQRCodeGenerator`; no third-party QR
library. Scale with nearest-neighbor interpolation to keep modules crisp.

## Rules

1. One `ProcessRunner`; no ad-hoc `Process` use in views, view models or UseCases.
2. Never shell out via a shell string.
3. Streams are cancellable and clean up their `Process`.
4. `LSUIElement` stays `YES` for agent apps.
5. Settings only via `SettingsService`; secrets only via Keychain.
6. Notification auth is lazy.

## Logging

`os.Logger(subsystem: Bundle.main.bundleIdentifier!, category: "adb")`. Levels:
`debug` (dev), `info`, `error`. Dynamic values default to private; mark
non-sensitive ones `privacy: .public` deliberately. Never log device serials,
file paths or tokens publicly. No `print`.

## Menu bar & Settings (HIG)

- Provide a real main menu (`.commands`) even for agent apps that show a
  window, with standard items and shortcuts (`⌘,` opens Settings, `⌘W`
  closes, `⌘Q` quits). Use the `Settings` scene for preferences — don't
  hand-roll a preferences window.
- Status-item menus: concise verb-first titles, disabled (not hidden)
  unavailable items, a Quit item, keyboard equivalents where natural.
- Use system controls and terminology; follow the macOS Human Interface
  Guidelines before building custom UI.
