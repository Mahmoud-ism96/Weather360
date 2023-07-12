package com.example.weather360.datasource

import com.example.weather360.model.Forecast
import com.example.weather360.network.RemoteSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

class FakeRemoteSource (private val forecast: Forecast) : RemoteSource {
    override fun getForecast(
        latitude: Double,
        longitude: Double,
        language: String
    ): Flow<Forecast> {
        return flowOf(forecast)
    }
}