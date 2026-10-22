package kolt.composeutils.components.core.dropdowns

import androidx.compose.foundation.clickable
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import kolt.composekmp.components.core.text.KoltText
import kolt.composekmp.wrappers.UiText


@Composable
fun TextDropDown(
    items: List<UiText>,
    onItemSelected: (index: Int) -> Unit
) {
    DropDownSpinner(
        items = items,
        onSelectedIndexChange = { onItemSelected(it) },
    ) { index, item, _, onClick ->

        KoltText(
            text = item,
            modifier = Modifier.clickable { onClick() }
        )
    }
}