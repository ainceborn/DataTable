package com.rainc.compose.datatable.tools

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import com.rainc.compose.datatable.model.Base64IconInfo

interface Base64IconDecoder {
    fun getIconDrawable(context: Context, iconInfo: Base64IconInfo, iconSize: Int): Drawable?
    fun getIconBitmap(context: Context, iconInfo: Base64IconInfo, iconSize: Int): Bitmap?
}