package com.rainc.sample.activity

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Note
import androidx.compose.material3.Icon
import androidx.compose.ui.graphics.vector.ImageVector
import com.rainc.compose.datatable.ResourceProvider
import com.rainc.sample.R

object TableResourceProvider : ResourceProvider() {


    override fun requestImageVector(obj: Any): ImageVector {
        return Icons.Default.Note
    }

    override fun requestDrawableResId(obj: Any): Int {
        return R.drawable.ic_launcher_foreground
    }
}