package com.example.weather360.model

import kotlinx.coroutines.flow.Flow

interface RepositoryInterface {
    fun getForecast(): Flow<Forecast>
    suspend fun insertLocation(favoriteLocation: FavoriteLocation)
    fun getStoredLocations(): Flow<List<FavoriteLocation>>
    suspend fun deleteLocation(favoriteLocation: FavoriteLocation)
}
