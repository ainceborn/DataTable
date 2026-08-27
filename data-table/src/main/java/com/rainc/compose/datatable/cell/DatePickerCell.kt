package com.rainc.compose.datatable.cell

import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.rainc.compose.datatable.CellAction
import com.rainc.compose.datatable.model.Cell
import com.rainc.compose.datatable.model.CellAttributes
import com.rainc.compose.datatable.model.CellStyle
import com.rainc.compose.datatable.model.CompilationKey
import com.rainc.compose.datatable.model.Coordinate
import com.rainc.compose.datatable.model.PickerMode
import com.rainc.compose.datatable.tools.updateTextColor
import com.rainc.compose.datatable.view.DatePickerDialog
import java.time.Instant
import java.util.Date
import java.util.UUID

@Immutable
data class DatePickerCell(
    override val uuid: UUID = UUID.randomUUID(),
    var rawValue: String,
    val stringValue: String,
    val dateInMills: Long,
    override val coordinate: Coordinate,
    override val hasError: Boolean = false,
    override val attr: CellAttributes = CellAttributes(),
) : Cell  {

    override val sortKeyValue: CompilationKey
        get() = CompilationKey.LongKey(dateInMills)

    @Composable
    override fun Render(onCellAction: ((CellAction) -> Unit)?, cellStyle: CellStyle) {
        val isDialogVisible = remember { mutableStateOf(false) }
        var textValue: String by remember(stringValue) { mutableStateOf(stringValue) }
        var dateInMillsState: Long by remember(dateInMills) { mutableLongStateOf(dateInMills) }


        TextButton(
            content = {
                Text(text = textValue, style = cellStyle.textStyle.updateTextColor(attr.textColor))
            },
            onClick = {
                isDialogVisible.value = true
            }
        )

        DatePickerDialog(
            dateInMills = dateInMillsState,
            show = isDialogVisible,
            onDismiss = {
                isDialogVisible.value = false
            },
            onDateSelected = {
                textValue = PickerMode.DATE.getDisplayValue(Date(it))
                dateInMillsState = it
                onCellAction?.invoke(CellAction.NewDateSelected(
                    newDate = Instant.ofEpochMilli(it),
                    trigger = this
                ))
            }
        )
    }
}