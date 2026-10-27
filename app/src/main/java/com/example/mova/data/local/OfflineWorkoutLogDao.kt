package com.example.mova.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.mova.data.local.OfflineWorkoutLog

@Dao
interface OfflineWorkoutLogDao {
    @Insert
    suspend fun insertLog(log: OfflineWorkoutLog)

    @Query("SELECT * FROM offline_workout_logs WHERE isSynced = 0")
    suspend fun getUnsyncedLogs(): List<OfflineWorkoutLog>

    @Query("UPDATE offline_workout_logs SET isSynced = 1 WHERE id = :logId")
    suspend fun markAsSynced(logId: Int)

    @Query("SELECT COUNT(*) FROM offline_workout_logs WHERE isSynced = 0")
    suspend fun getUnsyncedCount(): Int
}