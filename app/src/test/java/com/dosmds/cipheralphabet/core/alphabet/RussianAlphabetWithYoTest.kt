package com.dosmds.cipheralphabet.core.alphabet

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class RussianAlphabetWithYoTest {
    @Test
    fun letterByNumberReturnsRussianLetterWithYo() {
        assertEquals('А', RussianAlphabetWithYo.letterByNumber(1))
        assertEquals('Е', RussianAlphabetWithYo.letterByNumber(6))
        assertEquals('Ё', RussianAlphabetWithYo.letterByNumber(7))
        assertEquals('Я', RussianAlphabetWithYo.letterByNumber(33))
    }

    @Test
    fun numberByLetterReturnsRussianNumberWithYoIgnoringCase() {
        assertEquals(6, RussianAlphabetWithYo.numberByLetter('е'))
        assertEquals(7, RussianAlphabetWithYo.numberByLetter('ё'))
        assertEquals(33, RussianAlphabetWithYo.numberByLetter('Я'))
    }

    @Test
    fun unknownRussianWithYoValuesReturnNull() {
        assertNull(RussianAlphabetWithYo.letterByNumber(0))
        assertNull(RussianAlphabetWithYo.letterByNumber(34))
        assertNull(RussianAlphabetWithYo.numberByLetter('A'))
    }
}
