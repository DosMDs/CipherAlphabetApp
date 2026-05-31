package com.dosmds.cipheralphabet.core.history

import com.dosmds.cipheralphabet.core.converter.ConversionAlphabet
import com.dosmds.cipheralphabet.core.converter.ConversionDirection
import com.dosmds.cipheralphabet.core.converter.ConversionMode
import java.nio.charset.StandardCharsets
import java.util.Base64

object ConversionHistorySerializer {
    private const val FieldSeparator = "|"
    private const val ItemSeparator = "\n"
    private const val MaxItems = 20

    fun encode(items: List<ConversionHistoryItem>): String {
        return items.take(MaxItems)
            .joinToString(separator = ItemSeparator) { item ->
                listOf(
                    item.id.toString(),
                    item.mode.name,
                    item.direction.name,
                    item.alphabet.name,
                    item.shift.toString(),
                    item.createdAt.toString(),
                    encodeText(item.inputText),
                    encodeText(item.resultText)
                ).joinToString(separator = FieldSeparator)
            }
    }

    fun decode(raw: String): List<ConversionHistoryItem> {
        if (raw.isBlank()) {
            return emptyList()
        }

        return runCatching {
            raw.lineSequence()
                .filter { it.isNotBlank() }
                .mapNotNull(::decodeItem)
                .take(MaxItems)
                .toList()
        }.getOrElse { emptyList() }
    }

    private fun decodeItem(raw: String): ConversionHistoryItem? {
        val fields = raw.split(FieldSeparator)
        if (fields.size != 8) {
            return null
        }

        return runCatching {
            ConversionHistoryItem(
                id = fields[0].toLong(),
                mode = enumValueOf<ConversionMode>(fields[1]),
                direction = enumValueOf<ConversionDirection>(fields[2]),
                alphabet = enumValueOf<ConversionAlphabet>(fields[3]),
                shift = fields[4].toInt(),
                createdAt = fields[5].toLong(),
                inputText = decodeText(fields[6]),
                resultText = decodeText(fields[7])
            )
        }.getOrNull()
    }

    private fun encodeText(text: String): String {
        return Base64.getUrlEncoder()
            .withoutPadding()
            .encodeToString(text.toByteArray(StandardCharsets.UTF_8))
    }

    private fun decodeText(text: String): String {
        return String(Base64.getUrlDecoder().decode(text), StandardCharsets.UTF_8)
    }
}
