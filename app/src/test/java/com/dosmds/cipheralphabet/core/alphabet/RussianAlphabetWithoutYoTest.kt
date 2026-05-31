package com.dosmds.cipheralphabet.core.alphabet

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class RussianAlphabetWithoutYoTest {
    @Test
    fun letterByNumberReturnsRussianLetterWithoutYo() {
        assertEquals('А', RussianAlphabetWithoutYo.letterByNumber(1))
        assertEquals('Е', RussianAlphabetWithoutYo.letterByNumber(6))
        assertEquals('Ж', RussianAlphabetWithoutYo.letterByNumber(7))
        assertEquals('Я', RussianAlphabetWithoutYo.letterByNumber(32))
    }

    @Test
    fun numberByLetterTreatsYoAsYe() {
        assertEquals(6, RussianAlphabetWithoutYo.numberByLetter('е'))
        assertEquals(6, RussianAlphabetWithoutYo.numberByLetter('ё'))
        assertEquals(32, RussianAlphabetWithoutYo.numberByLetter('Я'))
    }

    @Test
    fun unknownRussianWithoutYoValuesReturnNull() {
        assertNull(RussianAlphabetWithoutYo.letterByNumber(0))
        assertNull(RussianAlphabetWithoutYo.letterByNumber(33))
        assertNull(RussianAlphabetWithoutYo.numberByLetter('A'))
    }
}
