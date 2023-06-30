package com.example.weather360.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity
data class FavoriteLocation(
    val name: String,
    val longitude: Double,
    val latitude: Double,
    @PrimaryKey(autoGenerate = true) val id: Long = 0
) : Serializable