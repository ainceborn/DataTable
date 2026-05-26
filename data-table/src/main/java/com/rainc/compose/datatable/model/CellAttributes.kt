package com.rainc.compose.datatable.model

import android.os.Bundle
import androidx.annotation.ColorInt
import androidx.compose.runtime.Immutable

@Immutable
class CellAttributes(
    @ColorInt
    val textColor: Int? = null,
    val style: String = "",
    val genericAttributes: Bundle = Bundle()
)