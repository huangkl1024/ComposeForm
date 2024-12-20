package com.github.huangkl1024.composeform.component

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Cancel
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun <T> OutlinedSelect(
    label: @Composable () -> Unit,
    options: List<T>,
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
    var expanded by remember { mutableStateOf(false) }
    var focused by remember { mutableStateOf(false) }
    val showValue by remember(value) {
        if(value == null) {
            mutableStateOf("")
        } else {
            mutableStateOf(convertOption2String(value))
        }
    }

    val focusRequester = FocusRequester()
    val focusManager = LocalFocusManager.current

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = {
            if (enabled) {
                // 启用点击才生效
                expanded = it
            }
        },
        modifier = modifier.onFocusChanged {
            focused = it.isFocused
        }
    ) {
        OutlinedTextField(
            // The `menuAnchor` modifier must be passed to the text field to handle
            // expanding/collapsing the menu on click. A read-only text field has
            // the anchor type `PrimaryNotEditable`.
            modifier = Modifier
                .menuAnchor(MenuAnchorType.PrimaryNotEditable)
                .fillMaxWidth()
                .focusRequester(focusRequester),
            value = showValue,
            onValueChange = {},
            readOnly = true,
            singleLine = true,
            label = label,
            trailingIcon = {
                if (expanded || showValue.isEmpty()) {
                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                } else {
                    if (canCancel && enabled) {
                        // 启用才显示取消按钮
                        IconButton({
                            onValueChange(null)
                            expanded = false
                        }) {
                            Icon(Icons.Outlined.Cancel, contentDescription = "Cancel value button")
                        }
                    }
                }
            },
            colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(),
            isError = isError,
            supportingText = supportingText,
            enabled = enabled
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
        ) {
            if (options.isEmpty()) {
                DropdownMenuItem(
                    text = {
                        Column(Modifier.fillMaxWidth()) {
                            Text("空")
                        }
                    },
                    onClick = {},
                    contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding,
                )
            } else {
                options.forEach { option ->
                    DropdownMenuItem(
                        text = {
                            renderOption(option)
                        },
                        onClick = {
                            expanded = false
                            onValueChange(option)
                            focusManager.clearFocus()
                        },
                        contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding,
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
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

    // The text that the user inputs into the text field can be used to filter the options.
    // This sample uses string subsequence matching.
    val filteredOptions = if (showValue != textFieldValueOfShowValue.text)
        optionsFilter(options, textFieldValueOfShowValue.text)
    else
        options
    val (allowExpanded, setExpanded) = remember { mutableStateOf(false) }
    val expanded = allowExpanded && filteredOptions.isNotEmpty()

    var focus by remember { mutableStateOf(false) }
    if (!focus && showValue != textFieldValueOfShowValue.text) {
        textFieldValueOfShowValue = TextFieldValue(showValue, TextRange(showValue.length))
    }

    val focusRequester = FocusRequester()
    val focusManager = LocalFocusManager.current


    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = {
            if (enabled) {
                setExpanded(it)
            }
        },
        modifier = modifier.onFocusChanged {
            focus = it.isFocused
        }
    ) {
        OutlinedTextField(
            // The `menuAnchor` modifier must be passed to the text field to handle
            // expanding/collapsing the menu on click. An editable text field has
            // the anchor type `PrimaryEditable`.
            modifier = Modifier
                .menuAnchor(MenuAnchorType.PrimaryEditable)
                .fillMaxWidth()
                .focusRequester(focusRequester),
            value = textFieldValueOfShowValue,
            onValueChange = { textFieldValueOfShowValue = it },
            singleLine = true,
            label = label,
            trailingIcon = {
                if (expanded || showValue.isEmpty()) {
                    ExposedDropdownMenuDefaults.TrailingIcon(
                        expanded = expanded,
                        // If the text field is editable, it is recommended to make the
                        // trailing icon a `menuAnchor` of type `SecondaryEditable`. This
                        // provides a better experience for certain accessibility services
                        // to choose a menu option without typing.
                        modifier = Modifier.menuAnchor(MenuAnchorType.SecondaryEditable),
                    )
                } else {
                    if (canCancel && enabled) {
                        IconButton({
                            onValueChange(null)
                            setExpanded(false)
                        }) {
                            Icon(Icons.Outlined.Cancel, contentDescription = "Cancel value button")
                        }
                    }

                }
            },
            colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(),
            isError = isError,
            enabled = enabled,
            supportingText = supportingText
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = {
                setExpanded(false)
            },
        ) {
            filteredOptions.forEach { option ->
                DropdownMenuItem(
                    text = {
                        renderOption(option)
                    },
                    onClick = {
                        val text = convertOption2String(option)
                        textFieldValueOfShowValue =
                            TextFieldValue(
                                text = text,
                                selection = TextRange(text.length),
                            )
                        setExpanded(false)
                        onValueChange(option)
                        focusManager.clearFocus()
                    },
                    contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding,
                )
            }
        }
    }
}