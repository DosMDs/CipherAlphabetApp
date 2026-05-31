package com.dosmds.cipheralphabet.core.converter

import com.dosmds.cipheralphabet.core.alphabet.Alphabet
import java.lang.Math.floorMod

object NumberLetterConverter {
    fun textToNumbers(text: String, alphabet: Alphabet, shift: Int = 0): String {
        return text.mapNotNull { char ->
            when {
                char.isWhitespace() -> null
                else -> alphabet.numberByLetter(char)
                    ?.let { shiftNumber(it, shift, alphabet.letters.size).toString() }
                    ?: char.toString()
            }
        }.joinToString(separator = " ")
    }

    fun numbersToText(input: String, alphabet: Alphabet, shift: Int = 0): String {
        return input.split(Regex("\\s+"))
            .filter { it.isNotEmpty() }
            .joinToString(separator = "") { token ->
                token.toIntOrNull()
                    ?.takeIf { it in 1..alphabet.letters.size }
                    ?.let { shiftNumber(it, -shift, alphabet.letters.size) }
                    ?.let { alphabet.letterByNumber(it)?.toString() }
                    ?: token
            }
    }

    private fun shiftNumber(number: Int, shift: Int, alphabetSize: Int): Int {
        return floorMod(number - 1 + shift, alphabetSize) + 1
    }
}
