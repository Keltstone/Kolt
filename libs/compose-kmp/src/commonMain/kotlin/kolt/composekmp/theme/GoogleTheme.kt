package kolt.composekmp.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp

/** Material You scheme from the device wallpaper (Android 12+); null elsewhere. */
@Composable
internal expect fun wallpaperColorScheme(isDark: Boolean): ColorScheme?

/**
 * Material 3 theme. Palette precedence: [colorScheme] > [seed] (approximate HSL scheme, see [seedColorScheme])
 * > wallpaper (when [useWallpaper], Android 12+) > the built-in Google-blue scheme.
 */
@Composable
fun GoogleTheme(
    isDarkTheme: Boolean? = null,
    colorScheme: ColorScheme? = null,
    seed: Color? = null,
    useWallpaper: Boolean = false,
    font: AppFont = AppFont.Roboto,
    content: @Composable () -> Unit,
) {
    val dark = isDarkTheme ?: isSystemInDarkTheme()
    val scheme = colorScheme
        ?: seed?.let { seedColorScheme(it, dark) }
        ?: (if (useWallpaper) wallpaperColorScheme(dark) else null)
        ?: if (dark) GoogleSchemes.dark else GoogleSchemes.light
    KoltThemeProvider(dark, font, scheme, { it.google(scheme, dark) }, content)
}

/** Google preset: only what differs from the shared base. */
internal fun KoltThemeSpec.google(scheme: ColorScheme, dark: Boolean = false): KoltThemeSpec {
    val text = typography.baseTextStyle
    return copy(
        colors = colors.withScheme(scheme).withSuccess(
            if (dark) Color(0xFF81C784) else Color(0xFF146C2E),
            if (dark) Color(0xFF1B3822) else Color(0xFFC8E6C9),
            if (dark) Color(0xFFC8E6C9) else Color(0xFF0A3818),
        ),
        layout = layout.copy(
            screenSide = 28.dp,
            topToTitle = 24.dp,
            titleToSubtitle = 12.dp,
            headerToContent = 32.dp,
            ctaGap = 16.dp,
            ctaBottom = 24.dp,
            ctaPrimaryHeight = 56.dp,
            ctaSecondaryHeight = 52.dp,
            screenTitle = text.copy(
                fontSize = 30.sp, fontWeight = FontWeight.Bold, lineHeight = 1.2.em, letterSpacing = (-0.5).sp,
            ),
            screenSubtitle = text.copy(fontSize = 15.sp, lineHeight = 1.5.em),
            body = text.copy(fontSize = 15.sp, lineHeight = 1.5.em),
            section = text.copy(fontSize = 12.sp, fontWeight = FontWeight.SemiBold, letterSpacing = 0.5.sp),
            sectionAllCaps = true,
            titleStyle = TitleStyle.Standard,
            fieldStyle = FieldStyle.Outlined,
            fieldRadius = 12.dp,
            fieldOutline = 1.dp,
            fieldFocusOutline = 2.dp,
            listStyle = ListStyle.Plain,
        ),
    )
}

/** M3 roles → Kolt roles. Roles M3 has no equivalent for (gold accent) keep the base value. */
internal fun BaseColors.withScheme(s: ColorScheme) = copy(
    primary = s.primary, onPrimary = s.onPrimary,
    secondary = s.secondary, onSecondary = s.onSecondary,
    tertiary = s.tertiary, onTertiary = s.onTertiary,
    iconTint = s.onSurfaceVariant, disabledIconTint = s.onSurface.copy(alpha = 0.38f),
    drawerItem = s.onSurfaceVariant, scrimColor = s.scrim, navigationBarColor = s.surfaceContainer,
    mainSurface = s.surface, onMainSurface = s.onSurface,
    secondarySurface = s.surfaceContainer, onSecondarySurface = s.onSurface,
    background = s.background, onBackground = s.onBackground,
    primaryCardContainer = s.primaryContainer, onPrimaryCardContainer = s.onPrimaryContainer,
    onPrimaryCardContainerDimmed = s.onPrimaryContainer.copy(alpha = 0.7f),
    secondaryCardContainer = s.secondaryContainer, onSecondaryCardContainer = s.onSecondaryContainer,
    tertiaryCardContainer = s.tertiaryContainer, onTertiaryCardContainer = s.onTertiaryContainer,
    greyCardContainer = s.surfaceContainerHigh, onGreyCardContainer = s.onSurface,
    error = s.error, onError = s.onError,
    errorCardContainer = s.errorContainer, onErrorCardContainer = s.onErrorContainer,
    accentedBlueTitle = s.primary, accentedBlackTitle = s.onSurface, accentedRedTitle = s.error,
    accentedBlueText = s.primary, accentedRedText = s.error,
    topAppBar = s.surface, onTopAppBar = s.onSurface,
    disabledText = s.onSurface.copy(alpha = 0.38f), subText = s.onSurfaceVariant,
    hintText = s.onSurfaceVariant, dividerColor = s.outlineVariant,
)

/** Sets the success trio, which M3 has no role for. */
internal fun BaseColors.withSuccess(success: Color, container: Color, onContainer: Color) = copy(
    successOverride = success, successContainerOverride = container, onSuccessContainerOverride = onContainer,
)

/** Google-blue M3 schemes (values from the onboarding prototype; secondary/tertiary tonal fill-ins). */
internal object GoogleSchemes {
    val light: ColorScheme = lightColorScheme(
        primary = Color(0xFF0B57D0), onPrimary = Color(0xFFFFFFFF),
        primaryContainer = Color(0xFFD3E3FD), onPrimaryContainer = Color(0xFF041E49),
        secondary = Color(0xFF565F71), onSecondary = Color(0xFFFFFFFF),
        secondaryContainer = Color(0xFFDAE2F9), onSecondaryContainer = Color(0xFF131C2B),
        tertiary = Color(0xFF705575), onTertiary = Color(0xFFFFFFFF),
        tertiaryContainer = Color(0xFFFAD8FD), onTertiaryContainer = Color(0xFF28132E),
        error = Color(0xFFBA1A1A), onError = Color(0xFFFFFFFF),
        errorContainer = Color(0xFFFFDAD6), onErrorContainer = Color(0xFF410002),
        background = Color(0xFFF8F9FD), onBackground = Color(0xFF1A1C22),
        surface = Color(0xFFF8F9FD), onSurface = Color(0xFF1A1C22),
        surfaceVariant = Color(0xFFE0E2EC), onSurfaceVariant = Color(0xFF444750),
        surfaceContainer = Color(0xFFEEF0F6), surfaceContainerHigh = Color(0xFFE4E7F0),
        surfaceContainerHighest = Color(0xFFDDE0EC),
        outline = Color(0xFF74777F), outlineVariant = Color(0xFFC4C6D0),
    )
    val dark: ColorScheme = darkColorScheme(
        primary = Color(0xFF8AB4F8), onPrimary = Color(0xFF002D6C),
        primaryContainer = Color(0xFF004494), onPrimaryContainer = Color(0xFFD3E3FD),
        secondary = Color(0xFFBEC6DC), onSecondary = Color(0xFF283141),
        secondaryContainer = Color(0xFF3E4759), onSecondaryContainer = Color(0xFFDAE2F9),
        tertiary = Color(0xFFDDBCE0), onTertiary = Color(0xFF3F2844),
        tertiaryContainer = Color(0xFF573E5C), onTertiaryContainer = Color(0xFFFAD8FD),
        error = Color(0xFFFFB4AB), onError = Color(0xFF690005),
        errorContainer = Color(0xFF93000A), onErrorContainer = Color(0xFFFFDAD6),
        background = Color(0xFF111318), onBackground = Color(0xFFE2E2E9),
        surface = Color(0xFF111318), onSurface = Color(0xFFE2E2E9),
        surfaceVariant = Color(0xFF44474F), onSurfaceVariant = Color(0xFFC4C6D0),
        surfaceContainer = Color(0xFF1D2026), surfaceContainerHigh = Color(0xFF282A31),
        surfaceContainerHighest = Color(0xFF33353D),
        outline = Color(0xFF8E9099), outlineVariant = Color(0xFF44474F),
    )
}
