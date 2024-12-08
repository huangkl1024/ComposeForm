package com.github.huangkl1024.composeform.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Cancel
import androidx.compose.material.icons.outlined.Timer
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import kotlinx.datetime.LocalTime


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OutlinedTimePicker(
    value: LocalTime?,
    onValueChange: (LocalTime?) -> Unit,
    timeFormat: (LocalTime?) -> String,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    isError: Boolean = false,
    supportingText: @Composable (() -> Unit)? = null,
    label: @Composable (() -> Unit)? = null,
    is24Hour: Boolean = true,
    confirmButtonText: String = "OK",
    dismissButtonText: String = "Cancel"
) {
    val focusRequester = FocusRequester()
    val focusManager = LocalFocusManager.current

    var showTimePickerDialog by remember { mutableStateOf(false) }
    if (showTimePickerDialog) {
        val initHour = value?.hour ?: 0
        val initialMinute = value?.minute ?: 0
        val timePickerState = rememberTimePickerState(initHour, initialMinute, is24Hour)
        TimePickerDialog(
            onDismissRequest = {
                showTimePickerDialog = false
                focusManager.clearFocus()
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        showTimePickerDialog = false
                        focusManager.clearFocus()
                        val selectedTime =
                            LocalTime(timePickerState.hour, timePickerState.minute)
                        onValueChange(selectedTime)
                    },
                ) {
                    Text(confirmButtonText)
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showTimePickerDialog = false
                        focusManager.clearFocus()
                    }
                ) {
                    Text(dismissButtonText)
                }
            },
            content = {
                TimePicker(state = timePickerState)
            }
        )
    }

    var trailingIcon: @Composable () -> Unit = {
        Icon(Icons.Outlined.Timer, contentDescription = "Time icon")
    }
    if (value != null) {
        trailingIcon = {
            if(enabled) {
                IconButton({ onValueChange(null) }) {
                    Icon(Icons.Outlined.Cancel, contentDescription = "Time cancel icon button")
                }
            }
        }
    }
    OutlinedTextField(
        label = label,
        value = timeFormat(value),
        onValueChange = {},
        readOnly = true,
        enabled = enabled,
        isError = isError,
        supportingText = supportingText,
        modifier = modifier
            .focusRequester(focusRequester)
            .onFocusChanged {
                if (enabled && it.isFocused) {
                    showTimePickerDialog = true
                }
            },
        trailingIcon = trailingIcon
    )
}

@Composable
fun TimePickerDialog(
    onDismissRequest: () -> Unit,
    confirmButton: @Composable () -> Unit,
    dismissButton: @Composable () -> Unit,
    content: @Composable () -> Unit,
    modifier: Modifier = Modifier,
) {
    Dialog(
        onDismissRequest = onDismissRequest,
        properties = DialogProperties(usePlatformDefaultWidth = false),
    ) {
        Surface(
            shape = MaterialTheme.shapes.extraLarge,
            tonalElevation = 6.dp,
            modifier =
            modifier
                .width(IntrinsicSize.Min)
                .height(IntrinsicSize.Min)
                .background(
                    shape = MaterialTheme.shapes.extraLarge,
                    color = MaterialTheme.colorScheme.surface
                ),
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                content()
                Row(
                    modifier = Modifier
                        .height(40.dp)
                        .fillMaxWidth()
                ) {
                    Spacer(modifier = Modifier.weight(1f))
                    dismissButton()
                    confirmButton()
                }
            }
        }
    }
}
