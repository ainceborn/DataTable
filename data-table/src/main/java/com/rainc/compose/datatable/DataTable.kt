package com.rainc.compose.datatable

import androidx.compose.foundation.ScrollState
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
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
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
import androidx.paging.LoadState
import androidx.paging.PagingData
import androidx.paging.compose.collectAsLazyPagingItems
import com.rainc.compose.datatable.model.Cell
import com.rainc.compose.datatable.model.CellStyle
import com.rainc.compose.datatable.model.DataUpdatePolicy
import com.rainc.compose.datatable.model.Header
import com.rainc.compose.datatable.model.PagingModel
import com.rainc.compose.datatable.model.Row
import com.rainc.compose.datatable.model.Table
import com.rainc.compose.datatable.model.TableConfig
import kotlinx.coroutines.flow.Flow
import java.util.UUID

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
fun PaginationDataTable(
    modifier: Modifier = Modifier,
    paginationData: Flow<PagingData<PagingModel>>,
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
    dataUpdatePolicy: DataUpdatePolicy = DataUpdatePolicy.NONE,
    onCellLongPress: ((Row)-> Unit)? = null,
    onCellAction: ((CellAction)-> Unit)? = null,
    onHeaderActionTriggered: ((Header, ColumnAction) -> Unit)? = null
) {
    val pagingRows = paginationData.collectAsLazyPagingItems()
    val horizontalScrollState = rememberScrollState()
    val verticalScrollState = rememberLazyListState()
    val buttonStyle = getButtonStyle()
    val cellStyle by remember { mutableStateOf(CellStyle(
        textStyle = dataTextStyle(),
        buttonStyle =buttonStyle
    )) }

    val rowsIds = remember { mutableStateOf(setOf<UUID>()) }
    val lastAction = remember { mutableStateOf<Pair<Header, ColumnAction>?>(null) }

    LaunchedEffect(key1 = pagingRows.itemCount) {
        val newRowsIds = pagingRows.itemSnapshotList.items.map { it.row.uuid }.toSet()

        if(rowsIds.value == newRowsIds) return@LaunchedEffect

        rowsIds.value = newRowsIds

        when(dataUpdatePolicy){
            DataUpdatePolicy.NONE -> return@LaunchedEffect
            DataUpdatePolicy.RETRIGGER_LAST_COLUMN_ACTION -> {
                lastAction.value?.let {
                    onHeaderActionTriggered?.invoke(it.first, it.second)
                }
            }
        }
    }

    val headerCellStyle by remember { mutableStateOf(CellStyle(
        textStyle = columnHeaderTextStyle(),
        buttonStyle = buttonStyle
    )) }

    val columnHeight = config.defaultHeightInDp.dp

    val headers by remember {
        derivedStateOf {
            pagingRows.itemSnapshotList.items
                .firstOrNull()
                ?.let { it as? PagingModel.RowWithHeaders }
                ?.headers
                ?: emptyList()
        }
    }

    val stickyColumn by remember {
        derivedStateOf {
            headers.mapIndexedNotNull { index, header ->
                if (header.isStickyColumn) index else null
            }
        }
    }

    val columns by remember {
        derivedStateOf {
            headers.mapIndexedNotNull { index, header ->
                if (!header.isStickyColumn) index else null
            }
        }
    }

    fun getColumnWidth(columnIndex: Int): Int? {
        return headers.getOrNull(columnIndex)?.config?.cellWidthInDp
    }


    Column(modifier) {
        // Top Row (Static Top Left + Scrollable Headers)
        Row(modifier = Modifier.fillMaxWidth()) {
            stickyColumn.forEach { index ->
                val width = getColumnWidth(index) ?: config.defaultCellWidth
                val header = headers[index]

                ColumnHeader(
                    header = header,
                    horizontalScrollState = null,
                    width = width.dp,
                    columnHeight = columnHeight,
                    headerCellStyle = headerCellStyle,
                    columnHeaderBackground = columnHeaderBackground,
                    columnHeaderDividerColor = columnHeaderDividerColor,
                    columnHeaderContentAlignment =columnHeaderContentAlignment,
                    onHeaderActionTriggered = { header, action->
                        lastAction.value = Pair(header,action)
                        onHeaderActionTriggered?.invoke(header,action)
                    }
                )
            }

            Row(
                modifier = Modifier.horizontalScroll(horizontalScrollState),
            ) {
                columns.forEach { index ->
                    val width = getColumnWidth(index) ?: config.defaultCellWidth
                    val header = headers[index]

                    ColumnHeader(
                        header = header,
                        horizontalScrollState = horizontalScrollState,
                        width = width.dp,
                        columnHeight = columnHeight,
                        headerCellStyle = headerCellStyle,
                        columnHeaderBackground = columnHeaderBackground,
                        columnHeaderDividerColor = columnHeaderDividerColor,
                        columnHeaderContentAlignment =columnHeaderContentAlignment,
                        onHeaderActionTriggered = { header, action->
                            lastAction.value = Pair(header,action)
                            onHeaderActionTriggered?.invoke(header,action)
                        }
                    )
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
            items(count = pagingRows.itemCount){ index ->
                val row = pagingRows[index]?.row ?: return@items

                Row {

                    if(stickyColumn.isEmpty().not()){

                        stickyColumn.forEach { columnIndex ->
                            val cell = row.cells[columnIndex]
                            val width = getColumnWidth(columnIndex) ?: config.defaultCellWidth
                            val columnWidth = width.dp

                            Cell(
                                cell = cell,
                                row = row,
                                columnWidth = columnWidth,
                                columnHeight = columnHeight,
                                background = rowHeaderBackground,
                                verticalCellDividerColor = horizontalCellDividerColor,
                                contentAlignment = rowHeaderContentAlignment,
                                cellStyle = cellStyle,
                                onCellLongPress = onCellLongPress,
                                onCellAction = onCellAction
                            )
                        }

                    }

                    // Scrollable Row Cells
                    Row(
                        modifier = Modifier
                            .horizontalScroll(horizontalScrollState)
                    ) {
                        columns.forEach {  columnIndex ->
                            val cell = row.cells[columnIndex]
                            val width = getColumnWidth(columnIndex) ?: config.defaultCellWidth
                            val columnWidth = width.dp

                            Cell(
                                cell = cell,
                                row = row,
                                columnWidth = columnWidth,
                                columnHeight = columnHeight,
                                background = dataBoxColor,
                                verticalCellDividerColor = verticalCellDividerColor,
                                contentAlignment = dataBoxContentAlignment,
                                cellStyle = cellStyle,
                                onCellLongPress = onCellLongPress,
                                onCellAction = onCellAction
                            )
                        }
                    }
                }
            }

            if (pagingRows.loadState.append is LoadState.Loading) {
                item {
                    CircularProgressIndicator(
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    }
}

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
    dataUpdatePolicy: DataUpdatePolicy = DataUpdatePolicy.NONE,
    onCellLongPress: ((Row)-> Unit)? = null,
    onCellAction: ((CellAction)-> Unit)? = null,
    onHeaderActionTriggered: ((Header, ColumnAction) -> Unit)? = null
) {
    val horizontalScrollState = rememberScrollState()
    val verticalScrollState = rememberLazyListState()
    val buttonStyle = getButtonStyle()
    val cellStyle by remember { mutableStateOf(CellStyle(
        textStyle = dataTextStyle(),
        buttonStyle =buttonStyle
    )) }

    val rowsIds = remember { mutableStateOf(setOf<UUID>()) }
    val lastAction = remember { mutableStateOf<Pair<Header, ColumnAction>?>(null) }

    LaunchedEffect(key1 = table.rows) {
        val newRowsIds = table.rows.map { it.uuid }.toSet()

        if(rowsIds.value == newRowsIds) return@LaunchedEffect

        rowsIds.value = newRowsIds

        when(dataUpdatePolicy){
            DataUpdatePolicy.NONE -> return@LaunchedEffect
            DataUpdatePolicy.RETRIGGER_LAST_COLUMN_ACTION -> {
                lastAction.value?.let {
                    onHeaderActionTriggered?.invoke(it.first, it.second)
                }
            }
        }
    }

    val headerCellStyle by remember { mutableStateOf(CellStyle(
        textStyle = columnHeaderTextStyle(),
        buttonStyle = buttonStyle
    )) }

    val columnHeight = config.defaultHeightInDp.dp

    val stickyColumn by remember {
        derivedStateOf {
            table.columnHeaders.mapIndexedNotNull { index, header ->
                if(header.isStickyColumn) index else null }
        }
    }

    val columns by remember {
        derivedStateOf {
            table.columnHeaders.mapIndexedNotNull { index, header ->
                if (header.isStickyColumn.not()) index else null
            }
        }
    }


    Column(modifier) {
        // Top Row (Static Top Left + Scrollable Headers)
        Row(modifier = Modifier.fillMaxWidth()) {
            stickyColumn.forEach { index ->
                val width = table.getColumnWidth(index) ?: config.defaultCellWidth
                val header = table.columnHeaders[index]

                ColumnHeader(
                    header = header,
                    horizontalScrollState = null,
                    width = width.dp,
                    columnHeight = columnHeight,
                    headerCellStyle = headerCellStyle,
                    columnHeaderBackground = columnHeaderBackground,
                    columnHeaderDividerColor = columnHeaderDividerColor,
                    columnHeaderContentAlignment =columnHeaderContentAlignment,
                    onHeaderActionTriggered = { header, action->
                        lastAction.value = Pair(header,action)
                        onHeaderActionTriggered?.invoke(header,action)
                    }
                )
            }

            Row(
                modifier = Modifier.horizontalScroll(horizontalScrollState),
            ) {
                columns.forEach { index ->
                    val width = table.getColumnWidth(index) ?: config.defaultCellWidth
                    val header = table.columnHeaders[index]

                    ColumnHeader(
                        header = header,
                        horizontalScrollState = horizontalScrollState,
                        width = width.dp,
                        columnHeight = columnHeight,
                        headerCellStyle = headerCellStyle,
                        columnHeaderBackground = columnHeaderBackground,
                        columnHeaderDividerColor = columnHeaderDividerColor,
                        columnHeaderContentAlignment =columnHeaderContentAlignment,
                        onHeaderActionTriggered = { header, action->
                            lastAction.value = Pair(header,action)
                            onHeaderActionTriggered?.invoke(header,action)
                        }
                    )
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

                    if(stickyColumn.isEmpty().not()){

                        stickyColumn.forEach {
                            val cell = row.cells[it]
                            val width = table.getColumnWidth(it) ?: config.defaultCellWidth
                            val columnWidth = width.dp

                            Cell(
                                cell = cell,
                                row = row,
                                columnWidth = columnWidth,
                                columnHeight = columnHeight,
                                background = rowHeaderBackground,
                                verticalCellDividerColor = horizontalCellDividerColor,
                                contentAlignment = rowHeaderContentAlignment,
                                cellStyle = cellStyle,
                                onCellLongPress = onCellLongPress,
                                onCellAction = onCellAction
                            )
                        }

                    }

                    // Scrollable Row Cells
                    Row(
                        modifier = Modifier
                            .horizontalScroll(horizontalScrollState)
                    ) {
                        columns.forEach {
                            val cell = row.cells.get(it)

                            val width = table.getColumnWidth(it) ?: config.defaultCellWidth
                            val columnWidth = width.dp

                            Cell(
                                cell = cell,
                                row = row,
                                columnWidth = columnWidth,
                                columnHeight = columnHeight,
                                background = dataBoxColor,
                                verticalCellDividerColor = verticalCellDividerColor,
                                contentAlignment = dataBoxContentAlignment,
                                cellStyle = cellStyle,
                                onCellLongPress = onCellLongPress,
                                onCellAction = onCellAction
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ColumnHeader(
    header: Header,
    horizontalScrollState: ScrollState?,
    width: Dp,
    columnHeight: Dp,
    headerCellStyle: CellStyle,
    columnHeaderBackground: Color,
    columnHeaderDividerColor: Color?,
    columnHeaderContentAlignment: Alignment,
    onHeaderActionTriggered: ((Header, ColumnAction) -> Unit)?
){
    Box(
        modifier = Modifier
            .width(width)
            .height(columnHeight)
            .background(columnHeaderBackground)
            .border(
                width = if (columnHeaderDividerColor != null) 0.5.dp else 0.dp,
                color = columnHeaderDividerColor ?: Color.Transparent,
                shape = RectangleShape // Ensures the border is applied to the rectangle
            ).run {
                if(horizontalScrollState != null) horizontalScroll(horizontalScrollState) else this
            },
        contentAlignment = columnHeaderContentAlignment,
    ) {
        ColumnHeader(
            modifier = Modifier.width(width),
            header = header,
            cellStyle = headerCellStyle,
            onHeaderActionTriggered ={ header, action->
                onHeaderActionTriggered?.invoke(header,action)
            }
        )
    }
}

@Composable
private fun Cell(
    cell: Cell,
    row: Row,
    columnWidth: Dp,
    columnHeight: Dp,
    cellStyle: CellStyle,
    background: Color,
    verticalCellDividerColor: Color?,
    contentAlignment: Alignment,
    onCellLongPress: ((Row)-> Unit)? = null,
    onCellAction: ((CellAction)-> Unit)?
)
{
    Box(
        modifier = Modifier
            .width(columnWidth)
            .height(columnHeight)
            .background(background)
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
        contentAlignment = contentAlignment,
    ) {
        key(cell.coordinate, cell.uuid) {
            cell.Render(
                cellStyle = cellStyle,
                onCellAction = onCellAction
            )
        }
    }
}