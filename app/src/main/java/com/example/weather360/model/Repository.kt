package com.example.weather360.model

import com.example.weather360.db.LocalSource
import com.example.weather360.network.RemoteSource
import kotlinx.coroutines.flow.Flow

class Repository private constructor(
    private var remoteSource: RemoteSource, private var localSource: LocalSource
) : RepositoryInterface {

    companion object {
        private var INSTANCE: Repository? = null
        fun getInstance(
            remoteSource: RemoteSource, localSource: LocalSource
        ): Repository {
            return INSTANCE ?: synchronized(this) {
                val instance = Repository(remoteSource, localSource)
                INSTANCE = instance
                instance
            }
        }
    }

    override fun getForecast(
        latitude: Double, longitude: Double, language: String
    ): Flow<Forecast> {
        return remoteSource.getForecast(latitude, longitude, language)
    }

    override suspend fun insertLocation(favoriteLocation: FavoriteLocation) {
        localSource.insertFavLocation(favoriteLocation)
    }

    override fun getStoredLocations(): Flow<List<FavoriteLocation>> {
        return localSource.getAllStoredFavLocations()
    }

    override suspend fun deleteLocation(favoriteLocation: FavoriteLocation) {
        localSource.deleteFavLocation(favoriteLocation)
    }

    override suspend fun insertAlertForecast(alertForecast: AlertForecast) {
        localSource.insertAlertForecast(alertForecast)
    }

    override suspend fun deleteAlertForecast(alertForecast: AlertForecast) {
        localSource.deleteAlertForecast(alertForecast)
    }

    override suspend fun deleteAlertForecastById(id: Int) {
        localSource.deleteAlertForecastById(id)
    }

    override fun getStoredAlertForecasts(): Flow<List<AlertForecast>> {
        return localSource.getAllStoredAlertForecasts()
    }

}