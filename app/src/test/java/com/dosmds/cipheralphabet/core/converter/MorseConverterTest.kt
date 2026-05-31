package com.dosmds.cipheralphabet.core.converter

import org.junit.Assert.assertEquals
import org.junit.Test

class MorseConverterTest {
    @Test
    fun encodeConvertsEnglishLetters() {
        val result = MorseConverter.encode(
            text = "SOS",
            table = EnglishMorseTable.table
        )

        assertEquals("... --- ...", result)
    }

    @Test
    fun decodeConvertsEnglishMorse() {
        val result = MorseConverter.decode(
            input = "... --- ...",
            table = EnglishMorseTable.table
        )

        assertEquals("SOS", result)
    }

    @Test
    fun encodeConvertsRussianLetters() {
        val result = MorseConverter.encode(
            text = "Привет",
            table = RussianMorseTable.table
        )

        assertEquals(".--. .-. .. .-- . -", result)
    }

    @Test
    fun decodeConvertsRussianMorse() {
        val result = MorseConverter.decode(
            input = ".--. .-. .. .-- . -",
            table = RussianMorseTable.table
        )

        assertEquals("ПРИВЕТ", result)
    }

    @Test
    fun encodeSeparatesWordsWithSlash() {
        val result = MorseConverter.encode(
            text = "HI WORLD",
            table = EnglishMorseTable.table
        )

        assertEquals(".... .. / .-- --- .-. .-.. -..", result)
    }

    @Test
    fun decodeSeparatesWordsBySlash() {
        val result = MorseConverter.decode(
            input = ".... .. / .-- --- .-. .-.. -..",
            table = EnglishMorseTable.table
        )

        assertEquals("HI WORLD", result)
    }

    @Test
    fun encodeReplacesUnknownCharactersWithQuestionMark() {
        val result = MorseConverter.encode(
            text = "A!",
            table = EnglishMorseTable.table
        )

        assertEquals(".- ?", result)
    }

    @Test
    fun decodeReplacesUnknownCodesWithQuestionMark() {
        val result = MorseConverter.decode(
            input = ".- invalid -...",
            table = EnglishMorseTable.table
        )

        assertEquals("A?B", result)
    }

    @Test
    fun russianYoEncodesAsYeAndDecodesAsYe() {
        val encoded = MorseConverter.encode(
            text = "ЁЕ",
            table = RussianMorseTable.table
        )
        val decoded = MorseConverter.decode(
            input = encoded,
            table = RussianMorseTable.table
        )

        assertEquals(". .", encoded)
        assertEquals("ЕЕ", decoded)
    }

    @Test
    fun emptyInputReturnsEmptyResult() {
        assertEquals("", MorseConverter.encode("", EnglishMorseTable.table))
        assertEquals("", MorseConverter.decode("   ", EnglishMorseTable.table))
    }
}
