plugins {
    id("io.github.keltstone.kolt.kmp.library-compose")
    id("io.github.keltstone.kolt.publish")
    alias(libs.plugins.dokka)
}

// Standalone: logs + UI in one artifact; plain Material3, no other Kolt lib.
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
    // Forwarding of handled errors; compileOnly so it is used only if the app ships Crashlytics.
    "androidMainCompileOnly"(platform("com.google.firebase:firebase-bom:${koltlibs.versions.firebaseBom.get()}"))
    "androidMainCompileOnly"("com.google.firebase:firebase-crashlytics")
    "androidMainImplementation"("androidx.activity:activity-compose:1.10.1")
}

mavenPublishing {
    coordinates(artifactId = "crashlogs")
    pom {
        name = "Kolt CrashLogs"
        description = "Opt-in on-device throwable and crash collector with a built-in viewer, for Kotlin Multiplatform."
        url = "https://github.com/keltstone/Kolt"
    }
}
