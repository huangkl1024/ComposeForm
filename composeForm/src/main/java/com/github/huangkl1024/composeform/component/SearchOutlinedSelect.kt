package com.github.huangkl1024.composeform.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.isImeVisible
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.Cancel
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun <T> SearchOutlinedSelect(
    label: @Composable () -> Unit,
    options: List<T>,
    optionsFilter: (List<T>, String) -> List<T>,
    renderOption: @Composable (T) -> Unit,
    convertOption2String: (option: T) -> String,
    value: T?,
    onValueChange: (T?) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    isError: Boolean = false,
    canCancel: Boolean = true,
    supportingText: @Composable (() -> Unit)? = null,
) {
    val showValue by remember(value) {
        if(value == null) {
            mutableStateOf("")
        } else {
            mutableStateOf(convertOption2String(value))
        }
    }
    var textFieldValueOfShowValue by remember(showValue) {
        val textField = TextFieldValue(showValue, TextRange(showValue.length))
        mutableStateOf(textField)
    }

    val filteredOptions = if (showValue != textFieldValueOfShowValue.text)
        optionsFilter(options, textFieldValueOfShowValue.text)
    else
        options

    val focusRequester = FocusRequester()
    val focusManager = LocalFocusManager.current
    var focus by remember { mutableStateOf(false) }
    var showBottomSheet by remember { mutableStateOf(false) }

    if (showBottomSheet) {
        ModalBottomSheet(
            onDismissRequest = { showBottomSheet = false },
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.onSurface,
        ) {
            Surface {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.Top
                ) {
                    var searchText by remember { mutableStateOf(TextFieldValue("")) }
                    val isImeVisible = WindowInsets.isImeVisible

                    LaunchedEffect(isImeVisible) {
                        if (isImeVisible) {
                            focusManager.clearFocus()
                        }
                    }

                    OutlinedTextField(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 8.dp),
                        value = searchText,
                        onValueChange = {
                            searchText = it
                            textFieldValueOfShowValue = it
                        },
                        singleLine = true,
                        leadingIcon = {
                            Icon(
                                Icons.Default.Search,
                                contentDescription = "Search"
                            )
                        },
                        trailingIcon = {
                            if (searchText.text.isNotEmpty()) {
                                IconButton(onClick = {
                                    searchText = TextFieldValue("")
                                    textFieldValueOfShowValue = TextFieldValue("")
                                }) {
                                    Icon(Icons.Outlined.Cancel, contentDescription = "Clear")
                                }
                            }
                        }
                    )

                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        contentPadding = PaddingValues(bottom = 80.dp)
                    ) {
                        items(filteredOptions) { option ->
                            ListItem(
                                modifier = Modifier.clickable {
                                    val text = convertOption2String(option)
                                    textFieldValueOfShowValue = TextFieldValue(
                                        text = text,
                                        selection = TextRange(text.length)
                                    )
                                    showBottomSheet = false
                                    onValueChange(option)
                                    focusManager.clearFocus()
                                },
                                headlineContent = { renderOption(option) }
                            )
                        }
                    }
                }
            }
        }
    }

    Column(modifier = modifier) {
        OutlinedTextField(
            modifier = Modifier
                .fillMaxWidth()
                .focusRequester(focusRequester)
                .onFocusChanged {
                    focus = it.isFocused
                    if (it.isFocused) {
                        showBottomSheet = true
                    }
                },
            value = textFieldValueOfShowValue,
            onValueChange = { },
            readOnly = true,
            singleLine = true,
            label = label,
            trailingIcon = {
                Row {
                    if (textFieldValueOfShowValue.text.isNotEmpty() && canCancel && enabled) {
                        IconButton(onClick = {
                            onValueChange(null)
                            textFieldValueOfShowValue = TextFieldValue("")
                        }) {
                            Icon(Icons.Outlined.Cancel, contentDescription = "Clear")
                        }
                    } else {
                        ExposedDropdownMenuDefaults.TrailingIcon(expanded = showBottomSheet)
                    }
                }
            },
            isError = isError,
            enabled = enabled,
            supportingText = supportingText
        )
    }
}