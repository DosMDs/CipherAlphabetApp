package com.dosmds.cipheralphabet.ui.screens

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.dosmds.cipheralphabet.core.converter.ConversionAlphabet
import com.dosmds.cipheralphabet.core.converter.ConversionDirection
import com.dosmds.cipheralphabet.core.converter.ConversionMode
import com.dosmds.cipheralphabet.core.history.ConversionHistoryItem
import com.dosmds.cipheralphabet.core.history.InMemoryConversionHistoryStore
import com.dosmds.cipheralphabet.data.storage.ConverterPreferencesRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class ConverterScreenStateHolder(
    private val repository: ConverterPreferencesRepository
) {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)
    private val historyStore = InMemoryConversionHistoryStore()

    var mode by mutableStateOf(ConversionMode.Numbers)
        private set
    var direction by mutableStateOf(ConversionDirection.Encode)
        private set
    var alphabet by mutableStateOf(ConversionAlphabet.English)
        private set
    var shiftText by mutableStateOf("0")
        private set
    var input by mutableStateOf("")
        private set
    var historyItems by mutableStateOf(emptyList<ConversionHistoryItem>())
        private set

    init {
        scope.launch {
            val state = repository.state.first()
            mode = state.mode
            direction = state.direction
            alphabet = state.alphabet
            shiftText = state.shiftText
            input = state.inputText
            historyStore.replaceAll(state.historyItems)
            historyItems = historyStore.items
        }
    }

    fun updateMode(value: ConversionMode) {
        mode = value
        saveConverterState()
    }

    fun updateDirection(value: ConversionDirection) {
        direction = value
        saveConverterState()
    }

    fun updateAlphabet(value: ConversionAlphabet) {
        alphabet = value
        saveConverterState()
    }

    fun updateShiftText(value: String) {
        shiftText = value
        saveConverterState()
    }

    fun updateInput(value: String) {
        input = value
        saveConverterState()
    }

    fun saveToHistory(resultText: String) {
        val added = historyStore.add(
            mode = mode,
            direction = direction,
            alphabet = alphabet,
            shift = shiftText.toIntOrNull() ?: 0,
            inputText = input,
            resultText = resultText
        )
        if (added) {
            historyItems = historyStore.items
            saveHistory()
        }
    }

    fun repeatHistoryItem(item: ConversionHistoryItem) {
        mode = item.mode
        direction = item.direction
        alphabet = item.alphabet
        shiftText = item.shift.toString()
        input = item.inputText
        saveConverterState()
    }

    fun clearHistory() {
        historyStore.clear()
        historyItems = historyStore.items
        saveHistory()
    }

    fun dispose() {
        scope.cancel()
    }

    private fun saveConverterState() {
        scope.launch {
            repository.saveConverterState(
                mode = mode,
                direction = direction,
                alphabet = alphabet,
                shiftText = shiftText,
                inputText = input
            )
        }
    }

    private fun saveHistory() {
        scope.launch {
            repository.saveHistory(historyItems)
        }
    }
}
