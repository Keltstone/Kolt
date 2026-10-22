plugins {
    id("io.github.keltstone.kolt.kmp.library-compose")
    id("io.github.keltstone.kolt.publish")
    alias(libs.plugins.dokka)
}

// Standalone: only depends on crashlogs (plain Material3, no Kolt UI lib).
kmp {
    enableUtils.set(false)
    enableDesktop.set(true)
    enableIos.set(true)
}

android {
    namespace = "kolt.crashlogs.ui"
}

dependencies {
    "commonMainApi"(project(":libs:crashlogs"))
    "androidMainImplementation"("androidx.activity:activity-compose:1.10.1")
}

mavenPublishing {
    coordinates(artifactId = "crashlogs-ui")
    pom {
        name = "Kolt CrashLogs UI"
        description = "Compose screen (and Android activity) for browsing recorded crashlogs."
        url = "https://github.com/keltstone/Kolt"
    }
}
