package com.rainc.compose.datatable

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.rainc.compose.datatable.model.CellStyle
import com.rainc.compose.datatable.model.Header
import com.rainc.compose.datatable.model.UIIcon
import com.rainc.compose.datatable.tools.Base64IconResolver.getIconBitmap

@Composable
fun ColumnHeader(
    modifier: Modifier = Modifier,
    header: Header,
    cellStyle: CellStyle,
    sortIconProvider: (ColumnAction.Sort.SortMode) -> UIIcon,
    onHeaderActionTriggered: ((Header, ColumnAction) -> Unit)? = null, ){
    Row(modifier = modifier) {
        Spacer(Modifier.width(8.dp))

        Text(
            modifier = Modifier
                .weight(1f, fill = true)
                .align(Alignment.CenterVertically),
            text = header.title,
            style = cellStyle.textStyle,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )

        when(header.action){
            ColumnAction.None -> {
                // No action to render
            }
            is ColumnAction.Sort -> {
                val icon = sortIconProvider(header.action.mode)

                IconButton(
                    modifier = Modifier.size(32.dp),
                    onClick = {
                        onHeaderActionTriggered?.invoke(header, header.action.copy(mode = header.action.mode.transition()))
                    },
                    content = {
                        when(icon){
                            is UIIcon.ResourceIcon -> {
                                Icon(
                                    painter = painterResource(icon.icon),
                                    contentDescription = "Sort Action Icon",
                                    tint = cellStyle.textStyle.color
                                )
                            }
                            is UIIcon.ComposeVectorIcon -> {
                                Icon(
                                    imageVector = icon.icon(),
                                    contentDescription = "Sort Action Icon",
                                    tint = cellStyle.textStyle.color
                                )
                            }

                            is UIIcon.Base64Icon -> {
                                val iconBitmap = icon.iconInfo.getIconBitmap(context = LocalContext.current)
                                iconBitmap?.asImageBitmap()?.let {
                                    Icon(
                                        bitmap = it,
                                        contentDescription = icon.iconInfo.contentDescription ?: "Sort Action Icon",
                                        tint = cellStyle.textStyle.color
                                    )
                                }
                            }
                        }
                    }
                )
            }
            is ColumnAction.Unspecialized -> {
                header.action.icon?.let {
                    IconButton(
                        modifier = Modifier.size(32.dp),
                        onClick = {
                            onHeaderActionTriggered?.invoke(header, header.action)
                        },
                        content = {
                            Icon(
                                imageVector = it,
                                contentDescription = header.action.description ?: "Header Action Icon",
                                tint = cellStyle.textStyle.color
                            )
                        }
                    )
                }
            }
        }
        Spacer(Modifier.width(4.dp))
    }
}