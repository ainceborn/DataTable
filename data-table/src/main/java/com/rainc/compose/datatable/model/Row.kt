package com.rainc.compose.datatable.model

import androidx.compose.runtime.Immutable
import java.util.UUID

@Immutable
data class Row(
    val uuid: UUID = UUID.randomUUID(),
    val index: Int,
    val cells: List<Cell>
)