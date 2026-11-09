package kolt.composekmp.components.core.text.textfield

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.input.InputTransformation
import androidx.compose.foundation.text.input.OutputTransformation
import androidx.compose.foundation.text.input.TextFieldLineLimits
import androidx.compose.foundation.text.input.maxLength
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.semantics.error
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kolt.composekmp.components.core.text.KoltText
import kolt.composekmp.theme.FieldStyle
import kolt.composekmp.theme.Kolt
import kolt.composekmp.theme.medium
import kolt.composekmp.theme.normal
import kolt.composekmp.theme.semiBold
import kolt.composekmp.wrappers.FieldError
import kolt.composekmp.wrappers.UiText
import kolt.composekmp.wrappers.asString
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

@Composable
fun KoltValidatedTextField(
    state: ValidatedTextFieldState,
    modifier: Modifier = Modifier,
    label: UiText? = null,
    placeholder: UiText? = null,
    helperText: UiText? = null,
    enabled: Boolean = true,
    readOnly: Boolean = false,
    textStyle: TextStyle = Kolt.typography.body.mediumMid.semiBold,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    onKeyboardAction: ((defaultAction: () -> Unit) -> Unit)? = null,
    lineLimits: TextFieldLineLimits = TextFieldLineLimits.SingleLine,
    inputTransformation: InputTransformation? = if (state.maxLength < Int.MAX_VALUE) {
        InputTransformation.maxLength(state.maxLength)
    } else null,
    colors: TextFieldColors = KoltTextFieldDefaults.defaultColor(),
    labelStyle: TextFieldItemStyle = KoltTextFieldDefaults.defaultLabel(),
    placeHolderStyle: TextFieldItemStyle = KoltTextFieldDefaults.defaultItem(
        unselectedColor = Kolt.colors.subText.copy(alpha = 0.8f),
        unselectedTextStyle = Kolt.typography.body.medium.normal
    ),
    helperStyle: TextFieldItemStyle = KoltTextFieldDefaults.defaultHelper(),
    errorConfig: FieldError = FieldError.Default,
    outputTransformation: OutputTransformation? = null,
    onValueChange: ((String) -> Unit)? = null,
    leadingIcon: (@Composable () -> Unit)? = null,
    trailingIcon: (@Composable () -> Unit)? = null,
    /**
     * Compact "floating" mode: while empty and unfocused only the [label] shows, as a hint inside
     * the field; on focus or once it has a value the label animates in above it, and the
     * [placeholder] takes over the hint slot. The length counter sits inline at the field's right
     * end instead of in the footer row. Off by default so existing screens are unaffected.
     */
    floatingLabel: Boolean = false,
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isFocused by interactionSource.collectIsFocusedAsState()
    val isError = state.error != null
    val errorDescription = state.error?.asString() ?: ""

    // Observe value changes and forward to external callback + validation
    if (onValueChange != null) {
        LaunchedEffect(state.textFieldState) {
            snapshotFlow { state.value }
                .drop(1)
                .onEach { text ->
                    state.onValueChange(text)
                    onValueChange(text)
                }
                .launchIn(this)
        }
    }

    Column(modifier = modifier) {
        // Label Section
        label?.let {
            val labelVisible = !floatingLabel || isFocused || state.value.isNotEmpty()
            AnimatedVisibility(
                visible = labelVisible,
                enter = expandVertically() + fadeIn(),
                exit = shrinkVertically() + fadeOut()
            ) {
                KoltText(
                    text = it,
                    style = labelStyle.unselectedTextStyle,
                    color = labelStyle.unselectedColor,
                    modifier = labelStyle.modifier
                )
            }
        }

        // The Modern BasicTextField (v2)
        BasicTextField(
            state = state.textFieldState,
            modifier = Modifier
                .fillMaxWidth()
                .background(colors.background, colors.borderShape)
                .then(
                    // A 0.dp BorderStroke would draw a hairline, so an inset field skips it.
                    when {
                        isError -> BorderStroke(1.dp, colors.errorBorderColor)
                        isFocused -> colors.focusedBorder
                        else -> colors.border
                    }.let { if (it.width > 0.dp) Modifier.border(it, colors.borderShape) else Modifier }
                )
                .semantics {
                    if (isError) error(errorDescription)
                },
            enabled = enabled,
            readOnly = readOnly,
            textStyle = textStyle.copy(color = colors.contentColor),
            keyboardOptions = keyboardOptions,
            onKeyboardAction = onKeyboardAction,
            lineLimits = lineLimits,
            interactionSource = interactionSource,
            cursorBrush = SolidColor(colors.cursorColor),
            inputTransformation = inputTransformation,
            outputTransformation = outputTransformation,
            decorator = { innerTextField ->
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(Kolt.sizes.paddingMedium)
                ) {
                    leadingIcon?.let { it() }

                    Box(modifier = Modifier.weight(1f)) {
                        // Floating mode: the label is the resting hint; the placeholder only appears
                        // once focused (the label has moved above by then).
                        val hint = if (floatingLabel && !isFocused) label ?: placeholder else placeholder
                        if (state.value.isEmpty() && hint != null) {
                            KoltText(
                                text = hint,
                                style = placeHolderStyle.unselectedTextStyle,
                                color = placeHolderStyle.unselectedColor,
                                modifier = placeHolderStyle.modifier
                            )
                        }
                        innerTextField()
                    }

                    if (floatingLabel && showCounter(state)) {
                        Counter(state, colors, Modifier.padding(start = Kolt.sizes.paddingXSmall))
                    }

                    trailingIcon?.let { it() }
                }
            }
        )

        // Helper and Counter Row
        val footerText = state.error ?: helperText
        // Floating mode drops the footer row entirely when there is nothing to show, so stacked
        // fields sit close together.
        if (!floatingLabel || footerText != null) Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 4.dp, vertical = 4.dp)
        ) {

            Box(modifier = Modifier.weight(1f)) {
                this@Row.AnimatedVisibility(
                    visible = footerText != null,
                    enter = expandVertically(),
                    exit = shrinkVertically()
                ) {
                    footerText?.let {
                        KoltText(
                            text = it,
                            style = if (isError) Kolt.typography.body.small else helperStyle.unselectedTextStyle,
                            color = if (isError) (errorConfig.color ?: colors.errorColor) else helperStyle.unselectedColor,
                            modifier = helperStyle.modifier
                        )
                    }
                }
            }

            if (!floatingLabel && showCounter(state)) Counter(state, colors)
        }
    }
}

private fun showCounter(state: ValidatedTextFieldState) =
    state.showCounter && state.maxLength < Int.MAX_VALUE

@Composable
private fun Counter(state: ValidatedTextFieldState, colors: TextFieldColors, modifier: Modifier = Modifier) {
    KoltText(
        text = UiText.DynamicString("${state.value.length} / ${state.maxLength}"),
        style = Kolt.typography.body.small,
        color = if (state.value.length >= state.maxLength) colors.errorColor else Kolt.colors.subText,
        modifier = modifier
    )
}

data class TextFieldColors(
    val background: Color,
    val contentColor: Color,
    val border: BorderStroke,
    val focusedBorder: BorderStroke,
    val errorBorderColor: Color,
    val errorColor: Color,
    val cursorColor: Color,
    val borderShape: Shape
)

data class TextFieldItemStyle(
    val unselectedColor: Color,
    val unselectedTextStyle: TextStyle,
    val errorColor: Color,
    val modifier: Modifier = Modifier,
)

object KoltTextFieldDefaults {
    @Composable
    fun defaultItem(
        unselectedColor: Color = Kolt.colors.onBackground.copy(alpha = 0.7f),
        unselectedTextStyle: TextStyle = Kolt.typography.body.mediumMid.medium,
        errorColor: Color = Kolt.colors.error,
        modifier: Modifier = Modifier
    ): TextFieldItemStyle {
        return TextFieldItemStyle(
            unselectedColor = unselectedColor,
            unselectedTextStyle = unselectedTextStyle,
            errorColor = errorColor,
            modifier = modifier,
        )
    }

    @Composable
    fun defaultColor(
        background: Color = if (Kolt.layout.fieldStyle == FieldStyle.Inset) {
            Kolt.colors.secondarySurface
        } else {
            Kolt.colors.mainSurface
        },
        contentColor: Color = Kolt.colors.onMainSurface,
        borderWidth: Dp = Kolt.layout.fieldOutline,
        borderColor: Color = Kolt.colors.onMainSurface.copy(alpha = 0.1f),
        focusedBorderWidth: Dp = Kolt.layout.fieldFocusOutline,
        focusedBorderColor: Color = Kolt.colors.primary,
        errorBorderColor: Color = Kolt.colors.error,
        errorColor: Color = Kolt.colors.error,
        cursorColor: Color = Kolt.colors.primary,
        borderShape: Shape = RoundedCornerShape(Kolt.layout.fieldRadius)
    ): TextFieldColors {
        return TextFieldColors(
            background = background,
            contentColor = contentColor,
            border = BorderStroke(width = borderWidth, color = borderColor),
            focusedBorder = BorderStroke(width = focusedBorderWidth, color = focusedBorderColor),
            errorBorderColor = errorBorderColor,
            cursorColor = cursorColor,
            errorColor = errorColor,
            borderShape = borderShape
        )
    }

    @Composable
    fun defaultLabel(
        color: Color = Kolt.colors.subText,
        textStyle: TextStyle = Kolt.typography.body.smallMedium,
        modifier: Modifier = Modifier.padding(
            start = Kolt.sizes.paddingXSmall,
            bottom = Kolt.sizes.paddingXSmall
        )
    ): TextFieldItemStyle {
        return TextFieldItemStyle(
            unselectedColor = color,
            unselectedTextStyle = textStyle,
            errorColor = color,
            modifier = modifier,
        )
    }

    @Composable
    fun defaultHelper(
        color: Color = Kolt.colors.subText,
        textStyle: TextStyle = Kolt.typography.body.small,
        modifier: Modifier = Modifier.padding(
            start = Kolt.sizes.paddingXSmall,
            bottom = Kolt.sizes.paddingXSmall
        )
    ): TextFieldItemStyle {
        return TextFieldItemStyle(
            unselectedColor = color,
            unselectedTextStyle = textStyle,
            errorColor = color,
            modifier = modifier,
        )
    }
}
