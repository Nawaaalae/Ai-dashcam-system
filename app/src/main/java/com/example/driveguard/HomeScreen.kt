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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll

@Composable
fun HomeScreen(
    onStartTrip: () -> Unit,
    onOpenHistory: () -> Unit,
    onOpenSettings: () -> Unit
) {
    val darkGreen = Color(0xFF050E09)
    val cardGreen = Color(0xFF0F1F18)
    val brightGreen = Color(0xFF1D9E75)
    val softGreen = Color(0xFF5DCAA5)
    val mutedText = Color(0xFF888780)
    val warningOrange = Color(0xFFEF9F27)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(darkGreen)
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        Text(
            text = "DriveGuard",
            color = Color.White,
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold
        )

        Text(
            text = "Good morning, Ahmed",
            color = softGreen,
            fontSize = 14.sp,
            modifier = Modifier.padding(top = 4.dp)
        )

        Spacer(modifier = Modifier.height(20.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = cardGreen)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Your driver score",
                    color = softGreen,
                    fontSize = 14.sp
                )

                Text(
                    text = "82",
                    color = Color.White,
                    fontSize = 64.sp,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = "Good driver",
                    color = brightGreen,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        Button(
            onClick = onStartTrip,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            colors = ButtonDefaults.buttonColors(containerColor = brightGreen),
            shape = RoundedCornerShape(50.dp)
        ) {
            Text(
                text = "Start new trip",
                color = Color.White,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(18.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            MiniStatCard(
                value = "14",
                label = "trips",
                modifier = Modifier.weight(1f)
            )

            MiniStatCard(
                value = "312",
                label = "km driven",
                modifier = Modifier.weight(1f)
            )

            MiniStatCard(
                value = "3",
                label = "incidents",
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(22.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Recent trips",
                color = Color.White,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = "See all",
                color = brightGreen,
                fontSize = 14.sp
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        TripPreviewRow(
            name = "Morning commute",
            details = "Today · 8.2 km · 24 min",
            score = "91",
            scoreColor = brightGreen
        )

        TripPreviewRow(
            name = "City centre",
            details = "Yesterday · 5.1 km · 19 min",
            score = "74",
            scoreColor = warningOrange
        )

        TripPreviewRow(
            name = "Evening drive",
            details = "Mon · 12.4 km · 31 min",
            score = "61",
            scoreColor = Color(0xFFE24B4A)
        )

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Button(
                onClick = onOpenHistory,
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(containerColor = cardGreen)
            ) {
                Text("History", color = Color.White)
            }

            Button(
                onClick = onOpenSettings,
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(containerColor = cardGreen)
            ) {
                Text("Settings", color = Color.White)
            }
        }
    }
}

@Composable
fun MiniStatCard(
    value: String,
    label: String,
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
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = value,
                color = Color.White,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = label,
                color = Color(0xFF5DCAA5),
                fontSize = 11.sp
            )
        }
    }
}

@Composable
fun TripPreviewRow(
    name: String,
    details: String,
    score: String,
    scoreColor: Color
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 8.dp),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF0F1F18))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .background(Color(0xFF1A2E24), RoundedCornerShape(10.dp))
                    .padding(10.dp)
            ) {
                Text(text = "🚗")
            }

            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 12.dp)
            ) {
                Text(
                    text = name,
                    color = Color.White,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold
                )

                Text(
                    text = details,
                    color = Color(0xFF888780),
                    fontSize = 11.sp
                )
            }

            Text(
                text = score,
                color = scoreColor,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}
