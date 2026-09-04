package com.rainc.compose.datatable.model

import androidx.compose.material3.FilterChipDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Immutable
data class ChipStyle(
    val containerColor: Color,
    val selectedContainerColor: Color,
    val labelColor: Color,
    val selectedLabelColor: Color,
    val checkmarkColor: Color,
    val borderColor: Color,
    val selectedBorderColor: Color,
    val borderWidth: Dp = 1.dp,
) {
    companion object {
        @Composable
        fun default(): ChipStyle {
            val colors = FilterChipDefaults.filterChipColors()
            val border = FilterChipDefaults.filterChipBorder(enabled = true, selected = false)
            return ChipStyle(
                containerColor = Color.Transparent,
                selectedContainerColor = Color.Transparent,
                labelColor = Color.Unspecified,
                selectedLabelColor = Color.Unspecified,
                checkmarkColor = Color.Unspecified,
                borderColor = Color.Unspecified,
                selectedBorderColor = Color.Unspecified,
            )
        }
    }
}
