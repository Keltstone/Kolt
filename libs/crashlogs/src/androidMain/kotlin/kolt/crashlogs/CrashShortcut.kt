package kolt.crashlogs

import android.content.Context
import android.content.Intent
import android.content.pm.ShortcutInfo
import android.content.pm.ShortcutManager
import android.graphics.drawable.Icon
import android.os.Build

private const val SHORTCUT_ID = "kolt_crashlogs"

/** Opens [CrashLogsActivity] in its own task, so it shows as a separate entry in Recents. */
internal fun crashLogsIntent(context: Context): Intent =
    Intent(context, CrashLogsActivity::class.java).addFlags(
        Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_NEW_DOCUMENT or Intent.FLAG_ACTIVITY_MULTIPLE_TASK,
    )

/** Adds (debuggable) or removes (release) the app-icon long-press "Crash logs" shortcut. */
internal fun updateCrashShortcut(context: Context, enabled: Boolean) {
    if (Build.VERSION.SDK_INT < 25) return
    val manager = context.getSystemService(ShortcutManager::class.java) ?: return
    runCatching {
        if (!enabled) {
            manager.removeDynamicShortcuts(listOf(SHORTCUT_ID))
            return
        }
        val shortcut = ShortcutInfo.Builder(context, SHORTCUT_ID)
            .setShortLabel("Crash logs")
            .setLongLabel("Crash logs")
            .setIcon(Icon.createWithResource(context, android.R.drawable.stat_notify_error))
            .setIntent(crashLogsIntent(context).setAction(Intent.ACTION_VIEW))
            .build()
        manager.addDynamicShortcuts(listOf(shortcut))
    }
}
