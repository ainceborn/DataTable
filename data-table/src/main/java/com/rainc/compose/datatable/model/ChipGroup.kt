package com.rainc.compose.datatable.model

import androidx.compose.runtime.Immutable
import java.io.Serializable

@Immutable
data class ChipGroup <T: Serializable>(
    val chips: List<ChipModel<T>>,
    val isSingleSelect: Boolean
):Serializable