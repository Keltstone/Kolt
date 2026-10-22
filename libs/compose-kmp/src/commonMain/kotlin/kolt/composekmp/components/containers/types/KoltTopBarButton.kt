package kolt.composekmp.components.containers.types

import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import kolt.composekmp.wrappers.UiColor
import kolt.composekmp.wrappers.UiImage

data class KoltTopBarButton(
    val icon: UiImage,
    val modifier: Modifier = Modifier,
    val tint: Color = Color.Gray,
    val onClick: () -> Unit,
)
