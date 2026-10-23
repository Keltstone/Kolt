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

private const val CHANNEL_ID = "kolt_crashlogs"

/**
 * Best-effort notification for a recorded throwable; tap opens [CrashLogsActivity]. Skipped
 * when notifications are not permitted. The library never requests POST_NOTIFICATIONS itself.
 */
internal fun notifyCrash(context: Context, summary: CrashSummary) {
    if (Build.VERSION.SDK_INT >= 33 &&
        context.checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED
    ) return

    val view = Intent(context, CrashLogsActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)

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
