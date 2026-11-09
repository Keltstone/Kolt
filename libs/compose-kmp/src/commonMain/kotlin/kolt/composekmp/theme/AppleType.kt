package kolt.composekmp.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

/**
 * HIG text styles (iOS default size category) as Material roles, so Material components inside
 * [AppleTheme] get HIG sizes, line heights and SF tracking, not M3's. Sizes are HIG's own scale
 * (34/28/22/20/17/16/15/13/12/11), independent of the Kolt scale.
 */
internal fun createAppleRoles(steps: BaseTextStyles): Typography {
    val base = steps.baseTextStyle
    fun hig(size: Int, line: Int, tracking: Double, weight: FontWeight = FontWeight.Normal): TextStyle =
        base.copy(fontSize = size.sp, lineHeight = line.sp, letterSpacing = tracking.sp, fontWeight = weight)
    val largeTitle = hig(34, 41, 0.37)
    val title1 = hig(28, 34, 0.36)
    val title2 = hig(22, 28, 0.35)
    val title3 = hig(20, 25, 0.38)
    val headline = hig(17, 22, -0.43, FontWeight.SemiBold)
    val body = hig(17, 22, -0.43)
    val callout = hig(16, 21, -0.31)
    val subhead = hig(15, 20, -0.23)
    val footnote = hig(13, 18, -0.08)
    val caption1 = hig(12, 16, 0.0)
    val caption2 = hig(11, 13, 0.06)
    return Typography(
        displayLarge = largeTitle, displayMedium = largeTitle, displaySmall = title1,
        headlineLarge = title1, headlineMedium = title2, headlineSmall = title3,
        titleLarge = title3, titleMedium = headline, titleSmall = subhead.copy(fontWeight = FontWeight.SemiBold),
        bodyLarge = body, bodyMedium = subhead, bodySmall = footnote,
        labelLarge = callout.copy(fontWeight = FontWeight.Medium), labelMedium = caption1, labelSmall = caption2,
    )
}
