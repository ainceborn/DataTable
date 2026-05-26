package com.rainc.compose.datatable.tools

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import com.rainc.compose.datatable.model.Base64IconInfo

object Base64IconResolver {

    var iconDecoder = object : Base64IconDecoder {
        override fun getIconDrawable(context: Context, iconInfo: Base64IconInfo, iconSize: Int): Drawable? {
            return null
        }

        override fun getIconBitmap(context: Context, iconInfo: Base64IconInfo, iconSize: Int): Bitmap? {
            return null
        }
    }

     fun Base64IconInfo.getIconDrawable(context: Context): Drawable? {
        return iconDecoder.getIconDrawable(
            iconInfo = this,
            iconSize = iconSize
        )
    }

    fun Base64IconInfo.getIconBitmap(context: Context): Bitmap? {
        return iconDecoder.getIconBitmap(
            iconInfo = this,
            iconSize = iconSize
        )
    }
}