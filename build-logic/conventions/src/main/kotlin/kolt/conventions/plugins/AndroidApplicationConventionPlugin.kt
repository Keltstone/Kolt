package kolt.conventions.plugins

import com.android.build.api.dsl.ApplicationExtension
import kolt.conventions.extensions.KoltExtension
import kolt.conventions.extensions.applyCrashlyticsIf
import kolt.conventions.extensions.crashlyticsAppIds
import kolt.conventions.extensions.koltLibs
import org.gradle.api.Project
import org.gradle.kotlin.dsl.getByType

class AndroidApplicationConventionPlugin : AndroidBaseConventionPlugin() {

    override val Project.commonExtension get() = extensions.getByType<ApplicationExtension>()

    override fun apply(target: Project) {
        target.run {
            // Must precede AGP so the Firebase plugins can still use the Variant API.
            applyCrashlyticsIf(
                koltLibs,
                enabled = { extensions.getByType<KoltExtension>().enableCrashlytics.orNull ?: false },
                appIds = {
                    val k = extensions.getByType<KoltExtension>()
                    crashlyticsAppIds(
                        extensions.getByType<ApplicationExtension>().defaultConfig.applicationId,
                        k.addDevSuffixToDebug.getOrElse(true),
                        k.debugApplicationIdSuffix.getOrElse(".dev"),
                    )
                },
            )

            // Apply the Android Application plugin first — required before any android {} block
            pluginManager.apply("com.android.application")

            setupHilt()
            setupCompose()
            super.apply(this)
        }
    }
}
