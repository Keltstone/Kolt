package kolt.locationpicker

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import kolt.location.CurrentLocationProvider
import kolt.location.PlatformLocationContext

@Composable
actual fun rememberCurrentLocationProvider(): CurrentLocationProvider =
    remember { CurrentLocationProvider(PlatformLocationContext()) }
