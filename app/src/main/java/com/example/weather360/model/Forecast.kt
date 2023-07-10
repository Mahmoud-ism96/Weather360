package com.example.weather360.model

import java.io.Serializable

data class Forecast(
    val current: Current,
    var daily: List<Daily>,
    val hourly: List<Hourly>,
    val alerts: List<Alerts>?,
    val lat: Double,
    val lon: Double,
    val timezone: String,
    val timezone_offset: Int
) : Serializable