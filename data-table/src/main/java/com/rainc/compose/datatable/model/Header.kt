package com.rainc.compose.datatable.model

import com.rainc.compose.datatable.ColumnAction

data class Header(
    val index: Int,
    val title: String,
    val action: ColumnAction
)