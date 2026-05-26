package com.rainc.compose.datatable.model

import com.rainc.compose.datatable.tools.PickerModePatterProvider
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

enum class PickerMode {
    DATE, TIME, DATE_TIME;

    fun toStringValue(value: Date): String {
        val patter = PickerModePatterProvider.patterResolver.resolvePattern(this)
        val formatter = SimpleDateFormat(patter, Locale.getDefault(Locale.Category.FORMAT))
        return formatter.format(value)
    }

    fun getDisplayValue(value: Date): String {
        return when (this) {
            DATE -> DateFormat.getDateInstance().format(value)
            TIME ->  DateFormat.getTimeInstance().format(value)
            DATE_TIME -> DateFormat.getDateTimeInstance().format(value)
        }
    }
}