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
import com.example.driveguard.data.entities.IncidentEntity
import com.example.driveguard.data.entities.RoutePointEntity

@Composable
fun SavedTripDetailScreen(
    tripId: Long,
    incidents: List<IncidentEntity>,
    routePoints: List<RoutePointEntity>,
    onBack: () -> Unit
) {
    val darkGreen = Color(0xFF050E09)
    val cardGreen = Color(0xFF0F1F18)
    val brightGreen = Color(0xFF1D9E75)
    val mutedText = Color(0xFF888780)
    val warningOrange = Color(0xFFEF9F27)
    val dangerRed = Color(0xFFE24B4A)
    val softGreen = Color(0xFF5DCAA5)

    val highIncidents = incidents.count { it.severity.equals("High", ignoreCase = true) }
    val mediumIncidents = incidents.count { it.severity.equals("Medium", ignoreCase = true) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(darkGreen)
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        Text(
            text = "Saved trip details",
            color = Color.White,
            fontSize = 26.sp,
            fontWeight = FontWeight.Bold
        )

        Text(
            text = "Room database evidence for Trip ID: $tripId",
            color = mutedText,
            fontSize = 13.sp,
            modifier = Modifier.padding(top = 4.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = cardGreen)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Database saved data",
                    color = Color.White,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = "This confirms the selected trip, incidents, and GPS route points were loaded from Room.",
                    color = mutedText,
                    fontSize = 12.sp,
                    modifier = Modifier.padding(top = 6.dp)
                )

                Spacer(modifier = Modifier.height(14.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    SavedDetailStat(
                        title = "Incidents",
                        value = incidents.size.toString()
                    )

                    SavedDetailStat(
                        title = "Route points",
                        value = routePoints.size.toString()
                    )

                    SavedDetailStat(
                        title = "High severity",
                        value = highIncidents.toString()
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(150.dp)
                .background(Color(0xFF0A1A10), RoundedCornerShape(18.dp))
                .drawBehind {
                    val w = size.width
                    val h = size.height

                    drawLine(
                        color = brightGreen,
                        start = Offset(w * 0.12f, h * 0.72f),
                        end = Offset(w * 0.34f, h * 0.52f),
                        strokeWidth = 6f
                    )

                    drawLine(
                        color = brightGreen,
                        start = Offset(w * 0.34f, h * 0.52f),
                        end = Offset(w * 0.58f, h * 0.64f),
                        strokeWidth = 6f
                    )

                    drawLine(
                        color = brightGreen,
                        start = Offset(w * 0.58f, h * 0.64f),
                        end = Offset(w * 0.86f, h * 0.34f),
                        strokeWidth = 6f
                    )

                    drawCircle(
                        color = softGreen,
                        radius = 10f,
                        center = Offset(w * 0.12f, h * 0.72f)
                    )

                    drawCircle(
                        color = brightGreen,
                        radius = 10f,
                        center = Offset(w * 0.86f, h * 0.34f)
                    )

                    if (incidents.isNotEmpty()) {
                        drawCircle(
                            color = dangerRed,
                            radius = 9f,
                            center = Offset(w * 0.58f, h * 0.64f)
                        )
                    }
                },
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "Saved route overview",
                    color = Color.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = "${routePoints.size} GPS points stored",
                    color = softGreen,
                    fontSize = 12.sp,
                    modifier = Modifier.padding(top = 6.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = cardGreen)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Incident summary",
                    color = Color.White,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    SavedDetailStat(
                        title = "High",
                        value = highIncidents.toString()
                    )

                    SavedDetailStat(
                        title = "Medium",
                        value = mediumIncidents.toString()
                    )

                    SavedDetailStat(
                        title = "Total",
                        value = incidents.size.toString()
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = cardGreen)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Saved incidents",
                    color = Color.White,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(10.dp))

                if (incidents.isEmpty()) {
                    Text(
                        text = "No incidents were saved for this trip.",
                        color = softGreen,
                        fontSize = 13.sp
                    )
                } else {
                    incidents.forEachIndexed { index, incident ->
                        SavedIncidentCard(
                            index = index,
                            incident = incident,
                            mutedText = mutedText,
                            softGreen = softGreen,
                            warningOrange = warningOrange,
                            dangerRed = dangerRed
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = cardGreen)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Saved route points",
                    color = Color.White,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = "First and last GPS points are shown for route evidence.",
                    color = mutedText,
                    fontSize = 12.sp,
                    modifier = Modifier.padding(top = 6.dp)
                )

                Spacer(modifier = Modifier.height(12.dp))

                if (routePoints.isEmpty()) {
                    Text(
                        text = "No GPS route points were saved for this trip.",
                        color = mutedText,
                        fontSize = 13.sp
                    )
                } else {
                    RoutePointEvidenceCard(
                        title = "Start point",
                        point = routePoints.first(),
                        mutedText = mutedText,
                        softGreen = softGreen
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    RoutePointEvidenceCard(
                        title = "End point",
                        point = routePoints.last(),
                        mutedText = mutedText,
                        softGreen = softGreen
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = onBack,
            modifier = Modifier
                .fillMaxWidth()
                .height(54.dp),
            colors = ButtonDefaults.buttonColors(containerColor = brightGreen),
            shape = RoundedCornerShape(50.dp)
        ) {
            Text(
                text = "Back to history",
                color = Color.White,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun SavedIncidentCard(
    index: Int,
    incident: IncidentEntity,
    mutedText: Color,
    softGreen: Color,
    warningOrange: Color,
    dangerRed: Color
) {
    val severityColor = when (incident.severity.lowercase()) {
        "high" -> dangerRed
        "medium" -> warningOrange
        else -> softGreen
    }

    val recommendation = when (incident.type.lowercase()) {
        "speeding" -> "Recommendation: keep within the speed limit and allow more time for the journey."
        "harsh braking" -> "Recommendation: increase following distance and brake earlier."
        "sudden acceleration" -> "Recommendation: accelerate smoothly to improve safety and efficiency."
        "sharp turn" -> "Recommendation: reduce speed before bends and avoid sudden steering."
        else -> "Recommendation: review this event and drive smoothly."
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 10.dp),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF07150F)
        )
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${index + 1}. ${incident.type}",
                    color = Color.White,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold
                )

                Card(
                    shape = RoundedCornerShape(50.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF0F1F18))
                ) {
                    Text(
                        text = incident.severity,
                        color = severityColor,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                    )
                }
            }

            Text(
                text = "Time: ${incident.timeText}",
                color = mutedText,
                fontSize = 12.sp,
                modifier = Modifier.padding(top = 8.dp)
            )

            Text(
                text = "Speed: %.0f mph".format(incident.speedMph),
                color = mutedText,
                fontSize = 12.sp,
                modifier = Modifier.padding(top = 4.dp)
            )

            Text(
                text = "Location: %.5f, %.5f".format(
                    incident.latitude,
                    incident.longitude
                ),
                color = mutedText,
                fontSize = 12.sp,
                modifier = Modifier.padding(top = 4.dp)
            )

            Text(
                text = recommendation,
                color = softGreen,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(top = 8.dp)
            )
        }
    }
}

@Composable
fun RoutePointEvidenceCard(
    title: String,
    point: RoutePointEntity,
    mutedText: Color,
    softGreen: Color
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF07150F))
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(
                text = title,
                color = softGreen,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = "Latitude: %.5f".format(point.latitude),
                color = mutedText,
                fontSize = 12.sp,
                modifier = Modifier.padding(top = 6.dp)
            )

            Text(
                text = "Longitude: %.5f".format(point.longitude),
                color = mutedText,
                fontSize = 12.sp
            )

            Text(
                text = "Speed: %.0f mph".format(point.speedMph),
                color = mutedText,
                fontSize = 12.sp
            )
        }
    }
}

@Composable
fun SavedDetailStat(
    title: String,
    value: String
) {
    Column {
        Text(
            text = title,
            color = Color(0xFF888780),
            fontSize = 12.sp
        )

        Text(
            text = value,
            color = Color.White,
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(top = 4.dp)
        )
    }
}