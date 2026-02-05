package com.rainc.compose.datatable.model

import androidx.compose.runtime.Immutable
import java.io.Serializable

@Immutable
data class Coordinate(val x: Int, val y: Int) : Serializable