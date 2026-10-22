package kolt.crashlogs

import kotlin.concurrent.Volatile

/** One recorded throwable, without its stack trace (cheap to list/search). */
data class CrashSummary(
    val id: String,
    val timeMillis: Long,
    val tag: String,
    val className: String,
    val message: String?,
)

/** A [CrashSummary] plus the full stack trace. */
data class CrashEntry(val summary: CrashSummary, val stackTrace: String)

/**
 * Opt-in on-device throwable collector (the Chucker 3.x "throwables" feature).
 *
 * Off until [install] is called with `enabled = true`. On Android the bundled
 * `CrashLogsInitializer` (App Startup) does that automatically for debuggable builds.
 * Everything is a no-op while disabled.
 */
object CrashLogs {
    /** Oldest entries are evicted past this count. Set before [install]. */
    @Volatile
    var maxEntries: Int = 50

    @Volatile
    private var store: CrashStore? = null

    @Volatile
    private var hookInstalled = false

    /** Called after every recorded entry (the Android notification uses it). */
    internal var onRecorded: ((CrashSummary) -> Unit)? = null

    /**
     * Enables or disables collection. When enabled, also hooks the uncaught-exception
     * handler (chained to the previous one, installed once).
     */
    fun install(enabled: Boolean = true) {
        store = if (enabled) createCrashStorage()?.let { CrashStore(it, maxEntries) } else null
        if (store != null && !hookInstalled) {
            hookInstalled = true
            installUncaughtHook { record("uncaught", it) }
        }
    }

    /** Records a handled throwable. */
    fun onError(tag: String, throwable: Throwable) {
        record(tag, throwable)
    }

    /** Recorded entries, newest first. */
    fun all(): List<CrashSummary> = store?.all().orEmpty()

    fun get(id: String): CrashEntry? = store?.get(id)

    /** Case-insensitive match on tag, class name or message. */
    fun search(query: String): List<CrashSummary> = store?.search(query).orEmpty()

    fun clear() {
        store?.clear()
    }

    private fun record(tag: String, throwable: Throwable) {
        // Never let the collector itself throw — it runs inside the crash path.
        runCatching {
            val summary = store?.record(tag, throwable) ?: return
            onRecorded?.invoke(summary)
        }
    }
}
