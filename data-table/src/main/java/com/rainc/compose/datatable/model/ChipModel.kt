package com.rainc.compose.datatable.model

import androidx.compose.runtime.Immutable
import java.io.Serializable

@Immutable
data class ChipModel<T:Serializable>(val label: String, val value: T, val isSelected: Boolean): Serializable{

    fun toId(): Int {
        return (label + value.toString()).hashCode()
    }
}
