package com.example.data.repository

import com.example.data.db.AlarmDao
import com.example.data.model.AlarmItem
import kotlinx.coroutines.flow.Flow

class AlarmRepository(private val alarmDao: AlarmDao) {

    val allAlarms: Flow<List<AlarmItem>> = alarmDao.getAllAlarms()

    suspend fun getAlarmById(id: Int): AlarmItem? {
        return alarmDao.getAlarmById(id)
    }

    suspend fun insertAlarm(alarm: AlarmItem): Long {
        return alarmDao.insertAlarm(alarm)
    }

    suspend fun updateAlarm(alarm: AlarmItem) {
        alarmDao.updateAlarm(alarm)
    }

    suspend fun deleteAlarm(alarm: AlarmItem) {
        alarmDao.deleteAlarm(alarm)
    }

    suspend fun toggleAlarmEnabled(id: Int, enabled: Boolean) {
        alarmDao.updateAlarmEnabled(id, enabled)
    }
}
