package com.example.mova.data.local

import androidx.room.Database
import androidx.room.TypeConverters
import androidx.room.RoomDatabase
import com.example.mova.data.models.UserEntity

@Database(
    entities = [UserEntity::class],
    version = 2,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class MovaDatabase : RoomDatabase() {

    abstract fun userDao(): UserDao
}