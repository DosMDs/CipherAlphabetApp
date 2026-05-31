package com.dosmds.cipheralphabet.core.alphabet

object RussianAlphabetWithoutYo : Alphabet {
    override val letters: List<Char> = listOf(
        'А', 'Б', 'В', 'Г', 'Д', 'Е', 'Ж', 'З', 'И', 'Й',
        'К', 'Л', 'М', 'Н', 'О', 'П', 'Р', 'С', 'Т', 'У', 'Ф',
        'Х', 'Ц', 'Ч', 'Ш', 'Щ', 'Ъ', 'Ы', 'Ь', 'Э', 'Ю', 'Я'
    )

    override fun normalizeLetter(letter: Char): Char {
        return when (letter.uppercaseChar()) {
            'Ё' -> 'Е'
            else -> letter.uppercaseChar()
        }
    }
}
