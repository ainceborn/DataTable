package com.rainc.compose.datatable.model

import androidx.compose.runtime.Immutable

@Immutable
data class Table(
    val columnHeaders: List<String>,
    val stickyRows: List<Row>,
    val rows: List<Row>,
)