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
    private val typography = createBaseTypography(defaultSizes(), null)
    private val base = KoltThemeSpec(getLightColors(), defaultSizes(), typography, KoltLayout())

    @Test
    fun defaultThemeValuesUnchanged() {
        val s = defaultSizes()
        assertEquals(16.dp, s.paddingMedium); assertEquals(8.dp, s.cornerRadiusNormal)
        assertEquals(14.sp, s.fontSizeMedium); assertEquals(56.dp, s.appBarSize)
        assertEquals(28.dp, s.cornerRadiusXXLargeMid)
        val c = getLightColors()
        assertEquals(Color(0xFFC20F2F), c.topAppBar); assertEquals(Color(0xFFBDBDBD), c.dividerColor)
        assertEquals(Color(0xFFBB86FC), getDarkColors().primary)
        assertEquals(c.primary, c.success) // no override: derives as before
    }

    @Test
    fun layoutDefaultsAreM3Compact() {
        val l = KoltLayout()
        assertEquals(16.dp, l.screenSide); assertEquals(56.dp, l.ctaPrimaryHeight)
        assertEquals(4.dp, l.fieldRadius); assertEquals(2.dp, l.fieldFocusOutline); assertEquals(56.dp, l.listRowMinHeight)
    }

    @Test
    fun m3PresetKeepsSizesAndTypography() {
        val scheme = M3Schemes.blue(false)
        val spec = KoltPreset(scheme) { b, roles -> b.copy(colors = b.colors.withScheme(scheme).withSuccess(false), layout = b.layout.withText(roles.headlineMedium, roles.bodyMedium, roles.bodyLarge, roles.labelMedium)) }
            .let { it.spec(base, it.roles(base.typography)) }
        assertEquals(base.sizes, spec.sizes); assertSame(base.typography, spec.typography)
        assertEquals(Color(0xFF0B57D0), spec.colors.primary); assertEquals(Color(0xFF146C2E), spec.colors.success)
        assertEquals(28.sp, spec.layout.screenTitle.fontSize); assertEquals(16.sp, spec.layout.body.fontSize)
        assertEquals(base.colors.accentedGoldText, spec.colors.accentedGoldText)
    }

    @Test
    fun applePresetIsHigNotMaterial() {
        val scheme = appleScheme(AppleBlueLight, false)
        val p = applePreset(scheme, false)
        val a = p.spec(base, p.roles(base.typography))
        assertEquals(base.sizes, a.sizes); assertSame(base.typography, a.typography)
        assertEquals(16.dp, a.layout.screenSide); assertEquals(44.dp, a.layout.minTouchTarget)
        assertEquals(34.sp, a.layout.screenTitle.fontSize); assertEquals(41.sp, a.layout.screenTitle.lineHeight)
        assertEquals(androidx.compose.ui.text.font.FontWeight.Normal, a.layout.screenTitle.fontWeight)
        assertEquals(17.sp, a.layout.body.fontSize); assertEquals(13.sp, a.layout.section.fontSize)
        assertEquals(TitleStyle.Large, a.layout.titleStyle); assertEquals(FieldStyle.Inset, a.layout.fieldStyle)
        assertFalse(p.ripple); assertTrue(p.shapes != null)
        assertTrue(contrast(a.colors.success, Color(0xFFF2F2F7)) >= 4.5f)
    }

    @Test
    fun customizeOverridesOnlyWhatDiffers() {
        val custom = { s: KoltThemeSpec -> s.copy(layout = s.layout.copy(screenSide = 28.dp)) }
        val out = custom(base)
        assertEquals(28.dp, out.layout.screenSide); assertEquals(base.layout.ctaGap, out.layout.ctaGap)
        assertSame(base.sizes, out.sizes)
    }

    @Test
    fun paletteFillsEveryRole() {
        val schemes = listOf(M3Schemes.blue(false), M3Schemes.blue(true), appleScheme(AppleBlueLight, false), appleScheme(AppleBlueDark, true))
        for (scheme in schemes) {
            val c = getLightColors().withScheme(scheme)
            assertNotEquals(getLightColors(), c)
            assertFalse(c.allRoles().any { it == Color.Unspecified })
        }
    }

    @Test
    fun seedSchemesPassContrastForAnySeed() {
        val seeds = listOf(0xFF0B57D0, 0xFFC20F2F, 0xFF2E7D32, 0xFFF9A825, 0xFF00897B, 0xFF6750A4, 0xFFFFFF00, 0xFF00FFFF)
        for (v in seeds) for (dark in listOf(false, true)) {
            val sc = seedColorScheme(Color(v), dark)
            assertTrue(contrast(sc.onPrimary, sc.primary) >= 4.5f, "onPrimary $v dark=$dark")
            assertTrue(contrast(sc.onSecondary, sc.secondary) >= 4.5f, "onSecondary $v dark=$dark")
            assertTrue(contrast(sc.onTertiary, sc.tertiary) >= 4.5f, "onTertiary $v dark=$dark")
            assertTrue(contrast(sc.onSurface, sc.surface) >= 4.5f)
            assertTrue(contrast(sc.onSurfaceVariant, sc.surface) >= 4.5f)
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
