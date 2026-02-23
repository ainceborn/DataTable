package com.rainc.compose.datatable.datasource

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.rainc.compose.datatable.model.PagingModel


abstract class PagingSource(
    private val api: PageApi,
    private val pageCount: Int
) : PagingSource<Int, PagingModel>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, PagingModel> {
        val page = params.key ?: 1

        val newItems = api.pageCount(page = page)
            .await()
            .getOrDefault(emptyList())

        return LoadResult.Page(
            data =  newItems,
            prevKey = if (page == 1) null else page - 1,
            nextKey = if (newItems.isEmpty() || pageCount == page) null else page + 1
        )
    }

    override fun getRefreshKey(state: PagingState<Int, PagingModel>): Int? {
        return state.anchorPosition
    }
}