package com.dosmds.cipheralphabet.core.history

import com.dosmds.cipheralphabet.core.converter.ConversionAlphabet
import com.dosmds.cipheralphabet.core.converter.ConversionDirection
import com.dosmds.cipheralphabet.core.converter.ConversionMode

class InMemoryConversionHistoryStore(
    private val maxItems: Int = 20,
    private val timestampProvider: () -> Long = System::currentTimeMillis
) {
    private val history = mutableListOf<ConversionHistoryItem>()
    private var nextId = 1L

    val items: List<ConversionHistoryItem>
        get() = history.toList()

    fun replaceAll(items: List<ConversionHistoryItem>) {
        history.clear()
        history.addAll(items.take(maxItems))
        nextId = (history.maxOfOrNull { it.id } ?: 0L) + 1L
    }

    fun add(
        mode: ConversionMode,
        direction: ConversionDirection,
        alphabet: ConversionAlphabet,
        shift: Int,
        inputText: String,
        resultText: String
    ): Boolean {
        if (inputText.isBlank() || resultText.isBlank()) {
            return false
        }

        val item = ConversionHistoryItem(
            id = nextId,
            mode = mode,
            direction = direction,
            alphabet = alphabet,
            shift = shift,
            inputText = inputText,
            resultText = resultText,
            createdAt = timestampProvider()
        )

        if (history.firstOrNull()?.hasSameOperationAs(item) == true) {
            return false
        }

        nextId += 1
        history.add(0, item)
        if (history.size > maxItems) {
            history.removeAt(history.lastIndex)
        }
        return true
    }

    fun clear() {
        history.clear()
    }
}
