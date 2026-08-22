package com.example.driveguard

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.content.pm.ActivityInfo
import android.content.pm.PackageManager
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.HandlerThread
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import kotlinx.coroutines.delay
import kotlin.math.abs
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics

data class DrivingEvent(
    val type: String,
    val severity: String,
    val timeText: String,
    val speedMph: Float,
    val latitude: Double,
    val longitude: Double,
    val timestampMillis: Long
)

@Composable
fun TripScreen(
    activeTripId: Long,
    onFlushRoutePoints: (List<RoutePoint>) -> Unit,
    onStopTrip: (Int, Int, Float, Float, List<RoutePoint>, List<DrivingEvent>) -> Unit
) {
    val context = LocalContext.current
    val activity = context.findActivity()

    DisposableEffect(Unit) {
        val originalOrientation = activity?.requestedOrientation
            ?: ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED

        activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

        onDispose {
            activity?.requestedOrientation = originalOrientation
        }
    }

    LaunchedEffect(Unit) {
        val intent = Intent(context, TripMonitoringService::class.java).apply {
            action = TripMonitoringService.ACTION_START
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(intent)
        } else {
            context.startService(intent)
        }
    }

    val darkGreen = Color(0xFF050E09)
    val cardGreen = Color(0xFF0F1F18)
    val brightGreen = Color(0xFF1D9E75)
    val mutedText = Color(0xFF888780)
    val warningOrange = Color(0xFFEF9F27)
    val dangerRed = Color(0xFFE24B4A)
    val softGreen = Color(0xFF5DCAA5)

    /*
        Detection constants used by DriveGuard during live monitoring.

        speedLimitMph:
        Prototype speed limit. If GPS speed goes above this value, a speeding
        incident can be logged after the cooldown window.

        minimumMovingSpeedMph:
        Sensor-fusion safety check. Accelerometer and gyroscope incidents are only
        logged when GPS speed is at least this value. This reduces false positives
        caused by moving or shaking the phone while the vehicle is stationary.

        harshBrakeThreshold:
        Negative Y-axis accelerometer threshold. Values below this indicate a strong
        forward deceleration pattern and may be logged as harsh braking.

        suddenAccelerationThreshold:
        Positive Y-axis accelerometer threshold. Values above this indicate strong
        acceleration and may be logged as sudden acceleration.

        sharpTurnThreshold:
        Z-axis gyroscope threshold. A large absolute Z rotation suggests a sharp
        turn or sudden steering movement.

        Cooldowns:
        Cooldowns prevent the same behaviour being logged repeatedly within a short
        time window.
    */
    val speedLimitMph = 30f
    val minimumMovingSpeedMph = 5f
    val harshBrakeThreshold = -12f
    val suddenAccelerationThreshold = 12f
    val sharpTurnThreshold = 3f
    val speedingCooldownMillis = 10_000L
    val sensorEventCooldownMillis = 5_000L

    var seconds by remember { mutableIntStateOf(0) }

    var speedMph by remember { mutableFloatStateOf(0f) }
    var maxSpeedMph by remember { mutableFloatStateOf(0f) }

    var latitude by remember { mutableDoubleStateOf(0.0) }
    var longitude by remember { mutableDoubleStateOf(0.0) }

    var distanceMeters by remember { mutableFloatStateOf(0f) }
    var previousLocation by remember { mutableStateOf<Location?>(null) }

    var accelX by remember { mutableFloatStateOf(0f) }
    var accelY by remember { mutableFloatStateOf(0f) }
    var accelZ by remember { mutableFloatStateOf(0f) }

    var gyroX by remember { mutableFloatStateOf(0f) }
    var gyroY by remember { mutableFloatStateOf(0f) }
    var gyroZ by remember { mutableFloatStateOf(0f) }

    var lastSpeedingTime by remember { mutableLongStateOf(0L) }
    var lastBrakeTime by remember { mutableLongStateOf(0L) }
    var lastAccelerationTime by remember { mutableLongStateOf(0L) }
    var lastTurnTime by remember { mutableLongStateOf(0L) }

    val drivingEvents = remember { mutableStateListOf<DrivingEvent>() }
    val routePoints = remember { mutableStateListOf<RoutePoint>() }

    var lastFlushedRoutePointCount by remember { mutableIntStateOf(0) }

    val latestEvent = drivingEvents.lastOrNull()
    val distanceKm = distanceMeters / 1000f

    LaunchedEffect(Unit) {
        while (true) {
            delay(1000)
            seconds++

            if (
                activeTripId != 0L &&
                seconds > 0 &&
                seconds % 30 == 0 &&
                routePoints.size > lastFlushedRoutePointCount
            ) {
                val newRoutePoints = routePoints
                    .drop(lastFlushedRoutePointCount)
                    .toList()

                onFlushRoutePoints(newRoutePoints)

                lastFlushedRoutePointCount = routePoints.size
            }
        }
    }

    DisposableEffect(Unit) {
        val locationManager =
            context.getSystemService(Context.LOCATION_SERVICE) as LocationManager

        val hasPermission =
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED

        val locationListener = object : LocationListener {
            override fun onLocationChanged(location: Location) {
                speedMph = location.speed * 2.23694f

                if (speedMph > maxSpeedMph) {
                    maxSpeedMph = speedMph
                }

                previousLocation?.let { oldLocation ->
                    val distanceAdded = oldLocation.distanceTo(location)

                    if (distanceAdded < 100) {
                        distanceMeters += distanceAdded
                    }
                }

                previousLocation = location

                latitude = location.latitude
                longitude = location.longitude

                val now = System.currentTimeMillis()

                if (location.latitude != 0.0 && location.longitude != 0.0) {
                    routePoints.add(
                        RoutePoint(
                            latitude = location.latitude,
                            longitude = location.longitude,
                            speedMph = speedMph,
                            timestampMillis = now
                        )
                    )
                }

                if (
                    speedMph > speedLimitMph &&
                    now - lastSpeedingTime > speedingCooldownMillis &&
                    latitude != 0.0 &&
                    longitude != 0.0
                ) {
                    drivingEvents.add(
                        DrivingEvent(
                            type = "Speeding",
                            severity = "Medium",
                            timeText = formatSeconds(seconds),
                            speedMph = speedMph,
                            latitude = latitude,
                            longitude = longitude,
                            timestampMillis = now
                        )
                    )
                    lastSpeedingTime = now
                }
            }

            override fun onProviderEnabled(provider: String) {}

            override fun onProviderDisabled(provider: String) {}

            @Deprecated("Deprecated in Java")
            override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {}
        }

        if (hasPermission) {
            locationManager.requestLocationUpdates(
                LocationManager.GPS_PROVIDER,
                1000L,
                1f,
                locationListener
            )
        }

        onDispose {
            locationManager.removeUpdates(locationListener)
        }
    }

    DisposableEffect(Unit) {
        val sensorManager =
            context.getSystemService(Context.SENSOR_SERVICE) as SensorManager

        val sensorThread = HandlerThread("SensorThread").apply {
            start()
        }

        val sensorHandler = Handler(sensorThread.looper)

        val accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        val gyroscope = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE)

        val sensorListener = object : SensorEventListener {
            override fun onSensorChanged(event: SensorEvent?) {
                if (event == null) return

                val now = System.currentTimeMillis()

                /*
                    GPS speed cross-check:
                    Sensor events are ignored if the phone/vehicle is not moving.
                    This reduces false positives caused by shaking or moving the phone by hand.
                */
                val isVehicleMoving = speedMph >= minimumMovingSpeedMph
                val hasValidLocation = latitude != 0.0 && longitude != 0.0

                when (event.sensor.type) {
                    Sensor.TYPE_ACCELEROMETER -> {
                        accelX = event.values[0]
                        accelY = event.values[1]
                        accelZ = event.values[2]

                        if (
                            isVehicleMoving &&
                            hasValidLocation &&
                            accelY < harshBrakeThreshold &&
                            now - lastBrakeTime > sensorEventCooldownMillis
                        ) {
                            drivingEvents.add(
                                DrivingEvent(
                                    type = "Harsh braking",
                                    severity = "High",
                                    timeText = formatSeconds(seconds),
                                    speedMph = speedMph,
                                    latitude = latitude,
                                    longitude = longitude,
                                    timestampMillis = now
                                )
                            )
                            lastBrakeTime = now
                        }

                        if (
                            isVehicleMoving &&
                            hasValidLocation &&
                            accelY > suddenAccelerationThreshold &&
                            now - lastAccelerationTime > sensorEventCooldownMillis
                        ) {
                            drivingEvents.add(
                                DrivingEvent(
                                    type = "Sudden acceleration",
                                    severity = "Medium",
                                    timeText = formatSeconds(seconds),
                                    speedMph = speedMph,
                                    latitude = latitude,
                                    longitude = longitude,
                                    timestampMillis = now
                                )
                            )
                            lastAccelerationTime = now
                        }
                    }

                    Sensor.TYPE_GYROSCOPE -> {
                        gyroX = event.values[0]
                        gyroY = event.values[1]
                        gyroZ = event.values[2]

                        if (
                            isVehicleMoving &&
                            hasValidLocation &&
                            abs(gyroZ) > sharpTurnThreshold &&
                            now - lastTurnTime > sensorEventCooldownMillis
                        ) {
                            drivingEvents.add(
                                DrivingEvent(
                                    type = "Sharp turn",
                                    severity = "Medium",
                                    timeText = formatSeconds(seconds),
                                    speedMph = speedMph,
                                    latitude = latitude,
                                    longitude = longitude,
                                    timestampMillis = now
                                )
                            )
                            lastTurnTime = now
                        }
                    }
                }
            }

            override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
        }

        accelerometer?.let {
            sensorManager.registerListener(
                sensorListener,
                it,
                SensorManager.SENSOR_DELAY_NORMAL,
                sensorHandler
            )
        }

        gyroscope?.let {
            sensorManager.registerListener(
                sensorListener,
                it,
                SensorManager.SENSOR_DELAY_NORMAL,
                sensorHandler
            )
        }

        onDispose {
            sensorManager.unregisterListener(sensorListener)
            sensorThread.quitSafely()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(darkGreen)
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        Text(
            text = "Live monitoring",
            color = Color.White,
            fontSize = 26.sp,
            fontWeight = FontWeight.Bold
        )

        Text(
            text = "Drive safely. Monitoring is active.",
            color = mutedText,
            fontSize = 13.sp,
            modifier = Modifier.padding(top = 4.dp)
        )

        Spacer(modifier = Modifier.height(14.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(145.dp)
                .background(Color(0xFF0A1A10), RoundedCornerShape(18.dp))
                .drawBehind {
                    val w = size.width
                    val h = size.height

                    drawLine(
                        color = Color(0xFF1A3025),
                        start = Offset(w * 0.28f, h),
                        end = Offset(w * 0.43f, 0f),
                        strokeWidth = 7f
                    )

                    drawLine(
                        color = Color(0xFF1A3025),
                        start = Offset(w * 0.72f, h),
                        end = Offset(w * 0.57f, 0f),
                        strokeWidth = 7f
                    )

                    drawLine(
                        color = Color(0xFF4C5B54),
                        start = Offset(w * 0.50f, h),
                        end = Offset(w * 0.50f, 0f),
                        strokeWidth = 3f
                    )
                },
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "Lane detection active",
                    color = Color.White,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "REC",
                    color = dangerRed,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        if (speedMph > speedLimitMph) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF2B130B))
            ) {
                Text(
                    text = "⚠ Speeding detected: %.0f mph".format(speedMph),
                    color = warningOrange,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(14.dp)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            LiveStatCard(
                title = "Speed",
                value = "%.0f mph".format(speedMph),
                modifier = Modifier.weight(1f)
            )

            LiveStatCard(
                title = "Limit",
                value = "%.0f mph".format(speedLimitMph),
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            LiveStatCard(
                title = "Time",
                value = formatSeconds(seconds),
                modifier = Modifier.weight(1f)
            )

            LiveStatCard(
                title = "Distance",
                value = "%.2f km".format(distanceKm),
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = cardGreen)
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Text(
                    text = "GPS route logging",
                    color = Color.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = "Latitude: %.5f".format(latitude),
                    color = mutedText,
                    fontSize = 12.sp
                )

                Text(
                    text = "Longitude: %.5f".format(longitude),
                    color = mutedText,
                    fontSize = 12.sp
                )

                Text(
                    text = "Route points: ${routePoints.size}",
                    color = mutedText,
                    fontSize = 12.sp
                )

                Text(
                    text = "Sensor fusion: GPS speed cross-check active",
                    color = softGreen,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(top = 8.dp)
                )

                Text(
                    text = "Sensor incidents only log above %.0f mph".format(
                        minimumMovingSpeedMph
                    ),
                    color = mutedText,
                    fontSize = 12.sp,
                    modifier = Modifier.padding(top = 2.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            SensorCard(
                title = "Accelerometer",
                line1 = "X: %.1f".format(accelX),
                line2 = "Y: %.1f".format(accelY),
                line3 = "Z: %.1f".format(accelZ),
                modifier = Modifier.weight(1f)
            )

            SensorCard(
                title = "Gyroscope",
                line1 = "X: %.1f".format(gyroX),
                line2 = "Y: %.1f".format(gyroY),
                line3 = "Z: %.1f".format(gyroZ),
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = cardGreen)
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Text(
                    text = "Incidents",
                    color = Color.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = "Total incidents: ${drivingEvents.size}",
                    color = if (drivingEvents.isEmpty()) softGreen else warningOrange,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold
                )

                latestEvent?.let {
                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = "Latest: ${it.type} at ${it.timeText}",
                        color = mutedText,
                        fontSize = 12.sp
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                val stopServiceIntent = Intent(context, TripMonitoringService::class.java).apply {
                    action = TripMonitoringService.ACTION_STOP
                }
                context.startService(stopServiceIntent)

                onStopTrip(
                    seconds,
                    drivingEvents.size,
                    maxSpeedMph,
                    distanceKm,
                    routePoints.toList(),
                    drivingEvents.toList()
                )
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            colors = ButtonDefaults.buttonColors(containerColor = brightGreen),
            shape = RoundedCornerShape(50.dp)
        ) {
            Text(
                text = "Stop Trip",
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            )
        }
    }
}

@Composable
fun LiveStatCard(
    title: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF0F1F18))
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Text(
                text = title,
                color = Color(0xFF888780),
                fontSize = 12.sp
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = value,
                color = Color.White,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun SensorCard(
    title: String,
    line1: String,
    line2: String,
    line3: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF0F1F18))
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(
                text = title,
                color = Color.White,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(text = line1, color = Color(0xFF888780), fontSize = 11.sp)
            Text(text = line2, color = Color(0xFF888780), fontSize = 11.sp)
            Text(text = line3, color = Color(0xFF888780), fontSize = 11.sp)
        }
    }
}

fun formatSeconds(totalSeconds: Int): String {
    val minutes = totalSeconds / 60
    val seconds = totalSeconds % 60
    return "%02d:%02d".format(minutes, seconds)
}

private fun Context.findActivity(): Activity? {
    var context = this

    while (context is ContextWrapper) {
        if (context is Activity) {
            return context
        }

        context = context.baseContext
    }

    return null
}