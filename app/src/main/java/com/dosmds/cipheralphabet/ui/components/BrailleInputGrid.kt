package com.dosmds.cipheralphabet.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.dosmds.cipheralphabet.core.converter.BrailleReferenceProvider
import com.dosmds.cipheralphabet.core.converter.BrailleSymbolEntry
import com.dosmds.cipheralphabet.core.converter.ConversionDirection

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun BrailleInputGrid(
    entries: List<BrailleSymbolEntry>,
    input: String,
    direction: ConversionDirection,
    onInputChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        CurrentBrailleInput(input = input)
        BrailleInputActions(
            hasInput = input.isNotEmpty(),
            onSpace = { onInputChange(input + " ") },
            onDelete = { onInputChange(input.dropLast(1)) },
            onClear = { onInputChange("") }
        )
        FlowRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            entries.forEach { entry ->
                BrailleInputCard(
                    entry = entry,
                    onClick = {
                        val symbol = BrailleReferenceProvider.inputSymbolFor(
                            entry = entry,
                            direction = direction
                        )
                        onInputChange(input + symbol)
                    }
                )
            }
        }
    }
}

@Composable
private fun CurrentBrailleInput(
    input: String,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.small,
        color = MaterialTheme.colorScheme.surfaceVariant,
        contentColor = MaterialTheme.colorScheme.onSurfaceVariant
    ) {
        Text(
            text = input.ifEmpty { "Нажмите символ в таблице" },
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
            style = MaterialTheme.typography.bodyLarge
        )
    }
}

@Composable
private fun BrailleInputActions(
    hasInput: Boolean,
    onSpace: () -> Unit,
    onDelete: () -> Unit,
    onClear: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Button(
            onClick = onSpace,
            modifier = Modifier.weight(1f),
            contentPadding = ButtonDefaults.TextButtonContentPadding
        ) {
            Text("Пробел", maxLines = 1)
        }
        FilledTonalButton(
            onClick = onDelete,
            modifier = Modifier.weight(1f),
            enabled = hasInput,
            contentPadding = ButtonDefaults.TextButtonContentPadding
        ) {
            Text("Удалить", maxLines = 1)
        }
        FilledTonalButton(
            onClick = onClear,
            modifier = Modifier.weight(1f),
            enabled = hasInput,
            contentPadding = ButtonDefaults.TextButtonContentPadding
        ) {
            Text("Очистить", maxLines = 1)
        }
    }
}

@Composable
private fun BrailleInputCard(
    entry: BrailleSymbolEntry,
    onClick: () -> Unit
) {
    OutlinedButton(
        onClick = onClick,
        modifier = Modifier.defaultMinSize(minWidth = 56.dp, minHeight = 56.dp),
        contentPadding = ButtonDefaults.TextButtonContentPadding
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            Text(
                text = entry.letter.toString(),
                style = MaterialTheme.typography.labelLarge,
                textAlign = TextAlign.Center
            )
            Text(
                text = entry.braille.toString(),
                style = MaterialTheme.typography.titleMedium,
                textAlign = TextAlign.Center
            )
        }
    }
}
