package kolt.conventions.extensions

import org.gradle.api.GradleException
import org.gradle.api.Project
import org.gradle.api.artifacts.VersionCatalog
import org.gradle.api.plugins.UnknownPluginException
import java.io.File
import org.gradle.kotlin.dsl.dependencies

/**
 * Opt-in Firebase Crashlytics: when [enabled] is true at evaluation time, validates the
 * pre-setup, applies the google-services + crashlytics Gradle plugins and adds the SDK
 * (version pinned by firebase-bom from [catalog]). The consumer's root build must declare both
 * plugins `apply false` (aliases `google-services`, `firebase-crashlytics` in the Kolt catalogs).
 *
 * Validation (hard-fails with every problem listed): `google-services.json` present and
 * containing a client for each id in [appIds] (debug suffix included). Once it passes, a marker
 * is written to `<root>/.gradle/kolt/` keyed by a fingerprint of the JSON + ids, so later builds
 * skip the check until one of them changes.
 *
 * MUST be called before the Android Gradle plugin is applied: AGP builds its variants in its
 * own `afterEvaluate`, and the Firebase plugins use the Variant API, which throws "too late to
 * add actions" if they are applied after it. Hooks run in registration order, so registering
 * first makes ours run first.
 */
internal fun Project.applyCrashlyticsIf(
    catalog: VersionCatalog,
    enabled: () -> Boolean,
    appIds: () -> List<String>,
) {
    afterEvaluate {
        if (!enabled()) return@afterEvaluate
        validateCrashlyticsSetup(appIds())
        listOf("com.google.gms.google-services", "com.google.firebase.crashlytics").forEach { id ->
            try {
                pluginManager.apply(id)
            } catch (e: UnknownPluginException) {
                throw GradleException(
                    "enableCrashlytics needs plugin '$id' on the build classpath. In the root build.gradle.kts " +
                        "add: alias(<catalog>.plugins.google.services) apply false and " +
                        "alias(<catalog>.plugins.firebase.crashlytics) apply false.",
                    e,
                )
            }
        }
        dependencies {
            add("implementation", platform("com.google.firebase:firebase-bom:${catalog.getVersion("firebaseBom")}"))
            add("implementation", "com.google.firebase:firebase-crashlytics")
        }
    }
}

/** Application ids google-services.json must cover: [applicationId] plus its debug-suffixed twin. */
internal fun crashlyticsAppIds(applicationId: String?, addDevSuffix: Boolean, suffix: String): List<String> =
    listOfNotNull(applicationId, applicationId?.takeIf { addDevSuffix && suffix.isNotEmpty() }?.plus(suffix))

private fun Project.validateCrashlyticsSetup(appIds: List<String>) {
    val json = listOf(file("google-services.json")) +
        (file("src").listFiles()?.map { File(it, "google-services.json") } ?: emptyList())
    val found = json.firstOrNull { it.isFile }
    val marker = rootProject.file(".gradle/kolt/crashlytics-${path.replace(':', '_')}.ok")
    val fingerprint = (found?.readText().hashCode().toString() + appIds.joinToString(",", "|"))

    if (marker.isFile && marker.readText() == fingerprint) return

    val problems = mutableListOf<String>()
    if (appIds.isEmpty()) problems += "applicationId is not set in defaultConfig, so it cannot be matched to google-services.json."
    if (found == null) {
        problems += "google-services.json not found in ${projectDir.name}/ (or ${projectDir.name}/src/<variant>/). " +
            "Download it from the Firebase console (Project settings > Your apps)."
    } else {
        val packages = Regex("\"package_name\"\\s*:\\s*\"([^\"]+)\"").findAll(found.readText()).map { it.groupValues[1] }.toSet()
        appIds.filterNot { it in packages }.forEach {
            problems += "google-services.json has no Android app for '$it'. Register it in the Firebase console " +
                "(debug builds use the applicationId suffix) and re-download the file."
        }
    }
    if (problems.isNotEmpty()) {
        throw GradleException(
            "Kolt: enableCrashlytics is on but setup is incomplete for project '$name':\n" +
                problems.joinToString("\n") { " - $it" },
        )
    }
    marker.parentFile.mkdirs()
    marker.writeText(fingerprint)
}
