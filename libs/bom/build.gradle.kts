// ──────────────────────────────────────────────────────────────────────────────
// Kolt BOM (Bill of Materials)
//
// A Maven platform POM that constrains all Kolt library versions to a
// known-compatible set. Consumers add this once:
//
//   implementation(platform("io.github.koltalabs.kolt:kolt-bom:<version>"))
//
// After which all `io.github.koltalabs.kolt:*` artifacts can be declared without
// explicit versions. The convention plugins automatically inject this BOM, so
// most consumer projects never need to add it manually.
//
// Version: set by the root build.gradle.kts from BOM_VERSION in version.properties.
// Format: YYYY.MM.PATCH  (calendar-based, like the AndroidX BOM)
// ──────────────────────────────────────────────────────────────────────────────

plugins {
    `java-platform`
    id("io.github.koltalabs.kolt.publish")
}

// Lib versions are exposed as extra properties by the root build.gradle.kts,
// which reads them from version.properties. This avoids re-parsing the file here.
val utilsVersion:        String by rootProject.extra
val logutilsVersion:     String by rootProject.extra
val crashlogsVersion:    String by rootProject.extra
val composeUtilsVersion: String by rootProject.extra
val composeKmpVersion:   String by rootProject.extra
val updateUtilsVersion:  String by rootProject.extra
val locationVersion:     String by rootProject.extra

val koltGroup = project.group.toString()

dependencies {
    constraints {
        api("$koltGroup:utils:$utilsVersion")
        api("$koltGroup:logutils:$logutilsVersion")
        api("$koltGroup:crashlogs:$crashlogsVersion")
        api("$koltGroup:crashlogs-no-op:$crashlogsVersion")
        api("$koltGroup:crashlogs-ui:$crashlogsVersion")
        api("$koltGroup:compose:$composeUtilsVersion")
        api("$koltGroup:compose-kmp:$composeKmpVersion")
        api("$koltGroup:update-utils:$updateUtilsVersion")
        api("$koltGroup:location:$locationVersion")
    }
}

mavenPublishing {
    coordinates(artifactId = "kolt-bom")
    pom {
        name = "Kolt BOM"
        description = "Bill of Materials for the Kolt library suite — " +
            "constrains utils, logutils, crashlogs, compose, compose-kmp, location, and update-utils to a " +
            "known-compatible version set."
        url = "https://github.com/kolta-labs/Kolt"
        licenses {
            license {
                name = "Apache-2.0"
                url = "https://www.apache.org/licenses/LICENSE-2.0"
            }
        }
        developers {
            developer {
                id = "kolta-labs"
                name = "Kolta Labs"
                url = "https://github.com/kolta-labs"
            }
        }
        scm {
            connection = "scm:git:git://github.com/kolta-labs/Kolt.git"
            developerConnection = "scm:git:ssh://github.com/kolta-labs/Kolt.git"
            url = "https://github.com/kolta-labs/Kolt"
        }
    }
}
