package com.rainc.compose.datatable.model

import androidx.compose.runtime.Immutable

@Immutable
data class Table(
    val columnHeaders: List<Header>,
    val rows: List<Row>,
) {
    fun getColumnWidth(columnIndex: Int): Int? {
        return columnHeaders.getOrNull(columnIndex)?.config?.cellWidthInDp
    }
}