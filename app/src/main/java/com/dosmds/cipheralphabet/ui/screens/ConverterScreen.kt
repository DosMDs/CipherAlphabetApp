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
import androidx.compose.material3.Card
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
import com.dosmds.cipheralphabet.core.converter.ReferenceEntry
import com.dosmds.cipheralphabet.core.converter.ReferenceTable
import com.dosmds.cipheralphabet.core.converter.ReferenceTables
import com.dosmds.cipheralphabet.ui.theme.CipherAlphabetAppTheme

private enum class MainSection {
    Converter,
    Reference
}

@Composable
fun ConverterScreen(modifier: Modifier = Modifier) {
    var section by rememberSaveable { mutableStateOf(MainSection.Converter) }
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

            ChoiceSection(title = "Раздел") {
                MainSection.entries.forEach { item ->
                    FilterChip(
                        selected = section == item,
                        onClick = { section = item },
                        label = { Text(item.title) }
                    )
                }
            }

            when (section) {
                MainSection.Converter -> {
                    ConverterContent(
                        mode = mode,
                        onModeChange = { mode = it },
                        direction = direction,
                        onDirectionChange = { direction = it },
                        alphabet = alphabet,
                        onAlphabetChange = { alphabet = it },
                        alphabetOptions = alphabetOptions,
                        shiftText = shiftText,
                        onShiftTextChange = { shiftText = it },
                        input = input,
                        onInputChange = { input = it },
                        result = result,
                        onCopyResult = {
                            clipboardManager.setPrimaryClip(
                                ClipData.newPlainText("Результат", result)
                            )
                        }
                    )
                }
                MainSection.Reference -> ReferenceContent()
            }

            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

@Composable
private fun ConverterContent(
    mode: ConversionMode,
    onModeChange: (ConversionMode) -> Unit,
    direction: ConversionDirection,
    onDirectionChange: (ConversionDirection) -> Unit,
    alphabet: ConversionAlphabet,
    onAlphabetChange: (ConversionAlphabet) -> Unit,
    alphabetOptions: List<ConversionAlphabet>,
    shiftText: String,
    onShiftTextChange: (String) -> Unit,
    input: String,
    onInputChange: (String) -> Unit,
    result: String,
    onCopyResult: () -> Unit
) {
    ChoiceSection(title = "Режим") {
        ConversionMode.entries.forEach { item ->
            FilterChip(
                selected = mode == item,
                onClick = { onModeChange(item) },
                label = { Text(item.title) }
            )
        }
    }

    ChoiceSection(title = "Направление") {
        ConversionDirection.entries.forEach { item ->
            FilterChip(
                selected = direction == item,
                onClick = { onDirectionChange(item) },
                label = { Text(item.title) }
            )
        }
    }

    ChoiceSection(title = "Алфавит") {
        alphabetOptions.forEach { item ->
            FilterChip(
                selected = alphabet == item,
                onClick = { onAlphabetChange(item) },
                label = { Text(item.title) }
            )
        }
    }

    if (mode == ConversionMode.Numbers) {
        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            OutlinedTextField(
                value = shiftText,
                onValueChange = onShiftTextChange,
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Смещение") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text)
            )
            Text(
                text = "При декодировании смещение применяется в обратную сторону.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }

    OutlinedTextField(
        value = input,
        onValueChange = onInputChange,
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
            onClick = { onInputChange("") },
            modifier = Modifier.weight(1f)
        ) {
            Text("Очистить")
        }
        Button(
            onClick = onCopyResult,
            modifier = Modifier.weight(1f),
            enabled = result.isNotEmpty()
        ) {
            Text("Скопировать")
        }
    }
}

@Composable
private fun ReferenceContent(
    tables: List<ReferenceTable> = ReferenceTables.all
) {
    Text(
        text = "Справочные таблицы",
        style = MaterialTheme.typography.titleLarge
    )

    tables.forEach { table ->
        ReferenceTableCard(table = table)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ReferenceTableCard(table: ReferenceTable) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.small
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = table.title,
                style = MaterialTheme.typography.titleMedium
            )
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                table.entries.forEach { entry ->
                    ReferenceEntryText(entry = entry)
                }
            }
        }
    }
}

@Composable
private fun ReferenceEntryText(entry: ReferenceEntry) {
    Text(
        text = "${entry.symbol} = ${entry.code}",
        style = MaterialTheme.typography.bodyMedium
    )
}

private val MainSection.title: String
    get() = when (this) {
        MainSection.Converter -> "Конвертер"
        MainSection.Reference -> "Справка"
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
