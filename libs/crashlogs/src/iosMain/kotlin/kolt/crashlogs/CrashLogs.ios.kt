package kolt.crashlogs

import kotlin.experimental.ExperimentalNativeApi
import kotlin.native.ReportUnhandledExceptionHook
import kotlin.native.setUnhandledExceptionHook
import kotlinx.cinterop.BetaInteropApi
import kotlinx.cinterop.ExperimentalForeignApi
import platform.Foundation.NSDate
import platform.Foundation.NSDocumentDirectory
import platform.Foundation.NSFileManager
import platform.Foundation.NSRecursiveLock
import platform.Foundation.NSSearchPathForDirectoriesInDomains
import platform.Foundation.NSString
import platform.Foundation.NSUTF8StringEncoding
import platform.Foundation.NSUserDomainMask
import platform.Foundation.create
import platform.Foundation.stringWithContentsOfFile
import platform.Foundation.timeIntervalSince1970
import platform.Foundation.writeToFile

@OptIn(ExperimentalForeignApi::class)
private class IosCrashStorage(private val dir: String) : CrashStorage {
    private val fm = NSFileManager.defaultManager

    init {
        fm.createDirectoryAtPath(dir, withIntermediateDirectories = true, attributes = null, error = null)
    }

    override fun read(name: String): String? =
        NSString.stringWithContentsOfFile("$dir/$name", NSUTF8StringEncoding, null)

    @OptIn(BetaInteropApi::class)
    // atomically = true makes Foundation write a temp file and rename it.
    override fun write(name: String, text: String) {
        NSString.create(string = text).writeToFile("$dir/$name", atomically = true, encoding = NSUTF8StringEncoding, error = null)
    }

    override fun delete(name: String) {
        fm.removeItemAtPath("$dir/$name", null)
    }
}

private val lock = NSRecursiveLock()

internal actual fun <T> locked(block: () -> T): T {
    lock.lock()
    try {
        return block()
    } finally {
        lock.unlock()
    }
}

internal actual fun nowMillis(): Long = (NSDate().timeIntervalSince1970 * 1000).toLong()

internal actual fun createCrashStorage(): CrashStorage? {
    val docs = NSSearchPathForDirectoriesInDomains(NSDocumentDirectory, NSUserDomainMask, true).first() as String
    return IosCrashStorage("$docs/crashlogs")
}

@OptIn(ExperimentalNativeApi::class)
internal actual fun installUncaughtHook(onCrash: (Throwable) -> Unit) {
    // Setting a hook replaces the default reporter, so the previous one (if any) is chained.
    var previous: ReportUnhandledExceptionHook? = null
    previous = setUnhandledExceptionHook { throwable ->
        onCrash(throwable)
        previous?.invoke(throwable) ?: throwable.printStackTrace()
    }
}
