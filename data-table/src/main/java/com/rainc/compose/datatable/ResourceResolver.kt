package com.rainc.compose.datatable

import androidx.annotation.DrawableRes

object ResourceResolver {
    private var resourceProvider: ResourceProvider? = null

    fun attachProvider(provider: ResourceProvider){
        resourceProvider = provider
    }

    @DrawableRes
    fun getSortIconId(mode: ColumnAction.Sort.SortMode): Int{
        return resourceProvider!!.requestDrawableResId(mode)
    }
}