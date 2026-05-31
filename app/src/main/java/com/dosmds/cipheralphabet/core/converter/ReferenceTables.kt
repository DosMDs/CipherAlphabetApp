package com.dosmds.cipheralphabet.core.converter

import com.dosmds.cipheralphabet.core.alphabet.Alphabet
import com.dosmds.cipheralphabet.core.alphabet.EnglishAlphabet
import com.dosmds.cipheralphabet.core.alphabet.RussianAlphabetWithYo
import com.dosmds.cipheralphabet.core.alphabet.RussianAlphabetWithoutYo

data class ReferenceEntry(
    val symbol: String,
    val code: String
)

data class ReferenceTable(
    val title: String,
    val entries: List<ReferenceEntry>
)

object ReferenceTables {
    val all: List<ReferenceTable> = listOf(
        alphabetTable("Английский алфавит", EnglishAlphabet),
        alphabetTable("Русский алфавит с Ё", RussianAlphabetWithYo),
        alphabetTable("Русский алфавит без Ё", RussianAlphabetWithoutYo),
        stringCodeTable("Морзе EN", EnglishMorseTable.symbols),
        stringCodeTable("Морзе RU", RussianMorseTable.symbols),
        charCodeTable("Брайль EN", EnglishBrailleTable.symbols),
        charCodeTable("Брайль RU", RussianBrailleTable.symbols)
    )

    private fun alphabetTable(title: String, alphabet: Alphabet): ReferenceTable {
        return ReferenceTable(
            title = title,
            entries = alphabet.letters.mapIndexed { index, letter ->
                ReferenceEntry(
                    symbol = letter.toString(),
                    code = (index + 1).toString()
                )
            }
        )
    }

    private fun stringCodeTable(title: String, symbols: Map<Char, String>): ReferenceTable {
        return ReferenceTable(
            title = title,
            entries = symbols.map { entry ->
                ReferenceEntry(
                    symbol = entry.key.toString(),
                    code = entry.value
                )
            }
        )
    }

    private fun charCodeTable(title: String, symbols: Map<Char, Char>): ReferenceTable {
        return ReferenceTable(
            title = title,
            entries = symbols.map { entry ->
                ReferenceEntry(
                    symbol = entry.key.toString(),
                    code = entry.value.toString()
                )
            }
        )
    }
}
