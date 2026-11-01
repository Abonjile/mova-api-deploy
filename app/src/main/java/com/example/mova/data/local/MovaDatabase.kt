package com.example.mova.data.local

import androidx.room.Database
import androidx.room.TypeConverters
import androidx.room.RoomDatabase
import com.example.mova.data.models.UserEntity

@Database(
    entities = [UserEntity::class, OfflineWorkoutLog::class,
        WorkoutEntity::class, ExerciseEntity::class, WorkoutExerciseEntity::class],
    version = 5,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class MovaDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun offlineWorkoutLogDao(): OfflineWorkoutLogDao
    abstract fun workoutDao(): WorkoutDao
    abstract fun exerciseDao(): ExerciseDao
    abstract fun workoutExerciseDao(): WorkoutExerciseDao
}