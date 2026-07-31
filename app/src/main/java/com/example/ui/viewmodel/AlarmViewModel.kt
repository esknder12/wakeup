package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.db.AlarmDatabase
import com.example.data.model.AlarmItem
import com.example.data.model.AlarmySound
import com.example.data.model.AlarmySoundCatalog
import com.example.data.model.MissionDifficulty
import com.example.data.model.MissionType
import com.example.data.repository.AlarmRepository
import com.example.util.SoundEngine
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.Calendar

class AlarmViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: AlarmRepository
    val soundEngine = SoundEngine(application.applicationContext)

    val alarms: StateFlow<List<AlarmItem>>

    private val _currentlyRingingAlarm = MutableStateFlow<AlarmItem?>(null)
    val currentlyRingingAlarm: StateFlow<AlarmItem?> = _currentlyRingingAlarm.asStateFlow()

    private val _activeSleepSound = MutableStateFlow<AlarmySound?>(null)
    val activeSleepSound: StateFlow<AlarmySound?> = _activeSleepSound.asStateFlow()

    private val _isPlayingSleepSound = MutableStateFlow(false)
    val isPlayingSleepSound: StateFlow<Boolean> = _isPlayingSleepSound.asStateFlow()

    private val _previewSoundId = MutableStateFlow<String?>(null)
    val previewSoundId: StateFlow<String?> = _previewSoundId.asStateFlow()

    private val _challengeActive = MutableStateFlow(true)
    val challengeActive: StateFlow<Boolean> = _challengeActive.asStateFlow()

    private val _challengeDay = MutableStateFlow(2) // Day 2 of 5 Topper Challenge
    val challengeDay: StateFlow<Int> = _challengeDay.asStateFlow()

    init {
        val alarmDao = AlarmDatabase.getDatabase(application).alarmDao()
        repository = AlarmRepository(alarmDao)
        alarms = repository.allAlarms.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )
    }

    fun toggleAlarm(alarm: AlarmItem) {
        viewModelScope.launch {
            repository.toggleAlarmEnabled(alarm.id, !alarm.isEnabled)
        }
    }

    fun saveAlarm(alarm: AlarmItem) {
        viewModelScope.launch {
            if (alarm.id == 0) {
                repository.insertAlarm(alarm)
            } else {
                repository.updateAlarm(alarm)
            }
        }
    }

    fun deleteAlarm(alarm: AlarmItem) {
        viewModelScope.launch {
            repository.deleteAlarm(alarm)
        }
    }

    fun startTestAlarm(alarm: AlarmItem) {
        _currentlyRingingAlarm.value = alarm
        val ringtone = AlarmySoundCatalog.allSounds.find { it.title == alarm.ringtoneName }
            ?: AlarmySoundCatalog.allSounds.first()
        soundEngine.playSound(ringtone, alarm.soundVolume)
    }

    fun dismissActiveAlarm() {
        soundEngine.stopCurrentSound()
        _currentlyRingingAlarm.value = null
    }

    fun previewRingtone(sound: AlarmySound) {
        if (_previewSoundId.value == sound.id) {
            soundEngine.stopCurrentSound()
            _previewSoundId.value = null
        } else {
            _previewSoundId.value = sound.id
            soundEngine.playSound(sound, 1.0f) {
                _previewSoundId.value = null
            }
        }
    }

    fun stopPreview() {
        soundEngine.stopCurrentSound()
        _previewSoundId.value = null
    }

    fun toggleSleepSound(sound: AlarmySound) {
        if (_activeSleepSound.value?.id == sound.id && _isPlayingSleepSound.value) {
            soundEngine.stopCurrentSound()
            _isPlayingSleepSound.value = false
        } else {
            _activeSleepSound.value = sound
            _isPlayingSleepSound.value = true
            soundEngine.playSound(sound, 0.7f) {
                _isPlayingSleepSound.value = false
            }
        }
    }

    fun addTopperRoutineAlarms() {
        viewModelScope.launch {
            val now = Calendar.getInstance()
            val morningMath = AlarmItem(
                hour = 5,
                minute = 0,
                label = "JEE/NEET PW Morning Study",
                isEnabled = true,
                repeatDays = "Mon,Tue,Wed,Thu,Fri,Sat",
                missionType = MissionType.MATH,
                missionTargetCount = 3,
                missionDifficulty = MissionDifficulty.HARD,
                ringtoneName = "End of the World",
                isWakeUpCheckEnabled = true,
                wakeUpCheckMinutes = 5
            )
            val revisionShake = AlarmItem(
                hour = 6,
                minute = 0,
                label = "Daily Mock Test Revision",
                isEnabled = true,
                repeatDays = "Mon,Tue,Wed,Thu,Fri",
                missionType = MissionType.SHAKE,
                missionTargetCount = 20,
                missionDifficulty = MissionDifficulty.MEDIUM,
                ringtoneName = "[Motivation] Get up and Grind",
                isWakeUpCheckEnabled = true,
                wakeUpCheckMinutes = 5
            )
            repository.insertAlarm(morningMath)
            repository.insertAlarm(revisionShake)
            _challengeDay.value = (_challengeDay.value + 1).coerceAtMost(5)
        }
    }

    fun calculateNextAlarmText(alarmsList: List<AlarmItem>): String {
        val enabledAlarms = alarmsList.filter { it.isEnabled }
        if (enabledAlarms.isEmpty()) return "No alarms set"

        val now = Calendar.getInstance()
        val currentMinutes = now.get(Calendar.HOUR_OF_DAY) * 60 + now.get(Calendar.MINUTE)

        var smallestDiff = Int.MAX_VALUE
        for (alarm in enabledAlarms) {
            val alarmMinutes = alarm.hour * 60 + alarm.minute
            var diff = alarmMinutes - currentMinutes
            if (diff <= 0) {
                diff += 24 * 60
            }
            if (diff < smallestDiff) {
                smallestDiff = diff
            }
        }

        val hours = smallestDiff / 60
        val mins = smallestDiff % 60
        return when {
            hours > 0 -> "Alarm in ${hours}h ${mins}m"
            else -> "Alarm in ${mins}m"
        }
    }

    override fun onCleared() {
        super.onCleared()
        soundEngine.destroy()
    }
}
