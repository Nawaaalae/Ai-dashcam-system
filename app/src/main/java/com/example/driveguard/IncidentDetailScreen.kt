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
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll

@Composable
fun IncidentDetailScreen(
    incidentType: String,
    severity: String,
    timeText: String,
    speedMph: Float,
    latitude: Double,
    longitude: Double,
    onBack: () -> Unit
) {
    val darkGreen = Color(0xFF050E09)
    val cardGreen = Color(0xFF0F1F18)
    val brightGreen = Color(0xFF1D9E75)
    val softGreen = Color(0xFF5DCAA5)
    val mutedText = Color(0xFF888780)
    val warningOrange = Color(0xFFEF9F27)
    val dangerRed = Color(0xFFE24B4A)

    val severityColor = when (severity) {
        "High" -> dangerRed
        "Medium" -> warningOrange
        else -> softGreen
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(darkGreen)
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        Text(
            text = "Incident detail",
            color = Color.White,
            fontSize = 26.sp,
            fontWeight = FontWeight.Bold
        )

        Text(
            text = "DriveGuard unsafe event record",
            color = mutedText,
            fontSize = 13.sp,
            modifier = Modifier.padding(top = 4.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(150.dp)
                .background(Color(0xFF0A1A10), RoundedCornerShape(16.dp))
                .drawBehind {
                    val w = size.width
                    val h = size.height

                    drawLine(
                        color = Color(0xFF1A3025),
                        start = Offset(0f, h * 0.55f),
                        end = Offset(w, h * 0.55f),
                        strokeWidth = 32f
                    )

                    drawLine(
                        color = Color(0xFF444441),
                        start = Offset(w * 0.15f, h * 0.55f),
                        end = Offset(w * 0.35f, h * 0.55f),
                        strokeWidth = 3f
                    )

                    drawLine(
                        color = Color(0xFF444441),
                        start = Offset(w * 0.60f, h * 0.55f),
                        end = Offset(w * 0.80f, h * 0.55f),
                        strokeWidth = 3f
                    )

                    drawCircle(
                        color = severityColor,
                        radius = 16f,
                        center = Offset(w * 0.50f, h * 0.55f)
                    )
                },
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Map pin",
                color = Color.White,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = cardGreen)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(18.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "⚠",
                    color = severityColor,
                    fontSize = 34.sp
                )

                Column(
                    modifier = Modifier.padding(start = 14.dp)
                ) {
                    Text(
                        text = incidentType,
                        color = Color.White,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Text(
                        text = "$severity severity",
                        color = severityColor,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        DetailRowsCard(
            rows = listOf(
                "Time" to timeText,
                "Speed" to "%.0f mph".format(speedMph),
                "Latitude" to "%.5f".format(latitude),
                "Longitude" to "%.5f".format(longitude),
                "Score impact" to "-8 points"
            )
        )

        Spacer(modifier = Modifier.height(16.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF1F1400))
        ) {
            Column(
                modifier = Modifier.padding(14.dp)
            ) {
                Text(
                    text = "Recommendation",
                    color = warningOrange,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = "DriveGuard recorded this event for post-trip review. Avoid interacting with the app while driving.",
                    color = Color(0xFFB87318),
                    fontSize = 12.sp,
                    modifier = Modifier.padding(top = 6.dp)
                )
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
                text = "Back to summary",
                color = Color.White,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun DetailRowsCard(
    rows: List<Pair<String, String>>
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF0F1F18))
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            rows.forEach { row ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = row.first,
                        color = Color(0xFF888780),
                        fontSize = 13.sp
                    )

                    Text(
                        text = row.second,
                        color = Color.White,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}