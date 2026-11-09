package kolt.composekmp.theme

import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.runComposeUiTest
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.test.Test
import kotlin.test.assertEquals

@OptIn(ExperimentalTestApi::class)
class ThemeProvidersTest {

    @Test
    fun defaultProviderKeepsNeutralLayout() = runComposeUiTest {
        var layout: KoltLayout? = null
        var primary: androidx.compose.ui.graphics.Color? = null
        setContent { CompositionBaseProvider(isDarkTheme = false) { layout = Kolt.layout; primary = Kolt.colors.primary } }
        assertEquals(KoltLayout(), layout)
        assertEquals(getLightColors().primary, primary)
    }

    @Test
    fun koltThemeAppliesSchemeAndCustomize() = runComposeUiTest {
        var layout: KoltLayout? = null
        var primary: androidx.compose.ui.graphics.Color? = null
        var side: androidx.compose.ui.unit.Dp? = null
        setContent {
            KoltTheme(
                isDarkTheme = false, colorScheme = M3Schemes.blue(false),
                customize = { it.copy(layout = it.layout.copy(screenSide = 28.dp)) },
            ) { layout = Kolt.layout; primary = Kolt.colors.primary; side = Kolt.sizes.paddingMedium }
        }
        assertEquals(28.dp, layout?.screenSide)
        assertEquals(56.dp, layout?.ctaPrimaryHeight)
        assertEquals(28.sp, layout?.screenTitle?.fontSize)
        assertEquals(M3Schemes.blue(false).primary, primary)
        assertEquals(16.dp, side)
    }

    @Test
    fun koltThemeWithoutArgsKeepsKoltPalette() = runComposeUiTest {
        var primary: androidx.compose.ui.graphics.Color? = null
        setContent { KoltTheme(isDarkTheme = false) { primary = Kolt.colors.primary } }
        assertEquals(getLightColors().primary, primary)
    }

    @Test
    fun appleThemeProvidesAppleLayoutAndDarkTint() = runComposeUiTest {
        var layout: KoltLayout? = null
        var primary: androidx.compose.ui.graphics.Color? = null
        setContent { AppleTheme(isDarkTheme = true) { layout = Kolt.layout; primary = Kolt.colors.primary } }
        assertEquals(16.dp, layout?.screenSide)
        assertEquals(TitleStyle.Large, layout?.titleStyle)
        assertEquals(AppleBlueDark, primary)
    }
}
