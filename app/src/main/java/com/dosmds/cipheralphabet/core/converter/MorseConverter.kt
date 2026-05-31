package com.dosmds.cipheralphabet.core.converter

data class MorseTable(
    val symbols: Map<Char, String>
) {
    val codes: Map<String, Char> = symbols.entries
        .fold(emptyMap()) { result, entry ->
            if (entry.value in result) result else result + (entry.value to entry.key)
        }
}

object EnglishMorseTable {
    val symbols: Map<Char, String> = mapOf(
        'A' to ".-",
        'B' to "-...",
        'C' to "-.-.",
        'D' to "-..",
        'E' to ".",
        'F' to "..-.",
        'G' to "--.",
        'H' to "....",
        'I' to "..",
        'J' to ".---",
        'K' to "-.-",
        'L' to ".-..",
        'M' to "--",
        'N' to "-.",
        'O' to "---",
        'P' to ".--.",
        'Q' to "--.-",
        'R' to ".-.",
        'S' to "...",
        'T' to "-",
        'U' to "..-",
        'V' to "...-",
        'W' to ".--",
        'X' to "-..-",
        'Y' to "-.--",
        'Z' to "--.."
    )

    val table = MorseTable(symbols)
}

object RussianMorseTable {
    val symbols: Map<Char, String> = mapOf(
        'А' to ".-",
        'Б' to "-...",
        'В' to ".--",
        'Г' to "--.",
        'Д' to "-..",
        'Е' to ".",
        'Ё' to ".",
        'Ж' to "...-",
        'З' to "--..",
        'И' to "..",
        'Й' to ".---",
        'К' to "-.-",
        'Л' to ".-..",
        'М' to "--",
        'Н' to "-.",
        'О' to "---",
        'П' to ".--.",
        'Р' to ".-.",
        'С' to "...",
        'Т' to "-",
        'У' to "..-",
        'Ф' to "..-.",
        'Х' to "....",
        'Ц' to "-.-.",
        'Ч' to "---.",
        'Ш' to "----",
        'Щ' to "--.-",
        'Ъ' to "--.--",
        'Ы' to "-.--",
        'Ь' to "-..-",
        'Э' to "..-..",
        'Ю' to "..--",
        'Я' to ".-.-"
    )

    val table = MorseTable(symbols)
}

object MorseConverter {
    fun encode(text: String, table: MorseTable): String {
        return text.trim()
            .split(Regex("\\s+"))
            .filter { it.isNotEmpty() }
            .joinToString(separator = " / ") { word ->
                encodeWord(word, table)
            }
    }

    fun decode(input: String, table: MorseTable): String {
        return input.trim()
            .split(Regex("\\s+"))
            .filter { it.isNotEmpty() }
            .joinToString(separator = "") { token ->
                when (token) {
                    "/" -> " "
                    else -> table.codes[token]?.toString() ?: "?"
                }
            }
    }

    private fun encodeWord(word: String, table: MorseTable): String {
        return word.map { char ->
            table.symbols[char.uppercaseChar()] ?: "?"
        }.joinToString(separator = " ")
    }
}
