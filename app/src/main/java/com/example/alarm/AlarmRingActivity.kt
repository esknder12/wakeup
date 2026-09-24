package com.example.alarm

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.KeyEvent
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.addCallback
import androidx.activity.compose.setContent
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.lifecycleScope
import com.example.data.db.AlarmDatabase
import com.example.data.model.AlarmItem
import com.example.data.model.AlarmySoundCatalog
import com.example.ui.screens.AlarmMissionScreen
import com.example.ui.theme.MyApplicationTheme
import com.example.util.AlarmLockState
import com.example.util.SoundEngine
import kotlinx.coroutines.launch

/**
 * Full screen "the alarm is ringing" surface.
 *
 * This is the screen that makes the volume keys inert: every key press a sleepy user could
 * use to silence the alarm is swallowed in [dispatchKeyEvent], the alarm stream itself is
 * held at the configured level by [AlarmLockState], and the only exit is finishing the
 * mission (which calls back into [dismissAlarm]).
 *
 * Launch it with [start] from an AlarmManager receiver or a full screen notification intent.
 */
class AlarmRingActivity : ComponentActivity() {

    private var alarm by mutableStateOf<AlarmItem?>(null)
    private var soundEngine: SoundEngine? = null
    private var isDismissing = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        showOverLockScreen()

        // Back key / back gesture must not be an escape hatch while the mission is unsolved.
        onBackPressedDispatcher.addCallback(this) {
            if (!AlarmLockState.isLocked) {
                finish()
            }
        }

        setContent {
            MyApplicationTheme {
                alarm?.let { ringing ->
                    AlarmMissionScreen(
                        alarm = ringing,
                        onDismiss = { dismissAlarm() }
                    )
                }
            }
        }

        val alarmId = intent.getIntExtra(EXTRA_ALARM_ID, INVALID_ALARM_ID)
        lifecycleScope.launch {
            val loaded = AlarmDatabase.getDatabase(applicationContext)
                .alarmDao()
                .getAlarmById(alarmId)

            if (loaded == null) {
                finish()
                return@launch
            }

            alarm = loaded
            startRinging(loaded)
        }
    }

    private fun startRinging(ringing: AlarmItem) {
        // The mission screen also holds the lock; both share one reference counted lock, so
        // engaging here simply guarantees the volume is pinned before the first note plays.
        if (ringing.isVolumeButtonLockEnabled) {
            AlarmLockState.engage(applicationContext, ringing.soundVolume)
        }

        val ringtone = AlarmySoundCatalog.allSounds.firstOrNull { it.title == ringing.ringtoneName }
            ?: AlarmySoundCatalog.getAlarmRingtones().first()

        soundEngine = SoundEngine(applicationContext).also {
            it.playSound(ringtone, ringing.soundVolume)
        }
    }

    /** Called only when the mission (and any wake up check) has actually been completed. */
    private fun dismissAlarm() {
        isDismissing = true
        soundEngine?.destroy()
        soundEngine = null
        AlarmLockState.releaseAll()
        finish()
    }

    /**
     * Swallows volume, media and navigation keys while the alarm is locked. Returning true
     * stops the system ever seeing the press, so Volume Down cannot turn the alarm down.
     */
    override fun dispatchKeyEvent(event: KeyEvent): Boolean {
        if (AlarmLockState.handleKeyEvent(event)) return true
        return super.dispatchKeyEvent(event)
    }

    /**
     * Home / recents cannot be intercepted as key events, so if the user leaves while the
     * mission is unsolved we simply come straight back to the front.
     */
    override fun onUserLeaveHint() {
        super.onUserLeaveHint()
        val ringing = alarm ?: return
        if (isDismissing || !ringing.isEscapeBlockEnabled || !AlarmLockState.isLocked) return

        startActivity(
            createIntent(this, ringing.id)
                .addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT)
        )
    }

    override fun onResume() {
        super.onResume()
        AlarmLockState.enforceNow()
    }

    override fun onDestroy() {
        super.onDestroy()
        soundEngine?.destroy()
        soundEngine = null
        // Never leave the device stuck with a locked volume if we are torn down unexpectedly.
        AlarmLockState.releaseAll()
    }

    private fun showOverLockScreen() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
            setShowWhenLocked(true)
            setTurnScreenOn(true)
        } else {
            @Suppress("DEPRECATION")
            window.addFlags(
                WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED or
                    WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON or
                    WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD
            )
        }
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
    }

    companion object {
        const val EXTRA_ALARM_ID = "com.example.alarm.EXTRA_ALARM_ID"
        private const val INVALID_ALARM_ID = -1

        fun createIntent(context: Context, alarmId: Int): Intent =
            Intent(context, AlarmRingActivity::class.java)
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                .putExtra(EXTRA_ALARM_ID, alarmId)

        /** Rings [alarmId] full screen with the volume keys locked. */
        fun start(context: Context, alarmId: Int) {
            context.startActivity(createIntent(context, alarmId))
        }
    }
}
