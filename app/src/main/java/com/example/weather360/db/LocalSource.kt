package com.example.weather360.db

import com.example.weather360.model.FavoriteLocation
import kotlinx.coroutines.flow.Flow

interface LocalSource {
    suspend fun insertFavLocation(favLocation: FavoriteLocation)
    suspend fun deleteFavLocation(favLocation: FavoriteLocation)
    fun getAllStoredFavLocations(): Flow<List<FavoriteLocation>>
}
