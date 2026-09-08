
package com.rainc.compose.datatable.view

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import com.rainc.compose.datatable.model.ChipModel
import com.rainc.compose.datatable.model.ChipStyle
import java.io.Serializable

@Composable
fun <T: Serializable> ChipGroup(
    data: List<ChipModel<T>>,
    textStyle: TextStyle,
    chipStyle: ChipStyle? = null,
    onSelectedChanged: (Pair<Int, ChipModel<T>>) -> Unit = {},
    chipView: @Composable (
        chipModel: ChipModel<T>,
        index: Int,
        textStyle: TextStyle,
    ) -> Unit = { chipModel, index, style ->
        DefaultChipView(
            chipModel = chipModel,
            index = index,
            textStyle = style,
            chipStyle = chipStyle,
            onSelectedChanged = onSelectedChanged
        )
    }
) {
    val listState = rememberLazyListState()

    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        LazyRow(
            state = listState,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            itemsIndexed(data) { index, it ->
                chipView.invoke(it, index, textStyle)
            }
        }
    }
}

@Composable
private fun <T: Serializable> DefaultChipView(
    chipModel: ChipModel<T>,
    index: Int,
    textStyle: TextStyle,
    chipStyle: ChipStyle? = null,
    onSelectedChanged: (Pair<Int, ChipModel<T>>) -> Unit = {}
) {
    val onSurface = MaterialTheme.colorScheme.onSurface
    val surfaceVariant = MaterialTheme.colorScheme.surfaceVariant
    val primary = MaterialTheme.colorScheme.primary
    val outline = MaterialTheme.colorScheme.outline

    FilterChip(
        label = { Text(text = chipModel.label, style = textStyle) },
        leadingIcon = if (chipModel.isSelected) {
            {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = null,
                    tint = chipStyle?.checkmarkColor ?: primary,
                )
            }
        } else null,
        selected = chipModel.isSelected,
        onClick = { onSelectedChanged.invoke(Pair(index, chipModel)) },
        colors = FilterChipDefaults.filterChipColors(
            containerColor = chipStyle?.containerColor ?: surfaceVariant,
            selectedContainerColor = chipStyle?.selectedContainerColor ?: primary.copy(alpha = 0.12f),
            labelColor = chipStyle?.labelColor ?: onSurface,
            selectedLabelColor = chipStyle?.selectedLabelColor ?: primary,
        ),
        border = FilterChipDefaults.filterChipBorder(
            enabled = true,
            selected = chipModel.isSelected,
            borderColor = chipStyle?.borderColor ?: outline,
            selectedBorderColor = chipStyle?.selectedBorderColor ?: primary,
            borderWidth = chipStyle?.borderWidth ?: 1.dp,
            selectedBorderWidth = chipStyle?.borderWidth ?: 1.dp,
        )
    )
}
