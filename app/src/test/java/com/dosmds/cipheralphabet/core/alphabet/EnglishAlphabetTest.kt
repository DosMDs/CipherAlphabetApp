package com.dosmds.cipheralphabet.core.alphabet

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class EnglishAlphabetTest {
    @Test
    fun letterByNumberReturnsEnglishLetter() {
        assertEquals('A', EnglishAlphabet.letterByNumber(1))
        assertEquals('Z', EnglishAlphabet.letterByNumber(26))
    }

    @Test
    fun numberByLetterReturnsEnglishNumberIgnoringCase() {
        assertEquals(1, EnglishAlphabet.numberByLetter('a'))
        assertEquals(26, EnglishAlphabet.numberByLetter('Z'))
    }

    @Test
    fun unknownEnglishValuesReturnNull() {
        assertNull(EnglishAlphabet.letterByNumber(0))
        assertNull(EnglishAlphabet.letterByNumber(27))
        assertNull(EnglishAlphabet.numberByLetter('Ж'))
    }
}
