package com.example

import android.view.KeyEvent
import com.example.data.model.AlarmItem
import com.example.util.AlarmLockState
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class AlarmLockStateTest {

  @Test
  fun `volume and media keys are swallowed while an alarm is ringing`() {
    assertTrue(AlarmLockState.isBlockedKey(KeyEvent.KEYCODE_VOLUME_DOWN))
    assertTrue(AlarmLockState.isBlockedKey(KeyEvent.KEYCODE_VOLUME_UP))
    assertTrue(AlarmLockState.isBlockedKey(KeyEvent.KEYCODE_VOLUME_MUTE))
    assertTrue(AlarmLockState.isBlockedKey(KeyEvent.KEYCODE_MUTE))
    assertTrue(AlarmLockState.isBlockedKey(KeyEvent.KEYCODE_HEADSETHOOK))
    assertTrue(AlarmLockState.isBlockedKey(KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE))
  }

  @Test
  fun `back and recents cannot be used to escape the mission`() {
    assertTrue(AlarmLockState.isBlockedKey(KeyEvent.KEYCODE_BACK))
    assertTrue(AlarmLockState.isBlockedKey(KeyEvent.KEYCODE_APP_SWITCH))
  }

  @Test
  fun `typing keys still work so missions can be answered`() {
    assertFalse(AlarmLockState.isBlockedKey(KeyEvent.KEYCODE_1))
    assertFalse(AlarmLockState.isBlockedKey(KeyEvent.KEYCODE_A))
    assertFalse(AlarmLockState.isBlockedKey(KeyEvent.KEYCODE_DEL))
    assertFalse(AlarmLockState.isBlockedKey(KeyEvent.KEYCODE_ENTER))
  }

  @Test
  fun `keys pass through normally when no alarm is ringing`() {
    val volumeDown = KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_VOLUME_DOWN)

    assertFalse(AlarmLockState.isLocked)
    assertFalse(AlarmLockState.handleKeyEvent(volumeDown))
  }

  @Test
  fun `new alarms lock the volume keys by default`() {
    val alarm = AlarmItem(hour = 6, minute = 30, label = "Morning Study Session")

    assertTrue(alarm.isVolumeButtonLockEnabled)
    assertTrue(alarm.isEscapeBlockEnabled)
  }
}
