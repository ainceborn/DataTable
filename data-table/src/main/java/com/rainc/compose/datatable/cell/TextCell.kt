package com.rainc.compose.datatable.cell

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.text.TextStyle
import com.rainc.compose.datatable.CellAction
import com.rainc.compose.datatable.model.Cell
import com.rainc.compose.datatable.model.CellStyle
import com.rainc.compose.datatable.model.Coordinate
import java.util.UUID

@Immutable
data class TextCell(
    val text: String,
    override val coordinate: Coordinate,
    override val uuid: UUID = UUID.randomUUID()
) : Cell {
    @Composable
    override fun Render(onCellAction: ((Cell, CellAction) -> Unit)?, cellStyle: CellStyle) {
        Text(text = text, style = cellStyle.textStyle)
    }
}
