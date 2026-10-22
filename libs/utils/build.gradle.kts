plugins {
    id("io.github.keltstone.kolt.kmp.library")
    alias(kmplibs.plugins.kotlin.serialization)
    id("io.github.keltstone.kolt.publish")
    alias(libs.plugins.dokka)
}

// This module IS utils — don't auto-add the util libraries to itself.
kmp {
    enableUtils.set(false)
    enableDesktop.set(true)
    enableIos.set(true)
    enableWasm.set(true)
}

android {
    namespace = "kolt.utils"
}

dependencies {
    // Time extensions in commonMain use kotlinx-datetime and kotlinx.serialization custom serializers.
    "commonMainImplementation"(
        "org.jetbrains.kotlinx:kotlinx-serialization-json:${kmplibs.versions.kotlinxSerialization.get()}"
    )
    "commonMainImplementation"(
        "org.jetbrains.kotlinx:kotlinx-datetime:${kmplibs.versions.kotlinxDatetime.get()}"
    )
}

mavenPublishing {
    coordinates(artifactId = "utils")
    pom {
        name = "Kolt Utils"
        description = "Common Kotlin Multiplatform util functions and extension methods."
        url = "https://github.com/keltstone/Kolt"
    }
}
