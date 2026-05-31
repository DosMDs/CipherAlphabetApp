package com.dosmds.cipheralphabet.core.converter

import org.junit.Assert.assertEquals
import org.junit.Test

class BrailleConverterTest {
    @Test
    fun encodeConvertsEnglishLettersToUnicodeBraille() {
        val result = BrailleConverter.encode(
            text = "Abc",
            table = EnglishBrailleTable.table
        )

        assertEquals("⠁⠃⠉", result)
    }

    @Test
    fun decodeConvertsEnglishBrailleToLetters() {
        val result = BrailleConverter.decode(
            input = "⠁⠃⠉",
            table = EnglishBrailleTable.table
        )

        assertEquals("ABC", result)
    }

    @Test
    fun encodeConvertsRussianLettersToUnicodeBraille() {
        val result = BrailleConverter.encode(
            text = "Привет",
            table = RussianBrailleTable.table
        )

        assertEquals("⠏⠗⠊⠺⠑⠞", result)
    }

    @Test
    fun decodeConvertsRussianBrailleToLetters() {
        val result = BrailleConverter.decode(
            input = "⠏⠗⠊⠺⠑⠞",
            table = RussianBrailleTable.table
        )

        assertEquals("ПРИВЕТ", result)
    }

    @Test
    fun encodePreservesWordSpaces() {
        val result = BrailleConverter.encode(
            text = "HI WORLD",
            table = EnglishBrailleTable.table
        )

        assertEquals("⠓⠊ ⠺⠕⠗⠇⠙", result)
    }

    @Test
    fun decodePreservesWordSpaces() {
        val result = BrailleConverter.decode(
            input = "⠓⠊ ⠺⠕⠗⠇⠙",
            table = EnglishBrailleTable.table
        )

        assertEquals("HI WORLD", result)
    }

    @Test
    fun encodeReplacesUnknownCharactersWithQuestionMark() {
        val result = BrailleConverter.encode(
            text = "A!",
            table = EnglishBrailleTable.table
        )

        assertEquals("⠁?", result)
    }

    @Test
    fun decodeReplacesUnknownCharactersWithQuestionMark() {
        val result = BrailleConverter.decode(
            input = "⠁!",
            table = EnglishBrailleTable.table
        )

        assertEquals("A?", result)
    }

    @Test
    fun russianTableSupportsYo() {
        val encoded = BrailleConverter.encode(
            text = "ЕЁ",
            table = RussianBrailleTable.table
        )
        val decoded = BrailleConverter.decode(
            input = encoded,
            table = RussianBrailleTable.table
        )

        assertEquals("⠑⠡", encoded)
        assertEquals("ЕЁ", decoded)
    }

    @Test
    fun emptyInputReturnsEmptyResult() {
        assertEquals("", BrailleConverter.encode("", EnglishBrailleTable.table))
        assertEquals("", BrailleConverter.decode("", EnglishBrailleTable.table))
    }
}
