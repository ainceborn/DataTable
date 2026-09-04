package com.rainc.compose.datatable.model

import androidx.compose.material3.SwitchColors
import androidx.compose.material3.SwitchDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable

@Immutable
data class SwitchStyle(
    val colors: SwitchColors,
) {
    companion object {
        @Composable
        fun default(): SwitchStyle = SwitchStyle(colors = SwitchDefaults.colors())
    }
}
