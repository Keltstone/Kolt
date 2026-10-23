plugins {
    id("io.github.keltstone.kolt.kmp.library")
    id("io.github.keltstone.kolt.publish")
    alias(libs.plugins.dokka)
}

kmp {
    enableUtils.set(false)
    enableDesktop.set(true)
    enableIos.set(true)
}

android {
    namespace = "kolt.crashlogs.noop"
}

dependencies {
    // Forwarding of handled errors; compileOnly so it is used only if the app ships Crashlytics.
    "androidMainCompileOnly"(platform("com.google.firebase:firebase-bom:${koltlibs.versions.firebaseBom.get()}"))
    "androidMainCompileOnly"("com.google.firebase:firebase-crashlytics")
}

mavenPublishing {
    coordinates(artifactId = "crashlogs-no-op")
    pom {
        name = "Kolt CrashLogs No-Op"
        description = "Empty drop-in for crashlogs (same API) for release builds."
        url = "https://github.com/keltstone/Kolt"
    }
}
