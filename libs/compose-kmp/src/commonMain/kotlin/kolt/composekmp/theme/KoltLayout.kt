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

/** Screen-level layout and shape tokens: `Kolt.layout`. Platform themes override via `copy()`. */
data class KoltLayout(
    val screenSide: Dp = 16.dp,
    val topToTitle: Dp = 24.dp,
    val titleToSubtitle: Dp = 8.dp,
    val headerToContent: Dp = 24.dp,
    val ctaGap: Dp = 12.dp,
    /** Gap above the bottom edge. Callers add the nav-bar / safe-area inset on top. */
    val ctaBottom: Dp = 16.dp,
    val ctaPrimaryHeight: Dp = 48.dp,
    val ctaSecondaryHeight: Dp = 48.dp,
    val minTouchTarget: Dp = 48.dp,
    val screenTitle: TextStyle = TextStyle.Default,
    val screenSubtitle: TextStyle = TextStyle.Default,
    val body: TextStyle = TextStyle.Default,
    val section: TextStyle = TextStyle.Default,
    /** Compose has no text-transform: render [section] text upper-cased when true. */
    val sectionAllCaps: Boolean = false,
    val titleStyle: TitleStyle = TitleStyle.Standard,
    val fieldStyle: FieldStyle = FieldStyle.Outlined,
    val fieldRadius: Dp = 8.dp,
    val fieldOutline: Dp = 1.dp,
    val fieldFocusOutline: Dp = 1.dp,
    val listStyle: ListStyle = ListStyle.Plain,
    val listRadius: Dp = 0.dp,
    val listRowMinHeight: Dp = 48.dp,
    val listRowPadding: Dp = 16.dp,
)

val LocalLayout = staticCompositionLocalOf { KoltLayout() }
