package com.example.weather360.network

import com.example.weather360.model.Forecast
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiInterface {
    @GET("onecall")
    suspend fun getWeather(
        @Query("lat") lat: Double,
        @Query("lon") lon: Double,
        @Query("lang") lang: String,
        @Query("appid") appId: String): Response<Forecast>
}