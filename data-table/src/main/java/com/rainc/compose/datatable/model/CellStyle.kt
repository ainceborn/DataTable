package com.rainc.compose.datatable.model

import androidx.compose.runtime.Immutable
import androidx.compose.ui.text.TextStyle

@Immutable
data class CellStyle(
   val textStyle: TextStyle,
   val buttonStyle: ButtonStyle
)