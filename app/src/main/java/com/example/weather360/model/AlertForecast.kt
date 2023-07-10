package com.example.weather360.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class AlertForecast(
    @PrimaryKey val id: Int,
    val time: String,
    val date: String,
    val longitude: Double,
    val latitude: Double
)