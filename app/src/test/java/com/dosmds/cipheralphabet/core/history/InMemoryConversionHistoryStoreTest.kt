package com.dosmds.cipheralphabet.core.history

import com.dosmds.cipheralphabet.core.converter.ConversionAlphabet
import com.dosmds.cipheralphabet.core.converter.ConversionDirection
import com.dosmds.cipheralphabet.core.converter.ConversionMode
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class InMemoryConversionHistoryStoreTest {
    @Test
    fun emptyInputIsNotAdded() {
        val store = InMemoryConversionHistoryStore()

        val added = store.addTestItem(inputText = "", resultText = "1")

        assertFalse(added)
        assertTrue(store.items.isEmpty())
    }

    @Test
    fun emptyResultIsNotAdded() {
        val store = InMemoryConversionHistoryStore()

        val added = store.addTestItem(inputText = "A", resultText = "")

        assertFalse(added)
        assertTrue(store.items.isEmpty())
    }

    @Test
    fun newItemsAreAddedToTop() {
        val store = InMemoryConversionHistoryStore()

        store.addTestItem(inputText = "A", resultText = "1")
        store.addTestItem(inputText = "B", resultText = "2")

        assertEquals("B", store.items[0].inputText)
        assertEquals("A", store.items[1].inputText)
    }

    @Test
    fun keepsMaximumTwentyItems() {
        val store = InMemoryConversionHistoryStore()

        (1..25).forEach { index ->
            store.addTestItem(
                inputText = "Input $index",
                resultText = "Result $index"
            )
        }

        assertEquals(20, store.items.size)
        assertEquals("Input 25", store.items.first().inputText)
        assertEquals("Input 6", store.items.last().inputText)
    }

    @Test
    fun clearRemovesAllItems() {
        val store = InMemoryConversionHistoryStore()
        store.addTestItem(inputText = "A", resultText = "1")

        store.clear()

        assertTrue(store.items.isEmpty())
    }

    @Test
    fun consecutiveFullDuplicateIsNotAdded() {
        val store = InMemoryConversionHistoryStore()

        val firstAdded = store.addTestItem(inputText = "A", resultText = "1")
        val secondAdded = store.addTestItem(inputText = "A", resultText = "1")

        assertTrue(firstAdded)
        assertFalse(secondAdded)
        assertEquals(1, store.items.size)
    }

    private fun InMemoryConversionHistoryStore.addTestItem(
        inputText: String,
        resultText: String
    ): Boolean {
        return add(
            mode = ConversionMode.Numbers,
            direction = ConversionDirection.Encode,
            alphabet = ConversionAlphabet.English,
            shift = 0,
            inputText = inputText,
            resultText = resultText
        )
    }
}
