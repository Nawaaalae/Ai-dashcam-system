package com.example.driveguard

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun HistoryScreen(
    trips: List<TripResult>,
    onTripClick: (Long) -> Unit,
    onDeleteTrip: (Long) -> Unit,
    onBackHome: () -> Unit
) {
    val darkGreen = Color(0xFF050E09)
    val cardGreen = Color(0xFF0F1F18)
    val brightGreen = Color(0xFF1D9E75)
    val mutedText = Color(0xFF888780)
    val warningOrange = Color(0xFFEF9F27)
    val dangerRed = Color(0xFFE24B4A)
    val softGreen = Color(0xFF5DCAA5)

    var showDeleteDialog by remember { mutableStateOf(false) }
    var tripToDeleteId by remember { mutableLongStateOf(0L) }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = {
                showDeleteDialog = false
            },
            title = {
                Text(text = "Delete this trip?")
            },
            text = {
                Text(
                    text = "This will permanently delete this trip, its incidents, and its GPS route points from this phone."
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        onDeleteTrip(tripToDeleteId)
                        showDeleteDialog = false
                    }
                ) {
                    Text(
                        text = "Delete",
                        color = dangerRed,
                        fontWeight = FontWeight.Bold
                    )
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showDeleteDialog = false
                    }
                ) {
                    Text(text = "Cancel")
                }
            }
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(darkGreen)
            .padding(16.dp)
    ) {
        Text(
            text = "Trip history",
            color = Color.White,
            fontSize = 26.sp,
            fontWeight = FontWeight.Bold
        )

        Text(
            text = "Tap a trip to view saved details",
            color = mutedText,
            fontSize = 13.sp,
            modifier = Modifier.padding(top = 4.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        if (trips.isEmpty()) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = cardGreen)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "No trips yet",
                        color = Color.White,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Text(
                        text = "Complete a trip and it will appear here.",
                        color = mutedText,
                        fontSize = 13.sp,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }
            }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.weight(1f)
            ) {
                itemsIndexed(trips) { index, trip ->
                    val scoreColor = when {
                        trip.score >= 80 -> softGreen
                        trip.score >= 60 -> warningOrange
                        else -> dangerRed
                    }

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                onTripClick(trip.tripId)
                            },
                        shape = RoundedCornerShape(18.dp),
                        colors = CardDefaults.cardColors(containerColor = cardGreen)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(
                                        text = "Trip ${trips.size - index}",
                                        color = Color.White,
                                        fontSize = 18.sp,
                                        fontWeight = FontWeight.Bold
                                    )

                                    Text(
                                        text = "Duration: ${formatSeconds(trip.durationSeconds)}",
                                        color = mutedText,
                                        fontSize = 12.sp,
                                        modifier = Modifier.padding(top = 4.dp)
                                    )
                                }

                                Card(
                                    shape = RoundedCornerShape(50.dp),
                                    colors = CardDefaults.cardColors(
                                        containerColor = Color(0xFF07150F)
                                    )
                                ) {
                                    Text(
                                        text = "${trip.score}",
                                        color = scoreColor,
                                        fontSize = 18.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(
                                            horizontal = 14.dp,
                                            vertical = 8.dp
                                        )
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                HistorySmallStat(
                                    title = "Distance",
                                    value = "%.2f km".format(trip.distanceKm)
                                )

                                HistorySmallStat(
                                    title = "Max speed",
                                    value = "%.0f mph".format(trip.maxSpeedMph)
                                )

                                HistorySmallStat(
                                    title = "Incidents",
                                    value = trip.incidentCount.toString()
                                )
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            Text(
                                text = "Route points: ${trip.routePointCount}",
                                color = mutedText,
                                fontSize = 12.sp
                            )

                            Spacer(modifier = Modifier.height(10.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Tap card to view saved trip data",
                                    color = brightGreen,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )

                                Button(
                                    onClick = {
                                        tripToDeleteId = trip.tripId
                                        showDeleteDialog = true
                                    },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = Color(0xFF2B130B)
                                    ),
                                    shape = RoundedCornerShape(50.dp)
                                ) {
                                    Text(
                                        text = "Delete",
                                        color = dangerRed,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = onBackHome,
            modifier = Modifier
                .fillMaxWidth()
                .height(54.dp),
            colors = ButtonDefaults.buttonColors(containerColor = brightGreen),
            shape = RoundedCornerShape(50.dp)
        ) {
            Text(
                text = "Back home",
                color = Color.White,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun HistorySmallStat(
    title: String,
    value: String
) {
    Column {
        Text(
            text = title,
            color = Color(0xFF888780),
            fontSize = 11.sp
        )

        Text(
            text = value,
            color = Color.White,
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(top = 3.dp)
        )
    }
}