package com.dosmds.cipheralphabet.core.history

import com.dosmds.cipheralphabet.core.converter.ConversionAlphabet
import com.dosmds.cipheralphabet.core.converter.ConversionDirection
import com.dosmds.cipheralphabet.core.converter.ConversionMode

data class ConversionHistoryItem(
    val id: Long,
    val mode: ConversionMode,
    val direction: ConversionDirection,
    val alphabet: ConversionAlphabet,
    val shift: Int,
    val inputText: String,
    val resultText: String,
    val createdAt: Long
) {
    fun hasSameOperationAs(other: ConversionHistoryItem): Boolean {
        return mode == other.mode &&
            direction == other.direction &&
            alphabet == other.alphabet &&
            shift == other.shift &&
            inputText == other.inputText &&
            resultText == other.resultText
    }
}
