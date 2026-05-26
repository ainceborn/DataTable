package com.rainc.compose.datatable

import android.os.Bundle
import androidx.compose.material3.ButtonDefaults
import androidx.compose.runtime.Composable
import com.rainc.compose.datatable.model.ButtonStyle
import com.rainc.compose.datatable.model.Header
import com.rainc.compose.datatable.model.TableConfig
import java.io.Serializable

@Composable
fun getButtonStyle(): ButtonStyle{
   return  ButtonStyle(
        shape = ButtonDefaults.shape,
        colors = ButtonDefaults.buttonColors(),
        elevation = ButtonDefaults.buttonElevation(),
        border = null,
        contentPadding = ButtonDefaults.ContentPadding,
    )
}

fun defaultTableConfig(
    cellHeight: Int = 56,
    defaultCellWidth: Int = 150,
): TableConfig {
    return TableConfig(
        defaultHeightInDp = cellHeight,
        defaultCellWidth = defaultCellWidth,
    )
}

fun List<Header>.getColumnWidth(columnIndex: Int): Int? {
    return getOrNull(columnIndex)?.config?.cellWidthInDp
}

fun Bundle.getIntOrNull(key: String): Int? {
    return if (containsKey(key)) getInt(key) else null
}

fun <T : Serializable> Bundle.getSerializableOrNull(
    key: String,
    clazz: Class<T>
): T? {
    return if (android.os.Build.VERSION.SDK_INT >= 33) {
        getSerializable(key, clazz)
    } else {
        @Suppress("DEPRECATION")
        getSerializable(key) as? T
    }
}