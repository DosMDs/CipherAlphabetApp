package com.dosmds.cipheralphabet.core.converter

import org.junit.Assert.assertEquals
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
}
