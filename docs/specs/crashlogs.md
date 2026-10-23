# crashlogs — opt-in crash/throwable collector (Chucker 3.x style)

Goal: record throwables + uncaught crashes to disk, browse them in-app. Standalone (no other Kolt lib), off by default.

Behaviour:
- `CrashLogs.install(enabled)`; `onError(tag, throwable)` records handled throwables; uncaught handler (chained to previous) records fatal ones.
- Android auto-installs via App Startup, enabled = FLAG_DEBUGGABLE (like logutils `LogInitializer`). Desktop/iOS: manual `install(true)`. Default off elsewhere.
- Uncaught hooks: JVM/Android `Thread.setDefaultUncaughtExceptionHandler`; iOS `setUnhandledExceptionHook`.
- Android notification per recorded entry, best effort (posted from the crash path; may be lost if the process dies first); tap opens `CrashLogsActivity`; skipped if POST_NOTIFICATIONS not granted. Lib never requests it.
- UI: list (tag, class, message, raw epoch ms) → detail (selectable stack), clear-all, search by text. Plain Material3 (no Kolt UI lib).

Contracts:
- `CrashSummary(id, timeMillis, tag, className, message?)`; `CrashEntry(summary, stackTrace)`
- `CrashLogs { install(enabled=true); onError(tag, t); all(): List<CrashSummary> (newest first); get(id): CrashEntry?; search(q); clear(); maxEntries (default 50, set before install) }`
- Storage (no DB, no serializer): `crashlogs/index.txt` = one tab-separated escaped summary line per entry; `<id>.txt` = summary line + raw stack trace. Dirs: Android `filesDir`, desktop `~/.kolt/crashlogs`, iOS Documents. Sync + atomic writes (tmp+rename / atomically) — must finish inside uncaught handler. Oldest evicted past `maxEntries`.
- Modules (`libs/`): `crashlogs` (ONE artifact: collector + Compose viewer `CrashLogsScreen` + Android `CrashLogsActivity`, exported=false; deps: androidx.startup, activity-compose, Compose Material3), `crashlogs-no-op` (same collector API, empty, no UI).
- Plugin: `kmp { enableCrashLogs.set(true) }` (kmp.application only) and `kolt { enableCrashLogs.set(true) }`, `Property<Boolean>`, default false, independent of enableUtils. `debugImplementation(crashlogs)` + `releaseImplementation(crashlogs-no-op)`, versions via BOM. KMP library modules and desktop/iOS are NOT wired (a duplicate real/no-op on one classpath would clash) — add `crashlogs` manually.
- Crashlytics (same plugin extensions, separate flag): `enableCrashlytics` (default false, app modules only) applies `com.google.gms.google-services` + `com.google.firebase.crashlytics` and adds firebase-bom (version key `firebaseBom` in the consumer catalog) + `firebase-crashlytics`. Consumer root must declare both plugins `apply false` (catalog aliases `google-services`, `firebase-crashlytics`) and supply `google-services.json`. Hook is registered BEFORE AGP is applied (else Variant API "too late" error). Independent of `enableCrashLogs`.
- Crashlytics setup check (hard-fail, VersionFloor-style): `google-services.json` in module dir or `src/<variant>/`, with a `package_name` for applicationId and (if addDevSuffixToDebug) applicationId+suffix. Passing writes `<root>/.gradle/kolt/crashlytics-<module>.ok` = hash(json)+ids; same fingerprint skips the check, any change re-runs it.
- Tandem: `CrashLogs.onError` also calls `FirebaseCrashlytics.recordException` (+log tag) when the class is present (androidMain, `compileOnly` firebase-crashlytics, LinkageError swallowed); opt out `CrashLogs.forwardToCrashlytics=false`. `crashlogs-no-op` forwards too (release: Crashlytics is the reporter). Uncaught crashes not forwarded (Crashlytics catches itself). Each works without the other.
- BOM + `version.properties` (`CRASHLOGS_*`, shared by all three) + `settings.gradle.kts` + root dependency substitution + `bumpCrashlogsVersion`.

Edge:
- Handler never throws; any storage failure swallowed; always delegates to previous handler.
- Re-entrancy guard (crash while recording).
- Android: notification targets `CrashLogsActivity` by explicit intent. `CrashLogsScreen` is not in no-op: reach the UI via the notification, not from code that ships in release.
- Not a remote crash reporter: nothing leaves device except user-initiated share.

Where: `libs/crashlogs*`, `build-logic/conventions/.../extensions/{KmpCustomExtensions,CustomExtensions}.kt`, `KmpBaseConventionPlugin.kt`, `AndroidBaseConventionPlugin.kt`.

Status: done, single-artifact (iOS compiled, not linked/run: no Xcode CLI here; plugin wiring compiled, not exercised end-to-end)
