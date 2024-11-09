package com.github.huangkl1024.composeform.vaidator

import com.github.huangkl1024.composeform.core.FormFieldValidator

class NotNullValidator<T>(errorMessage: String = "Value is not null!") :
    FormFieldValidator<T>(
        validate = { it != null },
        errorMessage = errorMessage
    )