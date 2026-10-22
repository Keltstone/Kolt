package io.github.koltalabs.kolt.crashlogs

/** Minimal text-file persistence; one implementation per platform. */
internal interface CrashStorage {
    fun read(name: String): String?

    /** Must be atomic (write temp, rename) so a crash mid-write never corrupts [name]. */
    fun write(name: String, text: String)

    fun delete(name: String)
}

internal expect fun createCrashStorage(): CrashStorage?

internal expect fun installUncaughtHook(onCrash: (Throwable) -> Unit)

internal expect fun nowMillis(): Long

/** Runs [block] under one process-wide lock. */
internal expect fun <T> locked(block: () -> T): T

private const val INDEX = "index.txt"
private const val SEP = '\t'

/**
 * Layout: `index.txt` holds one escaped summary line per entry (newest last);
 * `<id>.txt` holds the summary line followed by the raw stack trace.
 */
internal class CrashStore(private val storage: CrashStorage, private val maxEntries: Int) {
    private var counter = 0
    private var recording = false

    fun record(tag: String, throwable: Throwable): CrashSummary? = locked {
        if (recording) return@locked null // crash while recording
        recording = true
        try {
            val now = nowMillis()
            val summary = CrashSummary(
                id = "$now-${counter++}",
                timeMillis = now,
                tag = tag,
                className = throwable::class.qualifiedName ?: "Throwable",
                message = throwable.message,
            )
            storage.write("${summary.id}.txt", encode(summary) + "\n" + throwable.stackTraceToString())
            val kept = readIndex() + summary
            val evicted = kept.take((kept.size - maxEntries.coerceAtLeast(1)).coerceAtLeast(0))
            evicted.forEach { storage.delete("${it.id}.txt") }
            writeIndex(kept - evicted.toSet())
            summary
        } finally {
            recording = false
        }
    }

    fun all(): List<CrashSummary> = locked { readIndex().asReversed() }

    fun get(id: String): CrashEntry? = locked {
        val text = storage.read("$id.txt") ?: return@locked null
        val header = text.substringBefore('\n')
        CrashEntry(decode(header) ?: return@locked null, text.substringAfter('\n', ""))
    }

    fun search(query: String): List<CrashSummary> = all().filter {
        it.tag.contains(query, ignoreCase = true) ||
            it.className.contains(query, ignoreCase = true) ||
            it.message?.contains(query, ignoreCase = true) == true
    }

    fun clear() = locked {
        readIndex().forEach { storage.delete("${it.id}.txt") }
        writeIndex(emptyList())
    }

    private fun readIndex(): List<CrashSummary> =
        storage.read(INDEX).orEmpty().lineSequence().mapNotNull(::decode).toList()

    private fun writeIndex(entries: List<CrashSummary>) =
        storage.write(INDEX, entries.joinToString("\n") { encode(it) })

    private fun encode(s: CrashSummary) =
        listOf(s.id, s.timeMillis.toString(), s.tag, s.className, s.message ?: NULL_MARK)
            .joinToString(SEP.toString()) { escape(it) }

    private fun decode(line: String): CrashSummary? {
        val p = line.split(SEP)
        if (p.size != 5) return null
        val time = p[1].toLongOrNull() ?: return null
        return CrashSummary(
            id = unescape(p[0]),
            timeMillis = time,
            tag = unescape(p[2]),
            className = unescape(p[3]),
            message = p[4].takeIf { it != NULL_MARK }?.let(::unescape),
        )
    }

    // Escapes \, tab and newlines so one summary is always one line of 5 tab-separated fields.
    private fun escape(s: String) =
        s.replace("\\", "\\\\").replace("\t", "\\t").replace("\n", "\\n").replace("\r", "\\r")

    private fun unescape(s: String): String {
        val out = StringBuilder()
        var i = 0
        while (i < s.length) {
            val c = s[i]
            if (c == '\\' && i + 1 < s.length) {
                out.append(
                    when (val n = s[++i]) {
                        't' -> '\t'
                        'n' -> '\n'
                        'r' -> '\r'
                        else -> n
                    },
                )
            } else {
                out.append(c)
            }
            i++
        }
        return out.toString()
    }

    private companion object {
        // A null message; cannot collide with escaped text because a lone "\0" never appears there.
        const val NULL_MARK = "\u0000"
    }
}
