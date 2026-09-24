package com.example.data.db

import androidx.room.*
import com.example.data.model.AlarmItem
import kotlinx.coroutines.flow.Flow

@Dao
interface AlarmDao {
    @Query("SELECT * FROM alarms ORDER BY hour ASC, minute ASC")
    fun getAllAlarms(): Flow<List<AlarmItem>>

    @Query("SELECT * FROM alarms WHERE id = :id LIMIT 1")
    suspend fun getAlarmById(id: Int): AlarmItem?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAlarm(alarm: AlarmItem): Long

    @Update
    suspend fun updateAlarm(alarm: AlarmItem)

    @Delete
    suspend fun deleteAlarm(alarm: AlarmItem)

    @Query("UPDATE alarms SET isEnabled = :enabled WHERE id = :id")
    suspend fun updateAlarmEnabled(id: Int, enabled: Boolean)

    @Query("SELECT * FROM alarms WHERE isEnabled = 1 ORDER BY hour ASC, minute ASC")
    suspend fun getEnabledAlarms(): List<AlarmItem>

    /** Turns the "volume keys do nothing while ringing" guard on or off for one alarm. */
    @Query("UPDATE alarms SET isVolumeButtonLockEnabled = :locked WHERE id = :id")
    suspend fun updateVolumeButtonLock(id: Int, locked: Boolean)

    @Query("SELECT COUNT(*) FROM alarms")
    suspend fun getAlarmCount(): Int
}
