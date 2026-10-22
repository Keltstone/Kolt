package io.github.koltalabs.kolt.crashlogs

import android.content.Context
import java.io.File

internal class FileCrashStorage(private val dir: File) : CrashStorage {
    init {
        dir.mkdirs()
    }

    override fun read(name: String): String? =
        File(dir, name).takeIf { it.isFile }?.readText()

    override fun write(name: String, text: String) {
        val tmp = File(dir, "$name.tmp")
        tmp.writeText(text)
        if (!tmp.renameTo(File(dir, name))) {
            // renameTo fails over an existing file on some platforms.
            File(dir, name).delete()
            tmp.renameTo(File(dir, name))
        }
    }

    override fun delete(name: String) {
        File(dir, name).delete()
    }
}

private val lock = Any()

internal actual fun <T> locked(block: () -> T): T = synchronized(lock, block)

internal actual fun nowMillis(): Long = System.currentTimeMillis()

internal actual fun installUncaughtHook(onCrash: (Throwable) -> Unit) {
    val previous = Thread.getDefaultUncaughtExceptionHandler()
    Thread.setDefaultUncaughtExceptionHandler { thread, throwable ->
        onCrash(throwable)
        previous?.uncaughtException(thread, throwable)
    }
}

/** Set by [CrashLogsInitializer]; null until the app has started. */
internal var appContext: Context? = null

internal actual fun createCrashStorage(): CrashStorage? =
    appContext?.let { FileCrashStorage(File(it.filesDir, "crashlogs")) }
