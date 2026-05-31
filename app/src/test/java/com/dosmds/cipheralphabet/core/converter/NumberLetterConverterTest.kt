package com.dosmds.cipheralphabet.core.converter

import com.dosmds.cipheralphabet.core.alphabet.EnglishAlphabet
import com.dosmds.cipheralphabet.core.alphabet.RussianAlphabetWithYo
import com.dosmds.cipheralphabet.core.alphabet.RussianAlphabetWithoutYo
import org.junit.Assert.assertEquals
import org.junit.Test

class NumberLetterConverterTest {
    @Test
    fun textToNumbersConvertsEnglishText() {
        val result = NumberLetterConverter.textToNumbers(
            text = "AbZ",
            alphabet = EnglishAlphabet,
            shift = 0
        )

        assertEquals("1 2 26", result)
    }

    @Test
    fun numbersToTextConvertsEnglishNumbers() {
        val result = NumberLetterConverter.numbersToText(
            input = "1 2 26",
            alphabet = EnglishAlphabet,
            shift = 0
        )

        assertEquals("ABZ", result)
    }

    @Test
    fun textToNumbersAppliesPositiveShiftWithWrap() {
        val result = NumberLetterConverter.textToNumbers(
            text = "AZ",
            alphabet = EnglishAlphabet,
            shift = 1
        )

        assertEquals("2 1", result)
    }

    @Test
    fun numbersToTextAppliesPositiveShiftWithWrap() {
        val result = NumberLetterConverter.numbersToText(
            input = "2 1",
            alphabet = EnglishAlphabet,
            shift = 1
        )

        assertEquals("AZ", result)
    }

    @Test
    fun textToNumbersAppliesNegativeShiftWithWrap() {
        val result = NumberLetterConverter.textToNumbers(
            text = "AB",
            alphabet = EnglishAlphabet,
            shift = -1
        )

        assertEquals("26 1", result)
    }

    @Test
    fun numbersToTextAppliesNegativeShiftWithWrap() {
        val result = NumberLetterConverter.numbersToText(
            input = "26 1",
            alphabet = EnglishAlphabet,
            shift = -1
        )

        assertEquals("AB", result)
    }

    @Test
    fun converterSupportsLargeShiftValues() {
        val numbers = NumberLetterConverter.textToNumbers(
            text = "AZ",
            alphabet = EnglishAlphabet,
            shift = 27
        )
        val text = NumberLetterConverter.numbersToText(
            input = numbers,
            alphabet = EnglishAlphabet,
            shift = 27
        )

        assertEquals("2 1", numbers)
        assertEquals("AZ", text)
    }

    @Test
    fun textToNumbersKeepsUnknownCharactersAsTokens() {
        val result = NumberLetterConverter.textToNumbers(
            text = "A! Ж",
            alphabet = EnglishAlphabet,
            shift = 0
        )

        assertEquals("1 ! / Ж", result)
    }

    @Test
    fun numbersToTextReplacesInvalidTokensWithQuestionMark() {
        val result = NumberLetterConverter.numbersToText(
            input = "1 ! abc 27 2",
            alphabet = EnglishAlphabet,
            shift = 0
        )

        assertEquals("A???B", result)
    }

    @Test
    fun numbersToTextReplacesOutOfRangeEnglishNumbersWithQuestionMark() {
        val result = NumberLetterConverter.numbersToText(
            input = "1 27 3",
            alphabet = EnglishAlphabet,
            shift = 0
        )

        assertEquals("A?C", result)
    }

    @Test
    fun numbersToTextReplacesOutOfRangeRussianWithoutYoNumbersWithQuestionMark() {
        val result = NumberLetterConverter.numbersToText(
            input = "1 33",
            alphabet = RussianAlphabetWithoutYo,
            shift = 0
        )

        assertEquals("А?", result)
    }

    @Test
    fun numbersToTextReplacesOutOfRangeRussianWithYoNumbersWithQuestionMark() {
        val result = NumberLetterConverter.numbersToText(
            input = "1 34",
            alphabet = RussianAlphabetWithYo,
            shift = 0
        )

        assertEquals("А?", result)
    }

    @Test
    fun numbersToTextReplacesNonNumericTokensWithQuestionMark() {
        val result = NumberLetterConverter.numbersToText(
            input = "1 abc 3",
            alphabet = EnglishAlphabet,
            shift = 0
        )

        assertEquals("A?C", result)
    }

    @Test
    fun converterSupportsRussianAlphabetWithYo() {
        val numbers = NumberLetterConverter.textToNumbers(
            text = "ЕЁЯ",
            alphabet = RussianAlphabetWithYo,
            shift = 0
        )
        val text = NumberLetterConverter.numbersToText(
            input = numbers,
            alphabet = RussianAlphabetWithYo,
            shift = 0
        )

        assertEquals("6 7 33", numbers)
        assertEquals("ЕЁЯ", text)
    }

    @Test
    fun converterSupportsRussianAlphabetWithoutYo() {
        val numbers = NumberLetterConverter.textToNumbers(
            text = "ЕЁЖЯ",
            alphabet = RussianAlphabetWithoutYo,
            shift = 0
        )
        val text = NumberLetterConverter.numbersToText(
            input = numbers,
            alphabet = RussianAlphabetWithoutYo,
            shift = 0
        )

        assertEquals("6 6 7 32", numbers)
        assertEquals("ЕЕЖЯ", text)
    }

    @Test
    fun textToNumbersSeparatesWordsWithSlash() {
        val result = NumberLetterConverter.textToNumbers(
            text = "HELLO WORLD",
            alphabet = EnglishAlphabet,
            shift = 0
        )

        assertEquals("8 5 12 12 15 / 23 15 18 12 4", result)
    }

    @Test
    fun numbersToTextRestoresWordSpacesFromSlash() {
        val result = NumberLetterConverter.numbersToText(
            input = "8 5 12 12 15 / 23 15 18 12 4",
            alphabet = EnglishAlphabet,
            shift = 0
        )

        assertEquals("HELLO WORLD", result)
    }

    @Test
    fun emptyValuesReturnEmptyResult() {
        assertEquals(
            "",
            NumberLetterConverter.textToNumbers("", EnglishAlphabet, shift = 0)
        )
        assertEquals(
            "",
            NumberLetterConverter.numbersToText("   ", EnglishAlphabet, shift = 0)
        )
    }
}
