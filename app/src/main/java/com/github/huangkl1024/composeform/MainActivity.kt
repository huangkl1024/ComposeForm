package com.github.huangkl1024.composeform

import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import android.widget.Toast.LENGTH_SHORT
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType.Companion.Phone
import androidx.compose.ui.unit.dp
import com.github.huangkl1024.composeform.component.OutlinedDatePicker
import com.github.huangkl1024.composeform.component.OutlinedPasswordField
import com.github.huangkl1024.composeform.component.OutlinedSelect
import com.github.huangkl1024.composeform.component.OutlinedTimePicker
import com.github.huangkl1024.composeform.component.SearchOutlinedSelect
import com.github.huangkl1024.composeform.core.Form
import com.github.huangkl1024.composeform.core.FormField
import com.github.huangkl1024.composeform.core.FormValidator
import com.github.huangkl1024.composeform.formatter.fomatDateShort
import com.github.huangkl1024.composeform.formatter.formatTime
import com.github.huangkl1024.composeform.ui.theme.ComposeFormTheme
import com.github.huangkl1024.composeform.vaidator.EmailValidator
import com.github.huangkl1024.composeform.vaidator.MinLengthValidator
import com.github.huangkl1024.composeform.vaidator.NotBlankValidator
import com.github.huangkl1024.composeform.vaidator.NotNullValidator
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime
import java.util.stream.Collectors

class MainActivity : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ComposeFormTheme(dynamicColor = false) {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Box(Modifier.padding(innerPadding)) {
                        Column(
                            modifier = Modifier
                                .padding(top = 32.dp, start = 16.dp, end = 16.dp)
                                .fillMaxWidth()
                        ) {
                            FormPage()
                        }
                    }
                }
            }
        }
    }
}


enum class Sex(val code: Int, val desc: String) {
    MALE(1, "Male"),
    FEMALE(0, "Female")
}

enum class Hobby(val code: Int, val desc: String) {
    SWIMMING(0, "Swimming"),
    RUNNING(1, "Running"),
    SING(2, "Sing"),
    ROPE_SKIPPING(3, "Rope skipping"),
    SHOOTING(4, "Shooting")
}


class TestForm : Form<TestForm>() {
    val firstName = FormField(
        initValue = "",
        validators = mutableListOf(
            NotBlankValidator()
        )
    )

    val lastName = FormField(
        initValue = "",
    )

    val phone = FormField(
        initValue = "",
    )

    val email = FormField(
        initValue = "",
        validators = mutableListOf(
            EmailValidator()
        )
    )

    val password = FormField(
        initValue = "",
        validators = mutableListOf(
            NotBlankValidator(),
            MinLengthValidator(minLength = 8)
        )
    )

    val sex = FormField<Sex?>(
        initValue = null,
        validators = mutableListOf(
            NotNullValidator(),
        )
    )

    val hobby = FormField(
        initValue = Hobby.entries[0],
        validators = mutableListOf(
            NotNullValidator(),
        )
    )

    val birthday = FormField<LocalDate?>(
        initValue = null,
        validators = mutableListOf(
            NotNullValidator(),
        )
    )

    val time = FormField<LocalTime?>(
        initValue = null,
        validators = mutableListOf(
            NotNullValidator(),
        )
    )

    private val fields: List<FormField<*>> =
        mutableListOf(firstName, lastName, phone, email, password, sex, hobby, birthday, time)

    override fun fields(): List<FormField<*>> {
        return fields
    }

    override fun validators(): List<FormValidator<TestForm>> {
        return mutableListOf(
            object : FormValidator<TestForm>(validate = {
                it.phone.value.isNotEmpty() || it.email.value.isNotEmpty()
            }, errorMessage = "The phone and email must have one that is not empty") {}
        )
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun FormPage() {

    val form = remember {
        TestForm()
    }


    Form {
        Column {
            Row(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    FormItem(field = form.firstName) {
                        OutlinedTextField(
                            label = { Text("First name") },
                            value = value,
                            onValueChange = onValueChange,
                            isError = isError,
                            enabled = enabled,
                            supportingText = errorText {
                                Text(errorMessage, color = MaterialTheme.colorScheme.error)
                            },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                    FormItem(field = form.lastName) {
                        OutlinedTextField(
                            label = { Text("Last name") },
                            value = value,
                            onValueChange = onValueChange,
                            isError = isError,
                            enabled = enabled,
                            supportingText = errorText {
                                Text(errorMessage, color = MaterialTheme.colorScheme.error)
                            },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                    FormItem(field = form.phone) {
                        OutlinedTextField(
                            label = { Text("Phone") },
                            value = value,
                            onValueChange = onValueChange,
                            isError = isError,
                            enabled = enabled,
                            supportingText = defaultErrorText(),
                            modifier = Modifier.fillMaxWidth(),
                            keyboardOptions = KeyboardOptions.Default.copy(
                                keyboardType = Phone
                            ),
                        )
                    }
                    FormItem(field = form.email) {
                        OutlinedTextField(
                            label = { Text("Email") },
                            value = value,
                            onValueChange = onValueChange,
                            isError = isError,
                            enabled = enabled,
                            supportingText = defaultErrorText(),
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                    FormItem(field = form.password) {
                        OutlinedPasswordField(
                            label = { Text("Password") },
                            value = value,
                            onValueChange = onValueChange,
                            isError = isError,
                            enabled = enabled,
                            supportingText = defaultErrorText(),
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                    FormItem(field = form.sex) {
                        OutlinedSelect(
                            label = { Text("Sex") },
                            options = Sex.entries,
                            renderOption = {
                                Text(it.desc)
                            },
                            convertOption2String = {
                                it?.desc ?: ""
                            },
                            value = value,
                            onValueChange = onValueChange,
                            isError = isError,
                            enabled = enabled,
                            supportingText = defaultErrorText(),
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                    FormItem(field = form.hobby) {
                        SearchOutlinedSelect(
                            label = { Text("Hobby") },
                            options = Hobby.entries,
                            optionsFilter = { options, text ->
                                options.stream()
                                    .filter {
                                        it.desc.lowercase().contains(text.lowercase())
                                    }
                                    .collect(Collectors.toList())
                            },
                            convertOption2String = {
                                it?.desc ?: ""
                            },
                            renderOption = {
                                Text(it.desc)
                            },
                            value = value,
                            onValueChange = {
                                if (it == null) {
                                    onValueChange(Hobby.entries[0])
                                } else {
                                    onValueChange(it)
                                }
                            },
                            isError = isError,
                            enabled = enabled,
                            canCancel = false,
                            supportingText = defaultErrorText(),
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                    FormItem(field = form.birthday) {
                        OutlinedDatePicker(
                            label = { Text("Birthday") },
                            value = value,
                            onValueChange = onValueChange,
                            dateFormat = ::fomatDateShort,
                            isError = isError,
                            enabled = enabled,
                            supportingText = defaultErrorText(),
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                    FormItem(field = form.time) {
                        OutlinedTimePicker(
                            label = { Text("Time") },
                            value = value,
                            onValueChange = onValueChange,
                            timeFormat = ::formatTime,
                            isError = isError,
                            enabled = enabled,
                            supportingText = defaultErrorText(),
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }
            ActionRow(form)
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
private fun ActionRow(form: TestForm) {
    val context = LocalContext.current
    var disableAll by remember { mutableStateOf(false) }
    Row {
        Button(
            modifier = Modifier.weight(1f),
            onClick = {
                for (field in form.fields()) {
                    field.enabled = disableAll
                }
                disableAll = !disableAll
            }
        ) {
            Text(if (disableAll) "Enable All" else "Disable All")
        }
        Spacer(modifier = Modifier.width(16.dp))
        Button(
            modifier = Modifier.weight(1f),
            onClick = {
                val result = form.validate()
                if (result.success) {
                    Toast.makeText(context, "校验通过", LENGTH_SHORT).show()
                } else {
                    Toast.makeText(context, "校验不通过", LENGTH_SHORT).show()
                    Log.i("FormValidator", "不通过消息如下：${result.errorMessages}")
                }
            }
        ) {
            Text("Validate")
        }
    }
}

