package com.rainc.compose.datatable.model

import androidx.compose.runtime.Immutable

@Immutable
data class ColumnConfig(
    val cellWidthInDp: Int = 150,
    val cellHeightInDp: Int = 56
)