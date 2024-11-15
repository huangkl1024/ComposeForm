package com.github.huangkl1024.composeform.component

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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

interface SelectOption {
    /**
     * 获取显示值
     */
    fun getShowValue(): String
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun <T : SelectOption> OutlinedSelect(
    label: @Composable () -> Unit,
    options: List<T>,
    value: SelectOption?,
    onValueChange: (T?) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    isError: Boolean = false,
    supportingText: @Composable (() -> Unit)? = null,
) {
    var expanded by remember { mutableStateOf(false) }
    var focused by remember { mutableStateOf(false) }
    var selectedValue by remember { mutableStateOf(value) }

    val focusRequester = FocusRequester()
    val focusManager = LocalFocusManager.current

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = {
            if(enabled) {
                // 启用点击才生效
                expanded = it
            }
        },
        modifier = modifier.onFocusChanged {
            focused = it.isFocused
        }
    ) {
        val textValue = selectedValue?.getShowValue() ?: ""
        OutlinedTextField(
            // The `menuAnchor` modifier must be passed to the text field to handle
            // expanding/collapsing the menu on click. A read-only text field has
            // the anchor type `PrimaryNotEditable`.
            modifier = Modifier
                .menuAnchor(MenuAnchorType.PrimaryNotEditable)
                .fillMaxWidth()
                .focusRequester(focusRequester),
            value = textValue,
            onValueChange = {},
            readOnly = true,
            singleLine = true,
            label = label,
            trailingIcon = {
                if (expanded || textValue.isEmpty()) {
                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                } else {
                    IconButton({
                        selectedValue = null
                        onValueChange(null)
                        expanded = false
                    }, enabled = enabled) {
                        Icon(Icons.Filled.Cancel, contentDescription = "Cancel value button")
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
                            Text(
                                option.getShowValue(),
                                style = MaterialTheme.typography.bodyLarge
                            )
                        },
                        onClick = {
                            selectedValue = option
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

fun <T : SelectOption> toTextField(option: T?): TextFieldValue {
    val showValue = option?.getShowValue() ?: ""
    return TextFieldValue(showValue, TextRange(showValue.length))
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun <T : SelectOption> SearchOutlinedSelect(
    label: @Composable () -> Unit,
    options: List<T>,
    optionsFilter: (List<T>, String) -> List<T>,
    value: T?,
    onValueChange: (T?) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    isError: Boolean = false,
    supportingText: @Composable (() -> Unit)? = null,
) {

    var selectedValue by remember { mutableStateOf(value) }
    var selectedValueTextField by remember(selectedValue) {
        mutableStateOf(toTextField(selectedValue))
    }

    // The text that the user inputs into the text field can be used to filter the options.
    // This sample uses string subsequence matching.
    val filteredOptions = if (selectedValue?.getShowValue() != selectedValueTextField.text)
        optionsFilter(options, selectedValueTextField.text)
    else
        options
    val (allowExpanded, setExpanded) = remember { mutableStateOf(false) }
    val expanded = allowExpanded && filteredOptions.isNotEmpty()

    var focus by remember { mutableStateOf(false) }
    if (!focus && selectedValue?.getShowValue() != selectedValueTextField.text) {
        selectedValueTextField = toTextField(selectedValue)
    }

    val focusRequester = FocusRequester()
    val focusManager = LocalFocusManager.current


    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = {
            if(enabled) {
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
            value = selectedValueTextField,
            onValueChange = { selectedValueTextField = it },
            singleLine = true,
            label = label,
            trailingIcon = {
                if (expanded || selectedValue == null) {
                    ExposedDropdownMenuDefaults.TrailingIcon(
                        expanded = expanded,
                        // If the text field is editable, it is recommended to make the
                        // trailing icon a `menuAnchor` of type `SecondaryEditable`. This
                        // provides a better experience for certain accessibility services
                        // to choose a menu option without typing.
                        modifier = Modifier.menuAnchor(MenuAnchorType.SecondaryEditable),
                    )
                } else {
                    IconButton({
                        selectedValue = null
                        onValueChange(null)
                        setExpanded(false)
                    }, enabled = enabled) {
                        Icon(Icons.Filled.Cancel, contentDescription = "Cancel value button")
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
                        Text(
                            option.getShowValue(),
                            style = MaterialTheme.typography.bodyLarge
                        )
                    },
                    onClick = {
                        selectedValueTextField =
                            TextFieldValue(
                                text = option.getShowValue(),
                                selection = TextRange(option.getShowValue().length),
                            )
                        setExpanded(false)
                        selectedValue = option
                        onValueChange(option)
                        focusManager.clearFocus()
                    },
                    contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding,
                )
            }
        }
    }
}