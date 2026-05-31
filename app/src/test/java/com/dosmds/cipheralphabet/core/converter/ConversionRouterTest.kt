package com.dosmds.cipheralphabet.core.converter

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class ConversionRouterTest {
    @Test
    fun routesNumberEncode() {
        val result = ConversionRouter.convert(
            input = "AZ",
            mode = ConversionMode.Numbers,
            direction = ConversionDirection.Encode,
            alphabet = ConversionAlphabet.English,
            shift = 0
        )

        assertEquals("1 26", result)
    }

    @Test
    fun routesNumberDecode() {
        val result = ConversionRouter.convert(
            input = "1 26",
            mode = ConversionMode.Numbers,
            direction = ConversionDirection.Decode,
            alphabet = ConversionAlphabet.English,
            shift = 0
        )

        assertEquals("AZ", result)
    }

    @Test
    fun routesNumberEncodeWithNegativeShift() {
        val result = ConversionRouter.convert(
            input = "AB",
            mode = ConversionMode.Numbers,
            direction = ConversionDirection.Encode,
            alphabet = ConversionAlphabet.English,
            shift = -1
        )

        assertEquals("26 1", result)
    }

    @Test
    fun routesMorseEncode() {
        val result = ConversionRouter.convert(
            input = "SOS",
            mode = ConversionMode.Morse,
            direction = ConversionDirection.Encode,
            alphabet = ConversionAlphabet.English,
            shift = 0
        )

        assertEquals("... --- ...", result)
    }

    @Test
    fun routesMorseDecode() {
        val result = ConversionRouter.convert(
            input = "... --- ...",
            mode = ConversionMode.Morse,
            direction = ConversionDirection.Decode,
            alphabet = ConversionAlphabet.English,
            shift = 0
        )

        assertEquals("SOS", result)
    }

    @Test
    fun routesBrailleEncode() {
        val result = ConversionRouter.convert(
            input = "ABC",
            mode = ConversionMode.Braille,
            direction = ConversionDirection.Encode,
            alphabet = ConversionAlphabet.English,
            shift = 0
        )

        assertEquals("⠁⠃⠉", result)
    }

    @Test
    fun routesBrailleDecode() {
        val result = ConversionRouter.convert(
            input = "⠁⠃⠉",
            mode = ConversionMode.Braille,
            direction = ConversionDirection.Decode,
            alphabet = ConversionAlphabet.English,
            shift = 0
        )

        assertEquals("ABC", result)
    }

    @Test
    fun supportsRussianWithoutYoForMorse() {
        val availableAlphabets = ConversionRouter.availableAlphabets(ConversionMode.Morse)
        val result = ConversionRouter.convert(
            input = "ЖЯ",
            mode = ConversionMode.Morse,
            direction = ConversionDirection.Encode,
            alphabet = ConversionAlphabet.RussianWithoutYo,
            shift = 0
        )

        assertTrue(ConversionAlphabet.RussianWithoutYo in availableAlphabets)
        assertEquals("...- .-.-", result)
    }

    @Test
    fun supportsRussianWithoutYoForBraille() {
        val availableAlphabets = ConversionRouter.availableAlphabets(ConversionMode.Braille)
        val result = ConversionRouter.convert(
            input = "ЖЯ",
            mode = ConversionMode.Braille,
            direction = ConversionDirection.Encode,
            alphabet = ConversionAlphabet.RussianWithoutYo,
            shift = 0
        )

        assertTrue(ConversionAlphabet.RussianWithoutYo in availableAlphabets)
        assertEquals("⠚⠫", result)
    }

    @Test
    fun validInputReturnsConversionResultWithoutWarning() {
        val result = ConversionRouter.convertWithResult(
            input = "ABC",
            mode = ConversionMode.Numbers,
            direction = ConversionDirection.Encode,
            alphabet = ConversionAlphabet.English,
            shift = 0
        )

        assertEquals("1 2 3", result.text)
        assertNull(result.warning)
    }

    @Test
    fun unknownNumberEncodeSymbolsReturnWarning() {
        val result = ConversionRouter.convertWithResult(
            input = "A!",
            mode = ConversionMode.Numbers,
            direction = ConversionDirection.Encode,
            alphabet = ConversionAlphabet.English,
            shift = 0
        )

        assertEquals("1 !", result.text)
        assertEquals("Некоторые символы не удалось распознать.", result.warning)
    }

    @Test
    fun outOfRangeNumbersReturnWarning() {
        val result = ConversionRouter.convertWithResult(
            input = "1 27",
            mode = ConversionMode.Numbers,
            direction = ConversionDirection.Decode,
            alphabet = ConversionAlphabet.English,
            shift = 0
        )

        assertEquals("A?", result.text)
        assertEquals(
            "Некоторые числа находятся вне диапазона выбранного алфавита.",
            result.warning
        )
    }

    @Test
    fun nonNumericNumberDecodeTokensReturnWarning() {
        val result = ConversionRouter.convertWithResult(
            input = "1 abc 3",
            mode = ConversionMode.Numbers,
            direction = ConversionDirection.Decode,
            alphabet = ConversionAlphabet.English,
            shift = 0
        )

        assertEquals("A?C", result.text)
        assertEquals("Некоторые элементы не удалось распознать как числа.", result.warning)
    }

    @Test
    fun unknownMorseCodeReturnsWarning() {
        val result = ConversionRouter.convertWithResult(
            input = "... invalid",
            mode = ConversionMode.Morse,
            direction = ConversionDirection.Decode,
            alphabet = ConversionAlphabet.English,
            shift = 0
        )

        assertEquals("S?", result.text)
        assertEquals("Некоторые коды Морзе не удалось распознать.", result.warning)
    }

    @Test
    fun unknownBrailleSymbolReturnsWarning() {
        val result = ConversionRouter.convertWithResult(
            input = "⠁!",
            mode = ConversionMode.Braille,
            direction = ConversionDirection.Decode,
            alphabet = ConversionAlphabet.English,
            shift = 0
        )

        assertEquals("A?", result.text)
        assertEquals("Некоторые символы Брайля не удалось распознать.", result.warning)
    }

    @Test
    fun conversionDirectionCanBeSwapped() {
        assertEquals(ConversionDirection.Decode, ConversionDirection.Encode.opposite())
        assertEquals(ConversionDirection.Encode, ConversionDirection.Decode.opposite())
    }
}
