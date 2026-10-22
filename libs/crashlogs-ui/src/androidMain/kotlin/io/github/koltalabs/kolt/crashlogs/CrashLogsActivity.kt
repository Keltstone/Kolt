package io.github.koltalabs.kolt.crashlogs

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme

/** Opened by the crash notification via [ACTION_VIEW_CRASHLOGS] (also launchable directly). */
class CrashLogsActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { MaterialTheme { CrashLogsScreen(onBack = ::finish) } }
    }
}
