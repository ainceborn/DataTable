package com.rainc.compose.datatable.cell

import androidx.compose.material3.Switch
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import com.rainc.compose.datatable.CellAction
import com.rainc.compose.datatable.model.Cell
import com.rainc.compose.datatable.model.CellStyle
import com.rainc.compose.datatable.model.Coordinate
import java.util.UUID

@Immutable
data class SwitchCell(
    val value: Boolean,
    override val coordinate: Coordinate,
    override val uuid: UUID  = UUID.randomUUID()
) : Cell  {
    @Composable
    override fun Render(onCellAction: ((Cell, CellAction) -> Unit)?, cellStyle: CellStyle) {
        val isChecked = remember { mutableStateOf(value) }

        Switch(
            checked = isChecked.value,
            onCheckedChange = {
                isChecked.value = it
                onCellAction?.invoke(this, CellAction.ToggleBoolean(isChecked.value))
            }
        )

    }
}