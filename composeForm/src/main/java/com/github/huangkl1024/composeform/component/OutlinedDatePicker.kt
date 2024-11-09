package com.github.huangkl1024.composeform.component

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.Cancel
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.platform.LocalFocusManager
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OutlinedDatePicker(
    value: LocalDate?,
    onValueChange: (LocalDate?) -> Unit,
    dateFormat: (LocalDate?) -> String,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    isError: Boolean = false,
    supportingText: @Composable (() -> Unit)? = null,
    label: @Composable (() -> Unit)? = null,
    confirmButtonText: String = "OK",
    dismissButtonText: String = "Cancel"
) {
    val focusRequester = FocusRequester()
    val focusManager = LocalFocusManager.current
    var showDatePickerDialog by remember { mutableStateOf(false) }
    if (showDatePickerDialog) {
        val initialSelectedDateMillis = remember(value) {
            if (value != null) {
                val dateTime = LocalDateTime(
                    value.year, value.month,
                    value.dayOfMonth, 0, 0, 0
                )
                dateTime.toInstant(TimeZone.currentSystemDefault()).toEpochMilliseconds()
            } else {
                null
            }
        }
        val datePickerState = rememberDatePickerState(initialSelectedDateMillis)
        val confirmEnabled = remember {
            derivedStateOf { datePickerState.selectedDateMillis != null }
        }
        DatePickerDialog(
            onDismissRequest = {
                showDatePickerDialog = false
                focusManager.clearFocus()
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        showDatePickerDialog = false
                        focusManager.clearFocus()
                        val selectedDate =
                            Instant.fromEpochMilliseconds(datePickerState.selectedDateMillis!!)
                                .toLocalDateTime(TimeZone.currentSystemDefault()).date
                        onValueChange(selectedDate)
                    },
                    enabled = confirmEnabled.value
                ) {
                    Text(confirmButtonText)
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showDatePickerDialog = false
                        focusManager.clearFocus()
                    }
                ) {
                    Text(dismissButtonText)
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    var trailingIcon: @Composable () -> Unit =
        { Icon(Icons.Outlined.CalendarMonth, contentDescription = "Calendar month icon") }
    if (value != null) {
        trailingIcon = {
            IconButton({ onValueChange(null) }) {
                Icon(Icons.Outlined.Cancel, contentDescription = "Time cancel icon button")
            }
        }
    }
    OutlinedTextField(
        label = label,
        value = dateFormat(value),
        onValueChange = {},
        readOnly = true,
        enabled = enabled,
        isError = isError,
        supportingText = supportingText,
        modifier = modifier
            .focusRequester(focusRequester)
            .onFocusChanged {
                if (it.isFocused) {
                    showDatePickerDialog = true
                }
            },
        trailingIcon = trailingIcon
    )
}