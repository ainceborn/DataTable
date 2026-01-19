package com.rainc.compose.datatable.model

import androidx.compose.runtime.Immutable

@Immutable
data class Row(val index: Int, val cells: List<Cell>)