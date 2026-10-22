plugins {
    id("io.github.koltalabs.kolt.kmp.library")
    id("io.github.koltalabs.kolt.publish")
    alias(libs.plugins.dokka)
}

// Standalone: no other Kolt lib is wired in (incl. logutils).
kmp {
    enableUtils.set(false)
    enableDesktop.set(true)
    enableIos.set(true)
}

android {
    namespace = "kolt.crashlogs"
}

dependencies {
    // App Startup powers the auto-installing CrashLogsInitializer on Android.
    "androidMainImplementation"("androidx.startup:startup-runtime:1.2.0")
}

mavenPublishing {
    coordinates(artifactId = "crashlogs")
    pom {
        name = "Kolt CrashLogs"
        description = "Opt-in on-device throwable and crash collector for Kotlin Multiplatform."
        url = "https://github.com/kolta-labs/Kolt"
    }
}
