package com.github.huangkl1024.composeform.vaidator

import com.github.huangkl1024.composeform.core.FormFieldValidator

class NotBlankValidator(errorMessage: String = "Value is not null or blank!"): FormFieldValidator<String>(
    validate = {!it.isNullOrBlank()},
    errorMessage = errorMessage
)