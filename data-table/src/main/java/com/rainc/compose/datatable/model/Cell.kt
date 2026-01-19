package com.rainc.compose.datatable.model

import androidx.compose.runtime.Composable
import androidx.compose.ui.text.TextStyle
interface Cell {
    val coordinate: Coordinate
    @Composable
    fun Render(onCellAction:((Cell)-> Unit)?, cellStyle: CellStyle)
}