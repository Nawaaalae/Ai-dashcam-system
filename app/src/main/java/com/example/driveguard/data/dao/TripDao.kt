package com.example.driveguard.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.driveguard.data.entities.IncidentEntity
import com.example.driveguard.data.entities.RoutePointEntity
import com.example.driveguard.data.entities.TripEntity
import kotlinx.coroutines.flow.Flow
import androidx.room.Update

@Dao
interface TripDao {
    @Insert
    suspend fun insertTrip(trip: TripEntity): Long

    @Update
    suspend fun updateTrip(trip: TripEntity)

    @Insert
    suspend fun insertIncident(incident: IncidentEntity)

    @Insert
    suspend fun insertIncidents(incidents: List<IncidentEntity>)

    @Insert
    suspend fun insertRoutePoint(routePoint: RoutePointEntity)

    @Insert
    suspend fun insertRoutePoints(routePoints: List<RoutePointEntity>)

    @Query("SELECT * FROM trips ORDER BY startTimeMillis DESC")
    fun getAllTrips(): Flow<List<TripEntity>>

    @Query("SELECT * FROM incidents WHERE tripOwnerId = :tripId ORDER BY timestampMillis ASC")
    fun getIncidentsForTrip(tripId: Long): Flow<List<IncidentEntity>>

    @Query("SELECT * FROM route_points WHERE tripOwnerId = :tripId ORDER BY timestampMillis ASC")
    fun getRoutePointsForTrip(tripId: Long): Flow<List<RoutePointEntity>>

    @Query("DELETE FROM trips WHERE tripId = :tripId")
    suspend fun deleteTrip(tripId: Long)

    @Query("DELETE FROM trips")
    suspend fun deleteAllTrips()

    @Query("DELETE FROM route_points WHERE tripOwnerId = :tripId")
    suspend fun deleteRoutePointsForTrip(tripId: Long)
}