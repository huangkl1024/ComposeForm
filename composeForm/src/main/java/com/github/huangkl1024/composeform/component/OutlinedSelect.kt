package com.github.huangkl1024.composeform.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.TextFieldColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun <T> OutlinedSelect(
    options: List<T>,
    renderOption: @Composable (T) -> Unit,
    convertOption2String: (option: T) -> String,
    value: T?,
    onValueChange: (T?) -> Unit,
    trailingIconTint: Color = LocalContentColor.current,
    canCancel: Boolean = true,
    emptyOptionsShow: @Composable () -> Unit = {},

    // OutlinedFieldText props
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    textStyle: TextStyle = LocalTextStyle.current,
    label: @Composable (() -> Unit)? = null,
    placeholder: @Composable (() -> Unit)? = null,
    leadingIcon: @Composable (() -> Unit)? = null,
    prefix: @Composable (() -> Unit)? = null,
    suffix: @Composable (() -> Unit)? = null,
    supportingText: @Composable (() -> Unit)? = null,
    isError: Boolean = false,
    shape: Shape = OutlinedTextFieldDefaults.shape,
    colors: TextFieldColors = OutlinedTextFieldDefaults.colors()
) {
    var expanded by remember { mutableStateOf(false) }
    var focused by remember { mutableStateOf(false) }
    val showValue by remember(value) {
        if (value == null) {
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
        modifier = Modifier.onFocusChanged {
            focused = it.isFocused
        }
    ) {
        OutlinedTextField(
            // The `menuAnchor` modifier must be passed to the text field to handle
            // expanding/collapsing the menu on click. A read-only text field has
            // the anchor type `PrimaryNotEditable`.
            modifier = modifier
                .menuAnchor(MenuAnchorType.PrimaryNotEditable)
                .fillMaxWidth()
                .focusRequester(focusRequester),
            value = showValue,
            onValueChange = {},
            readOnly = true,
            singleLine = true,
            label = label,
            trailingIcon = {
                Icon(
                    imageVector = Icons.Filled.ArrowDropDown,
                    tint = trailingIconTint,
                    contentDescription = null,
                    modifier = Modifier.rotate(if (expanded) 180f else 0f)
                )
            },
            colors = colors,
            isError = isError,
            supportingText = supportingText,
            enabled = enabled,
            shape = shape,
            leadingIcon = leadingIcon,
            placeholder = placeholder,
            prefix = prefix,
            suffix = suffix,
            textStyle = textStyle
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            shape = shape
        ) {
            if (options.isEmpty()) {
                DropdownMenuItem(
                    text = {
                        Surface(
                            Modifier
                                .fillMaxWidth()
                                .height(20.dp)
                        ) {
                            emptyOptionsShow()
                        }
                    },
                    onClick = {},
                    contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding,
                )
            } else {
                options.forEach { option ->
                    val isSelected = option == value
                    DropdownMenuItem(
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(min = 48.dp)
                            .clip(MaterialTheme.shapes.small)
                            .background(
                                color = if (isSelected) {
                                    MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)
                                } else {
                                    Color.Transparent
                                }
                            ),
                        text = {
                            renderOption(option)
                        },
                        onClick = {
                            expanded = false
                            if (isSelected && canCancel) {
                                onValueChange(null)
                            } else {
                                onValueChange(option)
                            }
                            focusManager.clearFocus()
                        },
                        contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding,
                    )
                }
            }
        }
    }
}