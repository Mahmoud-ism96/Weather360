package com.example.weather360.model

import java.io.Serializable

data class Alerts(
    val sender_name: String,
    val event: String,
    val start: Long,
    val end: Long,
    val description: String,
    val tags: List<String>
) : Serializable