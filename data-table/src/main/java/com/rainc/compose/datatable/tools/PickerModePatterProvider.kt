package com.rainc.compose.datatable.tools

object PickerModePatterProvider {
    var patterResolver = object : PickerModePatterResolver {
        override fun resolvePattern(mode: com.rainc.compose.datatable.model.PickerMode): String {
            return when (mode) {
                com.rainc.compose.datatable.model.PickerMode.DATE,
                com.rainc.compose.datatable.model.PickerMode.DATE_TIME -> "yyyy-MM-dd'T'HH:mm:ss"
                com.rainc.compose.datatable.model.PickerMode.TIME -> "HH:mm:ss"
            }
        }
    }
}