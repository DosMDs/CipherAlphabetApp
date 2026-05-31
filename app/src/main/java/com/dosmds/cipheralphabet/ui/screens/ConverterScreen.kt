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
import androidx.compose.runtime.DisposableEffect
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
import com.dosmds.cipheralphabet.core.history.ConversionHistoryItem
import com.dosmds.cipheralphabet.data.storage.ConverterPreferencesRepository
import com.dosmds.cipheralphabet.ui.theme.CipherAlphabetAppTheme

private enum class MainSection {
    Converter,
    Reference,
    History
}

@Composable
fun ConverterScreen(modifier: Modifier = Modifier) {
    var section by rememberSaveable { mutableStateOf(MainSection.Converter) }
    val context = LocalContext.current
    val stateHolder = remember(context) {
        ConverterScreenStateHolder(
            repository = ConverterPreferencesRepository(context.applicationContext)
        )
    }
    val clipboardManager = remember(context) {
        context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    }

    DisposableEffect(stateHolder) {
        onDispose { stateHolder.dispose() }
    }

    val alphabetOptions = ConversionRouter.availableAlphabets(stateHolder.mode)
    val result = remember(
        stateHolder.mode,
        stateHolder.direction,
        stateHolder.alphabet,
        stateHolder.shiftText,
        stateHolder.input
    ) {
        ConversionRouter.convert(
            input = stateHolder.input,
            mode = stateHolder.mode,
            direction = stateHolder.direction,
            alphabet = stateHolder.alphabet,
            shift = stateHolder.shiftText.toIntOrNull() ?: 0
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
                        mode = stateHolder.mode,
                        onModeChange = stateHolder::updateMode,
                        direction = stateHolder.direction,
                        onDirectionChange = stateHolder::updateDirection,
                        alphabet = stateHolder.alphabet,
                        onAlphabetChange = stateHolder::updateAlphabet,
                        alphabetOptions = alphabetOptions,
                        shiftText = stateHolder.shiftText,
                        onShiftTextChange = stateHolder::updateShiftText,
                        input = stateHolder.input,
                        onInputChange = stateHolder::updateInput,
                        result = result,
                        onCopyResult = {
                            clipboardManager.setPrimaryClip(
                                ClipData.newPlainText("Результат", result)
                            )
                        },
                        onSaveToHistory = {
                            stateHolder.saveToHistory(resultText = result)
                        }
                    )
                }
                MainSection.Reference -> ReferenceContent()
                MainSection.History -> {
                    HistoryContent(
                        items = stateHolder.historyItems,
                        onRepeat = { item ->
                            stateHolder.repeatHistoryItem(item)
                            section = MainSection.Converter
                        },
                        onCopy = { item ->
                            clipboardManager.setPrimaryClip(
                                ClipData.newPlainText("Результат", item.resultText)
                            )
                        },
                        onClearHistory = {
                            stateHolder.clearHistory()
                        }
                    )
                }
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
    onCopyResult: () -> Unit,
    onSaveToHistory: () -> Unit
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

    Button(
        onClick = onSaveToHistory,
        modifier = Modifier.fillMaxWidth(),
        enabled = input.isNotBlank() && result.isNotBlank()
    ) {
        Text("Сохранить в историю")
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

@Composable
private fun HistoryContent(
    items: List<ConversionHistoryItem>,
    onRepeat: (ConversionHistoryItem) -> Unit,
    onCopy: (ConversionHistoryItem) -> Unit,
    onClearHistory: () -> Unit
) {
    Text(
        text = "История операций",
        style = MaterialTheme.typography.titleLarge
    )

    Button(
        onClick = onClearHistory,
        enabled = items.isNotEmpty()
    ) {
        Text("Очистить историю")
    }

    if (items.isEmpty()) {
        Text(
            text = "История пока пуста.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    } else {
        items.forEach { item ->
            HistoryItemCard(
                item = item,
                onRepeat = { onRepeat(item) },
                onCopy = { onCopy(item) }
            )
        }
    }
}

@Composable
private fun HistoryItemCard(
    item: ConversionHistoryItem,
    onRepeat: () -> Unit,
    onCopy: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.small
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text(
                text = "${item.mode.title} • ${item.direction.title} • ${item.alphabet.title}",
                style = MaterialTheme.typography.titleMedium
            )
            if (item.mode == ConversionMode.Numbers) {
                Text(
                    text = "Смещение: ${item.shift}",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
            Text(
                text = "Ввод: ${item.inputText}",
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                text = "Результат: ${item.resultText}",
                style = MaterialTheme.typography.bodyMedium
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Button(
                    onClick = onRepeat,
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Повторить")
                }
                Button(
                    onClick = onCopy,
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Копировать")
                }
            }
        }
    }
}

private val MainSection.title: String
    get() = when (this) {
        MainSection.Converter -> "Конвертер"
        MainSection.Reference -> "Справка"
        MainSection.History -> "История"
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
