package com.dosmds.cipheralphabet.data.storage

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.dosmds.cipheralphabet.core.converter.ConversionAlphabet
import com.dosmds.cipheralphabet.core.converter.ConversionDirection
import com.dosmds.cipheralphabet.core.converter.ConversionMode
import com.dosmds.cipheralphabet.core.history.ConversionHistoryItem
import com.dosmds.cipheralphabet.core.history.ConversionHistorySerializer
import java.io.IOException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map

private val Context.converterDataStore: DataStore<Preferences> by preferencesDataStore(
    name = "converter_state"
)

class ConverterPreferencesRepository(
    context: Context
) {
    private val dataStore = context.applicationContext.converterDataStore

    val state: Flow<PersistedConverterState> = dataStore.data
        .catch { error ->
            if (error is IOException) {
                emit(emptyPreferences())
            } else {
                throw error
            }
        }
        .map(::toState)

    suspend fun saveConverterState(
        mode: ConversionMode,
        direction: ConversionDirection,
        alphabet: ConversionAlphabet,
        shiftText: String,
        inputText: String
    ) {
        dataStore.edit { preferences ->
            preferences[ModeKey] = mode.name
            preferences[DirectionKey] = direction.name
            preferences[AlphabetKey] = alphabet.name
            preferences[ShiftTextKey] = shiftText
            preferences[InputTextKey] = inputText
        }
    }

    suspend fun saveHistory(items: List<ConversionHistoryItem>) {
        dataStore.edit { preferences ->
            preferences[HistoryKey] = ConversionHistorySerializer.encode(items)
        }
    }

    private fun toState(preferences: Preferences): PersistedConverterState {
        return PersistedConverterState(
            mode = enumValueOrDefault(
                value = preferences[ModeKey],
                defaultValue = ConversionMode.Numbers
            ),
            direction = enumValueOrDefault(
                value = preferences[DirectionKey],
                defaultValue = ConversionDirection.Encode
            ),
            alphabet = enumValueOrDefault(
                value = preferences[AlphabetKey],
                defaultValue = ConversionAlphabet.English
            ),
            shiftText = preferences[ShiftTextKey] ?: "0",
            inputText = preferences[InputTextKey].orEmpty(),
            historyItems = ConversionHistorySerializer.decode(preferences[HistoryKey].orEmpty())
        )
    }

    private inline fun <reified T : Enum<T>> enumValueOrDefault(
        value: String?,
        defaultValue: T
    ): T {
        return value
            ?.let { runCatching { enumValueOf<T>(it) }.getOrNull() }
            ?: defaultValue
    }

    private companion object {
        val ModeKey = stringPreferencesKey("mode")
        val DirectionKey = stringPreferencesKey("direction")
        val AlphabetKey = stringPreferencesKey("alphabet")
        val ShiftTextKey = stringPreferencesKey("shift_text")
        val InputTextKey = stringPreferencesKey("input_text")
        val HistoryKey = stringPreferencesKey("history")
    }
}
