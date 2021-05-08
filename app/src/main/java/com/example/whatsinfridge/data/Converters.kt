package com.example.whatsinfridge.data

import androidx.room.TypeConverter
import java.time.Instant

// Converters to store more complex data in Room database
class Converters {
    @TypeConverter
    fun timestampToInstant(timestamp: Long?): Instant? {
        return timestamp?.let { Instant.ofEpochSecond(it) }
    }
    @TypeConverter
    fun instantToTimestamp(instant: Instant?): Long? {
        return instant?.epochSecond
    }
}