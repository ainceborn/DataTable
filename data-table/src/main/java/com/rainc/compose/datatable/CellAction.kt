package com.rainc.compose.datatable

import com.rainc.compose.datatable.model.ChipGroup
import java.io.Serializable
import java.time.Instant

sealed class CellAction {
    data class UpdateText(val newText: String) : CellAction()
    data class ToggleBoolean(val newValue: Boolean) : CellAction()
    data object ButtonPressed : CellAction()
    data class NewDateSelected(val newDate: Instant) : CellAction()
    data class SegmentStateChange<T: Serializable>(val newChipGroup: ChipGroup<T>) : CellAction()
    data class UnspecifiedAction(val model: Serializable) : CellAction()
}