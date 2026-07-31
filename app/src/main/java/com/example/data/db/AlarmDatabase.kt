package com.example.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.data.model.AlarmItem
import com.example.data.model.MissionDifficulty
import com.example.data.model.MissionType
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(entities = [AlarmItem::class], version = 1, exportSchema = false)
@TypeConverters(Converters::class)
abstract class AlarmDatabase : RoomDatabase() {

    abstract fun alarmDao(): AlarmDao

    companion object {
        @Volatile
        private var INSTANCE: AlarmDatabase? = null

        fun getDatabase(context: Context): AlarmDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AlarmDatabase::class.java,
                    "alarmy_database"
                )
                    .addCallback(object : Callback() {
                        override fun onCreate(db: SupportSQLiteDatabase) {
                            super.onCreate(db)
                            INSTANCE?.let { database ->
                                CoroutineScope(Dispatchers.IO).launch {
                                    populateInitialData(database.alarmDao())
                                }
                            }
                        }
                    })
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }

        suspend fun populateInitialData(dao: AlarmDao) {
            if (dao.getAlarmCount() == 0) {
                dao.insertAlarm(
                    AlarmItem(
                        hour = 5,
                        minute = 30,
                        label = "Morning Study Revision (PW / Allen)",
                        isEnabled = true,
                        repeatDays = "Mon,Tue,Wed,Thu,Fri",
                        missionType = MissionType.MATH,
                        missionTargetCount = 3,
                        missionDifficulty = MissionDifficulty.MEDIUM,
                        ringtoneName = "End of the World",
                        isWakeUpCheckEnabled = true,
                        wakeUpCheckMinutes = 5
                    )
                )
                dao.insertAlarm(
                    AlarmItem(
                        hour = 6,
                        minute = 15,
                        label = "JEE / NEET Daily Practice",
                        isEnabled = true,
                        repeatDays = "Mon,Tue,Wed,Thu,Fri",
                        missionType = MissionType.SHAKE,
                        missionTargetCount = 20,
                        missionDifficulty = MissionDifficulty.MEDIUM,
                        ringtoneName = "Cock a doodle doo",
                        isWakeUpCheckEnabled = true,
                        wakeUpCheckMinutes = 5
                    )
                )
                dao.insertAlarm(
                    AlarmItem(
                        hour = 7,
                        minute = 0,
                        label = "Topper Morning Routine",
                        isEnabled = false,
                        repeatDays = "Sat,Sun",
                        missionType = MissionType.MEMORY,
                        missionTargetCount = 2,
                        missionDifficulty = MissionDifficulty.EASY,
                        ringtoneName = "[Motivation] Don't give up",
                        isWakeUpCheckEnabled = true,
                        wakeUpCheckMinutes = 5
                    )
                )
            }
        }
    }
}
