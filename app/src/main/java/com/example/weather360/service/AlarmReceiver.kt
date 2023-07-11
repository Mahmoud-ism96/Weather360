package com.example.weather360.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.example.weather360.R
import com.example.weather360.db.ConcreteLocalSource
import com.example.weather360.network.ApiClient
import com.example.weather360.util.CommonUtils.Companion.KEY_SELECTED_LANGUAGE
import com.example.weather360.util.CommonUtils.Companion.LANG_VALUE_AR
import com.example.weather360.util.CommonUtils.Companion.LANG_VALUE_EN
import com.example.weather360.util.SharedPreferencesSingleton
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AlarmReceiver : BroadcastReceiver() {

    private val CHANNEL_ID = "Alarm"


    override fun onReceive(context: Context, intent: Intent) {

        val id = intent.getIntExtra("id",0)
        val latitude = intent.getDoubleExtra("latitude", 0.0)
        val longitude = intent.getDoubleExtra("longitude", 0.0)

        var description : String? = null

        val notificationManager = context.getSystemService(NotificationManager::class.java)

        CoroutineScope(Dispatchers.IO).launch {

            val language = when (SharedPreferencesSingleton.readString(KEY_SELECTED_LANGUAGE, LANG_VALUE_EN)) {
                LANG_VALUE_EN -> "en"
                LANG_VALUE_AR -> "ar"
                else -> "en"
            }

            val forecast = ApiClient.getForecast(latitude, longitude,language)

            forecast.collectLatest {
                description = it.alerts?.get(0)?.description
            }

            if(description.isNullOrBlank())
                description = "Weather is fine"

            ConcreteLocalSource.getInstance(context).deleteAlertForecastById(id)

            withContext(Dispatchers.Main) {
                val builder =
                    NotificationCompat.Builder(context, CHANNEL_ID).setSmallIcon(R.drawable.bell)
                        .setContentTitle("Alert").setContentText(description)
                        .setPriority(NotificationCompat.PRIORITY_DEFAULT).setAutoCancel(true)

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    val name = context.getString(R.string.alerts)
                    val description = "Show alert notification"
                    val importance: Int = NotificationManager.IMPORTANCE_DEFAULT
                    val channel = NotificationChannel(CHANNEL_ID, name, importance)
                    channel.description = description
                    notificationManager.createNotificationChannel(channel)

                    builder.setChannelId(CHANNEL_ID)
                }

                notificationManager.notify(0, builder.build())
            }
        }
    }
}