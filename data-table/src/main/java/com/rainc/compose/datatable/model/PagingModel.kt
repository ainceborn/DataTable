package com.rainc.compose.datatable.model

import androidx.compose.runtime.Immutable

@Immutable
sealed class PagingModel(open val row: Row) {

    @Immutable
    data class RowWithHeaders(val headers: List<Header>, override val row: Row):PagingModel(row)

    @Immutable
    data class PagingRow(override val row: Row):PagingModel(row)
}