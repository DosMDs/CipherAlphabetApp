package com.dosmds.cipheralphabet.core.alphabet

interface Alphabet {
    val letters: List<Char>

    fun letterByNumber(number: Int): Char? {
        return letters.getOrNull(number - 1)
    }

    fun numberByLetter(letter: Char): Int? {
        val normalizedLetter = normalizeLetter(letter)
        val index = letters.indexOf(normalizedLetter)
        return if (index >= 0) index + 1 else null
    }

    fun normalizeLetter(letter: Char): Char {
        return letter.uppercaseChar()
    }
}
