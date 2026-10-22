import java.util.Properties

plugins {
    `kotlin-dsl`
}

val gradlePropsFile = rootDir.parentFile.resolve("gradle.properties")
val gradleProps = Properties().apply {
    if (gradlePropsFile.exists()) gradlePropsFile.inputStream().use { load(it) }
}
group = gradleProps.getProperty("GROUP", "io.github.keltstone.kolt")

// This module deliberately depends ONLY on the vanniktech maven-publish plugin — NOT on KGP/AGP.
// Keeping its classpath lean means a module that applies io.github.keltstone.kolt.publish (e.g. the
// BOM java-platform) does not pull Kotlin into its classloader scope, avoiding the
// KotlinNativeBundleBuildService classloader conflict in a whole-suite build.
dependencies {
    implementation(libs.vanniktech.maven.publish)
}

gradlePlugin {
    plugins {
        create("koltPublish") {
            id = "io.github.keltstone.kolt.publish"
            displayName = "Kolt Publish"
            description = "Applies the vanniktech maven-publish plugin from a shared, KGP-free classloader."
            implementationClass = "kolt.publish.KoltPublishConventionPlugin"
        }
    }
}
