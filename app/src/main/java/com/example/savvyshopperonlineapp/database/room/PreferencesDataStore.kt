package com.example.savvyshopperonlineapp.database.room


import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException

class DataStoreManager private constructor(context: Context) {
    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")
    private val dataStore = context.dataStore

    suspend fun saveFontSize(size: Float) {
        dataStore.edit { preferences ->
            preferences[FONT_SIZE_KEY] = size
        }
    }

    suspend fun saveFontColor(color: String) {
        dataStore.edit { preferences ->
            preferences[FONT_COLOR_KEY] = color
        }
    }

    fun getFontSize(): Flow<Float> {
        return dataStore.data
            .catch { exception ->
                if (exception is IOException) {
                    emit(emptyPreferences())
                } else {
                    throw exception
                }
            }
            .map { preferences ->
                preferences[FONT_SIZE_KEY] ?: 14f
            }
    }

    fun getFontColor(): Flow<String> {
        return dataStore.data
            .catch { exception ->
                if (exception is IOException) {
                    emit(emptyPreferences())
                } else {
                    throw exception
                }
            }
            .map { preferences ->
                preferences[FONT_COLOR_KEY] ?: "#000000"
            }
    }

    companion object {
        @Volatile
        private var INSTANCE: DataStoreManager? = null

        private val FONT_SIZE_KEY = floatPreferencesKey("font_size")
        private val FONT_COLOR_KEY = stringPreferencesKey("font_color")

        fun getInstance(context: Context): DataStoreManager {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: DataStoreManager(context.applicationContext).also { INSTANCE = it }
            }
        }
    }
}