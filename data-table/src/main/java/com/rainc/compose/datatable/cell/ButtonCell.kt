package com.rainc.compose.datatable.cell

import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import com.rainc.compose.datatable.CellAction
import com.rainc.compose.datatable.model.Cell
import com.rainc.compose.datatable.model.CellStyle
import com.rainc.compose.datatable.model.Coordinate
import java.util.UUID

@Immutable
data class ButtonCell(
    val buttonText:String,
    override val coordinate: Coordinate,
    override val sortKeyValue: String = buttonText,
    override val uuid: UUID = UUID.randomUUID(),
) : Cell {
    @Composable
    override fun Render(onCellAction: ((CellAction) -> Unit)?, cellStyle: CellStyle) {
        Button(
            content = {
                Text(text = buttonText)
            },
            onClick = {
                onCellAction?.invoke(CellAction.ButtonPressed(trigger = this))
            }
        )
    }
}