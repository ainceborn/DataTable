package com.rainc.compose.datatable.model

import java.io.Serializable

data class Base64IconInfo(
    val iconName: String,
    val icon: String,
    val iconSize: Int,
    val contentDescription: String? = null
): Serializable