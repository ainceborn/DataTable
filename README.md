## DataTable [Compose] [![](https://jitpack.io/v/ainceborn/DataTable.svg)](https://jitpack.io/#ainceborn/DataTable)

A Jetpack Compose data-table/grid library with two rendering modes: a fully-materialized `DataTable` (you own the whole `Table` in memory) and a `PaginationDataTable` backed by AndroidX Paging 3.

```kotlin
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        mavenCentral()
        maven { url = uri("https://jitpack.io") }
    }
}
```

```kotlin
dependencies {
    implementation("com.github.ainceborn:DataTable:1.0.9")
}
```

## Core model

| Type | What it is |
|---|---|
| `Table` | `columnHeaders: List<Header>` + `rows: List<Row>`. The whole grid, for `DataTable`. |
| `Row` | `uuid`, `index`, `cells: List<Cell>`, `isSelected: Boolean`. `cells[i]` corresponds to `columnHeaders[i]` / `Header.index`. |
| `Header` | `index`, `title`, `isStickyColumn`, `config: ColumnConfig` (column width), `action: ColumnAction` (e.g. `Sort`). |
| `Cell` | Interface every cell type implements — `uuid`, `coordinate`, `hasError`, `attr: CellAttributes`, `sortKeyValue`, and a `@Composable Render(...)`. |
| `TableConfig` | `defaultHeightInDp` (row height), `defaultCellWidth`. Build with `defaultTableConfig()`. |
| `PagingModel` | Paging-only wrapper around a `Row` — either `PagingRow` or `RowWithHeaders` (headers embedded in the first page's item, used only when you don't pass `headers` explicitly to `PaginationDataTable`). |

### Cell types

| Type | Description |
|---|---|
| `TextCell` | Read-only text display. |
| `ButtonCell` | Basic button. |
| `IconButtonCell` | Button with an icon resolved from `Base64IconInfo`. |
| `SwitchCell` | Material3 `Switch`. Appearance controlled via `SwitchStyle`. |
| `RadioButtonCell` | Single radio option. |
| `SegmentControl` | Row of `FilterChip` items backed by `ChipGroup<String>`. Appearance controlled via `ChipStyle`. |
| `DatePickerCell` | Tappable text that triggers a date picker dialog. |
| `DurationPickerCell` | Tappable text that triggers a duration picker dialog. |

All live in `com.rainc.compose.datatable.cell` and implement `Cell`. Each is an immutable `data class`; to change a cell's value you build a new instance via `.copy(...)`, not mutate it in place (see **Live cell updates** below).

### CellAttributes

Every cell carries `attr: CellAttributes`:

```kotlin
data class CellAttributes(
    val textColor: Int? = null,       // ARGB override for cell text; null = inherit from dataTextStyle
    val style: String = "",           // reserved for host-app styling hints
    val genericAttributes: Bundle = Bundle(), // arbitrary key-value extras
    val contentPadding: PaddingValues = PaddingValues(0.dp), // per-cell padding override
    val isEditable: Boolean = true,   // false = disabled state for interactive cells
)
```

### ErrorStyle

Controls the appearance of error indicators:

```kotlin
data class ErrorStyle(
    val borderColor: Color = Color(0xFFBB0000),       // border around cells with hasError = true
    val backgroundColor: Color = Color(0xFFFFF0F0),   // fill of error cells (when showErrorCellBackground = true)
    val indicatorColor: Color = Color(0xFFBB0000),     // leading edge bar color for error rows
    val indicatorWidth: Dp = 5.dp,
)
```

### SwitchStyle

Wraps Material3 `SwitchColors` for full theme control of `SwitchCell`:

```kotlin
data class SwitchStyle(val colors: SwitchColors)
```

Pass to `DataTable`/`PaginationDataTable` via `switchStyle`. Falls back to `SwitchDefaults.colors()` when `null`.

### ChipStyle

Controls the appearance of every chip in `SegmentControl`:

```kotlin
data class ChipStyle(
    val containerColor: Color,
    val selectedContainerColor: Color,
    val labelColor: Color,
    val selectedLabelColor: Color,
    val checkmarkColor: Color,
    val borderColor: Color,
    val selectedBorderColor: Color,
    val borderWidth: Dp = 1.dp,
)
```

Pass to `DataTable`/`PaginationDataTable` via `chipStyle`. Falls back to `MaterialTheme.colorScheme` tokens when `null`.

## Basic usage — `DataTable`

You own the `Table` and push new ones as data changes; `DataTable` just renders whatever `State<Table>` you give it.

```kotlin
val tableState = remember {
    mutableStateOf(
        Table(
            columnHeaders = listOf(
                Header(index = 0, title = "Name", action = ColumnAction.None),
                Header(index = 1, title = "Active", action = ColumnAction.None),
            ),
            rows = listOf(
                Row(
                    index = 0,
                    cells = listOf(
                        TextCell(text = "Alice", coordinate = Coordinate(0, 0)),
                        SwitchCell(value = true, coordinate = Coordinate(1, 0)),
                    )
                )
            )
        )
    )
}

DataTable(
    modifier = Modifier.fillMaxSize(),
    table = tableState,
    config = defaultTableConfig(cellHeight = 56),
    onCellAction = { action ->
        when (action) {
            is CellAction.ToggleBoolean -> { /* rebuild row/cell, push new Table */ }
            else -> Unit
        }
    }
)
```

## Pagination usage — `PaginationDataTable`

Implement `PageApi` — one method, called by Paging 3 whenever it needs page `page` (1-based):

```kotlin
class MyPageApi(private val scope: CoroutineScope) : PageApi {
    override fun pageCount(page: Int): Deferred<Result<List<PagingModel>>> =
        scope.async {
            runCatching {
                val rows = myBackend.fetchPage(page)
                rows.map { PagingModel.PagingRow(it) }
            }
        }
}
```

Wrap it in the library's `PagingSource` and build a `Pager`:

```kotlin
val pagingDataFlow: Flow<PagingData<PagingModel>> =
    Pager(
        config = PagingConfig(pageSize = 20, enablePlaceholders = false),
        pagingSourceFactory = { object : PagingSource(myPageApi, pageCount = 12) {} }
    ).flow.cachedIn(viewModelScope)

PaginationDataTable(
    modifier = Modifier.fillMaxSize(),
    paginationData = pagingDataFlow,
    headers = myColumnHeaders,
    config = defaultTableConfig(),
)
```

## Parameters

Both composables share this set:

| Parameter | Type | Default | Description |
|---|---|---|---|
| `modifier` | `Modifier` | `Modifier` | Modifier for the whole table. |
| `config` | `TableConfig` | `defaultTableConfig()` | Row height / default cell width. |
| `columnHeaderBackground` | `Color` | `Color.LightGray` | Header row background. |
| `columnHeaderTextStyle` | `() -> TextStyle` | white 14sp | Header text style. |
| `columnHeaderContentAlignment` | `Alignment` | `Center` | Header cell content alignment. |
| `columnHeaderDividerColor` | `Color?` | `null` | Divider below the header row. |
| `columnHeaderHeight` | `Dp?` | `null` (= data row height) | Header row height override. |
| `rowHeaderBackground` | `Color` | `Color.LightGray` | Background for cells in sticky leading columns. |
| `rowHeaderContentAlignment` | `Alignment` | `Center` | Content alignment for sticky leading cells. |
| `dataBoxColor` | `Color` | `Color.White` | Background for non-sticky data cells. |
| `dataBoxContentAlignment` | `Alignment` | `Center` | Content alignment for data cells. |
| `dataTextStyle` | `() -> TextStyle` | black 14sp | Text style passed to all data cells via `CellStyle`. |
| `errorStyle` | `ErrorStyle` | see `ErrorStyle` | Error indicator/border/background colors and widths. |
| `horizontalCellDividerColor` | `Color?` | `null` | Horizontal divider between rows. |
| `verticalCellDividerColor` | `Color?` | `null` | Vertical divider between columns. |
| `headerElevation` | `Dp` | `0.dp` | Shadow elevation of the header row (useful when `clearFocusOnTap = true` and the table scrolls under it). |
| `defaultCellContentPadding` | `PaddingValues` | `8dp h / 4dp v` | Default padding inside each cell; per-cell `CellAttributes.contentPadding` overrides this. |
| `showErrorCellBackground` | `Boolean` | `false` | When `true`, fills cells with `hasError = true` using `errorStyle.backgroundColor`. |
| `clearFocusOnTap` | `Boolean` | `false` | Clears keyboard focus when the user taps outside an active text field (useful inside `RecyclerView` embeddings). |
| `switchStyle` | `SwitchStyle?` | `null` | Color overrides for `SwitchCell`. `null` = `SwitchDefaults.colors()`. |
| `chipStyle` | `ChipStyle?` | `null` | Color overrides for `SegmentControl` chips. `null` = `MaterialTheme` defaults. |
| `cellBackgroundProvider` | `((Cell) -> Color?)?` | `null` | Per-cell background override. Return `null` to fall through to `dataBoxColor`/`rowHeaderBackground`. |
| `rowBackgroundProvider` | `((Row) -> Color?)?` | `null` | Per-row background override, takes precedence over `selectedRowBackground`. Return `null` to fall through. |
| `selectedRowBackground` | `Color` | `Color(0x330061A8)` | Background for rows where `isSelected == true`. |
| `onRowSelectionToggle` | `((Row) -> Unit)?` | `null` | Fired when the per-row checkbox is toggled. |
| `onSelectAllToggle` | `((Boolean) -> Unit)?` | `null` | Fired when the header "select all" checkbox is toggled. |
| `selectionColumnWidth` | `Dp` | `48.dp` | Width of the checkbox selection column. |
| `showHeaderRow` | `Boolean` | `true` | Show/hide the column header row. |
| `sortIconProvider` | `(SortMode) -> UIIcon` | built-in icon | Custom sort icon per sort state. |
| `onCellLongPress` | `((Row) -> Unit)?` | `null` | Fired on long-press of any cell in the row. |
| `onCellAction` | `((CellAction) -> Unit)?` | `null` | Text edits, toggles, button presses, date/duration picks, segment changes, list-picker actions. |
| `onHeaderActionTriggered` | `((Header, ColumnAction) -> Unit)?` | `null` | Sort header taps, etc. |
| `rootComposeView` | `AbstractComposeView?` | `null` | Pass the hosting `ComposeView` when embedding inside a `RecyclerView`/legacy view hierarchy for correct nested-scroll interop. |

`PaginationDataTable`-only:

| Parameter | Type | Default | Description |
|---|---|---|---|
| `paginationData` | `Flow<PagingData<PagingModel>>` | — | From `Pager(...).flow`. |
| `headers` | `List<Header>?` | `null` | Explicit column headers. Strongly recommended — see **Headers on the paginated path** below. |
| `rowOverrides` | `State<Map<UUID, Row>>` | empty | Live-patch overlay for already-loaded rows. See **Live-patching a paginated row** below. |
| `rowFilter` | `((Row) -> Boolean)?` | `null` | Client-side filter applied after page load. |
| `rowComparator` | `Comparator<Row>?` | `null` | Client-side sort applied after page load. |
| `dataUpdatePolicy` | `DataUpdatePolicy` | `NONE` | `RETRIGGER_LAST_COLUMN_ACTION` re-fires the last header action when the row set changes. |
| `progressBar` | `@Composable () -> Unit` | `CircularProgressIndicator` | Shown as the last list item while the next page is loading. |

## Important notes

### Live cell updates require a stable `uuid`
`Cell` implementations key their internal Compose state with `remember(value) { ... }` — keyed on the **incoming field**, not the `uuid`. This means:
- Pushing a new `Cell` instance with the **same `uuid`** but a **different value** correctly updates the rendered cell.
- Pushing a cell with a **new `uuid`** tears down and rebuilds that cell's composition from scratch (loses any transient local state, e.g. an in-progress text selection).

Keep the `uuid` stable for programmatic value pushes. Only mint a new `uuid` if you want the cell's Compose state reset.

### Row selection has no dedicated "selection mode" flag
The checkbox column only renders once **at least one** `Row.isSelected == true`. On `onCellLongPress`, set that row's `isSelected = true` to reveal the column, then let subsequent taps flow through `onRowSelectionToggle`/`onSelectAllToggle`. Exiting selection mode means resetting every row back to `isSelected = false`.

### Headers on the paginated path
`PaginationDataTable` can auto-detect headers from the first page's `PagingModel.RowWithHeaders` item, but that only works once the first page arrives — an empty-first-page state would render with no header row. Pass `headers` explicitly whenever you know the columns up front (the common case).

### Live-patching a paginated row (`rowOverrides`)
Paging 3 owns its own item cache; you can't mutate a loaded `Row` and expect the list to notice. `rowOverrides: State<Map<UUID, Row>>` is the escape hatch — consulted everywhere a page item is read (rendering, error state, selection) without going through `PagingSource`/`invalidate()`. Keep a `MutableStateFlow<Map<UUID, Row>>` and merge a patched `Row` into it on any live update (cell edit, selection toggle, server push, etc.).

### `PagingSource`'s `pageCount` is optional
`PagingSource(api, pageCount: Int? = null)`. Pass the real total if known — pagination stops once the requested page exceeds it. With `null`, the **only** end-of-data signal is an empty page. Make sure your `PageApi` returns an empty list once there's nothing left; a non-empty fallback will make Paging 3 request pages forever.

### Long-press vs. interactive cell content
`onCellLongPress` is detected on the `Initial` pointer pass and cancels the rest of the gesture, so it coexists correctly with interactive cell content (`TextField`, `Button`, `Switch`, chip rows, etc.) instead of being silently swallowed.

### `Table` extensions
`Table.sort(header, sortAction): Table` and `Table.filter(predicate: (Row) -> Boolean): Table` — both in `com.rainc.compose.datatable.tools`, both return a new `Table`. `LazyColumn` keys rows by `uuid`, so pushing a filtered/sorted `Table` diffs correctly.

## Migrating from 1.0.8

- `ColumnConfig.cellHeightInDp` was **removed** — row height is controlled solely by `TableConfig.defaultHeightInDp` (data rows) and `columnHeaderHeight` (header row).
- `PagingSource`'s constructor changed from `(api, pageCount: Int)` to `(api, pageCount: Int? = null)` — existing call sites passing an `Int` keep compiling as-is.
