// Shared composite-build configuration for consumers of Kolt libraries.
// Included via: apply(from = "<relative path>/KoltLibs/gradle/kolt-consumer.settings.gradle.kts")

import java.io.File
import java.util.Properties

val koltLibsDirPath = providers.gradleProperty("koltLibsDir").orNull

val koltLibsDir: File = if (!koltLibsDirPath.isNullOrBlank()) {
    val dir = File(koltLibsDirPath)
    if (dir.isAbsolute) dir else File(rootDir, koltLibsDirPath)
} else {
    listOf(
        File(rootDir, "../KoltLibs"),
        File(rootDir, "../../KoltLibs"),
        File(rootDir, "../Kolt"),
        File(rootDir, "../../Kolt")
    ).firstOrNull { it.isDirectory && File(it, "gradle.properties").exists() }
        ?: error("koltLibsDir property not configured and KoltLibs could not be found relative to $rootDir")
}

val koltPropertiesFile = File(koltLibsDir, "gradle.properties")
if (!koltPropertiesFile.exists()) {
    error("KoltLibs gradle.properties not found at: ${koltPropertiesFile.canonicalPath}")
}

val koltProperties = Properties().apply {
    koltPropertiesFile.inputStream().use { load(it) }
}

val koltGroup = koltProperties.getProperty("GROUP")
    ?: error("GROUP not found in ${koltPropertiesFile.canonicalPath}")

includeBuild(koltLibsDir) {
    dependencySubstitution {
        substitute(module("$koltGroup:kolt-bom")).using(project(":libs:bom"))
        substitute(module("$koltGroup:utils")).using(project(":libs:utils"))
        substitute(module("$koltGroup:logutils")).using(project(":libs:logutils"))
        substitute(module("$koltGroup:crashlogs")).using(project(":libs:crashlogs"))
        substitute(module("$koltGroup:crashlogs-no-op")).using(project(":libs:crashlogs-no-op"))
        substitute(module("$koltGroup:crashlogs-ui")).using(project(":libs:crashlogs-ui"))
        substitute(module("$koltGroup:compose")).using(project(":libs:compose-utils"))
        substitute(module("$koltGroup:compose-kmp")).using(project(":libs:compose-kmp"))
        substitute(module("$koltGroup:update-utils")).using(project(":libs:update-utils"))
        substitute(module("$koltGroup:location")).using(project(":libs:location"))
        substitute(module("$koltGroup:location-picker")).using(project(":libs:location-picker"))
        substitute(module("$koltGroup:detekt-rules")).using(project(":libs:detekt-rules"))
    }
}

