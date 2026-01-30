package com.rainc.compose.datatable

import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.vector.ImageVector
import java.io.Serializable

sealed class ColumnAction {

    @Immutable
    data class Sort(val mode: SortMode = SortMode.NONE):ColumnAction(){

        enum class SortMode {
            ASCENDING,
            DESCENDING,
            NONE;

            fun transition(): SortMode {
                return when (this) {
                    NONE -> ASCENDING
                    ASCENDING -> DESCENDING
                    DESCENDING -> NONE
                }
            }
        }
    }

    @Immutable
    data class Unspecialized(
        val tag: Serializable,
        val icon: ImageVector? = null,
        val description: String? = null
    ) : ColumnAction()

    @Immutable
    object None : ColumnAction()
}