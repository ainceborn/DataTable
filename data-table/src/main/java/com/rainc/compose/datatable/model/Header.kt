package com.rainc.compose.datatable.model

import com.rainc.compose.datatable.ColumnAction

data class Header(
    val index: Int,
    val title: String,
    val isStickyColumn: Boolean = false,
    val config: ColumnConfig = ColumnConfig(),
    val action: ColumnAction
)