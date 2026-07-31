package com.example.data.db

import androidx.room.TypeConverter
import com.example.data.model.MissionDifficulty
import com.example.data.model.MissionType

class Converters {
    @TypeConverter
    fun fromMissionType(type: MissionType): String = type.name

    @TypeConverter
    fun toMissionType(value: String): MissionType {
        return try {
            MissionType.valueOf(value)
        } catch (e: Exception) {
            MissionType.MATH
        }
    }

    @TypeConverter
    fun fromMissionDifficulty(diff: MissionDifficulty): String = diff.name

    @TypeConverter
    fun toMissionDifficulty(value: String): MissionDifficulty {
        return try {
            MissionDifficulty.valueOf(value)
        } catch (e: Exception) {
            MissionDifficulty.MEDIUM
        }
    }
}
