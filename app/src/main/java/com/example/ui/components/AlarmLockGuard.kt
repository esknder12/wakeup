package com.example.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.example.util.AlarmLockState

/**
 * Keeps the alarm stream pinned for as long as the composable that calls it is on screen,
 * and hands the user's volume back the moment it leaves.
 *
 * Put this at the top of any screen that rings an alarm - it works whether the mission is
 * hosted by the dedicated ringing Activity or shown inside the main app UI.
 *
 * @param active false disables the lock for alarms that opted out of it.
 * @param volumePercent 0f..1f of the device's maximum alarm volume to hold.
 */
@Composable
fun KeepAlarmVolumeLocked(active: Boolean, volumePercent: Float) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    DisposableEffect(active, volumePercent, lifecycleOwner) {
        if (!active) {
            onDispose { }
        } else {
            AlarmLockState.engage(context, volumePercent)

            // Coming back from the shade / another app is a classic escape attempt:
            // re-assert immediately instead of waiting for the next watchdog tick.
            val observer = LifecycleEventObserver { _, event ->
                if (event == Lifecycle.Event.ON_RESUME || event == Lifecycle.Event.ON_START) {
                    AlarmLockState.enforceNow()
                }
            }
            lifecycleOwner.lifecycle.addObserver(observer)

            onDispose {
                lifecycleOwner.lifecycle.removeObserver(observer)
                AlarmLockState.release()
            }
        }
    }
}
