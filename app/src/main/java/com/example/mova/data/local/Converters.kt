package com.example.mova.data.local

import androidx.room.TypeConverter
import org.json.JSONArray

/**
 * Room TypeConverters.
 * Stores List<String> as a JSON array string in a single TEXT column.
 */
class Converters {

    @TypeConverter
    fun fromStringList(value: List<String>?): String {
        if (value == null) return "[]"
        val array = JSONArray()
        value.forEach { array.put(it) }
        return array.toString()
    }

    @TypeConverter
    fun toStringList(value: String?): List<String> {
        if (value.isNullOrBlank()) return emptyList()
        return try {
            val array = JSONArray(value)
            List(array.length()) { i -> array.getString(i) }
        } catch (e: Exception) {
            emptyList()
        }
    }
}