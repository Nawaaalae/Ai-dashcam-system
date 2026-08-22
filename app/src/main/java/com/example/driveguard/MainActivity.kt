package com.example.driveguard

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.driveguard.data.DriveGuardDatabase
import com.example.driveguard.data.entities.IncidentEntity
import com.example.driveguard.data.entities.RoutePointEntity
import com.example.driveguard.data.entities.TripEntity
import com.example.driveguard.ui.theme.DriveGuardTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            DriveGuardTheme {
                val tripViewModel: TripViewModel = viewModel()

                var currentScreen by remember { mutableStateOf("home") }
                var selectedTripId by remember { mutableLongStateOf(0L) }
                var activeTripId by remember { mutableLongStateOf(0L) }

                val lastTripSeconds by tripViewModel.lastTripSeconds.collectAsStateWithLifecycle()
                val lastTripIncidentCount by tripViewModel.lastTripIncidentCount.collectAsStateWithLifecycle()
                val lastTripMaxSpeed by tripViewModel.lastTripMaxSpeed.collectAsStateWithLifecycle()
                val lastTripDistanceKm by tripViewModel.lastTripDistanceKm.collectAsStateWithLifecycle()
                val lastTripRoutePointCount by tripViewModel.lastTripRoutePointCount.collectAsStateWithLifecycle()
                val lastTripEndTimeMillis by tripViewModel.lastTripEndTimeMillis.collectAsStateWithLifecycle()
                val lastIncident by tripViewModel.lastIncident.collectAsStateWithLifecycle()
                val lastTripRoutePoints by tripViewModel.lastTripRoutePoints.collectAsStateWithLifecycle()
                val lastTripEvents by tripViewModel.lastTripEvents.collectAsStateWithLifecycle()

                val context = LocalContext.current
                val coroutineScope = rememberCoroutineScope()

                val database = remember {
                    DriveGuardDatabase.getDatabase(context)
                }

                val savedTrips by database
                    .tripDao()
                    .getAllTrips()
                    .collectAsState(initial = emptyList())

                val selectedTripIncidents by database
                    .tripDao()
                    .getIncidentsForTrip(selectedTripId)
                    .collectAsState(initial = emptyList())

                val selectedTripRoutePoints by database
                    .tripDao()
                    .getRoutePointsForTrip(selectedTripId)
                    .collectAsState(initial = emptyList())

                val tripHistory = savedTrips.map { tripEntity ->
                    TripResult(
                        tripId = tripEntity.tripId,
                        durationSeconds = tripEntity.durationSeconds,
                        incidentCount = tripEntity.incidentCount,
                        maxSpeedMph = tripEntity.maxSpeedMph,
                        distanceKm = tripEntity.distanceKm,
                        routePointCount = tripEntity.routePointCount,
                        score = tripEntity.score
                    )
                }

                var hasLocationPermission by remember {
                    mutableStateOf(
                        ContextCompat.checkSelfPermission(
                            context,
                            Manifest.permission.ACCESS_FINE_LOCATION
                        ) == PackageManager.PERMISSION_GRANTED
                    )
                }

                var hasNotificationPermission by remember {
                    mutableStateOf(
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                            ContextCompat.checkSelfPermission(
                                context,
                                Manifest.permission.POST_NOTIFICATIONS
                            ) == PackageManager.PERMISSION_GRANTED
                        } else {
                            true
                        }
                    )
                }

                val locationPermissionLauncher = rememberLauncherForActivityResult(
                    contract = ActivityResultContracts.RequestPermission()
                ) { isGranted ->
                    hasLocationPermission = isGranted
                }

                val notificationPermissionLauncher = rememberLauncherForActivityResult(
                    contract = ActivityResultContracts.RequestPermission()
                ) { isGranted ->
                    hasNotificationPermission = isGranted
                }

                LaunchedEffect(Unit) {
                    if (!hasLocationPermission) {
                        locationPermissionLauncher.launch(
                            Manifest.permission.ACCESS_FINE_LOCATION
                        )
                    }

                    if (
                        Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
                        !hasNotificationPermission
                    ) {
                        notificationPermissionLauncher.launch(
                            Manifest.permission.POST_NOTIFICATIONS
                        )
                    }
                }

                when (currentScreen) {
                    "home" -> HomeScreen(
                        onStartTrip = {
                            val tripStartMillis = System.currentTimeMillis()

                            coroutineScope.launch {
                                val newTripId = withContext(Dispatchers.IO) {
                                    database.tripDao().insertTrip(
                                        TripEntity(
                                            startTimeMillis = tripStartMillis,
                                            endTimeMillis = tripStartMillis,
                                            durationSeconds = 0,
                                            distanceKm = 0f,
                                            maxSpeedMph = 0f,
                                            incidentCount = 0,
                                            routePointCount = 0,
                                            score = 100
                                        )
                                    )
                                }

                                activeTripId = newTripId
                                currentScreen = "trip"
                            }
                        },
                        onOpenHistory = {
                            currentScreen = "history"
                        },
                        onOpenSettings = {
                            currentScreen = "settings"
                        }
                    )

                    "trip" -> TripScreen(
                        activeTripId = activeTripId,
                        onFlushRoutePoints = { newRoutePoints ->
                            val tripId = activeTripId

                            if (tripId != 0L && newRoutePoints.isNotEmpty()) {
                                coroutineScope.launch(Dispatchers.IO) {
                                    val routePointEntities = newRoutePoints.map { point ->
                                        RoutePointEntity(
                                            tripOwnerId = tripId,
                                            latitude = point.latitude,
                                            longitude = point.longitude,
                                            speedMph = point.speedMph,
                                            timestampMillis = point.timestampMillis
                                        )
                                    }

                                    database.tripDao().insertRoutePoints(routePointEntities)
                                }
                            }
                        },
                        onStopTrip = { seconds, incidentCount, maxSpeed, distanceKm, routePoints, drivingEvents ->
                            tripViewModel.saveCompletedTripState(
                                seconds = seconds,
                                incidentCount = incidentCount,
                                maxSpeedMph = maxSpeed,
                                distanceKm = distanceKm,
                                routePoints = routePoints,
                                drivingEvents = drivingEvents
                            )

                            currentScreen = "summary"
                        }
                    )

                    "summary" -> TripSummaryScreen(
                        tripSeconds = lastTripSeconds,
                        incidentCount = lastTripIncidentCount,
                        maxSpeedMph = lastTripMaxSpeed,
                        distanceKm = lastTripDistanceKm,
                        routePointCount = lastTripRoutePointCount,
                        drivingEvents = lastTripEvents,
                        onViewIncident = {
                            currentScreen = "incidentDetail"
                        },
                        onDone = {
                            val score = calculateDriverScore(
                                drivingEvents = lastTripEvents,
                                fallbackIncidentCount = lastTripIncidentCount
                            )

                            val endTimeMillis = lastTripEndTimeMillis
                            val startTimeMillis = endTimeMillis - (lastTripSeconds * 1000L)

                            val tripEntity = TripEntity(
                                startTimeMillis = startTimeMillis,
                                endTimeMillis = endTimeMillis,
                                durationSeconds = lastTripSeconds,
                                distanceKm = lastTripDistanceKm,
                                maxSpeedMph = lastTripMaxSpeed,
                                incidentCount = lastTripIncidentCount,
                                routePointCount = lastTripRoutePointCount,
                                score = score
                            )
                            coroutineScope.launch(Dispatchers.IO) {
                                val tripId = activeTripId

                                if (tripId != 0L) {
                                    database.tripDao().updateTrip(
                                        tripEntity.copy(
                                            tripId = tripId
                                        )
                                    )

                                    database.tripDao().deleteRoutePointsForTrip(tripId)

                                    val incidentEntities = lastTripEvents.map { event ->
                                        IncidentEntity(
                                            tripOwnerId = tripId,
                                            type = event.type,
                                            severity = event.severity,
                                            timeText = event.timeText,
                                            speedMph = event.speedMph,
                                            latitude = event.latitude,
                                            longitude = event.longitude,
                                            timestampMillis = event.timestampMillis
                                        )
                                    }

                                    val routePointEntities = lastTripRoutePoints.map { point ->
                                        RoutePointEntity(
                                            tripOwnerId = tripId,
                                            latitude = point.latitude,
                                            longitude = point.longitude,
                                            speedMph = point.speedMph,
                                            timestampMillis = point.timestampMillis
                                        )
                                    }

                                    database.tripDao().insertIncidents(incidentEntities)
                                    database.tripDao().insertRoutePoints(routePointEntities)

                                    activeTripId = 0L
                                    tripViewModel.clearCurrentTripState()
                                }
                            }

                            currentScreen = "home"
                        }
                    )

                    "incidentDetail" -> IncidentDetailScreen(
                        incidentType = lastIncident?.type ?: "Unsafe driving event",
                        severity = lastIncident?.severity ?: "Medium",
                        timeText = lastIncident?.timeText ?: "During trip",
                        speedMph = lastIncident?.speedMph ?: lastTripMaxSpeed,
                        latitude = lastIncident?.latitude ?: 0.0,
                        longitude = lastIncident?.longitude ?: 0.0,
                        onBack = {
                            currentScreen = "summary"
                        }
                    )

                    "history" -> HistoryScreen(
                        trips = tripHistory,
                        onTripClick = { tripId ->
                            selectedTripId = tripId
                            currentScreen = "savedTripDetail"
                        },
                        onDeleteTrip = { tripId ->
                            coroutineScope.launch(Dispatchers.IO) {
                                database.tripDao().deleteTrip(tripId)
                            }
                        },
                        onBackHome = {
                            currentScreen = "home"
                        }
                    )

                    "savedTripDetail" -> SavedTripDetailScreen(
                        tripId = selectedTripId,
                        incidents = selectedTripIncidents,
                        routePoints = selectedTripRoutePoints,
                        onBack = {
                            currentScreen = "history"
                        }
                    )

                    "settings" -> SettingsScreen(
                        onBackHome = {
                            currentScreen = "home"
                        }
                    )
                }
            }
        }
    }
}