package com.rainc.compose.datatable.cell

import android.os.Bundle
import androidx.annotation.ColorRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.dp
import com.rainc.compose.datatable.CellAction
import com.rainc.compose.datatable.getIntOrNull
import com.rainc.compose.datatable.getSerializableOrNull
import com.rainc.compose.datatable.model.Base64IconInfo
import com.rainc.compose.datatable.model.Cell
import com.rainc.compose.datatable.model.CellAttributes
import com.rainc.compose.datatable.model.CellStyle
import com.rainc.compose.datatable.model.CompilationKey
import com.rainc.compose.datatable.model.Coordinate
import com.rainc.compose.datatable.tools.Base64IconResolver.getIconBitmap
import java.util.UUID

@Immutable
data class IconButtonCell(
    val buttonText:String,
    override val coordinate: Coordinate,
    override val uuid: UUID = UUID.randomUUID(),
    override val hasError: Boolean = false,
    override val attr: CellAttributes = CellAttributes(),
) : Cell {

    companion object{
        const val KEY_CONTAINER_COLOR_RES = "containerColorRes"
        const val KEY_CONTENT_COLOR_RES = "contentColorRes"
        const val KEY_ICON_INFO = "iconInfo"

        fun buildAttribute(
            @ColorRes containerColorRes: Int?,
            @ColorRes contentColorRes: Int?,
            iconInfo: Base64IconInfo?
        ): CellAttributes {
            return CellAttributes(
                genericAttributes = Bundle().apply {
                    contentColorRes?.let { putInt(KEY_CONTENT_COLOR_RES, it) }
                    containerColorRes?.let { putInt(KEY_CONTAINER_COLOR_RES, it) }
                    iconInfo?.let { putSerializable(KEY_ICON_INFO, it) }
                }
            )
        }
    }


    override val sortKeyValue: CompilationKey
        get() = CompilationKey.StringKey(buttonText)

    @Composable
    override fun Render(onCellAction: ((CellAction) -> Unit)?, cellStyle: CellStyle) {
        val context = LocalContext.current

        val containerColorRes = attr.genericAttributes.getIntOrNull(KEY_CONTAINER_COLOR_RES)
        val contentColorRes = attr.genericAttributes.getIntOrNull(KEY_CONTENT_COLOR_RES)
        val iconInfo = attr.genericAttributes.getSerializableOrNull(KEY_ICON_INFO, Base64IconInfo::class.java)

        val iconBitmap = iconInfo?.getIconBitmap(context = context)

        val contentColor = contentColorRes?.let { colorResource(it) } ?: Color.Unspecified
        val containerColor = containerColorRes?.let { colorResource(it) } ?: Color.Unspecified

        Button(
            enabled = attr.isEditable,
            colors = ButtonDefaults.buttonColors(
                containerColor = containerColor,
                contentColor = contentColor
            ),
            content = {
                Row {
                    iconBitmap?.asImageBitmap()?.let {
                        Image(
                            modifier = Modifier.align(Alignment.CenterVertically),
                            bitmap = it,
                            colorFilter = ColorFilter.tint(contentColor),
                            contentDescription = null
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                    }
                    Text(
                        modifier = Modifier.align(Alignment.CenterVertically),
                        text = buttonText,
                        style = cellStyle.textStyle.copy(color = contentColor)
                    )
                }
            },
            onClick = {
                onCellAction?.invoke(CellAction.ButtonPressed(trigger = this))
            }
        )
    }
}