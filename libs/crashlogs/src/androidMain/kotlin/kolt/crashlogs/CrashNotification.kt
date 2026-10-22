package kolt.crashlogs

import android.Manifest
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build

/** Implicit action served by the optional `crashlogs-ui` activity. */
const val ACTION_VIEW_CRASHLOGS = "kolt.crashlogs.VIEW"

private const val CHANNEL_ID = "kolt_crashlogs"

/**
 * Best-effort notification for a recorded throwable. Skipped when notifications are not
 * permitted or no activity (the `crashlogs-ui` module) handles [ACTION_VIEW_CRASHLOGS].
 * The library never requests POST_NOTIFICATIONS itself.
 */
internal fun notifyCrash(context: Context, summary: CrashSummary) {
    if (Build.VERSION.SDK_INT >= 33 &&
        context.checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED
    ) return

    val view = Intent(ACTION_VIEW_CRASHLOGS).setPackage(context.packageName).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    if (context.packageManager.queryIntentActivities(view, 0).isEmpty()) return

    val manager = context.getSystemService(NotificationManager::class.java)
    manager.createNotificationChannel(
        NotificationChannel(CHANNEL_ID, "Crash logs", NotificationManager.IMPORTANCE_LOW),
    )
    val pending = PendingIntent.getActivity(context, 0, view, PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT)
    val notification = Notification.Builder(context, CHANNEL_ID)
        .setSmallIcon(android.R.drawable.stat_notify_error)
        .setContentTitle(summary.className.substringAfterLast('.'))
        .setContentText(summary.message ?: summary.tag)
        .setContentIntent(pending)
        .setAutoCancel(true)
        .build()
    manager.notify(summary.id.hashCode(), notification)
}
