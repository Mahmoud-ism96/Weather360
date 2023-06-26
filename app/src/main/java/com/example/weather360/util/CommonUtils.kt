package com.example.weather360.util

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class CommonUtils {
    companion object {
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