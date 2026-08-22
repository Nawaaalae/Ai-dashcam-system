package com.example.driveguard

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.platform.LocalContext

/**
 * Locks the screen to the given orientation while this composable is on screen,
 * and restores the previous orientation when it leaves the screen.
 *
 * Used on TripScreen to force portrait during active monitoring.
 */
@Composable
fun LockScreenOrientation(orientation: Int) {
    val context = LocalContext.current
    DisposableEffect(orientation) {
        val activity = context.findActivity()
        if (activity == null) {
            onDispose { }
        } else {
            val original = activity.requestedOrientation
            activity.requestedOrientation = orientation
            onDispose {
                activity.requestedOrientation = original
            }
        }
    }
}

/** Walks up the Context wrapper chain to find the host Activity. */
private fun Context.findActivity(): Activity? = when (this) {
    is Activity -> this
    is ContextWrapper -> baseContext.findActivity()
    else -> null
}