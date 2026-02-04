
package com.rainc.compose.datatable.view

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import com.rainc.compose.datatable.model.ChipModel
import java.io.Serializable

@Composable
fun <T: Serializable> ChipGroup(
    data: List<ChipModel<T>>,
    textStyle: TextStyle,
    onSelectedChanged: (Pair<Int, ChipModel<T>>) -> Unit = {},
    chipView: @Composable (
        chipModel: ChipModel<T>,
        index: Int, textStyle:
        TextStyle,
    ) -> Unit = { chipModel, index, textStyle ->
        DefaultChipView(
            chipModel = chipModel,
            index = index,
            textStyle = textStyle,
            onSelectedChanged = onSelectedChanged
        )
    }
) {
    val listState = rememberLazyListState()

    Column(modifier = Modifier.padding(8.dp).fillMaxSize()) {

        LazyRow(
            state = listState,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ){
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
    onSelectedChanged: (Pair<Int, ChipModel<T>>) -> Unit = {}
) {
    FilterChip(
        modifier = Modifier.padding(vertical = 0.dp),
        label = {
            Text(
                text = chipModel.label,
                style = textStyle
            )
        },
        selected = chipModel.isSelected,
        onClick = { onSelectedChanged.invoke(Pair(index, chipModel)) }
    )
}