package com.example.weather360.model

import kotlinx.coroutines.flow.Flow

interface RepositoryInterface {
    fun getForecast(): Flow<Forecast>
}
