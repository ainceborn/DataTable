package com.rainc.compose.datatable.tools

import androidx.annotation.ColorInt
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle

fun TextStyle.updateTextColor(@ColorInt colorInt: Int?): TextStyle {
    return colorInt?.let { this.copy(color = Color(it)) } ?: this
}