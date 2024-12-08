package com.github.huangkl1024.composeform.core

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf


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
    internal val value: MutableState<T>,
    internal val enabled: MutableState<Boolean> = mutableStateOf(true),
    private val validators: List<FormFieldValidator<T>> = emptyList(),
) {
    internal var isError: MutableState<Boolean> = mutableStateOf(false)
    internal var errorMessage: MutableState<String?> = mutableStateOf(null)
    internal val onValueChange: (T) -> Unit = { newValue ->
        validate(newValue)
        value.value = newValue
    }

    fun setEnabled(enabled: Boolean) {
        this.enabled.value = enabled
    }

    fun getEnabled(): Boolean {
        return enabled.value
    }

    fun getValue(): T {
        return value.value
    }

    fun setValue(newValue: T) {
        onValueChange(newValue)
    }

    fun validate(): String? {
        return validate(value.value)
    }

    private fun validate(newValue: T): String? {
        errorMessage.value = null
        isError.value = false
        validators.forEach {
            if (!it.validate(newValue)) {
                errorMessage.value = it.errorMessage
                isError.value = true
                return it.errorMessage
            }
        }
        return null
    }
}