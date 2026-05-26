package com.rainc.compose.datatable.model

import androidx.annotation.DrawableRes
import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.vector.ImageVector
import java.io.Serializable

sealed class UIIcon: Serializable {
    @Immutable
    data class ResourceIcon(@DrawableRes val icon: Int) : UIIcon()

    @Immutable
    abstract class ComposeVectorIcon: UIIcon() {
        abstract fun icon(): ImageVector

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false

            other as ComposeVectorIcon

            return icon() == other.icon()
        }

        override fun hashCode(): Int {
            return icon().hashCode()
        }
    }

    @Immutable
    data class Base64Icon(val iconInfo: Base64IconInfo): UIIcon()
}