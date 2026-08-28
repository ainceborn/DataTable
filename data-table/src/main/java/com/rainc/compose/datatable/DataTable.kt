package com.rainc.compose.datatable

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.rememberNestedScrollInteropConnection
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
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
import com.rainc.compose.datatable.model.UIIcon
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
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
    errorColor: Color = Color.Red,
    rowErrorIndicationWidth: Dp = 5.dp,
    horizontalCellDividerColor: Color? = null,
    verticalCellDividerColor: Color? = null,
    columnHeaderDividerColor: Color? = null,
    dataUpdatePolicy: DataUpdatePolicy = DataUpdatePolicy.NONE,
    sortIconProvider: (sortMode: ColumnAction.Sort.SortMode) -> UIIcon= { UIIcon.ResourceIcon(android.R.drawable.ic_menu_sort_by_size) },
    onCellLongPress: ((Row)-> Unit)? = null,
    onCellAction: ((CellAction)-> Unit)? = null,
    onHeaderActionTriggered: ((Header, ColumnAction) -> Unit)? = null,
    rootComposeView: ComposeView? = null, // for integration with RecyclerView
    selectedRowBackground: Color = Color(0x330061A8),
    onRowSelectionToggle: ((Row) -> Unit)? = null,
    selectionColumnWidth: Dp = 48.dp,
    onSelectAllToggle: ((Boolean) -> Unit)? = null,
    headerSelectionContent: @Composable (allSelected: Boolean, onToggle: (Boolean) -> Unit) -> Unit =
        { allSelected, onToggle -> Checkbox(checked = allSelected, onCheckedChange = onToggle) },
    rowSelectionContent: @Composable (row: Row, onToggle: (Row) -> Unit) -> Unit =
        { row, onToggle -> Checkbox(checked = row.isSelected, onCheckedChange = { onToggle(row) }) },
    showHeaderRow: Boolean = true,
    columnHeaderHeight: Dp? = null,
    headers: List<Header>? = null,
    rowBackgroundProvider: ((Row) -> Color?)? = null,
    cellBackgroundProvider: ((Cell) -> Color?)? = null,
    rowFilter: ((Row) -> Boolean)? = null,
    rowComparator: Comparator<Row>? = null,
    rowOverrides: State<Map<UUID, Row>> = remember { mutableStateOf(emptyMap()) },
    progressBar: @Composable () -> Unit = {
        CircularProgressIndicator(modifier = Modifier.size(40.dp))
    },
    headerElevation: Dp = 0.dp,
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

    // Resolves a loaded page item to its locally-patched Row, when one exists in [rowOverrides].
    // This is how a live cell/segment/selection update reaches an already-loaded page without
    // going through PagingSource — Paging3's own cache/diffing never needs to be touched.
    fun resolveRow(model: PagingModel?): Row? {
        val base = model?.row ?: return null
        return rowOverrides.value[base.uuid] ?: base
    }

    val errorRows by remember {
        derivedStateOf {
            pagingRows.itemSnapshotList.items
                .mapNotNull { resolveRow(it) }
                .filter { row -> row.cells.any { cell -> cell.hasError } }
                .map { it.uuid }
                .toSet()
        }
    }

    val hasError by remember {
        derivedStateOf {
            errorRows.isNotEmpty()
        }
    }

    val hasSelection by remember {
        derivedStateOf {
            pagingRows.itemSnapshotList.items.any { resolveRow(it)?.isSelected == true }
        }
    }

    val allSelected by remember {
        derivedStateOf {
            val items = pagingRows.itemSnapshotList.items
            items.isNotEmpty() && items.all { resolveRow(it)?.isSelected == true }
        }
    }

    val sortedRows: List<Row>? = if (rowComparator != null) {
        remember(pagingRows.itemSnapshotList, rowComparator, rowOverrides.value) {
            pagingRows.itemSnapshotList.items
                .mapNotNull { resolveRow(it) }
                .sortedWith(rowComparator)
        }
    } else null

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
    val headerRowHeight = columnHeaderHeight ?: columnHeight

    // `headers` param wins when supplied — the auto-detected fallback below only sees headers
    // once the first page has data, so a valid "no data yet" / empty-first-page state would
    // otherwise render with no header row at all.
    val resolvedHeaders by remember(headers) {
        derivedStateOf {
            headers ?: pagingRows.itemSnapshotList.items
                .firstOrNull()
                ?.let { it as? PagingModel.RowWithHeaders }
                ?.headers
                ?: emptyList()
        }
    }

    // Keyed on `resolvedHeaders` itself (not just `headers`) — `resolvedHeaders` above is backed
    // by a keyed remember(headers), which swaps in a BRAND NEW State object whenever `headers`
    // changes. An unkeyed remember{} here would permanently close over the very first such State
    // object (from the first composition) and never see later ones, silently freezing
    // stickyColumn/columns at whatever `resolvedHeaders` was on first render.
    val stickyColumn by remember(resolvedHeaders) {
        derivedStateOf {
            resolvedHeaders.mapIndexedNotNull { index, header ->
                if (header.isStickyColumn) index else null
            }
        }
    }

    val columns by remember(resolvedHeaders) {
        derivedStateOf {
            resolvedHeaders.mapIndexedNotNull { index, header ->
                if (!header.isStickyColumn) index else null
            }
        }
    }

    fun getColumnWidth(columnIndex: Int): Int? {
        return resolvedHeaders.getOrNull(columnIndex)?.config?.cellWidthInDp
    }


    Column(modifier) {
        // Top Row (Static Top Left + Scrollable Headers)
        if (showHeaderRow) {
            Row(modifier = Modifier.fillMaxWidth().shadow(elevation = headerElevation).zIndex(1f)) {
                if(hasError){
                    Box(
                        modifier = Modifier
                            .height(headerRowHeight)
                            .width(rowErrorIndicationWidth)
                    )
                }
                if(hasSelection){
                    Box(
                        modifier = Modifier
                            .width(selectionColumnWidth)
                            .height(headerRowHeight)
                            .background(columnHeaderBackground),
                        contentAlignment = Alignment.Center
                    ) {
                        headerSelectionContent(allSelected) { onSelectAllToggle?.invoke(it) }
                    }
                }
                stickyColumn.forEach { index ->
                    val width = getColumnWidth(index) ?: config.defaultCellWidth
                    val header = resolvedHeaders[index]

                    ColumnHeader(
                        header = header,
                        horizontalScrollState = null,
                        width = width.dp,
                        columnHeight = headerRowHeight,
                        headerCellStyle = headerCellStyle,
                        columnHeaderBackground = columnHeaderBackground,
                        columnHeaderDividerColor = columnHeaderDividerColor,
                        columnHeaderContentAlignment = columnHeaderContentAlignment,
                        sortIconProvider = sortIconProvider,
                        onHeaderActionTriggered = { header, action ->
                            lastAction.value = Pair(header, action)
                            onHeaderActionTriggered?.invoke(header, action)
                        }
                    )
                }

                Row(
                    modifier = Modifier.horizontalScroll(horizontalScrollState),
                ) {
                    columns.forEach { index ->
                        val width = getColumnWidth(index) ?: config.defaultCellWidth
                        val header = resolvedHeaders[index]

                        ColumnHeader(
                            header = header,
                            horizontalScrollState = horizontalScrollState,
                            width = width.dp,
                            columnHeight = headerRowHeight,
                            headerCellStyle = headerCellStyle,
                            columnHeaderBackground = columnHeaderBackground,
                            columnHeaderDividerColor = columnHeaderDividerColor,
                            columnHeaderContentAlignment = columnHeaderContentAlignment,
                            sortIconProvider = sortIconProvider
                        ) { header, action ->
                            lastAction.value = Pair(header, action)
                            onHeaderActionTriggered?.invoke(header, action)
                        }
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
            modifier = Modifier.fillMaxSize().run{
                if(rootComposeView != null){
                    this.nestedScroll(rememberNestedScrollInteropConnection())
                        .pointerInput(Unit) {
                            awaitPointerEventScope {
                                while (true) {
                                    val event = awaitPointerEvent()
                                    if (event.changes.any { it.pressed }) {
                                        rootComposeView.parent?.requestDisallowInterceptTouchEvent(true)
                                    }
                                }
                            }
                        }
                } else this
            }
        ) {
            // Always use items(count) so pagingRows[index] fires on every visible slot,
            // keeping Paging3 prefetch hints alive regardless of sort state.
            // When sorted, render from sortedRows[index] instead of the paged order.
            items(count = pagingRows.itemCount) { index ->
                // Side-effect: triggers Paging3 prefetch for this index.
                val pagedRow = resolveRow(pagingRows[index])
                val row = sortedRows?.getOrNull(index) ?: pagedRow ?: return@items
                if (rowFilter?.invoke(row) == false) return@items

                if(hasError){
                    Box(
                        modifier = Modifier
                            .height(columnHeight)
                            .width(rowErrorIndicationWidth)
                            .background(if(errorRows.contains(row.uuid)) errorColor else Color.Transparent)
                    )
                }

                val rowIsSelected = row.isSelected
                val customRowBackground = rowBackgroundProvider?.invoke(row)
                val rowHeaderCellBackground = customRowBackground
                    ?: if (rowIsSelected) selectedRowBackground else rowHeaderBackground
                val dataCellBackground = customRowBackground
                    ?: if (rowIsSelected) selectedRowBackground else dataBoxColor

                Row {
                    if(hasSelection){
                        Box(
                            modifier = Modifier
                                .width(selectionColumnWidth)
                                .height(columnHeight)
                                .background(rowHeaderCellBackground),
                            contentAlignment = Alignment.Center
                        ) {
                            rowSelectionContent(row) { onRowSelectionToggle?.invoke(it) }
                        }
                    }
                    if(stickyColumn.isEmpty().not()){
                        stickyColumn.forEach { columnIndex ->
                            val cell = row.cells[columnIndex]
                            val width = getColumnWidth(columnIndex) ?: config.defaultCellWidth
                            Cell(
                                cell = cell,
                                row = row,
                                columnWidth = width.dp,
                                columnHeight = columnHeight,
                                background = rowHeaderCellBackground,
                                verticalCellDividerColor = horizontalCellDividerColor,
                                contentAlignment = rowHeaderContentAlignment,
                                cellStyle = cellStyle,
                                errorColor = errorColor,
                                cellBackgroundProvider = cellBackgroundProvider,
                                onCellLongPress = onCellLongPress,
                                onCellAction = onCellAction
                            )
                        }
                    }
                    Row(modifier = Modifier.horizontalScroll(horizontalScrollState)) {
                        columns.forEach { columnIndex ->
                            val cell = row.cells[columnIndex]
                            val width = getColumnWidth(columnIndex) ?: config.defaultCellWidth
                            Cell(
                                cell = cell,
                                row = row,
                                columnWidth = width.dp,
                                columnHeight = columnHeight,
                                background = dataCellBackground,
                                verticalCellDividerColor = verticalCellDividerColor,
                                contentAlignment = dataBoxContentAlignment,
                                cellStyle = cellStyle,
                                errorColor = errorColor,
                                cellBackgroundProvider = cellBackgroundProvider,
                                onCellLongPress = onCellLongPress,
                                onCellAction = onCellAction
                            )
                        }
                    }
                }
            }

            if (pagingRows.loadState.append is LoadState.Loading) {
                item { progressBar.invoke() }
            }
        }
    }
}

@Composable
fun DataTable(
    modifier: Modifier = Modifier,
    table: State<Table>,
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
    errorColor: Color = Color.Red,
    rowErrorIndicationWidth: Dp = 5.dp,
    horizontalCellDividerColor: Color? = null,
    verticalCellDividerColor: Color? = null,
    columnHeaderDividerColor: Color? = null,
    dataUpdatePolicy: DataUpdatePolicy = DataUpdatePolicy.NONE,
    sortIconProvider: (sortMode: ColumnAction.Sort.SortMode) -> UIIcon= { UIIcon.ResourceIcon(android.R.drawable.ic_menu_sort_by_size) },
    onCellLongPress: ((Row)-> Unit)? = null,
    onCellAction: ((CellAction)-> Unit)? = null,
    onHeaderActionTriggered: ((Header, ColumnAction) -> Unit)? = null,
    rootComposeView: ComposeView? = null, // for integration with RecyclerView
    selectedRowBackground: Color = Color(0x330061A8),
    onRowSelectionToggle: ((Row) -> Unit)? = null,
    selectionColumnWidth: Dp = 48.dp,
    onSelectAllToggle: ((Boolean) -> Unit)? = null,
    headerSelectionContent: @Composable (allSelected: Boolean, onToggle: (Boolean) -> Unit) -> Unit =
        { allSelected, onToggle -> Checkbox(checked = allSelected, onCheckedChange = onToggle) },
    rowSelectionContent: @Composable (row: Row, onToggle: (Row) -> Unit) -> Unit =
        { row, onToggle -> Checkbox(checked = row.isSelected, onCheckedChange = { onToggle(row) }) },
    showHeaderRow: Boolean = true,
    columnHeaderHeight: Dp? = null,
    rowBackgroundProvider: ((Row) -> Color?)? = null,
    cellBackgroundProvider: ((Cell) -> Color?)? = null,
    headerElevation: Dp = 0.dp,
) {
    val columnHeaders by remember { derivedStateOf { table.value.columnHeaders } }
    val hasError by remember { derivedStateOf { table.value.rows.any { row -> row.cells.any { cell -> cell.hasError } } } }

    val errorRows by remember {
        derivedStateOf {
            if(hasError) table.value.rows.mapNotNull { row ->
               val hasErrorInRow = row.cells.any { cell -> cell.hasError }
                if(hasErrorInRow) row.uuid else null
            }.toSet() else emptySet()
        }
    }

    val hasSelection by remember { derivedStateOf { table.value.rows.any { it.isSelected } } }
    val allSelected by remember { derivedStateOf { table.value.rows.isNotEmpty() && table.value.rows.all { it.isSelected } } }


    val horizontalScrollState = rememberScrollState()
    val verticalScrollState = rememberLazyListState()
    val buttonStyle = getButtonStyle()
    val cellStyle by remember { mutableStateOf(CellStyle(
        textStyle = dataTextStyle(),
        buttonStyle =buttonStyle
    )) }

    val rowsIds = remember { mutableStateOf(setOf<UUID>()) }
    val lastAction = remember { mutableStateOf<Pair<Header, ColumnAction>?>(null) }

    LaunchedEffect(key1 = table.value.rows) {
        val newRowsIds = table.value.rows.map { it.uuid }.toSet()

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
    val headerRowHeight = columnHeaderHeight ?: columnHeight

    val stickyColumn by remember {
        derivedStateOf {
            columnHeaders.mapIndexedNotNull { index, header ->
                index.takeIf { header.isStickyColumn }
            }
        }
    }

    val columns by remember {
        derivedStateOf {
            columnHeaders.mapIndexedNotNull { index, header ->
                index.takeIf { !header.isStickyColumn }
            }
        }
    }


    Column(modifier) {
        // Top Row (Static Top Left + Scrollable Headers)
        if (showHeaderRow) {
            Row(modifier = Modifier.fillMaxWidth().shadow(elevation = headerElevation).zIndex(1f)) {
                if(hasError){
                    Box(
                        modifier = Modifier
                            .height(headerRowHeight)
                            .width(rowErrorIndicationWidth)
                    )
                }
                if(hasSelection){
                    Box(
                        modifier = Modifier
                            .width(selectionColumnWidth)
                            .height(headerRowHeight)
                            .background(columnHeaderBackground),
                        contentAlignment = Alignment.Center
                    ) {
                        headerSelectionContent(allSelected) { onSelectAllToggle?.invoke(it) }
                    }
                }
                stickyColumn.forEach { index ->
                    val width = columnHeaders.getColumnWidth(index) ?: config.defaultCellWidth
                    val header = columnHeaders[index]

                    ColumnHeader(
                        header = header,
                        horizontalScrollState = null,
                        width = width.dp,
                        columnHeight = headerRowHeight,
                        headerCellStyle = headerCellStyle,
                        columnHeaderBackground = columnHeaderBackground,
                        columnHeaderDividerColor = columnHeaderDividerColor,
                        columnHeaderContentAlignment =columnHeaderContentAlignment,
                        sortIconProvider = sortIconProvider
                    ) { header, action ->
                        lastAction.value = Pair(header, action)
                        onHeaderActionTriggered?.invoke(header, action)
                    }
                }

                Row(
                    modifier = Modifier.horizontalScroll(horizontalScrollState),
                ) {
                    columns.forEach { index ->
                        val width = columnHeaders.getColumnWidth(index) ?: config.defaultCellWidth
                        val header = columnHeaders[index]

                        ColumnHeader(
                            header = header,
                            horizontalScrollState = horizontalScrollState,
                            width = width.dp,
                            columnHeight = headerRowHeight,
                            headerCellStyle = headerCellStyle,
                            columnHeaderBackground = columnHeaderBackground,
                            columnHeaderDividerColor = columnHeaderDividerColor,
                            columnHeaderContentAlignment =columnHeaderContentAlignment,
                            sortIconProvider = sortIconProvider
                        ) { header, action ->
                            lastAction.value = Pair(header, action)
                            onHeaderActionTriggered?.invoke(header, action)
                        }
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
            modifier = Modifier.fillMaxSize().run{
                if(rootComposeView != null){
                   this.nestedScroll(rememberNestedScrollInteropConnection())
                       .pointerInput(Unit) {
                           awaitPointerEventScope {
                               while (true) {
                                   val event = awaitPointerEvent()
                                   if (event.changes.any { it.pressed }) {
                                       rootComposeView.parent?.requestDisallowInterceptTouchEvent(true)
                                   }
                               }
                           }
                       }
                } else this
            },
        ) {
            items(items = table.value.rows, key = { item -> item.uuid }){ row ->
                val rowIsSelected = row.isSelected
                val customRowBackground = rowBackgroundProvider?.invoke(row)
                val rowHeaderCellBackground = customRowBackground
                    ?: if (rowIsSelected) selectedRowBackground else rowHeaderBackground
                val dataCellBackground = customRowBackground
                    ?: if (rowIsSelected) selectedRowBackground else dataBoxColor

                Row {
                    if(hasError){
                        Box(
                            modifier = Modifier
                                .height(columnHeight)
                                .width(rowErrorIndicationWidth)
                                .background(if(errorRows.contains(row.uuid)) errorColor else Color.Transparent)
                        )
                    }
                    if(hasSelection){
                        Box(
                            modifier = Modifier
                                .width(selectionColumnWidth)
                                .height(columnHeight)
                                .background(rowHeaderCellBackground),
                            contentAlignment = Alignment.Center
                        ) {
                            rowSelectionContent(row) { onRowSelectionToggle?.invoke(it) }
                        }
                    }
                    if(stickyColumn.isEmpty().not()){
                        stickyColumn.forEach {
                            val cell = row.cells[it]
                            val width = columnHeaders.getColumnWidth(it) ?: config.defaultCellWidth
                            val columnWidth = width.dp

                            Cell(
                                cell = cell,
                                row = row,
                                columnWidth = columnWidth,
                                columnHeight = columnHeight,
                                background = rowHeaderCellBackground,
                                verticalCellDividerColor = horizontalCellDividerColor,
                                contentAlignment = rowHeaderContentAlignment,
                                cellStyle = cellStyle,
                                errorColor = errorColor,
                                cellBackgroundProvider = cellBackgroundProvider,
                                onCellLongPress = onCellLongPress,
                                onCellAction = onCellAction,
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

                            val width = columnHeaders.getColumnWidth(it) ?: config.defaultCellWidth
                            val columnWidth = width.dp

                            Cell(
                                cell = cell,
                                row = row,
                                columnWidth = columnWidth,
                                columnHeight = columnHeight,
                                background = dataCellBackground,
                                verticalCellDividerColor = verticalCellDividerColor,
                                contentAlignment = dataBoxContentAlignment,
                                cellStyle = cellStyle,
                                errorColor = errorColor,
                                cellBackgroundProvider = cellBackgroundProvider,
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
    sortIconProvider: (ColumnAction.Sort.SortMode) -> UIIcon,
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
            sortIconProvider = sortIconProvider,
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
    errorColor: Color,
    verticalCellDividerColor: Color?,
    contentAlignment: Alignment,
    cellBackgroundProvider: ((Cell) -> Color?)? = null,
    onCellLongPress: ((Row)-> Unit)? = null,
    onCellAction: ((CellAction)-> Unit)?
)
{
    Box(
        modifier = Modifier
            .width(columnWidth)
            .height(columnHeight)
    ) {

        Box(
            modifier = Modifier
                .matchParentSize()
                .background(cellBackgroundProvider?.invoke(cell) ?: background)
                .border(
                    width = if (verticalCellDividerColor != null) 0.5.dp else 0.dp,
                    color = verticalCellDividerColor ?: Color.Transparent,
                    shape = RectangleShape
                )
                .pointerInput(row.uuid) {
                    // Detected on the Initial pass (top-down, before children) so interactive cell
                    // content (TextField, Button, Switch, clickable Row, ...) never gets a chance to
                    // consume the down event first and swallow the long press. The long-press callback
                    // is fired by an independent timer job launched on this (unrestricted) PointerInputScope
                    // — not by timing out the event-await loop itself: a synthetic/slow hold can leave
                    // awaitPointerEvent() suspended with no further events until lift-off, and cancelling
                    // that suspension via withTimeout only takes effect once the next event finally
                    // arrives, which can be well after the timeout and silently drops the long press.
                    // awaitPointerEventScope's receiver is a restricted suspend scope that can't itself
                    // launch child coroutines, so the loop (and the launch call) run in a plain
                    // coroutineScope { } sitting directly under this (unrestricted) PointerInputScope.
                    val longPressTimeoutMillis = viewConfiguration.longPressTimeoutMillis
                    val touchSlopSquared = viewConfiguration.touchSlop * viewConfiguration.touchSlop

                    coroutineScope {
                        while (true) {
                            val down = awaitPointerEventScope {
                                awaitFirstDown(requireUnconsumed = false, pass = PointerEventPass.Initial)
                            }

                            var longPressTriggered = false

                            val longPressJob = launch {
                                delay(longPressTimeoutMillis)
                                longPressTriggered = true
                                onCellLongPress?.invoke(row)
                            }

                            awaitPointerEventScope {
                                while (true) {
                                    val event = awaitPointerEvent(pass = PointerEventPass.Initial)
                                    val change = event.changes.firstOrNull { it.id == down.id } ?: break

                                    if (longPressTriggered) {
                                        // The long press already fired — consume the rest of this
                                        // gesture (including the eventual lift-off) so the cell's own
                                        // click handling (Button, Switch, clickable, ...) doesn't also
                                        // see a completed tap and fire its normal single-press action.
                                        change.consume()
                                        if (!change.pressed) break
                                        continue
                                    }

                                    if (!change.pressed) break

                                    val dx = change.position.x - down.position.x
                                    val dy = change.position.y - down.position.y
                                    if (dx * dx + dy * dy > touchSlopSquared) break
                                }
                            }

                            longPressJob.cancel()
                        }
                    }
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

        if (cell.hasError) {
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .border(
                        width = 2.dp,
                        color = errorColor,
                        shape = RectangleShape
                    )
                    .zIndex(8f)
            )
        }
    }
}