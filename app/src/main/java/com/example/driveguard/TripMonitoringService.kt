package com.example.driveguard

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat

class TripMonitoringService : Service() {

    companion object {
        const val CHANNEL_ID = "driveguard_trip_monitoring"
        const val NOTIFICATION_ID = 1001
        const val ACTION_START = "com.example.driveguard.START_TRIP_MONITORING"
        const val ACTION_STOP = "com.example.driveguard.STOP_TRIP_MONITORING"
    }

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_START -> {
                startForeground(
                    NOTIFICATION_ID,
                    buildNotification()
                )
            }

            ACTION_STOP -> {
                stopForeground(STOP_FOREGROUND_REMOVE)
                stopSelf()
            }
        }

        return START_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    private fun buildNotification(): Notification {
        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_menu_mylocation)
            .setContentTitle("DriveGuard trip active")
            .setContentText("Monitoring GPS speed and driving behaviour.")
            .setOngoing(true)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .build()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Trip monitoring",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "Shows when DriveGuard is monitoring a trip"
            }

            val notificationManager =
                getSystemService(NotificationManager::class.java)

            notificationManager.createNotificationChannel(channel)
        }
    }
}