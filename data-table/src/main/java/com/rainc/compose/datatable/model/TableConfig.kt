package com.rainc.compose.datatable.model

import androidx.compose.runtime.Immutable

@Immutable
data class TableConfig(
    val cellHeightInDp: Int,
    val defaultCellWidth: Int = 150,
    val columnConfigs: Map<Int,ColumnConfig>,
){

    fun getColumnWidth(columnIndex: Int): Int {
        return columnConfigs[columnIndex]?.cellWidthInDp ?: defaultCellWidth
    }
}