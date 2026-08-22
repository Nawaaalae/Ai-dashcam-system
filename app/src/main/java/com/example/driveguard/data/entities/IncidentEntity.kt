package com.example.driveguard.data.entities


import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "incidents",
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
data class IncidentEntity(
    @PrimaryKey(autoGenerate = true)
    val incidentId: Long = 0,
    val tripOwnerId: Long,
    val type: String,
    val severity: String,
    val timeText: String,
    val speedMph: Float,
    val latitude: Double,
    val longitude: Double,
    val timestampMillis: Long
)