package com.rainc.compose.datatable.cell

import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import com.rainc.compose.datatable.model.Cell
import com.rainc.compose.datatable.model.CellStyle
import com.rainc.compose.datatable.model.Coordinate

@Immutable
data class ButtonCell(val buttonText:String, override val coordinate: Coordinate) : Cell {
    @Composable
    override fun Render(onCellAction: ((Cell) -> Unit)?, cellStyle: CellStyle) {
        Button(
            content = {
                Text(text = buttonText)
            },
            onClick = {
                onCellAction?.invoke(this)
            }
        )
    }
}