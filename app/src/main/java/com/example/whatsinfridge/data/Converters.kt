package com.example.whatsinfridge.data

import androidx.room.TypeConverter
import java.time.LocalDate

// Converters to store more complex data in Room database
class Converters {
    @TypeConverter
    fun stringToLocalDate(string: String): LocalDate? {
        return LocalDate.parse(string)
    }
    @TypeConverter
    fun localDateToString(localDate: LocalDate?): String? {
        return localDate?.toString()
    }
}