package com.rainc.compose.datatable.view

import android.R
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimeInput
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Compose24hTimePicker(
    show: Boolean,
    initialHour: Int = 12,
    initialMinute: Int = 0,
    onTimeSelected: (hour: Int, minute: Int) -> Unit,
    onDismiss: () -> Unit
) {
    var selectedHour by remember { mutableStateOf(initialHour) }
    var selectedMinute by remember { mutableStateOf(initialMinute) }

    val timePickerState = rememberTimePickerState(
        initialHour = selectedHour,
        initialMinute = selectedMinute,
        is24Hour = true,
    )

    if(show == false){
        return
    }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = MaterialTheme.shapes.medium,
            tonalElevation = 8.dp,
            modifier = Modifier.padding(16.dp)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(16.dp)
            ) {

                Spacer(modifier = Modifier.height(50.dp))

                TimeInput(
                    state = timePickerState,
                )

                Row(
                    horizontalArrangement = Arrangement.End,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    TextButton(onClick = onDismiss) {
                        Text(text= stringResource(R.string.cancel))
                    }
                    Spacer(modifier = Modifier.width(32.dp))
                    TextButton(onClick = {
                        onTimeSelected(timePickerState.hour, timePickerState.minute)
                        onDismiss()
                    }) {
                        Text(text= stringResource(R.string.ok))
                    }
                }
            }
        }
    }
}
