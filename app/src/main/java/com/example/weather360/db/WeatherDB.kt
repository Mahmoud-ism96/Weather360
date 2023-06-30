package com.example.weather360.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.weather360.model.FavoriteLocation

@Database(entities = [FavoriteLocation::class], version = 1)
abstract class WeatherDB : RoomDatabase() {
    abstract fun getFavoriteLocationDao(): FavoriteLocationDao

    companion object {
        @Volatile
        private var INSTANCE: WeatherDB? = null

        fun getInstance(context: Context): WeatherDB {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext, WeatherDB::class.java, "weather_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}