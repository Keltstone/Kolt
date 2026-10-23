package kolt.crashlogs

/**
 * API-identical stand-in for `crashlogs` for release builds: stores and shows nothing. The one
 * thing it still does is forward [CrashLogs.onError] to Crashlytics when the app has it.
 */
data class CrashSummary(
    val id: String,
    val timeMillis: Long,
    val tag: String,
    val className: String,
    val message: String?,
)

data class CrashEntry(val summary: CrashSummary, val stackTrace: String)

object CrashLogs {
    var maxEntries: Int = 50

    var forwardToCrashlytics: Boolean = true

    fun install(enabled: Boolean = true) = Unit

    fun onError(tag: String, throwable: Throwable) {
        if (forwardToCrashlytics) sendToCrashlytics(tag, throwable)
    }

    fun all(): List<CrashSummary> = emptyList()

    fun get(id: String): CrashEntry? = null

    fun search(query: String): List<CrashSummary> = emptyList()

    fun clear() = Unit
}

internal expect fun sendToCrashlytics(tag: String, throwable: Throwable)
