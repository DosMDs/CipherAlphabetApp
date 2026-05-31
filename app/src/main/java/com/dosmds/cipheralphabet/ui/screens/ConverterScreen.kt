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
import com.dosmds.cipheralphabet.core.alphabet.Alphabet
import com.dosmds.cipheralphabet.core.alphabet.EnglishAlphabet
import com.dosmds.cipheralphabet.core.alphabet.RussianAlphabetWithYo
import com.dosmds.cipheralphabet.core.alphabet.RussianAlphabetWithoutYo
import com.dosmds.cipheralphabet.core.converter.BrailleConverter
import com.dosmds.cipheralphabet.core.converter.EnglishBrailleTable
import com.dosmds.cipheralphabet.core.converter.EnglishMorseTable
import com.dosmds.cipheralphabet.core.converter.MorseConverter
import com.dosmds.cipheralphabet.core.converter.NumberLetterConverter
import com.dosmds.cipheralphabet.core.converter.RussianBrailleTable
import com.dosmds.cipheralphabet.core.converter.RussianMorseTable
import com.dosmds.cipheralphabet.ui.theme.CipherAlphabetAppTheme

private enum class ConverterMode(val title: String) {
    Numbers("Числа"),
    Morse("Морзе"),
    Braille("Брайль")
}

private enum class ConversionDirection(val title: String) {
    Encode("Кодировать"),
    Decode("Декодировать")
}

private enum class AlphabetOption(val title: String) {
    English("Английский"),
    RussianWithYo("Русский с Ё"),
    RussianWithoutYo("Русский без Ё")
}

@Composable
fun ConverterScreen(modifier: Modifier = Modifier) {
    var mode by rememberSaveable { mutableStateOf(ConverterMode.Numbers) }
    var direction by rememberSaveable { mutableStateOf(ConversionDirection.Encode) }
    var alphabetOption by rememberSaveable { mutableStateOf(AlphabetOption.English) }
    var shiftText by rememberSaveable { mutableStateOf("0") }
    var input by rememberSaveable { mutableStateOf("") }
    val context = LocalContext.current
    val clipboardManager = remember(context) {
        context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    }

    val alphabetOptions by remember(mode) {
        derivedStateOf { alphabetOptionsFor(mode) }
    }

    LaunchedEffect(mode) {
        if (alphabetOption !in alphabetOptions) {
            alphabetOption = alphabetOptions.first()
        }
    }

    val result = remember(mode, direction, alphabetOption, shiftText, input) {
        convertInput(
            input = input,
            mode = mode,
            direction = direction,
            alphabetOption = alphabetOption,
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
                ConverterMode.entries.forEach { item ->
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
                        selected = alphabetOption == item,
                        onClick = { alphabetOption = item },
                        label = { Text(item.title) }
                    )
                }
            }

            if (mode == ConverterMode.Numbers) {
                OutlinedTextField(
                    value = shiftText,
                    onValueChange = { shiftText = it },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Смещение") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
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

private fun alphabetOptionsFor(mode: ConverterMode): List<AlphabetOption> {
    return when (mode) {
        ConverterMode.Numbers -> listOf(
            AlphabetOption.English,
            AlphabetOption.RussianWithYo,
            AlphabetOption.RussianWithoutYo
        )
        ConverterMode.Morse,
        ConverterMode.Braille -> listOf(
            AlphabetOption.English,
            AlphabetOption.RussianWithYo
        )
    }
}

private fun convertInput(
    input: String,
    mode: ConverterMode,
    direction: ConversionDirection,
    alphabetOption: AlphabetOption,
    shift: Int
): String {
    return when (mode) {
        ConverterMode.Numbers -> convertNumbers(input, direction, alphabetOption.alphabet(), shift)
        ConverterMode.Morse -> convertMorse(input, direction, alphabetOption)
        ConverterMode.Braille -> convertBraille(input, direction, alphabetOption)
    }
}

private fun convertNumbers(
    input: String,
    direction: ConversionDirection,
    alphabet: Alphabet,
    shift: Int
): String {
    return when (direction) {
        ConversionDirection.Encode -> NumberLetterConverter.textToNumbers(input, alphabet, shift)
        ConversionDirection.Decode -> NumberLetterConverter.numbersToText(input, alphabet, shift)
    }
}

private fun convertMorse(
    input: String,
    direction: ConversionDirection,
    alphabetOption: AlphabetOption
): String {
    val table = when (alphabetOption) {
        AlphabetOption.English -> EnglishMorseTable.table
        AlphabetOption.RussianWithYo,
        AlphabetOption.RussianWithoutYo -> RussianMorseTable.table
    }
    return when (direction) {
        ConversionDirection.Encode -> MorseConverter.encode(input, table)
        ConversionDirection.Decode -> MorseConverter.decode(input, table)
    }
}

private fun convertBraille(
    input: String,
    direction: ConversionDirection,
    alphabetOption: AlphabetOption
): String {
    val table = when (alphabetOption) {
        AlphabetOption.English -> EnglishBrailleTable.table
        AlphabetOption.RussianWithYo,
        AlphabetOption.RussianWithoutYo -> RussianBrailleTable.table
    }
    return when (direction) {
        ConversionDirection.Encode -> BrailleConverter.encode(input, table)
        ConversionDirection.Decode -> BrailleConverter.decode(input, table)
    }
}

private fun AlphabetOption.alphabet(): Alphabet {
    return when (this) {
        AlphabetOption.English -> EnglishAlphabet
        AlphabetOption.RussianWithYo -> RussianAlphabetWithYo
        AlphabetOption.RussianWithoutYo -> RussianAlphabetWithoutYo
    }
}

@Preview(showBackground = true)
@Composable
private fun ConverterScreenPreview() {
    CipherAlphabetAppTheme {
        ConverterScreen()
    }
}
