package kolt.crashlogs

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent

/** Opened by the crash notification via [ACTION_VIEW_CRASHLOGS] (also launchable directly). */
class CrashLogsActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { CrashLogsScreen(onBack = ::finish) }
    }
}
