package kolt.composekmp.theme

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotEquals
import kotlin.test.assertSame
import kotlin.test.assertTrue

class KoltThemeSpecTest {
    private val base = KoltThemeSpec(getLightColors(), defaultSizes(), createBaseTypography(defaultSizes(), null), KoltLayout())

    @Test
    fun defaultThemeValuesUnchanged() {
        val s = defaultSizes()
        assertEquals(16.dp, s.paddingMedium); assertEquals(8.dp, s.cornerRadiusNormal)
        assertEquals(14.sp, s.fontSizeMedium); assertEquals(56.dp, s.appBarSize)
        val c = getLightColors()
        assertEquals(Color(0xFFC20F2F), c.topAppBar); assertEquals(Color(0xFFBDBDBD), c.dividerColor)
        assertEquals(Color(0xFFBB86FC), getDarkColors().primary)
        assertEquals(KoltLayout(), KoltLayout())
    }

    @Test
    fun googleOverridesOnlyColorsAndLayout() {
        val g = base.google(GoogleSchemes.light)
        assertEquals(base.sizes, g.sizes); assertSame(base.typography, g.typography)
        assertEquals(28.dp, g.layout.screenSide); assertEquals(56.dp, g.layout.ctaPrimaryHeight)
        assertEquals(30.sp, g.layout.screenTitle.fontSize); assertTrue(g.layout.ctaPrimaryHeight >= g.layout.minTouchTarget)
        assertEquals(Color(0xFF0B57D0), g.colors.primary)
        assertEquals(base.colors.accentedGoldText, g.colors.accentedGoldText)
    }

    @Test
    fun googlePalettesFillEveryRole() {
        for (scheme in listOf(GoogleSchemes.light, GoogleSchemes.dark)) {
            val c = getLightColors().withScheme(scheme)
            assertNotEquals(getLightColors(), c)
            assertFalse(c.allRoles().any { it == Color.Unspecified })
        }
    }

    @Test
    fun appleOverridesOnlyColorsAndLayout() {
        val a = base.apple(appleScheme(AppleBlueLight, false))
        assertEquals(base.sizes, a.sizes); assertSame(base.typography, a.typography)
        assertEquals(20.dp, a.layout.screenSide); assertEquals(50.dp, a.layout.ctaPrimaryHeight)
        assertEquals(34.sp, a.layout.screenTitle.fontSize); assertEquals(17.sp, a.layout.body.fontSize)
        assertEquals(10.dp, a.layout.listRadius); assertEquals(44.dp, a.layout.listRowMinHeight)
        assertEquals(TitleStyle.Large, a.layout.titleStyle); assertEquals(FieldStyle.Inset, a.layout.fieldStyle)
        assertEquals(AppleBlueLight, a.colors.primary)
    }

    @Test
    fun applePalettesFillEveryRole() {
        for (dark in listOf(false, true)) {
            val c = getLightColors().withScheme(appleScheme(AppleBlueLight, dark))
            assertFalse(c.allRoles().any { it == Color.Unspecified })
        }
    }

    @Test
    fun successOverridesAndSeedScheme() {
        val g = base.google(GoogleSchemes.light, false).colors
        assertEquals(Color(0xFF146C2E), g.success)
        assertEquals(Color(0xFFC8E6C9), g.successContainer)
        assertEquals(getLightColors().primary, getLightColors().success) // no override: derives as before
        for (dark in listOf(false, true)) {
            val sc = seedColorScheme(Color(0xFF0B57D0), dark)
            assertFalse(getLightColors().withScheme(sc).allRoles().any { it == Color.Unspecified })
            assertNotEquals(sc.primary, sc.onPrimary)
        }
    }

    private fun BaseColors.allRoles() = listOf(
        primary, onPrimary, secondary, onSecondary, tertiary, onTertiary, iconTint, disabledIconTint,
        drawerItem, scrimColor, navigationBarColor, mainSurface, onMainSurface, secondarySurface,
        onSecondarySurface, background, onBackground, primaryCardContainer, onPrimaryCardContainer,
        onPrimaryCardContainerDimmed, secondaryCardContainer, onSecondaryCardContainer,
        tertiaryCardContainer, onTertiaryCardContainer, greyCardContainer, onGreyCardContainer, error,
        onError, errorCardContainer, onErrorCardContainer, accentedBlueTitle, accentedBlackTitle,
        accentedRedTitle, accentedBlueText, accentedRedText, accentedGoldText, topAppBar, onTopAppBar,
        disabledText, subText, hintText, dividerColor,
    )
}
