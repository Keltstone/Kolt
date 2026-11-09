package kolt.composekmp.theme

import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember

/**
 * Everything a Kolt theme preset decides. A preset is `(base) -> base.copy(...)`: shared values
 * (the 4pt spacing scale, type-step names, font) stay in the base and are never repeated.
 */
data class KoltThemeSpec(
    val colors: BaseColors,
    val sizes: Sizes,
    val typography: BaseTextStyles,
    val layout: KoltLayout,
)

/**
 * Wraps [CompositionBaseProvider] (so Kolt.* defaults, fonts and dark mode are untouched), then
 * re-provides the locals with [preset] applied. [uiSizes] is not re-derived: presets must not
 * change [Sizes] unless they also handle [UiSizes].
 */
@Composable
internal fun KoltThemeProvider(
    isDarkTheme: Boolean?,
    font: AppFont,
    colorScheme: ColorScheme,
    preset: (KoltThemeSpec) -> KoltThemeSpec,
    content: @Composable () -> Unit,
) {
    CompositionBaseProvider(isDarkTheme = isDarkTheme, font = font) {
        val base = KoltThemeSpec(Kolt.colors, Kolt.sizes, Kolt.typography, Kolt.layout)
        val spec = remember(base, preset) { preset(base) }
        CompositionLocalProvider(
            LocalColors provides spec.colors,
            LocalUiColors provides spec.colors.toUiColors(),
            LocalSizes provides spec.sizes,
            LocalTypography provides spec.typography,
            LocalLayout provides spec.layout,
        ) {
            MaterialTheme(
                colorScheme = colorScheme,
                typography = remember(spec.typography) { createTextRoles(spec.typography) },
                content = content,
            )
        }
    }
}
