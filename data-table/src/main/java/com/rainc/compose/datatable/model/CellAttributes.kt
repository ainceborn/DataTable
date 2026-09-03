package com.rainc.compose.datatable.model

import android.os.Bundle
import androidx.annotation.ColorInt
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Immutable
import androidx.compose.ui.unit.dp

@Immutable
data class CellAttributes(
    @ColorInt
    val textColor: Int? = null,
    val style: String = "",
    val genericAttributes: Bundle = Bundle(),
    val contentPadding: PaddingValues = PaddingValues(0.dp),
    val isEditable: Boolean = true,
)