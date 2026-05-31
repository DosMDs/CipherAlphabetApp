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
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
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
import com.dosmds.cipheralphabet.BuildConfig
import com.dosmds.cipheralphabet.core.converter.ConversionAlphabet
import com.dosmds.cipheralphabet.core.converter.ConversionDirection
import com.dosmds.cipheralphabet.core.converter.ConversionMode
import com.dosmds.cipheralphabet.core.converter.ConversionRouter
import com.dosmds.cipheralphabet.core.converter.ConversionResult
import com.dosmds.cipheralphabet.core.converter.ReferenceEntry
import com.dosmds.cipheralphabet.core.converter.ReferenceTable
import com.dosmds.cipheralphabet.core.converter.ReferenceTables
import com.dosmds.cipheralphabet.core.history.ConversionHistoryItem
import com.dosmds.cipheralphabet.data.storage.ConverterPreferencesRepository
import com.dosmds.cipheralphabet.ui.theme.CipherAlphabetAppTheme

private enum class MainSection {
    Converter,
    Reference,
    History,
    About
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
    val conversionResult = remember(
        stateHolder.mode,
        stateHolder.direction,
        stateHolder.alphabet,
        stateHolder.shiftText,
        stateHolder.input
    ) {
        ConversionRouter.convertWithResult(
            input = stateHolder.input,
            mode = stateHolder.mode,
            direction = stateHolder.direction,
            alphabet = stateHolder.alphabet,
            shift = stateHolder.shiftText.toIntOrNull() ?: 0
        )
    }
    val resultText = conversionResult.text

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
                        result = conversionResult,
                        onCopyResult = {
                            clipboardManager.setPrimaryClip(
                                ClipData.newPlainText("Результат", resultText)
                            )
                        },
                        onSaveToHistory = {
                            stateHolder.saveToHistory(resultText = resultText)
                        },
                        onSwap = {
                            stateHolder.swapWithResult(resultText = resultText)
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
                MainSection.About -> AboutContent()
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
    result: ConversionResult,
    onCopyResult: () -> Unit,
    onSaveToHistory: () -> Unit,
    onSwap: () -> Unit
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

    Text(
        text = inputHint(mode, direction),
        style = MaterialTheme.typography.bodySmall,
        color = MaterialTheme.colorScheme.onSurfaceVariant
    )

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

    ResultCard(
        result = result,
        hasInput = input.isNotBlank(),
        onClearInput = { onInputChange("") },
        onCopyResult = onCopyResult,
        onSwap = onSwap,
        onSaveToHistory = onSaveToHistory
    )
}

@Composable
private fun ResultCard(
    result: ConversionResult,
    hasInput: Boolean,
    onClearInput: () -> Unit,
    onCopyResult: () -> Unit,
    onSwap: () -> Unit,
    onSaveToHistory: () -> Unit
) {
    val resultText = result.text
    val hasResult = resultText.isNotBlank()

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.small
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "Результат",
                style = MaterialTheme.typography.titleMedium
            )
            Text(
                text = resultText.ifBlank { "Результат появится здесь" },
                style = MaterialTheme.typography.titleLarge,
                color = if (hasResult) {
                    MaterialTheme.colorScheme.onSurface
                } else {
                    MaterialTheme.colorScheme.onSurfaceVariant
                }
            )
            result.warning?.let { warning ->
                WarningBlock(text = warning)
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Button(
                    onClick = onCopyResult,
                    modifier = Modifier.weight(1f),
                    enabled = hasResult
                ) {
                    Text("Копировать")
                }
                Button(
                    onClick = onClearInput,
                    modifier = Modifier.weight(1f),
                    enabled = hasInput
                ) {
                    Text("Очистить")
                }
            }
            Button(
                onClick = onSwap,
                modifier = Modifier.fillMaxWidth(),
                enabled = hasResult
            ) {
                Text("Поменять местами")
            }
            Button(
                onClick = onSaveToHistory,
                modifier = Modifier.fillMaxWidth(),
                enabled = hasInput && hasResult
            ) {
                Text("Сохранить в историю")
            }
        }
    }
}

@Composable
private fun WarningBlock(text: String) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.small,
        color = MaterialTheme.colorScheme.errorContainer,
        contentColor = MaterialTheme.colorScheme.onErrorContainer
    ) {
        Text(
            text = "⚠ $text",
            modifier = Modifier.padding(12.dp),
            style = MaterialTheme.typography.bodySmall
        )
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
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = MaterialTheme.shapes.small
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text(
                    text = "История пока пуста",
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = "Сохранённые операции появятся здесь",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
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
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = historyMetadata(item),
                style = MaterialTheme.typography.titleMedium
            )
            HorizontalDivider()
            Text(
                text = "Ввод",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = item.inputText,
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                text = "→",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = "Результат",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = item.resultText,
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

@Composable
private fun AboutContent() {
    Text(
        text = "О приложении",
        style = MaterialTheme.typography.titleLarge
    )

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.small
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "Кодовая Азбука",
                style = MaterialTheme.typography.titleLarge
            )
            Text(
                text = "Приложение для конвертации текста между числами, азбукой Морзе и символами Брайля.",
                style = MaterialTheme.typography.bodyMedium
            )
            HorizontalDivider()
            Text(
                text = "Возможности",
                style = MaterialTheme.typography.titleMedium
            )
            listOf(
                "русский и английский алфавиты",
                "русский алфавит с Ё и без Ё",
                "смещение для числового режима",
                "история операций",
                "справочные таблицы"
            ).forEach { feature ->
                Text(
                    text = "• $feature",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
            HorizontalDivider()
            Text(
                text = "Версия: ${BuildConfig.VERSION_NAME}",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

private val MainSection.title: String
    get() = when (this) {
        MainSection.Converter -> "Конвертер"
        MainSection.Reference -> "Справка"
        MainSection.History -> "История"
        MainSection.About -> "О приложении"
    }

private fun historyMetadata(item: ConversionHistoryItem): String {
    val base = "${item.mode.title} · ${item.direction.title} · ${item.alphabet.title}"
    return if (item.mode == ConversionMode.Numbers) {
        "$base · смещение ${item.shift}"
    } else {
        base
    }
}

private fun inputHint(
    mode: ConversionMode,
    direction: ConversionDirection
): String {
    return when (mode) {
        ConversionMode.Numbers -> when (direction) {
            ConversionDirection.Encode -> "Пример: ABC → 1 2 3"
            ConversionDirection.Decode -> "Пример: 1 2 3 / 4 5 6 → ABC DEF"
        }
        ConversionMode.Morse -> "Буквы разделяются пробелами, слова через /. Пример: ... --- ... → SOS"
        ConversionMode.Braille -> "Пример: ⠁⠃⠉ → ABC"
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
