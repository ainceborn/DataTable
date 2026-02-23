package com.rainc.compose.datatable.datasource

import com.rainc.compose.datatable.model.PagingModel
import kotlinx.coroutines.Deferred

interface PageApi {
    fun pageCount(page: Int) : Deferred<Result<List<PagingModel>>>
}