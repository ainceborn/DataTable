package com.rainc.compose.datatable.cell

import androidx.compose.material3.RadioButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.rainc.compose.datatable.CellAction
import com.rainc.compose.datatable.model.Cell
import com.rainc.compose.datatable.model.CellStyle
import com.rainc.compose.datatable.model.Coordinate
import java.util.UUID

@Immutable
data class RadioButtonCell(
    val value: Boolean,
    override val coordinate: Coordinate,
    override val uuid: UUID = UUID.randomUUID()
) : Cell  {
    @Composable
    override fun Render(onCellAction: ((Cell, CellAction) -> Unit)?, cellStyle: CellStyle) {
        var isEnabled by remember { mutableStateOf(value) }
        RadioButton(
            selected = isEnabled,
            onClick = {
                isEnabled = !isEnabled
                onCellAction?.invoke(this, CellAction.ToggleBoolean(isEnabled))
            }
        )
    }

}