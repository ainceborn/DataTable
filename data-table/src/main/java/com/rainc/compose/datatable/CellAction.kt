package com.rainc.compose.datatable

import com.rainc.compose.datatable.model.Cell
import com.rainc.compose.datatable.model.ChipGroup
import java.io.Serializable
import java.time.Instant

sealed class CellAction(val cell: Cell) : Serializable {
    data class UpdateText(val newText: String, private val trigger: Cell) : CellAction(trigger)
    data class ToggleBoolean(val newValue: Boolean, private val trigger: Cell) : CellAction(trigger)
    data class ButtonPressed(private val trigger: Cell) : CellAction(trigger)
    data class NewDateSelected(val newDate: Instant, private val trigger: Cell) : CellAction(trigger)
    sealed class SegmentStateChange(trigger: Cell) : CellAction(trigger) {
        data class StringSegmentChange(val newData: ChipGroup<String>, private val trigger: Cell) :
            SegmentStateChange(trigger)

        data class SerializableSegmentChange(val newData: ChipGroup<Serializable>, private val trigger: Cell) : SegmentStateChange(trigger)
    }
    data class UnspecifiedAction(val model: Serializable, private val trigger: Cell) : CellAction(trigger)
}