package com.example.driveguard

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun SettingsScreen(
    onBackHome: () -> Unit
) {
    val darkGreen = Color(0xFF050E09)
    val cardGreen = Color(0xFF0F1F18)
    val brightGreen = Color(0xFF1D9E75)
    val softGreen = Color(0xFF5DCAA5)
    val mutedText = Color(0xFF888780)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(darkGreen)
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        Text(
            text = "Settings",
            color = Color.White,
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold
        )

        Text(
            text = "DriveGuard configuration",
            color = mutedText,
            fontSize = 13.sp,
            modifier = Modifier.padding(top = 4.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Detection",
            color = softGreen,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(8.dp))

        SettingsCard {
            SettingValueRow(
                title = "Speed units",
                subtitle = "Used for live monitoring and summaries",
                value = "mph"
            )

            SettingValueRow(
                title = "Speed threshold",
                subtitle = "Speeding alert limit for prototype",
                value = "30 mph"
            )

            SettingSwitchRow(
                title = "Lane detection",
                subtitle = "Camera AI feature planned",
                checked = false
            )

            SettingSwitchRow(
                title = "Amber light detection",
                subtitle = "Traffic-light CV feature planned",
                checked = false
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Privacy & Data",
            color = softGreen,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(8.dp))

        SettingsCard {
            SettingValueRow(
                title = "Data storage",
                subtitle = "Trip, incident, and GPS data is stored locally",
                value = "Room DB"
            )

            SettingValueRow(
                title = "Data retention",
                subtitle = "Delete individual trips from History",
                value = "Manual"
            )

            SettingValueRow(
                title = "Cloud upload",
                subtitle = "No cloud upload during driving",
                value = "Off"
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "About",
            color = softGreen,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(8.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = cardGreen)
        ) {
            Column(
                modifier = Modifier.padding(14.dp)
            ) {
                Text(
                    text = "DriveGuard",
                    color = Color.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = "Android AI driving behaviour monitoring app for final year project.",
                    color = mutedText,
                    fontSize = 12.sp,
                    modifier = Modifier.padding(top = 6.dp)
                )

                Text(
                    text = "Student: Muhammad Nawal Ahmed",
                    color = softGreen,
                    fontSize = 12.sp,
                    modifier = Modifier.padding(top = 8.dp)
                )
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
                text = "Back Home",
                color = Color.White,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun SettingsCard(
    content: @Composable () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF0F1F18))
    ) {
        Column {
            content()
        }
    }
}

@Composable
fun SettingValueRow(
    title: String,
    subtitle: String,
    value: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = title,
                color = Color.White,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = subtitle,
                color = Color(0xFF888780),
                fontSize = 11.sp,
                modifier = Modifier.padding(top = 2.dp)
            )
        }

        Text(
            text = value,
            color = Color(0xFF5DCAA5),
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun SettingSwitchRow(
    title: String,
    subtitle: String,
    checked: Boolean
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = title,
                color = Color.White,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = subtitle,
                color = Color(0xFF888780),
                fontSize = 11.sp,
                modifier = Modifier.padding(top = 2.dp)
            )
        }

        Switch(
            checked = checked,
            onCheckedChange = null
        )
    }
}