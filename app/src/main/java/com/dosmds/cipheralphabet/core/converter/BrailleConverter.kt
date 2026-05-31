package com.dosmds.cipheralphabet.core.converter

data class BrailleTable(
    val symbols: Map<Char, Char>
) {
    val letters: Map<Char, Char> = symbols.entries
        .fold(emptyMap()) { result, entry ->
            if (entry.value in result) result else result + (entry.value to entry.key)
        }
}

object EnglishBrailleTable {
    val symbols: Map<Char, Char> = mapOf(
        'A' to '⠁',
        'B' to '⠃',
        'C' to '⠉',
        'D' to '⠙',
        'E' to '⠑',
        'F' to '⠋',
        'G' to '⠛',
        'H' to '⠓',
        'I' to '⠊',
        'J' to '⠚',
        'K' to '⠅',
        'L' to '⠇',
        'M' to '⠍',
        'N' to '⠝',
        'O' to '⠕',
        'P' to '⠏',
        'Q' to '⠟',
        'R' to '⠗',
        'S' to '⠎',
        'T' to '⠞',
        'U' to '⠥',
        'V' to '⠧',
        'W' to '⠺',
        'X' to '⠭',
        'Y' to '⠽',
        'Z' to '⠵'
    )

    val table = BrailleTable(symbols)
}

object RussianBrailleTable {
    val symbols: Map<Char, Char> = mapOf(
        'А' to '⠁',
        'Б' to '⠃',
        'В' to '⠺',
        'Г' to '⠛',
        'Д' to '⠙',
        'Е' to '⠑',
        'Ё' to '⠡',
        'Ж' to '⠚',
        'З' to '⠵',
        'И' to '⠊',
        'Й' to '⠯',
        'К' to '⠅',
        'Л' to '⠇',
        'М' to '⠍',
        'Н' to '⠝',
        'О' to '⠕',
        'П' to '⠏',
        'Р' to '⠗',
        'С' to '⠎',
        'Т' to '⠞',
        'У' to '⠥',
        'Ф' to '⠋',
        'Х' to '⠓',
        'Ц' to '⠉',
        'Ч' to '⠟',
        'Ш' to '⠱',
        'Щ' to '⠭',
        'Ъ' to '⠷',
        'Ы' to '⠮',
        'Ь' to '⠾',
        'Э' to '⠪',
        'Ю' to '⠳',
        'Я' to '⠫'
    )

    val table = BrailleTable(symbols)
}

object BrailleConverter {
    fun encode(text: String, table: BrailleTable): String {
        return text.map { char ->
            when {
                char.isWhitespace() -> ' '
                else -> table.symbols[char.uppercaseChar()] ?: '?'
            }
        }.joinToString(separator = "")
    }

    fun decode(input: String, table: BrailleTable): String {
        return input.map { char ->
            when {
                char.isWhitespace() -> ' '
                else -> table.letters[char] ?: '?'
            }
        }.joinToString(separator = "")
    }
}
