package com.example.util

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.database.ContentObserver
import android.media.AudioAttributes
import android.media.AudioFocusRequest
import android.media.AudioManager
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import android.util.Log
import android.view.KeyEvent
import androidx.core.content.ContextCompat
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

/**
 * Holds the ALARM audio stream pinned at the alarm's volume while a mission is ringing.
 *
 * A half-asleep user has three ways to kill an alarm without waking up:
 *   1. Hardware Volume Down (handled by swallowing the key events - see [AlarmLockState]).
 *   2. The volume slider in the notification shade / quick settings.
 *   3. Anything else that calls into AudioManager (Bixby, assistant, accessibility, OEM gestures).
 *
 * Key swallowing alone only covers (1), so this class additionally *watches* the stream and
 * snaps it straight back to the locked level whenever something lowers or mutes it:
 *   - a [ContentObserver] on Settings.System fires the instant the system volume changes,
 *   - a receiver for VOLUME_CHANGED_ACTION catches OEM paths that skip settings,
 *   - a [WATCHDOG_INTERVAL_MS] poll is the backstop for everything else.
 *
 * The user's original volume is captured on [engage] and restored on [release], so silencing
 * the alarm correctly leaves the phone exactly as loud as it was before it rang.
 */
class AlarmVolumeLock(context: Context) {

    private val appContext: Context = context.applicationContext
    private val audioManager: AudioManager =
        appContext.getSystemService(Context.AUDIO_SERVICE) as AudioManager
    private val scope = CoroutineScope(Dispatchers.Default + SupervisorJob())

    @Volatile
    private var engaged: Boolean = false

    /** Volume the user had before the alarm went off; restored on release. */
    private var previousVolume: Int? = null

    /** The level the alarm stream is force-held at while locked. */
    private var lockedVolume: Int = 0

    private var watchdogJob: Job? = null
    private var volumeObserver: ContentObserver? = null
    private var volumeReceiver: BroadcastReceiver? = null
    private var focusRequest: Any? = null // AudioFocusRequest on API 26+

    val isEngaged: Boolean
        get() = engaged

    /**
     * Locks the alarm stream at [volumePercent] (0f..1f) of the device maximum.
     * Never locks to silence: the level is floored at [MIN_PERCENT] of maximum.
     */
    @Synchronized
    fun engage(volumePercent: Float) {
        if (engaged) {
            enforceNow()
            return
        }

        val maxVolume = runCatching { audioManager.getStreamMaxVolume(STREAM) }.getOrDefault(0)
        if (maxVolume <= 0) {
            Log.w(TAG, "Alarm stream reports no volume range; lock not engaged")
            return
        }

        val floor = (maxVolume * MIN_PERCENT).roundToInt().coerceAtLeast(1)
        lockedVolume = (maxVolume * volumePercent.coerceIn(0f, 1f))
            .roundToInt()
            .coerceIn(floor, maxVolume)
        previousVolume = runCatching { audioManager.getStreamVolume(STREAM) }.getOrNull()
        engaged = true

        requestAlarmAudioFocus()
        enforceNow()
        registerVolumeObserver()
        registerVolumeReceiver()

        watchdogJob = scope.launch {
            while (isActive && engaged) {
                enforceNow()
                delay(WATCHDOG_INTERVAL_MS)
            }
        }

        Log.i(TAG, "Alarm volume locked at $lockedVolume/$maxVolume (was $previousVolume)")
    }

    /**
     * Snaps the alarm stream back to the locked level. Safe to call from any thread and
     * cheap enough to call on every swallowed key press.
     */
    fun enforceNow() {
        if (!engaged) return
        try {
            if (audioManager.isStreamMute(STREAM)) {
                audioManager.adjustStreamVolume(STREAM, AudioManager.ADJUST_UNMUTE, 0)
            }
            if (audioManager.getStreamVolume(STREAM) != lockedVolume) {
                // flags = 0 so we never pop the system volume UI while re-asserting.
                audioManager.setStreamVolume(STREAM, lockedVolume, 0)
            }
        } catch (security: SecurityException) {
            // Thrown when Do Not Disturb is on and the app has no notification policy access.
            Log.w(TAG, "Not allowed to hold the alarm volume: ${security.message}")
        } catch (t: Throwable) {
            Log.w(TAG, "Failed to re-assert alarm volume: ${t.message}")
        }
    }

    /** Releases the lock and gives the user their original volume back. */
    @Synchronized
    fun release(restoreVolume: Boolean = true) {
        if (!engaged) return
        engaged = false

        watchdogJob?.cancel()
        watchdogJob = null

        volumeObserver?.let { observer ->
            runCatching { appContext.contentResolver.unregisterContentObserver(observer) }
        }
        volumeObserver = null

        volumeReceiver?.let { receiver ->
            runCatching { appContext.unregisterReceiver(receiver) }
        }
        volumeReceiver = null

        abandonAlarmAudioFocus()

        if (restoreVolume) {
            previousVolume?.let { original ->
                runCatching { audioManager.setStreamVolume(STREAM, original, 0) }
            }
        }
        previousVolume = null

        Log.i(TAG, "Alarm volume lock released")
    }

    private fun registerVolumeObserver() {
        val observer = object : ContentObserver(Handler(Looper.getMainLooper())) {
            override fun onChange(selfChange: Boolean) {
                enforceNow()
            }
        }
        runCatching {
            appContext.contentResolver.registerContentObserver(
                Settings.System.CONTENT_URI,
                true,
                observer
            )
            volumeObserver = observer
        }.onFailure { Log.w(TAG, "Could not observe system volume: ${it.message}") }
    }

    private fun registerVolumeReceiver() {
        val receiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                enforceNow()
            }
        }
        runCatching {
            ContextCompat.registerReceiver(
                appContext,
                receiver,
                IntentFilter(VOLUME_CHANGED_ACTION),
                ContextCompat.RECEIVER_NOT_EXPORTED
            )
            volumeReceiver = receiver
        }.onFailure { Log.w(TAG, "Could not listen for volume broadcasts: ${it.message}") }
    }

    /**
     * Takes exclusive audio focus as an alarm so music/video apps duck out of the way and
     * cannot keep playing over (or ducking) the alarm.
     */
    private fun requestAlarmAudioFocus() {
        runCatching {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val attributes = AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_ALARM)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .build()
                val request = AudioFocusRequest
                    .Builder(AudioManager.AUDIOFOCUS_GAIN_TRANSIENT_EXCLUSIVE)
                    .setAudioAttributes(attributes)
                    .setWillPauseWhenDucked(false)
                    .setOnAudioFocusChangeListener { /* an alarm never yields focus */ }
                    .build()
                audioManager.requestAudioFocus(request)
                focusRequest = request
            } else {
                @Suppress("DEPRECATION")
                audioManager.requestAudioFocus(
                    null,
                    STREAM,
                    AudioManager.AUDIOFOCUS_GAIN_TRANSIENT_EXCLUSIVE
                )
            }
        }.onFailure { Log.w(TAG, "Could not take alarm audio focus: ${it.message}") }
    }

    private fun abandonAlarmAudioFocus() {
        runCatching {
            val request = focusRequest
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && request is AudioFocusRequest) {
                audioManager.abandonAudioFocusRequest(request)
            } else {
                @Suppress("DEPRECATION")
                audioManager.abandonAudioFocus(null)
            }
        }
        focusRequest = null
    }

    companion object {
        private const val TAG = "AlarmVolumeLock"
        private const val STREAM = AudioManager.STREAM_ALARM

        /** Hidden but stable system broadcast sent whenever a stream volume changes. */
        private const val VOLUME_CHANGED_ACTION = "android.media.VOLUME_CHANGED_ACTION"

        /** An alarm is never allowed to be quieter than this fraction of the maximum. */
        private const val MIN_PERCENT = 0.5f

        /** Backstop poll; anything that dodges the observer is undone within this window. */
        private const val WATCHDOG_INTERVAL_MS = 250L
    }
}

/**
 * Process-wide handle on the currently ringing alarm's volume lock.
 *
 * Both the ringing Activity and the mission Composable ask for the lock, so it is
 * reference counted: the volume is only handed back once the last holder lets go.
 * Activities route their hardware keys through [handleKeyEvent] to swallow them.
 */
object AlarmLockState {

    /**
     * Keys a sleepy user reaches for to make the noise stop. Volume and media keys are the
     * important ones; back/menu/recents/assistant are included so the mission cannot be
     * dodged by leaving the screen.
     *
     * POWER and HOME are deliberately absent: Android never delivers them to apps, so no
     * app can block them (see the note in the README of this feature).
     */
    private val blockedKeys: Set<Int> = setOf(
        KeyEvent.KEYCODE_VOLUME_UP,
        KeyEvent.KEYCODE_VOLUME_DOWN,
        KeyEvent.KEYCODE_VOLUME_MUTE,
        KeyEvent.KEYCODE_MUTE,
        KeyEvent.KEYCODE_HEADSETHOOK,
        KeyEvent.KEYCODE_MEDIA_PLAY,
        KeyEvent.KEYCODE_MEDIA_PAUSE,
        KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE,
        KeyEvent.KEYCODE_MEDIA_STOP,
        KeyEvent.KEYCODE_MEDIA_NEXT,
        KeyEvent.KEYCODE_MEDIA_PREVIOUS,
        KeyEvent.KEYCODE_MEDIA_CLOSE,
        KeyEvent.KEYCODE_CAMERA,
        KeyEvent.KEYCODE_BACK,
        KeyEvent.KEYCODE_MENU,
        KeyEvent.KEYCODE_APP_SWITCH,
        KeyEvent.KEYCODE_SEARCH,
        KeyEvent.KEYCODE_ASSIST,
        KeyEvent.KEYCODE_VOICE_ASSIST
    )

    @Volatile
    private var lock: AlarmVolumeLock? = null

    private var holders: Int = 0

    /** True while an alarm mission is holding the volume down. */
    val isLocked: Boolean
        get() = lock?.isEngaged == true

    @Synchronized
    fun engage(context: Context, volumePercent: Float): AlarmVolumeLock {
        val active = lock ?: AlarmVolumeLock(context).also { lock = it }
        holders++
        active.engage(volumePercent)
        return active
    }

    /** Drops one holder; the user's volume comes back when the last one releases. */
    @Synchronized
    fun release() {
        if (holders > 0) holders--
        if (holders == 0) {
            lock?.release()
            lock = null
        }
    }

    /** Emergency valve: drop every holder (used when the ringing Activity is destroyed). */
    @Synchronized
    fun releaseAll() {
        holders = 0
        lock?.release()
        lock = null
    }

    fun enforceNow() {
        lock?.enforceNow()
    }

    fun isBlockedKey(keyCode: Int): Boolean = keyCode in blockedKeys

    /**
     * Call from `Activity.dispatchKeyEvent`. Returns true when the key was swallowed,
     * meaning the system must not act on it.
     */
    fun handleKeyEvent(event: KeyEvent): Boolean {
        val active = lock
        if (active == null || !active.isEngaged) return false
        if (!isBlockedKey(event.keyCode)) return false
        // Something may still have slipped a change through before we got the key.
        active.enforceNow()
        return true
    }
}
