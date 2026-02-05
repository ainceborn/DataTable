package com.rainc.compose.datatable.cell

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.rainc.compose.datatable.CellAction
import com.rainc.compose.datatable.model.Cell
import com.rainc.compose.datatable.model.CellStyle
import com.rainc.compose.datatable.model.ChipGroup
import com.rainc.compose.datatable.model.Coordinate
import com.rainc.compose.datatable.view.ChipGroup
import java.util.UUID

@Immutable
data class SegmentControl(
    val data: ChipGroup<String>,
    override val coordinate: Coordinate,
    override var sortKeyValue: String = data.chips.joinToString { "it.value - ${it.isSelected}" },
    override val uuid: UUID = UUID.randomUUID(),
) : Cell  {
    @Composable
    override fun Render(onCellAction: ((CellAction) -> Unit)?, cellStyle: CellStyle) {
        var chips by remember { mutableStateOf(data.chips) }

        ChipGroup(
            data = chips,
            textStyle = cellStyle.textStyle,
            onSelectedChanged = { (index, chip) ->
                chips = if(data.isSingleSelect){
                   chips.mapIndexed { chipIndex, model ->
                        if(chipIndex == index){
                            model.copy(isSelected = model.isSelected.not())
                        } else {
                            model.copy(isSelected = false)
                        }
                    }
                } else {
                   buildList {
                        chips.forEachIndexed { chipIndex, model ->
                            if(chipIndex == index){
                                add(model.copy(isSelected = !model.isSelected))
                            } else {
                                add(model)
                            }
                        }
                    }
                }

                onCellAction?.invoke(CellAction.SegmentStateChange.StringSegmentChange(data.copy(chips = chips), trigger = this))
            }
        )
    }
}

