plugins {
    id("io.github.koltalabs.kolt.kmp.library")
    id("io.github.koltalabs.kolt.publish")
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

mavenPublishing {
    coordinates(artifactId = "crashlogs-no-op")
    pom {
        name = "Kolt CrashLogs No-Op"
        description = "Empty drop-in for crashlogs (same API) for release builds."
        url = "https://github.com/kolta-labs/Kolt"
    }
}
