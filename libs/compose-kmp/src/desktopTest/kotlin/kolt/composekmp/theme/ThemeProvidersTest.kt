package kolt.composekmp.theme

import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.runComposeUiTest
import androidx.compose.ui.unit.dp
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
    fun googleThemeProvidesGoogleLayoutAndColors() = runComposeUiTest {
        var layout: KoltLayout? = null
        var primary: androidx.compose.ui.graphics.Color? = null
        var side: androidx.compose.ui.unit.Dp? = null
        setContent { GoogleTheme(isDarkTheme = false) { layout = Kolt.layout; primary = Kolt.colors.primary; side = Kolt.sizes.paddingMedium } }
        assertEquals(28.dp, layout?.screenSide)
        assertEquals(GoogleSchemes.light.primary, primary)
        assertEquals(16.dp, side)
    }

    @Test
    fun appleThemeProvidesAppleLayoutAndDarkTint() = runComposeUiTest {
        var layout: KoltLayout? = null
        var primary: androidx.compose.ui.graphics.Color? = null
        setContent { AppleTheme(isDarkTheme = true) { layout = Kolt.layout; primary = Kolt.colors.primary } }
        assertEquals(20.dp, layout?.screenSide)
        assertEquals(TitleStyle.Large, layout?.titleStyle)
        assertEquals(AppleBlueDark, primary)
    }
}
