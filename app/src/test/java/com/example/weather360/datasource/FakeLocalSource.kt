package com.example.weather360.datasource

import com.example.weather360.db.LocalSource
import com.example.weather360.model.AlertForecast
import com.example.weather360.model.FavoriteLocation
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

class FakeLocalSource(
    var favLocations: MutableList<FavoriteLocation> = mutableListOf(),
    var alertForecasts: MutableList<AlertForecast> = mutableListOf()
) : LocalSource {
    override suspend fun insertFavLocation(favLocation: FavoriteLocation) {
        favLocations.add(favLocation)
    }

    override suspend fun deleteFavLocation(favLocation: FavoriteLocation) {
        favLocations.remove(favLocation)
    }

    override fun getAllStoredFavLocations(): Flow<List<FavoriteLocation>> {
        return flowOf(favLocations)
    }

    override suspend fun insertAlertForecast(alertForecast: AlertForecast) {
        alertForecasts.add(alertForecast)
    }

    override suspend fun deleteAlertForecast(alertForecast: AlertForecast) {
        alertForecasts.remove(alertForecast)
    }

    override fun getAllStoredAlertForecasts(): Flow<List<AlertForecast>> {
        return flowOf(alertForecasts)
    }

    override suspend fun deleteAlertForecastById(id: Int) {
        alertForecasts.removeIf {
            it.id == id
        }
    }
}