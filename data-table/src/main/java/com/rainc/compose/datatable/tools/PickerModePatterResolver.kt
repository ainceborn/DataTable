package com.rainc.compose.datatable.tools

import com.rainc.compose.datatable.model.PickerMode

interface PickerModePatterResolver {
    fun resolvePattern(mode: PickerMode): String
}