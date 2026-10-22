// Moved to compose-kmp. This file is a backward-compatibility wrapper.
// Please migrate imports to: kolt.composekmp.components.core.messages.MessageDialog
@file:Suppress("unused")

package kolt.composeutils.components.messages

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.window.DialogProperties
import kolt.composekmp.components.core.messages.DialogButtonStyle
import kolt.composekmp.wrappers.UiText
import kolt.composekmp.theme.Kolt
import kolt.composekmp.theme.semiBold

/**
 * @deprecated Moved to compose-kmp. Use [kolt.composekmp.components.core.messages.MessageDialog].
 */
@Deprecated(
    message = "MessageDialog has been moved to compose-kmp. Use kolt.composekmp.components.core.messages.MessageDialog",
    replaceWith = ReplaceWith(
        "MessageDialog",
        "kolt.composekmp.components.core.messages.MessageDialog"
    ),
    level = DeprecationLevel.WARNING
)
@Composable
fun MessageDialog(
    modifier: Modifier = Modifier,
    title: UiText? = null,
    message: UiText? = null,
    titleStyle: TextStyle = Kolt.typography.body.large.semiBold,
    messageStyle: TextStyle = Kolt.typography.body.medium,
    titleAlign: TextAlign = TextAlign.Center,
    messageAlign: TextAlign = TextAlign.Center,
    messageContent: (@Composable (UiText) -> Unit)? = null,
    positiveText: UiText? = UiText.DynamicString("OK"),
    negativeText: UiText? = null,
    buttonStyle: DialogButtonStyle = DialogButtonStyle.primary(),
    listener: (Boolean) -> Unit = {},
    cancellable: Boolean = true,
    dialogBackground: Color = Kolt.colors.mainSurface,
    onDismissRequest: () -> Unit,
    dialogProperties: DialogProperties = DialogProperties(),
    customviewContent: (@Composable () -> Unit)? = null
) {
    kolt.composekmp.components.core.messages.MessageDialog(
        modifier = modifier,
        title = title,
        message = message,
        titleStyle = titleStyle,
        messageStyle = messageStyle,
        titleAlign = titleAlign,
        messageAlign = messageAlign,
        messageContent = messageContent,
        positiveText = positiveText,
        negativeText = negativeText,
        buttonStyle = buttonStyle,
        listener = listener,
        cancellable = cancellable,
        dialogBackground = dialogBackground,
        onDismissRequest = onDismissRequest,
        dialogProperties = dialogProperties,
        customviewContent = customviewContent,
    )
}
