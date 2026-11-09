package kolt.composekmp.components.core.buttons

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import kolt.composekmp.components.core.buttons.types.ButtonStyle
import kolt.composekmp.theme.Kolt
import kolt.composekmp.wrappers.UiImage
import kolt.composekmp.wrappers.UiText

/** Themed button; [leadingIcon] / [trailingIcon] are optional and independent. */
@Composable
fun KoltButton(
    text: UiText,
    modifier: Modifier = Modifier,
    textModifier: Modifier = Modifier,
    enabled: Boolean = true,
    buttonStyle: ButtonStyle = ButtonStyle.primary(),
    leadingIcon: UiImage? = null,
    trailingIcon: UiImage? = null,
    contentPadding: PaddingValues = PaddingValues(
        horizontal = Kolt.sizes.paddingMedium,
        vertical = Kolt.sizes.noPadding,
    ),
    onClick: () -> Unit
) {
    KoltIconTextButton(
        text = text,
        modifier = modifier,
        leadingIcon = leadingIcon,
        trailingIcon = trailingIcon,
        textModifier = textModifier,
        enabled = enabled,
        buttonStyle = buttonStyle,
        contentPadding = contentPadding,
        onClick = onClick
    )
}
