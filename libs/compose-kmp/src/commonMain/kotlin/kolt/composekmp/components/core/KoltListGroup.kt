package kolt.composekmp.components.core

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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

/** Collects the rows of a [KoltListGroup]. */
class KoltListScope internal constructor() {
    internal val rows = mutableListOf<@Composable RowScope.() -> Unit>()

    /** One row. Min height and horizontal padding come from [Kolt.layout]. */
    fun row(content: @Composable RowScope.() -> Unit) {
        rows += content
    }
}

/**
 * A group of rows. Under a `GroupedInset` theme (Apple) it is a rounded filled card with inset
 * separators; under `Plain` (Material) it is a bare column. Same component, the theme picks the look.
 */
@Composable
fun KoltListGroup(modifier: Modifier = Modifier, content: KoltListScope.() -> Unit) {
    val grouped = Kolt.layout.listStyle == ListStyle.GroupedInset
    val rows = KoltListScope().apply(content).rows
    Column(
        modifier = modifier
            .fillMaxWidth()
            .then(
                if (grouped) {
                    Modifier.clip(RoundedCornerShape(Kolt.layout.listRadius)).background(Kolt.colors.secondarySurface)
                } else {
                    Modifier
                }
            ),
    ) {
        rows.forEachIndexed { index, row ->
            if (grouped && index > 0) {
                Box(
                    Modifier
                        .padding(start = Kolt.layout.listRowPadding)
                        .fillMaxWidth()
                        .defaultMinSize(minHeight = Kolt.sizes.paddingTiny)
                        .background(Kolt.colors.dividerColor)
                )
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .defaultMinSize(minHeight = Kolt.layout.listRowMinHeight)
                    .padding(horizontal = Kolt.layout.listRowPadding, vertical = Kolt.sizes.paddingSmallMedium),
                verticalAlignment = Alignment.CenterVertically,
                content = row,
            )
        }
    }
}
