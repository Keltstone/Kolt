package kolt.crashlogs

import com.google.firebase.crashlytics.FirebaseCrashlytics

// firebase-crashlytics is compileOnly: present at runtime only if the app uses Crashlytics.
// A missing class surfaces as a LinkageError, so catch Throwable and carry on.
internal actual fun sendToCrashlytics(tag: String, throwable: Throwable) {
    try {
        FirebaseCrashlytics.getInstance().apply {
            log("[$tag] ${throwable.message}")
            recordException(throwable)
        }
    } catch (_: Throwable) {
    }
}
