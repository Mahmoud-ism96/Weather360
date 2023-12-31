package com.example.weather360.model

import kotlinx.coroutines.flow.Flow

interface RepositoryInterface {
    fun getForecast(latitude: Double, longitude: Double, language: String): Flow<Forecast>
    suspend fun insertLocation(favoriteLocation: FavoriteLocation)
    fun getStoredLocations(): Flow<List<FavoriteLocation>>
    suspend fun deleteLocation(favoriteLocation: FavoriteLocation)

    suspend fun insertAlertForecast(alertForecast: AlertForecast)
    suspend fun deleteAlertForecast(alertForecast: AlertForecast)
    suspend fun deleteAlertForecastById(id: Int)
    fun getStoredAlertForecasts(): Flow<List<AlertForecast>>
}
