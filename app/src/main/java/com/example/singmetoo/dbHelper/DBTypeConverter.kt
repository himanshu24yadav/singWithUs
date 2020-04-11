package com.example.singmetoo.dbHelper

import androidx.room.TypeConverter
import java.util.*

class DBTypeConverter {

    @TypeConverter
    fun dateToLong (date:Date?) : Long?{
        return date?.time
    }

    @TypeConverter
    fun convertToDate(value:Long?) : Date? {
        return value?.let { Date(it) }
    }
}