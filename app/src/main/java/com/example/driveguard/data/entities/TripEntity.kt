package com.example.driveguard.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "trips")
data class TripEntity(
    @PrimaryKey(autoGenerate = true)
    val tripId: Long = 0,
    val startTimeMillis: Long,
    val endTimeMillis: Long,
    val durationSeconds: Int,
    val distanceKm: Float,
    val maxSpeedMph: Float,
    val incidentCount: Int,
    val routePointCount: Int,
    val score: Int
)