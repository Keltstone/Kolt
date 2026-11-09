package kolt.composekmp.theme

import androidx.compose.material3.ColorScheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import kotlin.math.max
import kotlin.math.min

/**
 * Material-style scheme from a [seed] colour, in plain HSL tones.
 *
 * ponytail: an approximation, not Material's HCT: hues/tones are close to M3 but not identical to
 * Android's wallpaper scheme. Swap in material-color-utilities (Android-only) if exact output matters.
 */
fun seedColorScheme(seed: Color, dark: Boolean): ColorScheme {
    val (h, s, _) = seed.hsl()
    val sat = s.coerceIn(0.45f, 0.9f)
    fun tone(hue: Float, saturation: Float, t: Int) = Color.hsl((hue + 360f) % 360f, saturation, t / 100f)
    // HSL tones don't guarantee contrast like HCT does: darken the light accent until white text passes 4.5:1.
    fun p(t: Int) = tone(h, sat, t)
    fun p4(t: Int) = (t downTo 10).map { p(it) }.firstOrNull { contrast(Color.White, it) >= 4.5f } ?: p(10)
    fun s2(t: Int) = tone(h, sat * 0.3f, t)
    fun t3(t: Int) = tone(h + 60f, sat * 0.5f, t)
    fun n(t: Int) = tone(h, 0.08f, t)
    fun nv(t: Int) = tone(h, 0.12f, t)
    return if (dark) darkColorScheme(
        primary = p(80), onPrimary = p(20), primaryContainer = p(30), onPrimaryContainer = p(90),
        secondary = s2(80), onSecondary = s2(20), secondaryContainer = s2(30), onSecondaryContainer = s2(90),
        tertiary = t3(80), onTertiary = t3(20), tertiaryContainer = t3(30), onTertiaryContainer = t3(90),
        background = n(10), onBackground = n(90), surface = n(10), onSurface = n(90),
        surfaceVariant = nv(30), onSurfaceVariant = nv(80),
        surfaceContainer = n(12), surfaceContainerHigh = n(17), surfaceContainerHighest = n(22),
        outline = nv(60), outlineVariant = nv(30),
    ) else lightColorScheme(
        primary = p4(40), onPrimary = Color.White, primaryContainer = p(90), onPrimaryContainer = p(10),
        secondary = (40 downTo 10).map { s2(it) }.firstOrNull { contrast(Color.White, it) >= 4.5f } ?: s2(10), onSecondary = Color.White, secondaryContainer = s2(90), onSecondaryContainer = s2(10),
        tertiary = (40 downTo 10).map { t3(it) }.firstOrNull { contrast(Color.White, it) >= 4.5f } ?: t3(10), onTertiary = Color.White, tertiaryContainer = t3(90), onTertiaryContainer = t3(10),
        background = n(98), onBackground = n(10), surface = n(98), onSurface = n(10),
        surfaceVariant = nv(90), onSurfaceVariant = nv(30),
        surfaceContainer = n(94), surfaceContainerHigh = n(92), surfaceContainerHighest = n(90),
        outline = nv(50), outlineVariant = nv(80),
    )
}

internal fun contrast(a: Color, b: Color): Float {
    val (hi, lo) = listOf(a.luminance(), b.luminance()).sortedDescending()
    return (hi + 0.05f) / (lo + 0.05f)
}

private fun Color.hsl(): Triple<Float, Float, Float> {
    val mx = max(red, max(green, blue))
    val mn = min(red, min(green, blue))
    val l = (mx + mn) / 2f
    val d = mx - mn
    if (d == 0f) return Triple(0f, 0f, l)
    val s = d / (1f - kotlin.math.abs(2f * l - 1f))
    val h = when (mx) {
        red -> ((green - blue) / d) % 6f
        green -> (blue - red) / d + 2f
        else -> (red - green) / d + 4f
    } * 60f
    return Triple((h + 360f) % 360f, s, l)
}
