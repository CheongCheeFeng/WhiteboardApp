package com.example.whiteboardapp.utils

import android.content.Context
import android.widget.Toast
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map


const val DRAWING_DATA_STORE = "drawing_data"

val Context.drawingDataStore by preferencesDataStore(name = DRAWING_DATA_STORE)

class DataStoreManager(private val context: Context) {

    suspend fun getAllDrawings(): Set<Preferences.Key<*>> {
        return context.drawingDataStore.data
            .map { preferences -> preferences.asMap().keys }
            .first()
    }

    suspend fun initDrawing(preferenceKey: String) {
        val pairsKey = stringPreferencesKey(preferenceKey)
        context.drawingDataStore.edit { preferences ->
            preferences[pairsKey] = ""
        }
    }

    suspend fun saveDrawing(
        key: String,
        drawing: String
    ) {
        val pairsKey = stringPreferencesKey(key)
        context.drawingDataStore.edit { preferences ->
            preferences[pairsKey] = drawing
        }
        Toast.makeText(context, "Drawing Saved", Toast.LENGTH_SHORT)
            .show()
    }

    suspend fun getDrawing(
        key: String,
    ): String? {
        val pairsKey = stringPreferencesKey(key)
        context.drawingDataStore.data.first()[pairsKey]?.let {
            if(it.isNotEmpty()) return it
            return null
        }
        return null
    }
}