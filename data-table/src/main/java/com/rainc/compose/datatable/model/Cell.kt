package com.rainc.compose.datatable.model

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import com.rainc.compose.datatable.CellAction
import java.util.UUID

@Immutable
interface Cell {
    val uuid: UUID
    val coordinate: Coordinate

    @Composable
    fun Render(onCellAction:((Cell, CellAction)-> Unit)?, cellStyle: CellStyle)
}