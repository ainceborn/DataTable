package com.rainc.compose.datatable.cell

import androidx.compose.foundation.clickable
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import com.rainc.compose.datatable.CellAction
import com.rainc.compose.datatable.model.Cell
import com.rainc.compose.datatable.model.CellAttributes
import com.rainc.compose.datatable.model.CellStyle
import com.rainc.compose.datatable.model.CompilationKey
import com.rainc.compose.datatable.model.Coordinate
import java.util.UUID

@Immutable
data class TextCell(
    val text: String,
    override val coordinate: Coordinate,
    override val uuid: UUID = UUID.randomUUID(),
    override val hasError: Boolean = false,
    override val attr: CellAttributes = CellAttributes(),
) : Cell {

    override val sortKeyValue: CompilationKey
        get() = CompilationKey.StringKey(text)

    @Composable
    override fun Render(onCellAction: ((CellAction) -> Unit)?, cellStyle: CellStyle) {
        val textStyle = if (attr.textColor != null)
            cellStyle.textStyle.copy(color = Color(attr.textColor!!))
        else
            cellStyle.textStyle

        val columnTitle = attr.genericAttributes.getString("columnTitle", "")
        val showDialog = remember { mutableStateOf(false) }
        val isOverflowing = remember { mutableStateOf(false) }

        Text(
            text = text,
            style = textStyle,
            overflow = TextOverflow.Ellipsis,
            onTextLayout = { isOverflowing.value = it.hasVisualOverflow },
            modifier = if (isOverflowing.value) Modifier.clickable { showDialog.value = true } else Modifier,
        )

        if (showDialog.value) {
            AlertDialog(
                onDismissRequest = { showDialog.value = false },
                title = if (columnTitle.isNotEmpty()) {{ Text(columnTitle) }} else null,
                text = { Text(text) },
                confirmButton = {
                    TextButton(onClick = { showDialog.value = false }) { Text("Close") }
                },
            )
        }
    }
}
