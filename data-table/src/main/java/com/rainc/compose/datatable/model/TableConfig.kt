package com.rainc.compose.datatable.model

import androidx.compose.runtime.Immutable

@Immutable
data class TableConfig(
    val defaultHeightInDp: Int,
    val defaultCellWidth: Int = 150
)