package com.example.weather360.util

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import com.example.weather360.model.Forecast
import com.example.weather360.service.AlarmReceiver
import java.util.Calendar

object AlarmUtils {

    fun setAlarm(
        context: Context,
        timeInMillis: Long,
        requestCode: Int,
        cachedForecast: Forecast,
        type: String
    ) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val alarmIntent = Intent(context, AlarmReceiver::class.java).apply {
            putExtra("id", requestCode)
            putExtra("latitude", cachedForecast.lat)
            putExtra("longitude", cachedForecast.lon)
            putExtra("type",type)
        }.let { intent ->
            PendingIntent.getBroadcast(context, requestCode, intent, PendingIntent.FLAG_IMMUTABLE)
        }

        val c = Calendar.getInstance()

        c.timeInMillis = timeInMillis

        alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, timeInMillis, alarmIntent)
    }

    fun cancelAlarm(context: Context, requestCode: Int) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val alarmIntent = Intent(context, AlarmReceiver::class.java).let { intent ->
            PendingIntent.getBroadcast(context, requestCode, intent, PendingIntent.FLAG_IMMUTABLE)
        }

        alarmManager.cancel(alarmIntent)
    }
}