package com.example.driveguard

data class RoutePoint(
    val latitude: Double,
    val longitude: Double,
    val speedMph: Float,
    val timestampMillis: Long
)