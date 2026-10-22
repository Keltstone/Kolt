package io.github.koltalabs.kolt.crashlogs

import android.content.Context
import android.content.pm.ApplicationInfo
import androidx.startup.Initializer

/**
 * Registered via App Startup (see androidMain/AndroidManifest.xml). Enables [CrashLogs]
 * when the host app is debuggable; release builds stay off. Override afterwards with
 * [CrashLogs.install].
 */
class CrashLogsInitializer : Initializer<Unit> {
    override fun create(context: Context) {
        appContext = context.applicationContext
        val debuggable = (context.applicationInfo.flags and ApplicationInfo.FLAG_DEBUGGABLE) != 0
        CrashLogs.onRecorded = { notifyCrash(context.applicationContext, it) }
        CrashLogs.install(debuggable)
    }

    override fun dependencies(): List<Class<out Initializer<*>>> = emptyList()
}
