package com.example.driveguard

import org.junit.Assert.assertEquals
import org.junit.Test

class ScoringCalculatorTest {

    private fun event(type: String): DrivingEvent {
        return DrivingEvent(
            type = type,
            severity = "Medium",
            timeText = "00:10",
            speedMph = 20f,
            latitude = 54.0,
            longitude = -6.0,
            timestampMillis = 1000L
        )
    }

    @Test
    fun calculate_noEvents_returns100() {
        val score = ScoringCalculator.calculate(
            drivingEvents = emptyList(),
            fallbackIncidentCount = 0
        )

        assertEquals(100, score)
    }

    @Test
    fun calculate_fiveFallbackIncidents_returns60() {
        val score = ScoringCalculator.calculate(
            drivingEvents = emptyList(),
            fallbackIncidentCount = 5
        )

        assertEquals(60, score)
    }

    @Test
    fun calculate_manyFallbackIncidents_doesNotGoBelowZero() {
        val score = ScoringCalculator.calculate(
            drivingEvents = emptyList(),
            fallbackIncidentCount = 13
        )

        assertEquals(0, score)
    }

    @Test
    fun calculate_speedingEvent_appliesSpeedingPenalty() {
        val score = ScoringCalculator.calculate(
            drivingEvents = listOf(event("Speeding"))
        )

        assertEquals(95, score)
    }

    @Test
    fun calculate_harshBrakingEvent_appliesHarshBrakingPenalty() {
        val score = ScoringCalculator.calculate(
            drivingEvents = listOf(event("Harsh braking"))
        )

        assertEquals(90, score)
    }

    @Test
    fun calculate_multipleEvents_appliesWeightedPenalties() {
        val score = ScoringCalculator.calculate(
            drivingEvents = listOf(
                event("Speeding"),
                event("Sudden acceleration"),
                event("Sharp turn"),
                event("Harsh braking")
            )
        )

        assertEquals(70, score)
    }
}