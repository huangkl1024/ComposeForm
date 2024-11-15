package com.github.huangkl1024.composeform.core

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf

open class FormFieldValidator<T>(
    /**
     * 校验数据
     * @return true-校验通过, false-校验不通过
     */
    val validate: (T?) -> Boolean,
    val errorMessage: String
)

abstract class Form {
    /**
     * 获取字段列表
     */
    abstract fun fields(): List<FormField<*>>

    /**
     *校验字段
     * @return true-校验通过, false-校验不通过
     */
    fun validate(): Boolean {
        var hasError = false
        fields().forEach {
            if (!it.validate() && !hasError) {
                hasError = true
            }
        }
        return !hasError
    }
}

class FormField<T>(
    val value: MutableState<T?>,
    val enabled: MutableState<Boolean> = mutableStateOf(true),
    private val validators: List<FormFieldValidator<T>> = emptyList(),
) {
    var isError: MutableState<Boolean> = mutableStateOf(false)
    var errorMessage: MutableState<String?> = mutableStateOf(null)
    val onValueChange: (T?) -> Unit = { newValue ->
        validate(newValue)
        value.value = newValue
    }

    fun validate(): Boolean {
        return validate(value.value)
    }

    private fun validate(newValue: T?): Boolean {
        errorMessage.value = null
        isError.value = false
        validators.forEach {
            if (!it.validate(newValue)) {
                errorMessage.value = it.errorMessage
                isError.value = true
                return false
            }
        }
        return !isError.value
    }
}