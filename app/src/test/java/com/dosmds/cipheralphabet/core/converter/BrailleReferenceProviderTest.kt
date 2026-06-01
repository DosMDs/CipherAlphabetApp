package com.dosmds.cipheralphabet.core.converter

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Test

class BrailleReferenceProviderTest {
    @Test
    fun returnsEnglishBrailleEntries() {
        val entries = BrailleReferenceProvider.entriesFor(ConversionAlphabet.English)

        assertEquals(BrailleSymbolEntry('A', '⠁'), entries.first())
        assertEquals(BrailleSymbolEntry('Z', '⠵'), entries.last())
        assertEquals(26, entries.size)
    }

    @Test
    fun returnsRussianWithYoBrailleEntries() {
        val entries = BrailleReferenceProvider.entriesFor(ConversionAlphabet.RussianWithYo)

        assertEquals(BrailleSymbolEntry('А', '⠁'), entries.first())
        assertEquals(BrailleSymbolEntry('Ё', '⠡'), entries[6])
        assertEquals(33, entries.size)
    }

    @Test
    fun returnsRussianWithoutYoBrailleEntries() {
        val entries = BrailleReferenceProvider.entriesFor(ConversionAlphabet.RussianWithoutYo)

        assertEquals(BrailleSymbolEntry('А', '⠁'), entries.first())
        assertEquals(BrailleSymbolEntry('Я', '⠫'), entries.last())
        assertEquals(32, entries.size)
    }

    @Test
    fun russianWithoutYoDoesNotIncludeYo() {
        val entries = BrailleReferenceProvider.entriesFor(ConversionAlphabet.RussianWithoutYo)

        assertFalse(entries.any { it.letter == 'Ё' })
    }

    @Test
    fun encodeDirectionAddsLetter() {
        val entry = BrailleSymbolEntry('A', '⠁')

        val symbol = BrailleReferenceProvider.inputSymbolFor(
            entry = entry,
            direction = ConversionDirection.Encode
        )

        assertEquals('A', symbol)
    }

    @Test
    fun decodeDirectionAddsBrailleSymbol() {
        val entry = BrailleSymbolEntry('A', '⠁')

        val symbol = BrailleReferenceProvider.inputSymbolFor(
            entry = entry,
            direction = ConversionDirection.Decode
        )

        assertEquals('⠁', symbol)
    }
}
