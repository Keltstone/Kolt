package kolt.composekmp.components.core.buttons

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import kolt.composekmp.components.core.image.KoltImage
import kolt.composekmp.wrappers.UiImage
import kolt.composekmp.theme.Kolt


@Composable
fun CircularButton(
    icon: UiImage,
    modifier: Modifier = Modifier,
    iconModifier: Modifier = Modifier.padding(Kolt.sizes.paddingXSmall),
    buttonColor: Color = Kolt.colors.primary,
    onClick: () -> Unit
) {

    Box(
        modifier = modifier
            .size(Kolt.sizes.floatingButtonSize)
            .background(buttonColor, shape = RoundedCornerShape(50)),
        contentAlignment = Alignment.Center
    ) {
        KoltImage(
            image = icon,
            modifier = iconModifier.clickable {
                onClick()
            },
        )
    }
}