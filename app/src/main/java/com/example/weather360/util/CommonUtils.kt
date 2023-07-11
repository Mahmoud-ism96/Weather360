package com.example.weather360.util

import android.app.Activity
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import com.example.weather360.model.Forecast
import java.io.File
import java.io.FileInputStream
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.ObjectInputStream
import java.io.ObjectOutputStream
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

        const val LOC_VALUE_GPS = "GPS"
        const val LOC_VALUE_MAP = "MAP"

        const val TEMP_VALUE_CELSIUS = "CELSIUS"
        const val TEMP_VALUE_KELVIN = "KELVIN"
        const val TEMP_VALUE_FAHRENHEIT = "FAHRENHEIT"

        const val WIND_VALUE_METER = "METER"
        const val WIND_VALUE_MILE = "MILE"

        const val LANG_VALUE_EN = "EN"
        const val LANG_VALUE_AR = "AR"

        fun getDayOfWeek(timestamp: Long,language : String): String {
            val date = Date(timestamp * 1000L)

            val sdf = SimpleDateFormat("EE", Locale(language))
            return sdf.format(date)
        }

        fun fromUnixToString(time: Long,language: String): String {
            val simpleDateFormat = SimpleDateFormat("MMM dd, yyyy", Locale(language))
            val date = Date(time * 1000L)
            return simpleDateFormat.format(date).uppercase(Locale(language))
        }

        fun fromUnixToTime(time: Long, language: String): String {
            val simpleDateFormat = SimpleDateFormat("h:mm a", Locale(language))
            val date = Date(time * 1000L)
            return simpleDateFormat.format(date).uppercase(Locale(language))
        }

        fun capitalizeWords(description: String): String {
            val capitalizedDescription =
                description.split(" ").joinToString(" ") { it.capitalize() }
            return capitalizedDescription
        }

        fun checkConnectivity(activity: Activity): Boolean {
            val connectivityManager =
                activity.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val networkCapabilities =
                connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
            return networkCapabilities != null && networkCapabilities.hasCapability(
                NetworkCapabilities.NET_CAPABILITY_INTERNET
            )
        }

        fun writeCache(forecast: Forecast, context: Context) {
            val filename = "forecast.ser"
            val fileOutputStream = FileOutputStream(File(context.filesDir, filename))
            val objectOutputStream = ObjectOutputStream(fileOutputStream)
            objectOutputStream.writeObject(forecast)
            objectOutputStream.close()
            fileOutputStream.close()
        }

        fun readCache(context: Context): Forecast? {
            val filename = "forecast.ser"
            return try {
                val fileInputStream = FileInputStream(File(context.filesDir, filename))
                val objectInputStream = ObjectInputStream(fileInputStream)
                val forecast = objectInputStream.readObject() as Forecast
                objectInputStream.close()
                fileInputStream.close()

                forecast
            } catch (e: FileNotFoundException) {
                //TODO: Show alert that internet is needed
                e.printStackTrace()
                null
            }
        }
    }
}