package com.github.huangkl1024.composeform.core

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue


data class FormValidatorResult(
    val success: Boolean,
    val errorMessages: List<String> = mutableListOf()
)

open class FormValidator<T : Form<T>>(
    /**
     * 校验数据
     * @return true-校验通过, false-校验不通过
     */
    val validate: (T) -> Boolean,
    val errorMessage: String
)

open class FormFieldValidator<T>(
    /**
     * 校验数据
     * @return true-校验通过, false-校验不通过
     */
    val validate: (T) -> Boolean,
    val errorMessage: String
)

abstract class Form<T : Form<T>> {
    /**
     * 获取字段列表
     */
    abstract fun fields(): List<FormField<*>>

    /**
     * 获取校验器列表
     */
    open fun validators(): List<FormValidator<T>> = emptyList()

    /**
     *校验字段
     * @return true-校验通过, false-校验不通过
     */
    fun validate(): FormValidatorResult {
        val errorMessages = mutableListOf<String>()

        // 字段
        fields().forEach {
            val fieldErrorMessage = it.validate()
            if (fieldErrorMessage != null) {
                errorMessages.add(fieldErrorMessage)
            }
        }

        // 总体
        validators().forEach {
            if (!it.validate(this as T)) {
                errorMessages.add(it.errorMessage)
            }
        }

        return FormValidatorResult(errorMessages.isEmpty(), errorMessages)
    }
}

class FormField<T>(
    initValue: T,
    initEnabled: Boolean = true,
    private val validators: List<FormFieldValidator<T>> = emptyList(),
) {

    internal val _value: MutableState<T> = mutableStateOf(initValue)
    var value by _value
    internal val onValueChange: (T) -> Unit = { newValue ->
        validate(newValue)
        value = newValue
    }

    internal val _enabled: MutableState<Boolean> = mutableStateOf(initEnabled)
    var enabled by _enabled

    internal var _isError: MutableState<Boolean> = mutableStateOf(false)
    var isError by _isError

    internal var _errorMessage: MutableState<String?> = mutableStateOf(null)
    var errorMessage by _errorMessage



    fun validate(): String? {
        return validate(value)
    }

    private fun validate(newValue: T): String? {
        _errorMessage.value = null
        _isError.value = false
        validators.forEach {
            if (!it.validate(newValue)) {
                _errorMessage.value = it.errorMessage
                _isError.value = true
                return it.errorMessage
            }
        }
        return null
    }
}