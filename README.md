## 🎨 DataTable [Compose] [![](https://jitpack.io/v/ainceborn/DataTable.svg)](https://jitpack.io/#ainceborn/DataTable)

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

## 🧱 Core model

| Type | What it is |
|---|---|
| `Table` | `columnHeaders: List<Header>` + `rows: List<Row>`. The whole grid, for `DataTable`. |
| `Row` | `uuid`, `index`, `cells: List<Cell>`, `isSelected: Boolean`. `cells[i]` corresponds to `columnHeaders[i]` / `Header.index`. |
| `Header` | `index`, `title`, `isStickyColumn`, `config: ColumnConfig` (column width), `action: ColumnAction` (e.g. `Sort`). |
| `Cell` | Interface every cell type implements — `uuid`, `coordinate`, `hasError`, `attr: CellAttributes`, `sortKeyValue`, and a `@Composable Render(...)`. |
| `TableConfig` | `defaultHeightInDp` (row height), `defaultCellWidth`. Build with `defaultTableConfig()`. |
| `PagingModel` | Paging-only wrapper around a `Row` — either `PagingRow` or `RowWithHeaders` (headers embedded in the first page's item, used only when you don't pass `headers` explicitly to `PaginationDataTable`). |

### Cell types

`TextCell`, `ButtonCell`, `IconButtonCell`, `SwitchCell`, `RadioButtonCell`, `SegmentControl`, `DatePickerCell`, `DurationPickerCell` — all live in `com.rainc.compose.datatable.cell` and implement `Cell`. Each is an immutable `data class`; to change a cell's value you build a new instance via `.copy(...)`, not mutate it in place (see **Live cell updates** below).

## 🚀 Basic usage — `DataTable`

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
            is CellAction.ToggleBoolean -> {
                // rebuild the affected row/cell and push a new Table into tableState
            }
            else -> Unit
        }
    }
)
```

## 📄 Pagination usage — `PaginationDataTable`

Implement `PageApi` — one method, called by Paging 3 whenever it needs page `page` (1-based):

```kotlin
class MyPageApi(private val scope: CoroutineScope) : PageApi {
    override fun pageCount(page: Int): Deferred<Result<List<PagingModel>>> =
        scope.async {
            runCatching {
                val rows = myBackend.fetchPage(page) // your own paging call
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
        // pageCount: Int? — pass the real total if you know it up front, or leave it null and
        // rely on an empty page as the end-of-data signal (see "Nullable pageCount" below).
        pagingSourceFactory = { object : PagingSource(myPageApi, pageCount = 12) {} }
    ).flow.cachedIn(viewModelScope)

PaginationDataTable(
    modifier = Modifier.fillMaxSize(),
    paginationData = pagingDataFlow,
    headers = myColumnHeaders, // explicit — see "Headers on the paginated path" below
    config = defaultTableConfig(),
)
```

## 🎛️ Parameters

Both composables share this set:

| Parameter | Description |
|---|---|
| `modifier` | Modifier for the whole table. |
| `config` | `TableConfig` — row height / default cell width. Build with `defaultTableConfig()`. |
| `columnHeaderBackground` / `columnHeaderContentAlignment` / `columnHeaderTextStyle` | Header row styling. |
| `rowHeaderBackground` / `rowHeaderContentAlignment` | Styling for cells in sticky (leading) columns. |
| `dataBoxColor` / `dataBoxContentAlignment` / `dataTextStyle` | Styling for non-sticky data cells. |
| `errorColor` / `rowErrorIndicationWidth` | Color/width of the leading error-indicator bar, shown for any row containing a cell with `hasError = true`. |
| `horizontalCellDividerColor` / `verticalCellDividerColor` / `columnHeaderDividerColor` | Divider colors (optional). |
| `dataUpdatePolicy` | `DataUpdatePolicy.NONE` or `RETRIGGER_LAST_COLUMN_ACTION` — re-fires the last header action (e.g. re-apply sort) when the row set changes. |
| `sortIconProvider` | `(SortMode) -> UIIcon` — custom sort icon per header. |
| `onCellLongPress` | `(Row) -> Unit` — fired on long-press of any cell in the row. |
| `onCellAction` | `(CellAction) -> Unit` — text edits, toggles, button presses, date/duration picks, segment changes, list-picker/unspecified actions. |
| `onHeaderActionTriggered` | `(Header, ColumnAction) -> Unit` — sort clicks, etc. |
| `rootComposeView` | Pass the hosting `ComposeView` when embedding inside a `RecyclerView`/legacy view hierarchy, so touch/nested-scroll interop works correctly. |
| `showHeaderRow` | Hide the header row entirely. Default `true`. |
| `columnHeaderHeight` | Header row height, independent of `config.defaultHeightInDp` (data row height). `null` = same as data rows. |
| `selectedRowBackground` / `onRowSelectionToggle` / `onSelectAllToggle` / `selectionColumnWidth` | Row selection — see **Row selection** below. |
| `rowBackgroundProvider` | `(Row) -> Color?` — per-row background override, takes precedence over `selectedRowBackground`. Return `null` to fall through to the default. |

`PaginationDataTable`-only:

| Parameter | Description |
|---|---|
| `paginationData` | `Flow<PagingData<PagingModel>>` — from `Pager(...).flow`. |
| `headers` | Explicit `List<Header>`. Strongly recommended — see **Headers on the paginated path** below. |
| `rowOverrides` | `State<Map<UUID, Row>>` — live-patch overlay for already-loaded rows. See **Live-patching a paginated row** below. |
| `progressBar` | `@Composable () -> Unit` shown as the last list item while the next page is loading (`loadState.append is LoadState.Loading`). Defaults to a plain `CircularProgressIndicator`. |

## ⚠️ Important notes

### Live cell updates require a stable `uuid`
`Cell` implementations key their internal Compose state (`TextField` value, `Switch` checked state, etc.) with `remember(value) { ... }` / `remember(data) { ... }` — keyed on the **incoming field**, not the `uuid`. This means:
- Pushing a new `Cell` instance with the **same `uuid`** but a **different value** correctly updates the rendered cell (as of 1.0.9 — see Changelog).
- Pushing a cell with a **new `uuid`** tears down and rebuilds that cell's composition from scratch (loses any transient local state, e.g. an in-progress text selection).

So: for a server-driven/programmatic value push, keep the `uuid` the same. Only mint a new `uuid` if you actually want the cell's Compose state reset.

### Row selection has no dedicated "selection mode" flag
The checkbox column (header + per-row) only renders once **at least one** `Row.isSelected == true`. There's no separate on/off switch for "selection mode" — if you want a long-press to *enter* selection mode with nothing pre-selected, handle that yourself: on `onCellLongPress`, set that one row's `isSelected = true` (which reveals the column), and let subsequent taps flow through `onRowSelectionToggle`/`onSelectAllToggle` as usual. Exiting selection mode means resetting every row back to `isSelected = false`.

### Headers on the paginated path
`PaginationDataTable` can auto-detect headers from the first page's `PagingModel.RowWithHeaders` item, but that only works once the first page has actually arrived — a valid "no data yet" / empty-first-page state would otherwise render with **no header row at all**. Pass `headers` explicitly whenever you already know the columns up front (the common case); the library falls back to auto-detection only when `headers == null`.

### Live-patching a paginated row (`rowOverrides`)
Paging 3 owns its own item cache/diffing; you can't just mutate a `Row` you got back from a loaded page and expect the list to notice. `rowOverrides: State<Map<UUID, Row>>` (keyed by `Row.uuid`) is the escape hatch — when present, it's consulted everywhere a page item is read (cell rendering, `hasError`/`errorRows`, `hasSelection`/`allSelected`), **without** going through `PagingSource`/`invalidate()` or a network refetch. Typical usage: keep your own `MutableStateFlow<Map<UUID, Row>>` (or `mutableStateOf`), and merge a patched `Row` into it whenever you get a live update (cell edit pushed from a server, local selection toggle, etc.) for a row that's already loaded.

### `PagingSource`'s `pageCount` is optional
`PagingSource(api, pageCount: Int? = null)`. Pass the real total if your backend reports it — pagination stops as soon as the requested page exceeds it. Leave it `null` if the total isn't known up front; in that case the **only** end-of-data signal is an empty page (`pageCount(page)` resolving to an empty list). Either way, make sure your `PageApi` implementation actually returns an empty list once there's nothing left — a "loop back to page 1" or similarly non-empty fallback response will make Paging 3 request pages forever.

### Long-press vs. interactive cell content
`onCellLongPress` is detected on the `Initial` pointer pass and independently cancels/consumes the rest of the gesture once it fires, so it correctly coexists with interactive cell content (`TextField`, `Button`, `Switch`, list-picker rows, etc.) instead of being silently swallowed by it.

### `Table` extensions
`Table.sort(header, sortAction): Table` and `Table.filter(predicate: (Row) -> Boolean): Table` — both in `com.rainc.compose.datatable.tools`, both return a new `Table` (no in-place mutation). `LazyColumn` in `DataTable` keys rows by `uuid`, so pushing a filtered/sorted `Table` diffs correctly without extra bookkeeping.

## 🔄 Migrating from 1.0.8

- `ColumnConfig.cellHeightInDp` was **removed** — row height is controlled solely by `TableConfig.defaultHeightInDp` (data rows) and `columnHeaderHeight` (header row, defaults to the same value).
- `PagingSource`'s constructor changed from `(api, pageCount: Int)` to `(api, pageCount: Int? = null)` — existing call sites passing an `Int` keep compiling as-is.
