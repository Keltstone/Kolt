package kolt.composekmp.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp

/** iOS system blue (light / dark variants). */
val AppleBlueLight = Color(0xFF007AFF)
val AppleBlueDark = Color(0xFF0A84FF)

/**
 * Apple HIG theme. [tint] defaults to systemBlue. [font] defaults to the platform system font
 * (SF on iOS; the platform default elsewhere).
 */
@Composable
fun AppleTheme(
    isDarkTheme: Boolean? = null,
    tint: Color? = null,
    font: AppFont = AppFont.Custom(FontFamily.Default),
    content: @Composable () -> Unit,
) {
    val dark = isDarkTheme ?: isSystemInDarkTheme()
    val scheme = appleScheme(tint ?: if (dark) AppleBlueDark else AppleBlueLight, dark)
    KoltThemeProvider(dark, font, scheme, { it.apple(scheme, dark) }, content)
}

/** Apple preset: only what differs from the shared base. Colours reuse [withScheme]. */
internal fun KoltThemeSpec.apple(scheme: ColorScheme, dark: Boolean = false): KoltThemeSpec {
    val text = typography.baseTextStyle
    return copy(
        colors = colors.withScheme(scheme).withSuccess(
            if (dark) Color(0xFF30D158) else Color(0xFF34C759),
            if (dark) Color(0xFF30D158).copy(alpha = 0.24f) else Color(0xFF34C759).copy(alpha = 0.16f),
            if (dark) Color(0xFF30D158) else Color(0xFF1E7B34),
        ),
        layout = layout.copy(
            screenSide = 20.dp,
            topToTitle = 16.dp,
            titleToSubtitle = 10.dp,
            headerToContent = 28.dp,
            ctaGap = 12.dp,
            ctaBottom = 20.dp,
            ctaPrimaryHeight = 50.dp,
            ctaSecondaryHeight = 50.dp,
            screenTitle = text.copy(
                fontSize = 34.sp, fontWeight = FontWeight.Bold, lineHeight = 1.18.em, letterSpacing = 0.4.sp,
            ),
            screenSubtitle = text.copy(fontSize = 15.sp),
            body = text.copy(fontSize = 17.sp, lineHeight = 1.29.em),
            section = text.copy(fontSize = 13.sp),
            sectionAllCaps = true,
            titleStyle = TitleStyle.Large,
            fieldStyle = FieldStyle.Inset,
            fieldRadius = 10.dp,
            fieldOutline = 0.dp,
            fieldFocusOutline = 0.dp,
            listStyle = ListStyle.GroupedInset,
            listRadius = 10.dp,
            listRowMinHeight = 44.dp,
            listRowPadding = 16.dp,
        ),
    )
}

private val labelRgb = Color(0xFF3C3C43)
private val labelRgbDark = Color(0xFFEBEBF5)

/** iOS system colours as an M3 scheme, so [withScheme] maps them to Kolt roles. */
internal fun appleScheme(tint: Color, dark: Boolean): ColorScheme = if (dark) darkColorScheme(
    primary = tint, onPrimary = Color.White,
    primaryContainer = tint.copy(alpha = 0.24f), onPrimaryContainer = tint,
    secondary = Color(0xFF98989D), onSecondary = Color.Black,
    secondaryContainer = Color(0xFF2C2C2E), onSecondaryContainer = Color.White,
    tertiary = Color(0xFFFF9F0A), onTertiary = Color.Black,
    tertiaryContainer = Color(0xFF2E2405), onTertiaryContainer = Color(0xFFFFD54F),
    error = Color(0xFFFF453A), onError = Color.White,
    errorContainer = Color(0xFFFF453A).copy(alpha = 0.24f), onErrorContainer = Color(0xFFFF453A),
    background = Color.Black, onBackground = Color.White,
    surface = Color.Black, onSurface = Color.White,
    surfaceVariant = Color(0xFF2C2C2E), onSurfaceVariant = labelRgbDark.copy(alpha = 0.6f),
    surfaceContainer = Color(0xFF1C1C1E), surfaceContainerHigh = Color(0xFF2C2C2E),
    surfaceContainerHighest = Color(0xFF3A3A3C),
    outline = labelRgbDark.copy(alpha = 0.3f), outlineVariant = Color(0xFF545458).copy(alpha = 0.6f),
) else lightColorScheme(
    primary = tint, onPrimary = Color.White,
    primaryContainer = tint.copy(alpha = 0.12f), onPrimaryContainer = tint,
    secondary = Color(0xFF8E8E93), onSecondary = Color.White,
    secondaryContainer = Color(0xFFE5E5EA), onSecondaryContainer = Color.Black,
    tertiary = Color(0xFFFF9500), onTertiary = Color.White,
    tertiaryContainer = Color(0xFFFFF8E1), onTertiaryContainer = Color(0xFF7A4100),
    error = Color(0xFFFF3B30), onError = Color.White,
    errorContainer = Color(0xFFFF3B30).copy(alpha = 0.12f), onErrorContainer = Color(0xFFFF3B30),
    background = Color(0xFFF2F2F7), onBackground = Color.Black,
    surface = Color(0xFFF2F2F7), onSurface = Color.Black,
    surfaceVariant = Color(0xFFE5E5EA), onSurfaceVariant = labelRgb.copy(alpha = 0.6f),
    surfaceContainer = Color.White, surfaceContainerHigh = Color(0xFFE5E5EA),
    surfaceContainerHighest = Color(0xFFD1D1D6),
    outline = labelRgb.copy(alpha = 0.3f), outlineVariant = labelRgb.copy(alpha = 0.29f),
)
