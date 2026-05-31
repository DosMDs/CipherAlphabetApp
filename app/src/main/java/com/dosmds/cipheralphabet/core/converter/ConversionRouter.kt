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

fun ConversionDirection.opposite(): ConversionDirection {
    return when (this) {
        ConversionDirection.Encode -> ConversionDirection.Decode
        ConversionDirection.Decode -> ConversionDirection.Encode
    }
}

enum class ConversionAlphabet {
    English,
    RussianWithYo,
    RussianWithoutYo
}

data class ConversionResult(
    val text: String,
    val warning: String? = null
)

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
        return convertWithResult(
            input = input,
            mode = mode,
            direction = direction,
            alphabet = alphabet,
            shift = shift
        ).text
    }

    fun convertWithResult(
        input: String,
        mode: ConversionMode,
        direction: ConversionDirection,
        alphabet: ConversionAlphabet,
        shift: Int
    ): ConversionResult {
        if (input.isBlank()) {
            return ConversionResult(text = "")
        }

        val text = when (mode) {
            ConversionMode.Numbers -> convertNumbers(input, direction, alphabet.coreAlphabet(), shift)
            ConversionMode.Morse -> convertMorse(input, direction, alphabet)
            ConversionMode.Braille -> convertBraille(input, direction, alphabet)
        }

        return ConversionResult(
            text = text,
            warning = warningFor(input, mode, direction, alphabet)
        )
    }

    private fun warningFor(
        input: String,
        mode: ConversionMode,
        direction: ConversionDirection,
        alphabet: ConversionAlphabet
    ): String? {
        return when (mode) {
            ConversionMode.Numbers -> numberWarning(input, direction, alphabet.coreAlphabet())
            ConversionMode.Morse -> morseWarning(input, direction, alphabet.morseTable())
            ConversionMode.Braille -> brailleWarning(input, direction, alphabet.brailleTable())
        }
    }

    private fun numberWarning(
        input: String,
        direction: ConversionDirection,
        alphabet: Alphabet
    ): String? {
        return when (direction) {
            ConversionDirection.Encode -> {
                val hasUnknownSymbol = input.any { char ->
                    !char.isWhitespace() && alphabet.numberByLetter(char) == null
                }
                if (hasUnknownSymbol) "Некоторые символы не удалось распознать." else null
            }
            ConversionDirection.Decode -> {
                val tokens = input.split(Regex("\\s+"))
                    .filter { it.isNotEmpty() && it != "/" }
                val hasOutOfRangeNumber = tokens
                    .mapNotNull { it.toIntOrNull() }
                    .any { it !in 1..alphabet.letters.size }
                val hasUnknownToken = tokens.any { it.toIntOrNull() == null }
                if (hasOutOfRangeNumber) {
                    "Некоторые числа находятся вне диапазона выбранного алфавита."
                } else if (hasUnknownToken) {
                    "Некоторые элементы не удалось распознать как числа."
                } else {
                    null
                }
            }
        }
    }

    private fun morseWarning(
        input: String,
        direction: ConversionDirection,
        table: MorseTable
    ): String? {
        return when (direction) {
            ConversionDirection.Encode -> {
                val hasUnknownSymbol = input.any { char ->
                    !char.isWhitespace() && table.symbols[char.uppercaseChar()] == null
                }
                if (hasUnknownSymbol) "Некоторые символы не удалось распознать." else null
            }
            ConversionDirection.Decode -> {
                val hasUnknownCode = input.split(Regex("\\s+"))
                    .filter { it.isNotEmpty() && it != "/" }
                    .any { table.codes[it] == null }
                if (hasUnknownCode) "Некоторые коды Морзе не удалось распознать." else null
            }
        }
    }

    private fun brailleWarning(
        input: String,
        direction: ConversionDirection,
        table: BrailleTable
    ): String? {
        return when (direction) {
            ConversionDirection.Encode -> {
                val hasUnknownSymbol = input.any { char ->
                    !char.isWhitespace() && table.symbols[char.uppercaseChar()] == null
                }
                if (hasUnknownSymbol) "Некоторые символы не удалось распознать." else null
            }
            ConversionDirection.Decode -> {
                val hasUnknownSymbol = input.any { char ->
                    !char.isWhitespace() && table.letters[char] == null
                }
                if (hasUnknownSymbol) "Некоторые символы Брайля не удалось распознать." else null
            }
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

    private fun ConversionAlphabet.morseTable(): MorseTable {
        return when (this) {
            ConversionAlphabet.English -> EnglishMorseTable.table
            ConversionAlphabet.RussianWithYo,
            ConversionAlphabet.RussianWithoutYo -> RussianMorseTable.table
        }
    }

    private fun ConversionAlphabet.brailleTable(): BrailleTable {
        return when (this) {
            ConversionAlphabet.English -> EnglishBrailleTable.table
            ConversionAlphabet.RussianWithYo,
            ConversionAlphabet.RussianWithoutYo -> RussianBrailleTable.table
        }
    }
}
