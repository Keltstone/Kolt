package io.github.koltalabs.kolt.crashlogs

/** API-identical stand-in for `crashlogs`: every call does nothing. Used for release builds. */
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

    fun install(enabled: Boolean = true) = Unit

    fun onError(tag: String, throwable: Throwable) = Unit

    fun all(): List<CrashSummary> = emptyList()

    fun get(id: String): CrashEntry? = null

    fun search(query: String): List<CrashSummary> = emptyList()

    fun clear() = Unit
}
