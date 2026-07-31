package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "alarms")
data class AlarmItem(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val hour: Int,
    val minute: Int,
    val label: String,
    val isEnabled: Boolean = true,
    val repeatDays: String = "Mon,Tue,Wed,Thu,Fri", // Comma separated or "Everyday", "Weekdays"
    val missionType: MissionType = MissionType.MATH,
    val missionTargetCount: Int = missionType.defaultTarget,
    val missionDifficulty: MissionDifficulty = MissionDifficulty.MEDIUM,
    val ringtoneName: String = "End of the World Siren",
    val isWakeUpCheckEnabled: Boolean = true,
    val wakeUpCheckMinutes: Int = 5,
    val soundVolume: Float = 1.0f
) {
    fun getFormattedTime(): String {
        val displayHour = when {
            hour == 0 -> 12
            hour > 12 -> hour - 12
            else -> hour
        }
        val amPm = if (hour < 12) "AM" else "PM"
        return String.format("%02d:%02d %s", displayHour, minute, amPm)
    }

    fun getRepeatSubtitle(): String {
        return when (repeatDays) {
            "Mon,Tue,Wed,Thu,Fri,Sat,Sun" -> "Everyday"
            "Mon,Tue,Wed,Thu,Fri" -> "Weekdays"
            "Sat,Sun" -> "Weekends"
            else -> repeatDays
        }
    }
}
