package com.example.driveguard

data class TripResult(
    val tripId: Long,
    val durationSeconds: Int,
    val incidentCount: Int,
    val maxSpeedMph: Float,
    val distanceKm: Float,
    val routePointCount: Int,
    val score: Int
)