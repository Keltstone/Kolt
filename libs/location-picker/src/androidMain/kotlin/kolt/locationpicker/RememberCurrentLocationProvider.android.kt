package kolt.locationpicker

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import kolt.location.CurrentLocationProvider
import kolt.location.PlatformLocationContext

@Composable
actual fun rememberCurrentLocationProvider(): CurrentLocationProvider {
    val context = LocalContext.current
    return remember(context) { CurrentLocationProvider(PlatformLocationContext(context)) }
}
