package com.example.driveguard

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class DetectionRulesTest {

    @Test
    fun isSpeeding_atSpeedLimit_returnsFalse() {
        assertFalse(DetectionRules.isSpeeding(30f))
    }

    @Test
    fun isSpeeding_aboveSpeedLimit_returnsTrue() {
        assertTrue(DetectionRules.isSpeeding(31f))
    }

    @Test
    fun isVehicleMoving_belowMinimumSpeed_returnsFalse() {
        assertFalse(DetectionRules.isVehicleMoving(4.9f))
    }

    @Test
    fun isVehicleMoving_atMinimumSpeed_returnsTrue() {
        assertTrue(DetectionRules.isVehicleMoving(5f))
    }

    @Test
    fun isHarshBraking_atThreshold_returnsFalse() {
        assertFalse(DetectionRules.isHarshBraking(-12f))
    }

    @Test
    fun isHarshBraking_belowThreshold_returnsTrue() {
        assertTrue(DetectionRules.isHarshBraking(-12.1f))
    }

    @Test
    fun isSuddenAcceleration_atThreshold_returnsFalse() {
        assertFalse(DetectionRules.isSuddenAcceleration(12f))
    }

    @Test
    fun isSuddenAcceleration_aboveThreshold_returnsTrue() {
        assertTrue(DetectionRules.isSuddenAcceleration(12.1f))
    }

    @Test
    fun isSharpTurn_atThreshold_returnsFalse() {
        assertFalse(DetectionRules.isSharpTurn(3f))
    }

    @Test
    fun isSharpTurn_aboveThreshold_returnsTrue() {
        assertTrue(DetectionRules.isSharpTurn(3.1f))
    }

    @Test
    fun canLogSensorIncident_withoutValidLocation_returnsFalse() {
        assertFalse(
            DetectionRules.canLogSensorIncident(
                speedMph = 10f,
                latitude = 0.0,
                longitude = 0.0
            )
        )
    }

    @Test
    fun canLogSensorIncident_movingWithValidLocation_returnsTrue() {
        assertTrue(
            DetectionRules.canLogSensorIncident(
                speedMph = 10f,
                latitude = 54.123,
                longitude = -6.123
            )
        )
    }
}