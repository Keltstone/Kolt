# crashlogs — opt-in crash/throwable collector (Chucker 3.x style)

Goal: record throwables + uncaught crashes to disk, browse them in-app. Standalone (no other Kolt lib), off by default.

Behaviour:
- `CrashLogs.install(enabled)`; `onError(tag, throwable)` records handled throwables; uncaught handler (chained to previous) records fatal ones.
- Android auto-installs via App Startup, enabled = FLAG_DEBUGGABLE (like logutils `LogInitializer`). Desktop/iOS: manual `install(true)`. Default off elsewhere.
- Uncaught hooks: JVM/Android `Thread.setDefaultUncaughtExceptionHandler`; iOS `setUnhandledExceptionHook`.
- Android notification per recorded entry, best effort (posted from the crash path; may be lost if the process dies first); tap fires implicit action `io.github.koltalabs.kolt.crashlogs.VIEW`; skipped if no UI module resolves it or POST_NOTIFICATIONS not granted. Lib never requests it.
- UI: list (tag, class, message, raw epoch ms) → detail (selectable stack), clear-all, search by text. Plain Material3 (no Kolt UI lib).

Contracts:
- `CrashSummary(id, timeMillis, tag, className, message?)`; `CrashEntry(summary, stackTrace)`
- `CrashLogs { install(enabled=true); onError(tag, t); all(): List<CrashSummary> (newest first); get(id): CrashEntry?; search(q); clear(); maxEntries (default 50, set before install) }`
- Storage (no DB, no serializer): `crashlogs/index.txt` = one tab-separated escaped summary line per entry; `<id>.txt` = summary line + raw stack trace. Dirs: Android `filesDir`, desktop `~/.kolt/crashlogs`, iOS Documents. Sync + atomic writes (tmp+rename / atomically) — must finish inside uncaught handler. Oldest evicted past `maxEntries`.
- Modules (`libs/`): `crashlogs` (core, KMP android/desktop/ios, zero deps except androidx.startup), `crashlogs-ui` (Compose screen + Android `CrashLogsActivity`, exported=false), `crashlogs-no-op` (same API, empty).
- Plugin: `kmp { enableCrashLogs.set(true) }` (kmp.application only) and `kolt { enableCrashLogs.set(true) }`, `Property<Boolean>`, default false, independent of enableUtils. `debugImplementation(crashlogs, crashlogs-ui)` + `releaseImplementation(crashlogs-no-op)`, versions via BOM. KMP library modules and desktop/iOS are NOT wired (a duplicate real/no-op on one classpath would clash) — add `crashlogs` manually.
- BOM + `version.properties` (`CRASHLOGS_*`, shared by all three) + `settings.gradle.kts` + root dependency substitution + `bumpCrashlogsVersion`.

Edge:
- Handler never throws; any storage failure swallowed; always delegates to previous handler.
- Re-entrancy guard (crash while recording).
- Android release: UI not referenced from code (only via intent), so no `crashlogs-ui-no-op`.
- Not a remote crash reporter: nothing leaves device except user-initiated share.

Where: `libs/crashlogs*`, `build-logic/conventions/.../extensions/{KmpCustomExtensions,CustomExtensions}.kt`, `KmpBaseConventionPlugin.kt`, `AndroidBaseConventionPlugin.kt`.

Status: done (iOS compiled, not linked/run: no Xcode CLI here; plugin wiring compiled, not exercised end-to-end)
