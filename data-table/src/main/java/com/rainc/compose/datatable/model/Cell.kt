package com.rainc.compose.datatable.model

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import com.rainc.compose.datatable.CellAction
import java.io.Serializable
import java.util.UUID

@Immutable
interface Cell : Serializable {
    val sortKeyValue: String
    val uuid: UUID
    val coordinate: Coordinate

    @Composable
    fun Render(onCellAction:((CellAction)-> Unit)?, cellStyle: CellStyle)
}