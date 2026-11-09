package kolt.composekmp.theme

import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/** How a text field is drawn. One field component reads this; there is no per-platform field. */
enum class FieldStyle { Outlined, Inset }

/** How a screen title is drawn. One scaffold reads this. */
enum class TitleStyle { Standard, Large }

/** How grouped content (settings rows) is drawn. One list component reads this. */
enum class ListStyle { Plain, GroupedInset }

/**
 * Screen-level layout and shape tokens: `Kolt.layout`. Defaults are Material 3 compact
 * (16dp margins, 56dp CTAs/rows, outlined 4dp fields). Other themes override via `copy()`.
 * The four text styles are filled from the typography by [KoltTheme] / [AppleTheme];
 * under a bare [CompositionBaseProvider] they stay `TextStyle.Default`.
 */
data class KoltLayout(
    val screenSide: Dp = 16.dp,
    val topToTitle: Dp = 24.dp,
    val titleToSubtitle: Dp = 8.dp,
    val headerToContent: Dp = 24.dp,
    val ctaGap: Dp = 16.dp,
    /** Gap above the bottom edge. Callers add the nav-bar / safe-area inset on top. */
    val ctaBottom: Dp = 16.dp,
    val ctaPrimaryHeight: Dp = 56.dp,
    val ctaSecondaryHeight: Dp = 56.dp,
    /** M3 / Android 48dp; HIG 44pt. */
    val minTouchTarget: Dp = 48.dp,
    val screenTitle: TextStyle = TextStyle.Default,
    val screenSubtitle: TextStyle = TextStyle.Default,
    val body: TextStyle = TextStyle.Default,
    val section: TextStyle = TextStyle.Default,
    /** Compose has no text-transform: render [section] text upper-cased when true. */
    val sectionAllCaps: Boolean = false,
    val titleStyle: TitleStyle = TitleStyle.Standard,
    val fieldStyle: FieldStyle = FieldStyle.Outlined,
    val fieldRadius: Dp = 4.dp,
    val fieldOutline: Dp = 1.dp,
    val fieldFocusOutline: Dp = 2.dp,
    val listStyle: ListStyle = ListStyle.Plain,
    val listRadius: Dp = 0.dp,
    val listRowMinHeight: Dp = 56.dp,
    val listRowPadding: Dp = 16.dp,
)

/** Fills the four text styles in one call (themes pass their own type roles). */
fun KoltLayout.withText(title: TextStyle, subtitle: TextStyle, body: TextStyle, section: TextStyle) =
    copy(screenTitle = title, screenSubtitle = subtitle, body = body, section = section)

val LocalLayout = staticCompositionLocalOf { KoltLayout() }
