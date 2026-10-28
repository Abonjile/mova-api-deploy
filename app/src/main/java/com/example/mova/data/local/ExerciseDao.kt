package com.example.mova.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.mova.data.local.ExerciseEntity

@Dao
interface ExerciseDao {
    @Query("SELECT * FROM cached_exercises")
    suspend fun getAllExercises(): List<ExerciseEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertExercises(exercises: List<ExerciseEntity>)
}
