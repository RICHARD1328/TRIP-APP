package com.tripdirect.data.local.database

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.tripdirect.data.model.ItineraryDay

class DatabaseConverters {

    private val gson = Gson()

    @TypeConverter
    fun fromItineraryDaysList(days: List<ItineraryDay>?): String {
        if (days == null) return "[]"
        return gson.toJson(days)
    }

    @TypeConverter
    fun toItineraryDaysList(daysJson: String?): List<ItineraryDay> {
        if (daysJson.isNullOrBlank()) return emptyList()
        val type = object : TypeToken<List<ItineraryDay>>() {}.type
        return try {
            gson.fromJson(daysJson, type) ?: emptyList()
        } catch (e: Exception) {
            emptyList()
        }
    }

    @TypeConverter
    fun fromStringList(strings: List<String>?): String {
        if (strings == null) return "[]"
        return gson.toJson(strings)
    }

    @TypeConverter
    fun toStringList(json: String?): List<String> {
        if (json.isNullOrBlank()) return emptyList()
        val type = object : TypeToken<List<String>>() {}.type
        return try {
            gson.fromJson(json, type) ?: emptyList()
        } catch (e: Exception) {
            emptyList()
        }
    }
}
