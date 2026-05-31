package com.dosmds.cipheralphabet.core.converter

import com.dosmds.cipheralphabet.core.alphabet.Alphabet
import com.dosmds.cipheralphabet.core.alphabet.EnglishAlphabet
import com.dosmds.cipheralphabet.core.alphabet.RussianAlphabetWithYo
import com.dosmds.cipheralphabet.core.alphabet.RussianAlphabetWithoutYo

enum class ConversionMode {
    Numbers,
    Morse,
    Braille
}

enum class ConversionDirection {
    Encode,
    Decode
}

enum class ConversionAlphabet {
    English,
    RussianWithYo,
    RussianWithoutYo
}

object ConversionRouter {
    fun availableAlphabets(mode: ConversionMode): List<ConversionAlphabet> {
        return when (mode) {
            ConversionMode.Numbers,
            ConversionMode.Morse,
            ConversionMode.Braille -> listOf(
                ConversionAlphabet.English,
                ConversionAlphabet.RussianWithYo,
                ConversionAlphabet.RussianWithoutYo
            )
        }
    }

    fun convert(
        input: String,
        mode: ConversionMode,
        direction: ConversionDirection,
        alphabet: ConversionAlphabet,
        shift: Int
    ): String {
        return when (mode) {
            ConversionMode.Numbers -> convertNumbers(input, direction, alphabet.coreAlphabet(), shift)
            ConversionMode.Morse -> convertMorse(input, direction, alphabet)
            ConversionMode.Braille -> convertBraille(input, direction, alphabet)
        }
    }

    private fun convertNumbers(
        input: String,
        direction: ConversionDirection,
        alphabet: Alphabet,
        shift: Int
    ): String {
        return when (direction) {
            ConversionDirection.Encode -> NumberLetterConverter.textToNumbers(input, alphabet, shift)
            ConversionDirection.Decode -> NumberLetterConverter.numbersToText(input, alphabet, shift)
        }
    }

    private fun convertMorse(
        input: String,
        direction: ConversionDirection,
        alphabet: ConversionAlphabet
    ): String {
        val table = when (alphabet) {
            ConversionAlphabet.English -> EnglishMorseTable.table
            ConversionAlphabet.RussianWithYo,
            ConversionAlphabet.RussianWithoutYo -> RussianMorseTable.table
        }
        return when (direction) {
            ConversionDirection.Encode -> MorseConverter.encode(input, table)
            ConversionDirection.Decode -> MorseConverter.decode(input, table)
        }
    }

    private fun convertBraille(
        input: String,
        direction: ConversionDirection,
        alphabet: ConversionAlphabet
    ): String {
        val table = when (alphabet) {
            ConversionAlphabet.English -> EnglishBrailleTable.table
            ConversionAlphabet.RussianWithYo,
            ConversionAlphabet.RussianWithoutYo -> RussianBrailleTable.table
        }
        return when (direction) {
            ConversionDirection.Encode -> BrailleConverter.encode(input, table)
            ConversionDirection.Decode -> BrailleConverter.decode(input, table)
        }
    }

    private fun ConversionAlphabet.coreAlphabet(): Alphabet {
        return when (this) {
            ConversionAlphabet.English -> EnglishAlphabet
            ConversionAlphabet.RussianWithYo -> RussianAlphabetWithYo
            ConversionAlphabet.RussianWithoutYo -> RussianAlphabetWithoutYo
        }
    }
}
