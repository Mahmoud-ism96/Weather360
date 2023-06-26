package com.example.weather360.network

import com.example.weather360.model.Forecast

sealed class ApiStatus {
    class Success(val forecast: Forecast) : ApiStatus()
    class Failure(val err: Throwable) : ApiStatus()
    object Loading : ApiStatus()
}