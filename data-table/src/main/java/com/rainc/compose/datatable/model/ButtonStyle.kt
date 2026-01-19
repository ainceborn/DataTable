package com.rainc.compose.datatable.model

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonElevation
import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Shape

@Immutable
data class ButtonStyle(
    val shape: Shape,
    val colors: ButtonColors,
    val elevation: ButtonElevation?,
    val border: BorderStroke?,
    val contentPadding: PaddingValues,
)