package com.example.driveguard

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class TripViewModel : ViewModel() {

    private val _lastTripSeconds = MutableStateFlow(0)
    val lastTripSeconds: StateFlow<Int> = _lastTripSeconds.asStateFlow()

    private val _lastTripIncidentCount = MutableStateFlow(0)
    val lastTripIncidentCount: StateFlow<Int> = _lastTripIncidentCount.asStateFlow()

    private val _lastTripMaxSpeed = MutableStateFlow(0f)
    val lastTripMaxSpeed: StateFlow<Float> = _lastTripMaxSpeed.asStateFlow()

    private val _lastTripDistanceKm = MutableStateFlow(0f)
    val lastTripDistanceKm: StateFlow<Float> = _lastTripDistanceKm.asStateFlow()

    private val _lastTripRoutePointCount = MutableStateFlow(0)
    val lastTripRoutePointCount: StateFlow<Int> = _lastTripRoutePointCount.asStateFlow()

    private val _lastTripEndTimeMillis = MutableStateFlow(0L)
    val lastTripEndTimeMillis: StateFlow<Long> = _lastTripEndTimeMillis.asStateFlow()

    private val _lastIncident = MutableStateFlow<DrivingEvent?>(null)
    val lastIncident: StateFlow<DrivingEvent?> = _lastIncident.asStateFlow()

    private val _lastTripRoutePoints = MutableStateFlow<List<RoutePoint>>(emptyList())
    val lastTripRoutePoints: StateFlow<List<RoutePoint>> = _lastTripRoutePoints.asStateFlow()

    private val _lastTripEvents = MutableStateFlow<List<DrivingEvent>>(emptyList())
    val lastTripEvents: StateFlow<List<DrivingEvent>> = _lastTripEvents.asStateFlow()

    fun saveCompletedTripState(
        seconds: Int,
        incidentCount: Int,
        maxSpeedMph: Float,
        distanceKm: Float,
        routePoints: List<RoutePoint>,
        drivingEvents: List<DrivingEvent>
    ) {
        _lastTripSeconds.value = seconds
        _lastTripIncidentCount.value = incidentCount
        _lastTripMaxSpeed.value = maxSpeedMph
        _lastTripDistanceKm.value = distanceKm
        _lastTripRoutePointCount.value = routePoints.size
        _lastTripRoutePoints.value = routePoints
        _lastTripEvents.value = drivingEvents
        _lastIncident.value = drivingEvents.lastOrNull()
        _lastTripEndTimeMillis.value = System.currentTimeMillis()
    }

    fun clearCurrentTripState() {
        _lastTripSeconds.value = 0
        _lastTripIncidentCount.value = 0
        _lastTripMaxSpeed.value = 0f
        _lastTripDistanceKm.value = 0f
        _lastTripRoutePointCount.value = 0
        _lastTripRoutePoints.value = emptyList()
        _lastTripEvents.value = emptyList()
        _lastIncident.value = null
        _lastTripEndTimeMillis.value = 0L
    }
}