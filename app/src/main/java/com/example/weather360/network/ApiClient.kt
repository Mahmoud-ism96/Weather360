package com.example.weather360.network

import com.example.weather360.model.Forecast
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ApiClient : RemoteSource {
    private const val BASE_URL = "https://api.openweathermap.org/data/2.5/"
    private val retrofit: Retrofit =
        Retrofit.Builder().baseUrl(BASE_URL).addConverterFactory(GsonConverterFactory.create())
            .build()
    private val retrofitService = retrofit.create(ApiInterface::class.java)


    override fun getForecast(latitude: Double, longitude: Double): Flow<Forecast> = flow {
        val response = retrofitService.getWeather(
            latitude, longitude, "en", "0d5916207f9ba5c980b88f81bbece1ea"
        )
        if (response.isSuccessful) {
            emit(response.body()!!)
        }

    }
}