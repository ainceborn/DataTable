package com.rainc.compose.datatable.tools

import com.rainc.compose.datatable.ColumnAction
import com.rainc.compose.datatable.ColumnAction.Sort.SortMode.*
import com.rainc.compose.datatable.model.Header
import com.rainc.compose.datatable.model.Table

fun Table.sort(header: Header, sortAction: ColumnAction.Sort) : Table{
    val rows = when(sortAction.mode){
        ASCENDING -> rows.sortedBy { row ->
            row.cells[header.index].sortKeyValue
        }
        DESCENDING -> rows.sortedByDescending { row ->
            row.cells[header.index].sortKeyValue
        }
        NONE ->  rows.sortedBy { it.index }
    }

    return copy(
        columnHeaders = columnHeaders.mapIndexed { index, it ->
            if (it.action is ColumnAction.Sort){
                if(index == header.index){
                    it.copy(action =  sortAction)
                }
                else it.copy(action =  ColumnAction.Sort(mode = NONE))
            } else it
        },
        rows = rows
    )
}