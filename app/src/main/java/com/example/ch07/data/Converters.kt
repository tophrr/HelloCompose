package com.example.ch07.data

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class Converters {

    @TypeConverter
    fun fromTags(tags: List<String>): String = Gson().toJson(tags)

    @TypeConverter
    fun toTags(value: String): List<String> {
        val type = object : TypeToken<List<String>>() {}.type
        return Gson().fromJson<List<String>>(value, type) ?: emptyList()
    }
}
