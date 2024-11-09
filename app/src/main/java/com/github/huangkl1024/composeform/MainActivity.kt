package com.github.huangkl1024.composeform

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.github.huangkl1024.composeform.core.Form
import com.github.huangkl1024.composeform.core.FormField
import com.github.huangkl1024.composeform.ui.theme.ComposeFormTheme
import com.github.huangkl1024.composeform.vaidator.NotBlankValidator

class MainActivity : ComponentActivity() {
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

class TestForm : Form() {
    val name = FormField(
        value = mutableStateOf(""),
        validators = mutableListOf(
            NotBlankValidator()
        )
    )

    private val fields: List<FormField<*>> = mutableListOf(name)
    override fun fields(): List<FormField<*>> {
        return fields
    }
}

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
                Column {
                    FormItem(field = form.name) {
                        OutlinedTextField(
                            label = { Text("名称") },
                            value = value ?: "",
                            onValueChange = onValueChange,
                            isError = isError,
                            supportingText = errorText {
                                Text(errorMessage, color = MaterialTheme.colorScheme.error)
                            },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }
            ActionRow(form)
        }
    }
}

@Composable
private fun ActionRow(form: TestForm) {
    Row {
        Button(
            enabled = false,
            modifier = Modifier.weight(1f),
            onClick = {
                // nothing
            }
        ) {
            Text("Back")
        }
        Spacer(modifier = Modifier.width(16.dp))
        Button(
            modifier = Modifier.weight(1f),
            onClick = {
                form.validate()
            }
        ) {
            Text("Validate")
        }
    }
}

