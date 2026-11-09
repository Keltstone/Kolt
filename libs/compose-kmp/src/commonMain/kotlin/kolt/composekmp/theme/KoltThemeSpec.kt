package kolt.composekmp.theme

import androidx.compose.material3.ColorScheme
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LocalRippleConfiguration
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember

/**
 * Everything a Kolt theme decides. Presets and app customisations are `(spec) -> spec.copy(...)`:
 * shared values (4dp spacing scale, type steps, font) stay in the base and are never repeated.
 */
data class KoltThemeSpec(
    val colors: BaseColors,
    val sizes: Sizes,
    val typography: BaseTextStyles,
    val layout: KoltLayout,
)

/** What a platform theme adds on top of the shared base. Internal: apps use [customize] instead. */
internal class KoltPreset(
    /** null keeps the Kolt palette and the enclosing Material scheme. */
    val scheme: ColorScheme? = null,
    val roles: (BaseTextStyles) -> Typography = ::createTextRoles,
    val shapes: Shapes? = null,
    val ripple: Boolean = true,
    val spec: (KoltThemeSpec, Typography) -> KoltThemeSpec,
)

/**
 * Wraps [CompositionBaseProvider] (Kolt.* defaults, fonts, dark mode untouched), then re-provides
 * the locals with [preset] and the app's [customize] applied.
 * [compactText] = false drops the Android <400dp text down-scaling and uses the exact M3 scale.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun KoltThemeProvider(
    isDarkTheme: Boolean?,
    font: AppFont,
    compactText: Boolean,
    preset: KoltPreset,
    customize: (KoltThemeSpec) -> KoltThemeSpec,
    content: @Composable () -> Unit,
) {
    CompositionBaseProvider(isDarkTheme = isDarkTheme, font = font) {
        val current = KoltThemeSpec(Kolt.colors, Kolt.sizes, Kolt.typography, Kolt.layout)
        val uiSizes = if (compactText) Kolt.uiSizes else defaultUiSizes()
        val base = if (compactText) current else current.copy(
            sizes = defaultSizes(),
            typography = createBaseTypography(defaultSizes(), current.typography.baseTextStyle.fontFamily),
        )
        val spec = remember(base, preset, customize) {
            customize(preset.spec(base, preset.roles(base.typography)))
        }
        CompositionLocalProvider(
            LocalColors provides spec.colors,
            LocalUiColors provides spec.colors.toUiColors(),
            LocalSizes provides spec.sizes,
            LocalUiSizes provides uiSizes,
            LocalTypography provides spec.typography,
            LocalLayout provides spec.layout,
            LocalRippleConfiguration provides if (preset.ripple) LocalRippleConfiguration.current else null,
        ) {
            MaterialTheme(
                colorScheme = preset.scheme ?: MaterialTheme.colorScheme,
                typography = remember(spec.typography) { preset.roles(spec.typography) },
                shapes = preset.shapes ?: MaterialTheme.shapes,
                content = content,
            )
        }
    }
}
