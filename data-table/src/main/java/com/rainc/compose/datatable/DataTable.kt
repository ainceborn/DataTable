package com.rainc.compose.datatable

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.rainc.compose.datatable.model.Cell
import com.rainc.compose.datatable.model.CellStyle
import com.rainc.compose.datatable.model.Header
import com.rainc.compose.datatable.model.Row
import com.rainc.compose.datatable.model.Table
import com.rainc.compose.datatable.model.TableConfig

/**
 * @param table 2D list of strings representing the grid content.
 * @param config Configuration for table dimensions.
 * @param modifier Modifier for styling and positioning.
 * @param columnHeaderBackground Background color of column headers.
 * @param columnHeaderContentAlignment Alignment of column header text.
 * @param columnHeaderTextStyle Style for column header text.
 * @param rowHeaderBackground Background color of row headers.
 * @param rowHeaderContentAlignment Alignment of row header text.
 * @param dataBoxColor Background color of data boxes.
 * @param dataBoxContentAlignment Alignment of data box text.
 * @param dataTextStyle Style for data box text.
 * @param horizontalCellDividerColor Color of horizontal cell dividers.
 * @param verticalCellDividerColor Color of vertical cell dividers.
 * @param columnHeaderDividerColor Color of column header dividers.
 * @param onCellLongPress Callback for handling long press on a cell. Returns the Data Row Header.
 * @param onCellAction Callback for handling cell actions.
 * @param onHeaderActionTriggered Callback for handling header actions.
 * You can use it to fire an event for example an api call or open a dialog box.
 *
 */

@Composable
fun DataTable(
    modifier: Modifier = Modifier,
    table: Table,
    config: TableConfig = defaultTableConfig(),
    columnHeaderBackground: Color = Color.LightGray,
    columnHeaderContentAlignment: Alignment = Alignment.Center,
    columnHeaderTextStyle: () -> TextStyle = {
        TextStyle.Default.copy(color = Color.White, fontSize = 14.sp)
    },
    rowHeaderBackground: Color = Color.LightGray,
    rowHeaderContentAlignment: Alignment = Alignment.Center,
    dataBoxColor: Color = Color.White,
    dataBoxContentAlignment: Alignment = Alignment.Center,
    dataTextStyle: () -> TextStyle = { TextStyle.Default.copy(color = Color.Black, fontSize = 14.sp) },
    horizontalCellDividerColor: Color? = null,
    verticalCellDividerColor: Color? = null,
    columnHeaderDividerColor: Color? = null,
    onCellLongPress: ((Row)-> Unit)? = null,
    onCellAction: ((Cell, CellAction)-> Unit)? = null,
    onHeaderActionTriggered: ((Header, ColumnAction) -> Unit)? = null
) {
    val horizontalScrollState = rememberScrollState()
    val verticalScrollState = rememberLazyListState()
    val buttonStyle = getButtonStyle()
    val cellStyle by remember { mutableStateOf(CellStyle(
        textStyle = dataTextStyle(),
        buttonStyle =buttonStyle
    )) }

    val headerCellStyle by remember { mutableStateOf(CellStyle(
        textStyle = columnHeaderTextStyle(),
        buttonStyle = buttonStyle
    )) }

    val columnHeight = config.cellHeightInDp.dp


    Column(modifier) {
        // Top Row (Static Top Left + Scrollable Headers)
        Row(modifier = Modifier.fillMaxWidth()) {

            // Scrollable Column Headers
            Row(
                modifier = Modifier
                    .horizontalScroll(horizontalScrollState),
            ) {
                table.columnHeaders.forEachIndexed { index, header ->
                    Box(
                        modifier = Modifier
                            .width(config.getColumnWidth(index).dp)
                            .height(columnHeight)
                            .background(columnHeaderBackground)
                            .border(
                                width = if (columnHeaderDividerColor != null) 0.5.dp else 0.dp,
                                color = columnHeaderDividerColor ?: Color.Transparent,
                                shape = RectangleShape // Ensures the border is applied to the rectangle
                            )
                            .horizontalScroll(horizontalScrollState),
                        contentAlignment = columnHeaderContentAlignment,
                    ) {
                        ColumnHeader(
                            modifier = Modifier.width(config.getColumnWidth(index).dp),
                            header = header,
                            cellStyle = headerCellStyle,
                            onHeaderActionTriggered = onHeaderActionTriggered
                        )
                    }
                }
            }
        }

        // TODO migrate to vertical scroll for static content
        /*Column(modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState())) {
            // Top-Left Static Cell
            table.rows.fastForEach {
                val row = it

            }
        }*/
        // Rows
        LazyColumn(
            state = verticalScrollState,
            modifier = Modifier.fillMaxSize(),
        ) {
            items(items = table.rows, key = { item -> item.uuid }){ row ->
                Row {
                    var columnIndex = 0

                    if(table.stickyRows.isEmpty().not()){
                        val stickyRow = table.stickyRows[row.index]

                        Row {
                            stickyRow.cells.forEach { cell ->
                                val columnWidth = config.getColumnWidth(columnIndex++).dp
                                // Sticky First Column
                                Box(
                                    modifier = Modifier
                                        .width(columnWidth)
                                        .height(columnHeight)
                                        .background(rowHeaderBackground)
                                        .border(
                                            width = if (horizontalCellDividerColor != null) 0.5.dp else 0.dp,
                                            color = horizontalCellDividerColor ?: Color.Transparent,
                                            shape = RectangleShape // Ensures the border is applied to the rectangle
                                        ),
                                    contentAlignment = rowHeaderContentAlignment,
                                ) {
                                    key(cell.coordinate, cell.uuid) {
                                        cell.Render(
                                            cellStyle = cellStyle,
                                            onCellAction = onCellAction
                                        )
                                    }
                                }
                            }
                        }

                    }

                    // Scrollable Row Cells
                    Row(
                        modifier = Modifier
                            .horizontalScroll(horizontalScrollState)
                    ) {
                        row.cells.forEach { cell ->
                            val columnWidth = config.getColumnWidth(columnIndex++).dp
                            Box(
                                modifier = Modifier
                                    .width(columnWidth)
                                    .height(columnHeight)
                                    .background(dataBoxColor)
                                    .border(
                                        width = if (verticalCellDividerColor != null) 0.5.dp else 0.dp,
                                        color = verticalCellDividerColor ?: Color.Transparent,
                                        shape = RectangleShape // Ensures the border is applied to the rectangle
                                    )
                                    .pointerInput(Unit) {
                                        detectTapGestures(onLongPress = {
                                            onCellLongPress?.invoke(row) // Pass row header as callback
                                        })
                                    },
                                contentAlignment = dataBoxContentAlignment,
                            ) {
                                cell.Render(
                                    cellStyle = cellStyle,
                                    onCellAction = onCellAction
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}