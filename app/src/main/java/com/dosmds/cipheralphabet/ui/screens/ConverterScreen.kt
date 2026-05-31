package com.dosmds.cipheralphabet.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.dosmds.cipheralphabet.core.converter.ConversionAlphabet
import com.dosmds.cipheralphabet.core.converter.ConversionDirection
import com.dosmds.cipheralphabet.core.converter.ConversionMode
import com.dosmds.cipheralphabet.core.converter.ConversionRouter
import com.dosmds.cipheralphabet.ui.theme.CipherAlphabetAppTheme

@Composable
fun ConverterScreen(modifier: Modifier = Modifier) {
    var mode by rememberSaveable { mutableStateOf(ConversionMode.Numbers) }
    var direction by rememberSaveable { mutableStateOf(ConversionDirection.Encode) }
    var alphabet by rememberSaveable { mutableStateOf(ConversionAlphabet.English) }
    var shiftText by rememberSaveable { mutableStateOf("0") }
    var input by rememberSaveable { mutableStateOf("") }
    val context = LocalContext.current
    val clipboardManager = remember(context) {
        context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    }

    val alphabetOptions by remember(mode) {
        derivedStateOf { ConversionRouter.availableAlphabets(mode) }
    }

    LaunchedEffect(mode) {
        if (alphabet !in alphabetOptions) {
            alphabet = alphabetOptions.first()
        }
    }

    val result = remember(mode, direction, alphabet, shiftText, input) {
        ConversionRouter.convert(
            input = input,
            mode = mode,
            direction = direction,
            alphabet = alphabet,
            shift = shiftText.toIntOrNull() ?: 0
        )
    }

    Scaffold(modifier = modifier.fillMaxSize()) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(horizontal = 20.dp, vertical = 16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(18.dp)
        ) {
            Text(
                text = "Шифровальный алфавит",
                style = MaterialTheme.typography.headlineSmall
            )

            ChoiceSection(title = "Режим") {
                ConversionMode.entries.forEach { item ->
                    FilterChip(
                        selected = mode == item,
                        onClick = { mode = item },
                        label = { Text(item.title) }
                    )
                }
            }

            ChoiceSection(title = "Направление") {
                ConversionDirection.entries.forEach { item ->
                    FilterChip(
                        selected = direction == item,
                        onClick = { direction = item },
                        label = { Text(item.title) }
                    )
                }
            }

            ChoiceSection(title = "Алфавит") {
                alphabetOptions.forEach { item ->
                    FilterChip(
                        selected = alphabet == item,
                        onClick = { alphabet = item },
                        label = { Text(item.title) }
                    )
                }
            }

            if (mode == ConversionMode.Numbers) {
                OutlinedTextField(
                    value = shiftText,
                    onValueChange = { shiftText = it },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Смещение") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text)
                )
            }

            OutlinedTextField(
                value = input,
                onValueChange = { input = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp),
                label = { Text("Ввод") }
            )

            OutlinedTextField(
                value = result,
                onValueChange = {},
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp),
                label = { Text("Результат") },
                readOnly = true
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Button(
                    onClick = {
                        input = ""
                        shiftText = "0"
                    },
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Очистить")
                }
                Button(
                    onClick = {
                        clipboardManager.setPrimaryClip(
                            ClipData.newPlainText("Результат", result)
                        )
                    },
                    modifier = Modifier.weight(1f),
                    enabled = result.isNotEmpty()
                ) {
                    Text("Скопировать")
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

private val ConversionMode.title: String
    get() = when (this) {
        ConversionMode.Numbers -> "Числа"
        ConversionMode.Morse -> "Морзе"
        ConversionMode.Braille -> "Брайль"
    }

private val ConversionDirection.title: String
    get() = when (this) {
        ConversionDirection.Encode -> "Кодировать"
        ConversionDirection.Decode -> "Декодировать"
    }

private val ConversionAlphabet.title: String
    get() = when (this) {
        ConversionAlphabet.English -> "Английский"
        ConversionAlphabet.RussianWithYo -> "Русский с Ё"
        ConversionAlphabet.RussianWithoutYo -> "Русский без Ё"
    }

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ChoiceSection(
    title: String,
    content: @Composable () -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium
        )
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            content()
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun ConverterScreenPreview() {
    CipherAlphabetAppTheme {
        ConverterScreen()
    }
}
