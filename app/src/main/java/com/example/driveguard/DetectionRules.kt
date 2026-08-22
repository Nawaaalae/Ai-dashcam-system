package com.example.driveguard

/**
 * Pure detection threshold logic for DriveGuard.
 *
 * These constants and helper functions are separated from TripScreen so they can
 * be unit tested and explained clearly in the final report.
 */
object DetectionRules {
    const val SPEED_LIMIT_MPH = 30f
    const val MINIMUM_MOVING_SPEED_MPH = 5f
    const val HARSH_BRAKE_THRESHOLD = -12f
    const val SUDDEN_ACCELERATION_THRESHOLD = 12f
    const val SHARP_TURN_THRESHOLD = 3f

    fun isSpeeding(speedMph: Float): Boolean {
        return speedMph > SPEED_LIMIT_MPH
    }

    fun hasValidLocation(latitude: Double, longitude: Double): Boolean {
        return latitude != 0.0 && longitude != 0.0
    }

    fun isVehicleMoving(speedMph: Float): Boolean {
        return speedMph >= MINIMUM_MOVING_SPEED_MPH
    }

    fun canLogSensorIncident(
        speedMph: Float,
        latitude: Double,
        longitude: Double
    ): Boolean {
        return isVehicleMoving(speedMph) && hasValidLocation(latitude, longitude)
    }

    fun isHarshBraking(accelY: Float): Boolean {
        return accelY < HARSH_BRAKE_THRESHOLD
    }

    fun isSuddenAcceleration(accelY: Float): Boolean {
        return accelY > SUDDEN_ACCELERATION_THRESHOLD
    }

    fun isSharpTurn(gyroZ: Float): Boolean {
        return kotlin.math.abs(gyroZ) > SHARP_TURN_THRESHOLD
    }
}