package com.dosmds.cipheralphabet.core.history

import com.dosmds.cipheralphabet.core.converter.ConversionAlphabet
import com.dosmds.cipheralphabet.core.converter.ConversionDirection
import com.dosmds.cipheralphabet.core.converter.ConversionMode
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ConversionHistorySerializerTest {
    @Test
    fun encodedHistoryCanBeDecoded() {
        val items = listOf(
            historyItem(
                id = 7L,
                inputText = "HELLO WORLD",
                resultText = "8 5 12 12 15 / 23 15 18 12 4"
            ),
            historyItem(
                id = 8L,
                mode = ConversionMode.Braille,
                inputText = "Привет",
                resultText = "⠏⠗⠊⠺⠑⠞"
            )
        )

        val decoded = ConversionHistorySerializer.decode(
            ConversionHistorySerializer.encode(items)
        )

        assertEquals(items, decoded)
    }

    @Test
    fun corruptedHistoryReturnsEmptyList() {
        val decoded = ConversionHistorySerializer.decode("not|valid|history")

        assertTrue(decoded.isEmpty())
    }

    @Test
    fun decodedHistoryIsLimitedToTwentyItems() {
        val items = (1..25).map { index ->
            historyItem(
                id = index.toLong(),
                inputText = "Input $index",
                resultText = "Result $index"
            )
        }

        val decoded = ConversionHistorySerializer.decode(
            ConversionHistorySerializer.encode(items)
        )

        assertEquals(20, decoded.size)
        assertEquals("Input 1", decoded.first().inputText)
        assertEquals("Input 20", decoded.last().inputText)
    }

    private fun historyItem(
        id: Long,
        mode: ConversionMode = ConversionMode.Numbers,
        inputText: String,
        resultText: String
    ): ConversionHistoryItem {
        return ConversionHistoryItem(
            id = id,
            mode = mode,
            direction = ConversionDirection.Encode,
            alphabet = ConversionAlphabet.English,
            shift = 0,
            inputText = inputText,
            resultText = resultText,
            createdAt = id * 100L
        )
    }
}
