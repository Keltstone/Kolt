package kolt.composekmp.components.core.dropdowns

import androidx.compose.foundation.clickable
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import kolt.composekmp.components.core.image.KoltIcon
import kolt.composekmp.wrappers.UiImage
import kolt.composekmp.wrappers.UiText


@Composable
fun IconDropDown(
    items: List<UiText>,
    icon: UiImage,
    modifier: Modifier = Modifier,
    onItemSelected: (index: Int) -> Unit
) {
    DropDownSpinner(
        items = items,
        onSelectedIndexChange = { onItemSelected(it) },
    ) { _, _, _, onClick ->

        KoltIcon(
            icon = icon,
                modifier = modifier.clickable { onClick() }
            )
    }
}