package com.github.huangkl1024.composeform.core

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue


class FormItemErrorMessageScope(
    val errorMessage: String
)


class FormScope {
    @Composable
    fun <T> FormItem(
        field: FormField<T>,
        content: @Composable FormItemScope<T>.() -> Unit
    ) {
        val scope = remember { FormItemScope(field) }
        scope.content()
    }
}

class FormItemScope<T>(field: FormField<T>) {
    var value by field.value
    val onValueChange: (T?) -> Unit = field.onValueChange
    val isError by field.isError
    val enabled by field.enabled
    private val errorMessage: String? by field.errorMessage

    @Composable
    fun errorText(content: @Composable FormItemErrorMessageScope.() -> Unit): (@Composable () -> Unit)? {
        if (isError) {
            return {
                val scope = FormItemErrorMessageScope(errorMessage = errorMessage ?: "")
                scope.content()
            }
        }
        return null
    }

    @Composable
    fun defaultErrorText(): (@Composable () -> Unit)? {
        return errorText {
            Text(errorMessage, color = MaterialTheme.colorScheme.error)
        }
    }
}


@Composable
fun Form(content: @Composable FormScope.() -> Unit) {
    val scope = FormScope()
    scope.content()
}

