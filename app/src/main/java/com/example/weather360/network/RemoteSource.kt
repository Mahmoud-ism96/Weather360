package com.example.weather360.network

import com.example.weather360.model.Forecast
import kotlinx.coroutines.flow.Flow

interface RemoteSource {
    fun getForecast(latitude: Double, longitude: Double): Flow<Forecast>
}
