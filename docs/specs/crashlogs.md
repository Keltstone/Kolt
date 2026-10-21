# crashlogs — opt-in crash/throwable collector (Chucker 3.x style)

Goal: record throwables + uncaught crashes to disk, browse them in-app. Standalone (no other Kolt lib), off by default.

Behaviour:
- `CrashLogs.install(enabled)`; `onError(tag, throwable)` records handled throwables; uncaught handler (chained to previous) records fatal ones.
- Android auto-installs via App Startup, enabled = FLAG_DEBUGGABLE (like logutils `LogInitializer`). Desktop/iOS: manual `install(true)`. Default off elsewhere.
- Uncaught hooks: JVM/Android `Thread.setDefaultUncaughtExceptionHandler`; iOS `setUnhandledExceptionHook`.
- Android notification per entry (fatal ones shown next launch); tap fires implicit action `io.github.koltalabs.kolt.crashlogs.VIEW`; skipped if no UI module resolves it. POST_NOTIFICATIONS not requested by lib.
- UI: list (time, tag, class, message) → detail (full stack, share/copy), clear-all, search by text.

Contracts:
- `CrashEntry(id: String, timeMillis: Long, tag: String, className: String, message: String?, stackTrace: String)`
- `CrashLogs { install(enabled); onError(tag, t); all(): List<CrashEntry> (newest first); get(id); search(q); clear(); maxEntries (default 50) }`
- Storage: `<filesDir>/crashlogs/<id>.json` (full entry) + `index.json` (id,time,tag,className,message only; list/search read only index). No DB. Write sync + atomic (tmp+rename) — must finish inside uncaught handler. Oldest evicted past `maxEntries`.
- Modules (`libs/`): `crashlogs` (core, KMP android/desktop/ios, zero deps except androidx.startup), `crashlogs-ui` (Compose screen + Android `CrashLogsActivity`, exported=false), `crashlogs-no-op` (same API, empty).
- Plugin: `kmp { enableCrashLogs.set(true) }` and `kolt { enableCrashLogs.set(true) }`, `Property<Boolean>`, default false. Android: `debugImplementation(crashlogs, crashlogs-ui)` + `releaseImplementation(crashlogs-no-op)`. Desktop/iOS (no build types): real `crashlogs` in commonMain, runtime-gated off.
- BOM + `version.properties` (`CRASHLOGS_*`) + `settings.gradle.kts` include all three.

Edge:
- Handler never throws; any storage failure swallowed; always delegates to previous handler.
- Re-entrancy guard (crash while recording).
- Android release: UI not referenced from code (only via intent), so no `crashlogs-ui-no-op`.
- Not a remote crash reporter: nothing leaves device except user-initiated share.

Where: `libs/crashlogs*`, `build-logic/conventions/.../extensions/{KmpCustomExtensions,CustomExtensions}.kt`, `KmpBaseConventionPlugin.kt`, `AndroidBaseConventionPlugin.kt`.

Status: planned
