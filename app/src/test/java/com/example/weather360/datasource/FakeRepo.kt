package com.example.weather360.datasource

import com.example.weather360.model.AlertForecast
import com.example.weather360.model.FavoriteLocation
import com.example.weather360.model.Forecast
import com.example.weather360.model.RepositoryInterface
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

class FakeRepo(
    var favLocations: MutableList<FavoriteLocation> = mutableListOf(),
    var alertForecasts: MutableList<AlertForecast> = mutableListOf()
) : RepositoryInterface {
    override fun getForecast(
        latitude: Double,
        longitude: Double,
        language: String
    ): Flow<Forecast> {
        TODO("Not yet implemented")
    }

    override suspend fun insertLocation(favoriteLocation: FavoriteLocation) {
        favLocations.add(favoriteLocation)
    }

    override fun getStoredLocations(): Flow<List<FavoriteLocation>> {
        return flowOf(favLocations)
    }

    override suspend fun deleteLocation(favoriteLocation: FavoriteLocation) {
        favLocations.remove(favoriteLocation)
    }

    override suspend fun insertAlertForecast(alertForecast: AlertForecast) {
        TODO("Not yet implemented")
    }

    override suspend fun deleteAlertForecast(alertForecast: AlertForecast) {
        TODO("Not yet implemented")
    }

    override suspend fun deleteAlertForecastById(id: Int) {
        TODO("Not yet implemented")
    }

    override fun getStoredAlertForecasts(): Flow<List<AlertForecast>> {
        TODO("Not yet implemented")
    }
}