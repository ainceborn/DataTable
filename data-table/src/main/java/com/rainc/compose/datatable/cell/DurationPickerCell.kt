package com.rainc.compose.datatable.cell

import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.rainc.compose.datatable.CellAction
import com.rainc.compose.datatable.model.Cell
import com.rainc.compose.datatable.model.CellAttributes
import com.rainc.compose.datatable.model.CellStyle
import com.rainc.compose.datatable.model.CompilationKey
import com.rainc.compose.datatable.model.Coordinate
import com.rainc.compose.datatable.tools.updateTextColor
import com.rainc.compose.datatable.view.Compose24hTimePicker
import java.util.UUID
import kotlin.time.Duration
import kotlin.time.DurationUnit

@Immutable
data class DurationPickerCell(
    override val uuid: UUID = UUID.randomUUID(),
    var rawValue: String,
    val stringValue: String,
    val duration: Duration,
    override val coordinate: Coordinate,
    override val hasError: Boolean = false,
    override val attr: CellAttributes = CellAttributes(),
) : Cell {

    override val sortKeyValue: CompilationKey
        get() = CompilationKey.LongKey(duration.toLong(DurationUnit.MILLISECONDS))

    @Composable
    override fun Render(onCellAction: ((CellAction) -> Unit)?, cellStyle: CellStyle) {
        val isDialogVisible = remember { mutableStateOf(false) }
        var textValue: String by remember { mutableStateOf(stringValue) }
        var hoursState by remember { mutableStateOf(duration.inWholeHours.toInt()) }
        var minutesState by remember { mutableStateOf((duration.inWholeMinutes % 60).toInt()) }

        TextButton(
            content = {
                Text(text = textValue, style = cellStyle.textStyle.updateTextColor(attr.textColor))
            },
            onClick = {
                isDialogVisible.value = true
            }
        )

        Compose24hTimePicker(
            initialHour = hoursState,
            initialMinute = minutesState,
            show = isDialogVisible.value,
            onDismiss = {
                isDialogVisible.value = false
            },
            onTimeSelected = { hours, minutes ->
                hoursState = hours
                minutesState = minutes
                textValue = String.format("%02d:%02d", hours, minutes)
                onCellAction?.invoke(CellAction.DurationSelected(hours = hours, minutes = minutes,  trigger = this))
            }
        )
    }
}




