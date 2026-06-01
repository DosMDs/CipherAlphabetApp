package com.dosmds.cipheralphabet.core.converter

import com.dosmds.cipheralphabet.core.alphabet.EnglishAlphabet
import com.dosmds.cipheralphabet.core.alphabet.RussianAlphabetWithYo
import com.dosmds.cipheralphabet.core.alphabet.RussianAlphabetWithoutYo

data class BrailleSymbolEntry(
    val letter: Char,
    val braille: Char
)

object BrailleReferenceProvider {
    fun entriesFor(alphabet: ConversionAlphabet): List<BrailleSymbolEntry> {
        val letters = when (alphabet) {
            ConversionAlphabet.English -> EnglishAlphabet.letters
            ConversionAlphabet.RussianWithYo -> RussianAlphabetWithYo.letters
            ConversionAlphabet.RussianWithoutYo -> RussianAlphabetWithoutYo.letters
        }
        val symbols = when (alphabet) {
            ConversionAlphabet.English -> EnglishBrailleTable.symbols
            ConversionAlphabet.RussianWithYo,
            ConversionAlphabet.RussianWithoutYo -> RussianBrailleTable.symbols
        }

        return letters.mapNotNull { letter ->
            symbols[letter]?.let { braille ->
                BrailleSymbolEntry(letter = letter, braille = braille)
            }
        }
    }

    fun inputSymbolFor(
        entry: BrailleSymbolEntry,
        direction: ConversionDirection
    ): Char {
        return when (direction) {
            ConversionDirection.Encode -> entry.letter
            ConversionDirection.Decode -> entry.braille
        }
    }
}
