package com.example.weather360.db

import android.content.Context
import com.example.weather360.model.AlertForecast
import com.example.weather360.model.FavoriteLocation
import kotlinx.coroutines.flow.Flow

class ConcreteLocalSource private constructor(context: Context) : LocalSource {

    private val favoriteLocationDao: FavoriteLocationDao by lazy {
        val db: WeatherDB = WeatherDB.getInstance(context.applicationContext)
        db.getFavoriteLocationDao()
    }

    private val alertForecastDao: AlertForecastDao by lazy {
        val db: WeatherDB = WeatherDB.getInstance(context.applicationContext)
        db.getAlertForecastDao()
    }

    companion object {
        private var INSTANCE: ConcreteLocalSource? = null

        fun getInstance(context: Context): ConcreteLocalSource {
            return INSTANCE ?: synchronized(this) {
                val instance = ConcreteLocalSource(context)
                INSTANCE = instance
                instance
            }
        }
    }

    override suspend fun insertFavLocation(favLocation: FavoriteLocation) {
        favoriteLocationDao.insert(favLocation)
    }

    override suspend fun deleteFavLocation(favLocation: FavoriteLocation) {
        favoriteLocationDao.delete(favLocation)
    }

    override fun getAllStoredFavLocations(): Flow<List<FavoriteLocation>> {
        return favoriteLocationDao.getAll()
    }

    override suspend fun insertAlertForecast(alertForecast: AlertForecast) {
        alertForecastDao.insert(alertForecast)
    }

    override suspend fun deleteAlertForecast(alertForecast: AlertForecast) {
        alertForecastDao.delete(alertForecast)
    }

    override suspend fun deleteAlertForecastById(id: Int) {
        alertForecastDao.deleteById(id)
    }

    override fun getAllStoredAlertForecasts(): Flow<List<AlertForecast>> {
        return alertForecastDao.getAll()
    }
}