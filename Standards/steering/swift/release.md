# Preparing a macOS Release

Only when asked to prepare a release — not for ordinary feature work.

## 1. Freeze & clean

- Working tree clean, `scripts/check.sh` green on the release commit.
- Remove debug logging, `#if DEBUG`-only UI, and TODOs that block shipping.
- Run the pre-commit exposure check (rule 22 in `AGENTS.md`) across the whole
  tree, not just the diff.

## 2. Version bump

- `CFBundleShortVersionString` (marketing, semver) and `CFBundleVersion`
  (monotonic build number) — from one place (`.xcconfig`).
- Update the changelog.

## 3. Release configuration

- Release build, optimization on, Hardened Runtime on.
- Minimum entitlements; verify with `codesign -d --entitlements :- App.app`.
- `LSUIElement` unchanged (agent apps stay Dock-less).
- No dev endpoints, test keys or verbose logging in Release.

## 4. One round of launch/perf optimization

- Profile once with Instruments (Time Profiler, Allocations, Leaks) on a
  realistic data load (e.g. high-rate logcat). Fix only what's measurably bad.
- Confirm streams/tasks are cancelled and `Process`es don't leak after
  stopping.

## 5. Whole-journey tests

- Run unit tests and a UI smoke test of the main journeys on the oldest
  supported macOS (14) and the latest.
- Manually exercise first-run: permission prompts, notification
  authorization, no-device-connected state.

## 6. Sign, notarize, distribute

- Developer ID sign (or Mac App Store signing) → `notarytool submit --wait` →
  `stapler staple` → verify with `spctl -a -vv`.
- Package as signed `.dmg`/`.zip`; for auto-update use Sparkle with EdDSA
  signing.
- Signing certs and notary credentials live in the Keychain / CI secrets —
  never in the repo.

## 7. Staged rollout

Ship to a small group first (TestFlight for MAS, or a beta channel in
Sparkle), watch crash reports, then widen.

## 8. Tag & close

Tag `vX.Y.Z`, attach the notarized artifact, archive dSYMs, update the
changelog.

## Agent rules

- Never put signing identities, notary credentials, or provisioning profiles
  in the repo or in chat.
- Don't publish, tag-push, or upload a build without explicit user approval.
