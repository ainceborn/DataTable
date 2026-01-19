package com.rainc.compose.datatable

import androidx.compose.material3.ButtonDefaults
import androidx.compose.runtime.Composable
import com.rainc.compose.datatable.model.ButtonStyle

@Composable
fun getButtonStyle(): ButtonStyle{
   return  ButtonStyle(
        shape = ButtonDefaults.shape,
        colors = ButtonDefaults.buttonColors(),
        elevation = ButtonDefaults.buttonElevation(),
        border = null,
        contentPadding = ButtonDefaults.ContentPadding,
    )
}