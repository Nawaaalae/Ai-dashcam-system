package com.example.driveguard.data.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "route_points",
    foreignKeys = [
        ForeignKey(
            entity = TripEntity::class,
            parentColumns = ["tripId"],
            childColumns = ["tripOwnerId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("tripOwnerId")]
)
data class RoutePointEntity(
    @PrimaryKey(autoGenerate = true)
    val pointId: Long = 0,
    val tripOwnerId: Long,
    val latitude: Double,
    val longitude: Double,
    val speedMph: Float,
    val timestampMillis: Long
)