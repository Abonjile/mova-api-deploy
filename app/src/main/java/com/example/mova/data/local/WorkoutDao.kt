package com.example.mova.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.mova.data.local.WorkoutEntity

@Dao
interface WorkoutDao {
    @Query("SELECT * FROM cached_workouts")
    suspend fun getAllWorkouts(): List<WorkoutEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWorkouts(workouts: List<WorkoutEntity>)

    @Query("DELETE FROM cached_workouts")
    suspend fun clearWorkouts()
}