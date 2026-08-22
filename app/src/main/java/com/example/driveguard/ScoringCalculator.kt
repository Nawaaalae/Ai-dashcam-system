package com.example.driveguard

/**
 * Pure scoring logic for DriveGuard.
 *
 * This class is separated from the UI so it can be tested using local unit tests.
 * The score starts at 100 and subtracts weighted penalties for each unsafe event.
 */
object ScoringCalculator {
    const val SPEEDING_PENALTY = 5
    const val SUDDEN_ACCELERATION_PENALTY = 7
    const val SHARP_TURN_PENALTY = 8
    const val HARSH_BRAKING_PENALTY = 10
    const val UNKNOWN_EVENT_PENALTY = 6
    const val FALLBACK_INCIDENT_PENALTY = 8

    fun calculate(
        drivingEvents: List<DrivingEvent>,
        fallbackIncidentCount: Int = drivingEvents.size
    ): Int {
        val penalty = if (drivingEvents.isEmpty()) {
            fallbackIncidentCount * FALLBACK_INCIDENT_PENALTY
        } else {
            drivingEvents.sumOf { event ->
                when (event.type.lowercase()) {
                    "speeding" -> SPEEDING_PENALTY
                    "sudden acceleration" -> SUDDEN_ACCELERATION_PENALTY
                    "sharp turn" -> SHARP_TURN_PENALTY
                    "harsh braking" -> HARSH_BRAKING_PENALTY
                    else -> UNKNOWN_EVENT_PENALTY
                }
            }
        }

        return (100 - penalty).coerceIn(0, 100)
    }
}