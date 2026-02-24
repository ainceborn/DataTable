package com.rainc.compose.datatable

import androidx.compose.material3.ButtonDefaults
import androidx.compose.runtime.Composable
import com.rainc.compose.datatable.model.ButtonStyle
import com.rainc.compose.datatable.model.Header
import com.rainc.compose.datatable.model.TableConfig

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