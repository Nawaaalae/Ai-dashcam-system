package com.example.driveguard

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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun TripSummaryScreen(
    tripSeconds: Int,
    incidentCount: Int,
    maxSpeedMph: Float,
    distanceKm: Float,
    routePointCount: Int,
    drivingEvents: List<DrivingEvent> = emptyList(),
    onViewIncident: () -> Unit,
    onDone: () -> Unit
) {
    val darkGreen = Color(0xFF050E09)
    val cardGreen = Color(0xFF0F1F18)
    val brightGreen = Color(0xFF1D9E75)
    val softGreen = Color(0xFF5DCAA5)
    val mutedText = Color(0xFF9C9A92)
    val warningOrange = Color(0xFFEF9F27)
    val dangerRed = Color(0xFFE24B4A)

    val score = calculateDriverScore(
        drivingEvents = drivingEvents,
        fallbackIncidentCount = incidentCount
    )

    val minutes = tripSeconds / 60
    val seconds = tripSeconds % 60
    val durationText = "%02d:%02d".format(minutes, seconds)

    val scoreColor = when {
        score >= 85 -> brightGreen
        score >= 70 -> softGreen
        score >= 50 -> warningOrange
        else -> dangerRed
    }

    val scoreGrade = when {
        score >= 85 -> "Excellent"
        score >= 70 -> "Good"
        score >= 50 -> "Needs improvement"
        else -> "Poor"
    }

    val scoreExplanation = if (drivingEvents.isEmpty() && incidentCount > 0) {
        "Score uses incident count fallback because no full event list was available."
    } else {
        "Weighted score based on event type severity."
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(darkGreen)
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        Text(
            text = "Trip summary",
            color = Color.White,
            fontSize = 26.sp,
            fontWeight = FontWeight.Bold
        )

        Text(
            text = "DriveGuard trip completed",
            color = mutedText,
            fontSize = 13.sp,
            modifier = Modifier.padding(top = 4.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(140.dp)
                .background(Color(0xFF0A1A10), RoundedCornerShape(16.dp))
                .drawBehind {
                    val w = size.width
                    val h = size.height

                    drawLine(
                        color = brightGreen,
                        start = Offset(w * 0.10f, h * 0.75f),
                        end = Offset(w * 0.30f, h * 0.55f),
                        strokeWidth = 6f
                    )

                    drawLine(
                        color = brightGreen,
                        start = Offset(w * 0.30f, h * 0.55f),
                        end = Offset(w * 0.55f, h * 0.62f),
                        strokeWidth = 6f
                    )

                    drawLine(
                        color = brightGreen,
                        start = Offset(w * 0.55f, h * 0.62f),
                        end = Offset(w * 0.85f, h * 0.30f),
                        strokeWidth = 6f
                    )

                    drawCircle(
                        color = softGreen,
                        radius = 10f,
                        center = Offset(w * 0.10f, h * 0.75f)
                    )

                    drawCircle(
                        color = brightGreen,
                        radius = 10f,
                        center = Offset(w * 0.85f, h * 0.30f)
                    )

                    if (incidentCount > 0) {
                        drawCircle(
                            color = dangerRed,
                            radius = 9f,
                            center = Offset(w * 0.55f, h * 0.62f)
                        )
                    }
                },
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Route overview",
                color = softGreen,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = cardGreen)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(22.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "$score",
                    color = scoreColor,
                    fontSize = 58.sp,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = "/100",
                    color = mutedText,
                    fontSize = 16.sp
                )

                Text(
                    text = scoreGrade,
                    color = scoreColor,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(top = 4.dp)
                )

                Text(
                    text = scoreExplanation,
                    color = mutedText,
                    fontSize = 11.sp,
                    modifier = Modifier.padding(top = 10.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            SummaryStatCard(
                label = "Duration",
                value = durationText,
                modifier = Modifier.weight(1f)
            )

            SummaryStatCard(
                label = "Route points",
                value = routePointCount.toString(),
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            SummaryStatCard(
                label = "Max speed",
                value = "%.0f mph".format(maxSpeedMph),
                modifier = Modifier.weight(1f)
            )

            SummaryStatCard(
                label = "Distance",
                value = "%.2f km".format(distanceKm),
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        SummaryStatCard(
            label = "Incidents",
            value = "$incidentCount",
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = cardGreen)
        ) {
            Column(
                modifier = Modifier.padding(14.dp)
            ) {
                Text(
                    text = "Scoring weights",
                    color = Color.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Speeding: -5 points",
                    color = mutedText,
                    fontSize = 12.sp
                )

                Text(
                    text = "Sudden acceleration: -7 points",
                    color = mutedText,
                    fontSize = 12.sp
                )

                Text(
                    text = "Sharp turn: -8 points",
                    color = mutedText,
                    fontSize = 12.sp
                )

                Text(
                    text = "Harsh braking: -10 points",
                    color = mutedText,
                    fontSize = 12.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = cardGreen)
        ) {
            Column(
                modifier = Modifier.padding(14.dp)
            ) {
                Text(
                    text = "Incidents detected",
                    color = Color.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )

                if (incidentCount > 0) {
                    Text(
                        text = "Total unsafe driving events: $incidentCount",
                        color = warningOrange,
                        fontSize = 14.sp,
                        modifier = Modifier.padding(top = 8.dp)
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Button(
                        onClick = onViewIncident,
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1F1400)),
                        shape = RoundedCornerShape(50.dp)
                    ) {
                        Text(
                            text = "View incident detail",
                            color = warningOrange,
                            fontWeight = FontWeight.Bold
                        )
                    }
                } else {
                    Text(
                        text = "No unsafe driving events detected.",
                        color = softGreen,
                        fontSize = 14.sp,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = onDone,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            colors = ButtonDefaults.buttonColors(containerColor = brightGreen),
            shape = RoundedCornerShape(50.dp)
        ) {
            Text(
                text = "Done",
                color = Color.White,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun SummaryStatCard(
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF0F1F18))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp)
        ) {
            Text(
                text = label.uppercase(),
                color = Color(0xFF5DCAA5),
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 1
            )

            Text(
                text = value,
                color = Color.White,
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(top = 6.dp)
            )
        }
    }
}

/**
 * Calculates a DriveGuard driver score from detected driving events.
 *
 * The score starts at 100 and subtracts weighted penalties based on event type:
 * - Speeding: 5 points
 * - Sudden acceleration: 7 points
 * - Sharp turn: 8 points
 * - Harsh braking: 10 points
 *
 * Harsh braking has the highest penalty because it is usually linked to poor
 * anticipation, short following distance, or sudden risk response. Speeding has
 * a lower repeated penalty because it may occur across several GPS updates, so
 * cooldown logic prevents over-penalising the same behaviour.
 *
 * If no full event list is available, the function falls back to a simple
 * incident-count formula for backward compatibility with older saved trips.
 */
fun calculateDriverScore(
    drivingEvents: List<DrivingEvent>,
    fallbackIncidentCount: Int = drivingEvents.size
): Int {
    return ScoringCalculator.calculate(
        drivingEvents = drivingEvents,
        fallbackIncidentCount = fallbackIncidentCount
    )
}

/**
 * Backward-compatible score calculation for older parts of the app that only
 * provide an incident count instead of a full event list.
 */
fun calculateDriverScore(incidentCount: Int): Int {
    return ScoringCalculator.calculate(
        drivingEvents = emptyList(),
        fallbackIncidentCount = incidentCount
    )
}