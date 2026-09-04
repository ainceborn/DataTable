package com.rainc.compose.datatable.model

import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Immutable
data class ErrorStyle(
    val borderColor: Color = Color(0xFFBB0000),
    val backgroundColor: Color = Color(0xFFFFF0F0),
    val indicatorColor: Color = Color(0xFFBB0000),
    val indicatorWidth: Dp = 5.dp,
)
