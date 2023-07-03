package com.example.weather360.util

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class CommonUtils {
    companion object {
        const val KEY_FIRST_STARTUP = "first_startup"
        const val KEY_SELECTED_LOCATION = "selected_location"
        const val KEY_SELECTED_TEMP_UNIT = "selected_temp_unit"
        const val KEY_SELECTED_WIND_SPEED = "selected_wind_speed"
        const val KEY_SELECTED_LANGUAGE = "selected_language"

        const val KEY_CURRENT_LAT = "current_lat"
        const val KEY_CURRENT_LONG = "current_long"

        fun getDayOfWeek(timestamp: Long): String {
            val date = Date(timestamp * 1000L)

            val sdf = SimpleDateFormat("EE", Locale.getDefault())
            return sdf.format(date)
        }

        fun fromUnixToString(time: Int): String {
            val simpleDateFormat = SimpleDateFormat("MMM dd, yyyy", Locale.ENGLISH)
            val date = Date(time * 1000L)
            return simpleDateFormat.format(date).uppercase(Locale.ROOT)
        }

        fun fromUnixToTime(time: Int): String{
            val simpleDateFormat = SimpleDateFormat("h:mm a", Locale.getDefault())
            val date = Date(time * 1000L)
            return simpleDateFormat.format(date).uppercase(Locale.ROOT)
        }

        fun capitalizeWords(description: String): String {
            val capitalizedDescription =
                description.split(" ").joinToString(" ") { it.capitalize() }
            return capitalizedDescription
        }
    }

}