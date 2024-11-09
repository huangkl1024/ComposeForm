package com.github.huangkl1024.composeform.vaidator

import com.github.huangkl1024.composeform.core.FormFieldValidator


class MinLengthValidator(minLength: Int, errorMessage: String = "Value min len is $minLength") :
    FormFieldValidator<String>(
        validate = {
            (it?.length ?: -1) >= minLength
        },
        errorMessage = errorMessage
    )