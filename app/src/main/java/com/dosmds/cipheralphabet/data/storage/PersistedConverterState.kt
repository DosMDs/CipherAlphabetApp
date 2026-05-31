package com.dosmds.cipheralphabet.data.storage

import com.dosmds.cipheralphabet.core.converter.ConversionAlphabet
import com.dosmds.cipheralphabet.core.converter.ConversionDirection
import com.dosmds.cipheralphabet.core.converter.ConversionMode
import com.dosmds.cipheralphabet.core.history.ConversionHistoryItem

data class PersistedConverterState(
    val mode: ConversionMode = ConversionMode.Numbers,
    val direction: ConversionDirection = ConversionDirection.Encode,
    val alphabet: ConversionAlphabet = ConversionAlphabet.English,
    val shiftText: String = "0",
    val inputText: String = "",
    val historyItems: List<ConversionHistoryItem> = emptyList()
)
