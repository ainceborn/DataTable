package com.rainc.sample.activity

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.rainc.compose.datatable.CellAction
import com.rainc.compose.datatable.ColumnAction
import com.rainc.compose.datatable.DataTable
import com.rainc.compose.datatable.ResourceResolver
import com.rainc.compose.datatable.cell.ButtonCell
import com.rainc.compose.datatable.cell.RadioButtonCell
import com.rainc.compose.datatable.cell.SegmentControl
import com.rainc.compose.datatable.cell.SwitchCell
import com.rainc.compose.datatable.cell.TextCell
import com.rainc.compose.datatable.defaultTableConfig
import com.rainc.compose.datatable.model.Coordinate
import com.rainc.compose.datatable.model.Header
import com.rainc.compose.datatable.model.Row
import com.rainc.compose.datatable.model.Table
import com.rainc.compose.datatable.tools.sort

class MainActivity : ComponentActivity() {

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        ResourceResolver.attachProvider(TableResourceProvider)

        val table = Table(
            columnHeaders =
                buildList {
                    for (i in 1..6) {
                        Header(index = i-1, title ="Column $i", action = when(i){
                            2,3,4 ->ColumnAction.Sort()
                            else -> ColumnAction.None
                        }).also { add(it) }
                    }
                }
            ,
            rows = listOf(
                Row(
                    index = 0,
                    cells = listOf(
                        ButtonCell("Cell 1", Coordinate(0, 0)),
                        TextCell("Cell 2", Coordinate(1, 0)),
                        TextCell("Cell 3", Coordinate(2, 0)),
                        TextCell("Cell 4", Coordinate(3, 0)),
                        TextCell("Cell 5", Coordinate(4, 0)),
                        TextCell("Cell 6", Coordinate(5, 0))
                    )
                ),
                Row(
                    index = 1,
                    cells = listOf(
                        TextCell("Cell 7", Coordinate(0, 1)),
                        TextCell("Cell 8", Coordinate(1, 1)),
                        ButtonCell("Cell 9", Coordinate(2, 1)),
                        TextCell("Cell 10", Coordinate(3, 1)),
                        TextCell("Cell 11", Coordinate(4, 1)),
                        TextCell("Cell 12", Coordinate(5, 1))
                    )
                ),
                Row(
                    index = 2,
                    cells = listOf(
                        TextCell("Cell 13", Coordinate(0, 2)),
                        TextCell("Cell 14", Coordinate(1, 2)),
                        TextCell("Cell 15", Coordinate(2, 2)),
                        TextCell("Cell 16", Coordinate(3, 2)),
                        ButtonCell("Cell 17", Coordinate(4, 2)),
                        TextCell("Cell 18", Coordinate(5, 2))
                    )
                ),
                Row(
                    index = 3,
                    cells = listOf(
                        TextCell("Cell 19", Coordinate(0, 3)),
                        ButtonCell("Cell 20", Coordinate(1, 3)),
                        TextCell("Cell 21", Coordinate(2, 3)),
                        TextCell("Cell 22", Coordinate(3, 3)),
                        TextCell("Cell 23", Coordinate(4, 3)),
                        TextCell("Cell 24", Coordinate(5, 3))
                    )
                ),
                Row(
                    index = 4,
                    cells = listOf(
                        TextCell("Cell 25", Coordinate(0, 4)),
                        TextCell("Cell 26", Coordinate(1, 4)),
                        ButtonCell("Cell 27", Coordinate(2, 4)),
                        TextCell("Cell 28", Coordinate(3, 4)),
                        TextCell("Cell 29", Coordinate(4, 4)),
                        TextCell("Cell 30", Coordinate(5, 4))
                    )
                ),
                Row(
                    index = 5,
                    cells = listOf(
                        TextCell("Cell 31", Coordinate(0, 5)),
                        TextCell("Cell 32", Coordinate(1, 5)),
                        TextCell("Cell 33", Coordinate(2, 5)),
                        ButtonCell("Cell 34", Coordinate(3, 5)),
                        TextCell("Cell 35", Coordinate(4, 5)),
                        TextCell("Cell 36", Coordinate(5, 5))
                    )
                ),
                Row(
                    index = 6,
                    cells = listOf(
                        TextCell("Cell 37", Coordinate(0, 6)),
                        TextCell("Cell 38", Coordinate(1, 6)),
                        TextCell("Cell 39", Coordinate(2, 6)),
                        ButtonCell("Cell 40", Coordinate(3, 6)),
                        TextCell("Cell 41", Coordinate(4, 6)),
                        TextCell("Cell 42", Coordinate(5, 6))
                    )
                ),
                Row(
                    index = 7,
                    cells = listOf(
                        TextCell("Cell 43", Coordinate(0, 7)),
                        TextCell("Cell 44", Coordinate(1, 7)),
                        TextCell("Cell 45", Coordinate(2, 7)),
                        TextCell("Cell 46", Coordinate(3, 7)),
                        TextCell("Cell 47", Coordinate(4, 7)),
                        TextCell("Cell 48", Coordinate(5, 7))
                    )
                )
            ),
            stickyRows = listOf()
        )


        setContent {
            enableEdgeToEdge()

            var tableState by remember { mutableStateOf(table) }

            Scaffold(modifier = Modifier
                .fillMaxSize()
                .padding(top = 24.dp)) { innerPadding ->
                DataTable(
                    modifier = Modifier.padding(innerPadding),
                    table = tableState,
                    config = defaultTableConfig(),
                    horizontalCellDividerColor = Color.Black,
                    verticalCellDividerColor = Color.Black,
                    columnHeaderDividerColor = Color.Black,
                    onCellAction = { action ->
                        when(action){
                            is CellAction.ButtonPressed -> {
                                when(action.cell){
                                    is ButtonCell -> {
                                        // TODO: Handle button press action
                                    }
                                    else -> return@DataTable
                                }
                            }
                            is CellAction.NewDateSelected -> {
                                // TODO: Handle new date selected action
                            }
                            is CellAction.SegmentStateChange.StringSegmentChange -> {
                                when(action.cell){
                                    is SegmentControl -> {
                                       // TODO: Handle segment control state change
                                    } else -> return@DataTable
                                }
                            }
                            is CellAction.SegmentStateChange.SerializableSegmentChange -> {
                                // TODO: Handle serializable segment change
                            }
                            is CellAction.ToggleBoolean -> {
                                when(action.cell){
                                    is RadioButtonCell -> {
                                        // TODO: Handle radio button toggle action
                                    }
                                    is SwitchCell -> {
                                        // TODO: Handle switch toggle action
                                    }
                                    else -> return@DataTable
                                }
                            }
                            is CellAction.UnspecifiedAction -> {
                                // TODO: Handle unspecified action
                            }
                            is CellAction.UpdateText -> {
                                // TODO: Handle text update action
                            }
                        }
                    },
                    onHeaderActionTriggered = { header, columnAction ->
                        when (columnAction) {
                            is ColumnAction.Sort -> {
                                tableState = tableState.sort(
                                    header = header,
                                    sortAction = columnAction
                                )
                            }
                            is ColumnAction.Unspecialized -> TODO()
                            ColumnAction.None -> TODO()
                        }
                    }
                )
            }

        }
    }

}