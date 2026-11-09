package kolt.composekmp.components.core

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import kolt.composekmp.theme.Kolt
import kolt.composekmp.theme.ListStyle

/**
 * A group of [KoltListRow]s. Under a `GroupedInset` theme (Apple) it is a rounded filled card;
 * under `Plain` (Google) it is a bare column. Same component, the theme picks the look.
 */
@Composable
fun KoltListGroup(modifier: Modifier = Modifier, content: @Composable ColumnScope.() -> Unit) {
    val grouped = Kolt.layout.listStyle == ListStyle.GroupedInset
    Column(
        modifier = modifier
            .fillMaxWidth()
            .then(
                if (grouped) {
                    Modifier
                        .clip(RoundedCornerShape(Kolt.layout.listRadius))
                        .background(Kolt.colors.secondarySurface)
                } else {
                    Modifier
                }
            ),
        content = content,
    )
}

/** One row: min height and horizontal padding come from [Kolt.layout]. */
@Composable
fun KoltListRow(modifier: Modifier = Modifier, content: @Composable RowScope.() -> Unit) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .defaultMinSize(minHeight = Kolt.layout.listRowMinHeight)
            .padding(horizontal = Kolt.layout.listRowPadding, vertical = Kolt.sizes.paddingSmallMedium),
        verticalAlignment = Alignment.CenterVertically,
        content = content,
    )
}
