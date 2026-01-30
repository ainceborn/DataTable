package com.rainc.compose.datatable

import androidx.annotation.DrawableRes
import androidx.compose.ui.graphics.vector.ImageVector

abstract class ResourceProvider {
    abstract fun requestImageVector(obj:Any): ImageVector

    @DrawableRes
    abstract fun requestDrawableResId(obj:Any): Int
}