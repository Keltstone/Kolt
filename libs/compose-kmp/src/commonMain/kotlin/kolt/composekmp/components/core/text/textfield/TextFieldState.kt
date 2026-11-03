package kolt.composekmp.components.core.text.textfield

import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.listSaver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import kolt.composekmp.wrappers.UiText

data class ValidationRule(
    val test: (String) -> Boolean,  // true if invalid
    val message: UiText
)

class ValidatedTextFieldState(
    initialValue: String = "",
    private val rules: List<ValidationRule> = emptyList(),
    val maxLength: Int = Int.MAX_VALUE,
    val showCounter: Boolean = true
) {
    val textFieldState = TextFieldState(initialValue)

    // Tracks if the user has interacted with the field
    var isDirty by mutableStateOf(false)
        private set

    var error by mutableStateOf<UiText?>(null)
        private set

    val value: String get() = textFieldState.text.toString()

    // Internal logic to run rules
    private fun runValidation(): UiText? {
        return rules.firstOrNull { it.test(value) }?.message
    }

    // Call this when the user types (e.g., in onValueChange)
    fun onValueChange(newValue: String) {
        isDirty = true
        // Only update UI error if we are already in an error state
        // or let the UI handle the visibility
        error = runValidation()
    }

    fun validate(): Boolean {
        isDirty = true
        return runValidation().also {
            if (error != it) error = it
        } == null
    }

    // Logic-only check for the "Add" button
    fun isValid(): Boolean = runValidation() == null

    /** Replaces the text without marking the field dirty — used when restoring saved state. */
    fun restoreText(text: String) {
        textFieldState.edit { replace(0, length, text) }
    }

    fun clear() {
        textFieldState.edit { replace(0, length, "") }
        error = null
        isDirty = false
    }
}

fun List<ValidatedTextFieldState>.validateAll(): Boolean {
    return this.map { it.validate() }.all { it }
}

/**
 * [remember] for a [ValidatedTextFieldState] that also survives configuration changes and process
 * death: only the typed text is saved, the rules/limits come from [factory] again on restore.
 * [keys] behave like [remember]'s — a key change drops both the state and its saved text.
 */
@Composable
fun rememberSavedTextFieldState(
    vararg keys: Any?,
    factory: () -> ValidatedTextFieldState
): ValidatedTextFieldState = rememberSaveable(
    *keys,
    saver = listSaver<ValidatedTextFieldState, String>(
        save = { listOf(it.value) },
        restore = { factory().also { state -> state.restoreText(it[0]) } }
    ),
    init = factory
)
