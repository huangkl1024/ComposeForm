package com.github.huangkl1024.composeform.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.isImeVisible
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.Cancel
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.TextFieldColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.background
import androidx.compose.material3.ListItemDefaults
import androidx.compose.runtime.derivedStateOf
import androidx.compose.ui.draw.clip

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun <T> SearchOutlinedSelect(
    options: List<T>,
    optionsFilter: (List<T>, String) -> List<T>,
    renderOption: @Composable (T) -> Unit,
    convertOption2String: (option: T) -> String,
    value: T?,
    onValueChange: (T?) -> Unit,
    trailingIconTint: Color = LocalContentColor.current,
    canCancel: Boolean = true,

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
    val showValue by remember(value) {
        if (value == null) {
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
                            .padding(bottom = 16.dp),
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
                        contentPadding = PaddingValues(bottom = 80.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        items(filteredOptions) { option ->
                            val isSelected = remember(value, option) {
                                derivedStateOf { value == option }
                            }
                            
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .heightIn(min = 48.dp)
                                    .clip(MaterialTheme.shapes.small)
                                    .background(
                                        color = if (isSelected.value) {
                                            MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)
                                        } else {
                                            Color.Transparent
                                        }
                                    )
                                    .clickable {
                                        if (isSelected.value && canCancel) {
                                            textFieldValueOfShowValue = TextFieldValue("")
                                            showBottomSheet = false
                                            onValueChange(null)
                                        } else {
                                            val text = convertOption2String(option)
                                            textFieldValueOfShowValue = TextFieldValue(
                                                text = text,
                                                selection = TextRange(text.length)
                                            )
                                            showBottomSheet = false
                                            onValueChange(option)
                                        }
                                        focusManager.clearFocus()
                                    }
                                    .padding(horizontal = 16.dp, vertical = 8.dp),
                                contentAlignment = Alignment.CenterStart
                            ) {
                                renderOption(option)
                            }
                        }
                    }
                }
            }
        }
    }

    OutlinedTextField(
        modifier = modifier
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
            Icon(
                imageVector = Icons.Filled.ArrowDropDown,
                tint = trailingIconTint,
                contentDescription = null,
                modifier = Modifier.rotate(if (showBottomSheet) 180f else 0f)
            )
        },
        isError = isError,
        enabled = enabled,
        supportingText = supportingText,
        textStyle = textStyle,
        shape = shape,
        colors = colors,
        placeholder = placeholder,
        leadingIcon = leadingIcon,
        prefix = prefix,
        suffix = suffix
    )
}