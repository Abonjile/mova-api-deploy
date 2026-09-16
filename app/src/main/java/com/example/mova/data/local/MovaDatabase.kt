package com.example.mova.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.mova.data.models.UserEntity

@Database(
    entities = [UserEntity::class],
    version = 1,
    exportSchema = false
)
abstract class MovaDatabase : RoomDatabase() {

    abstract fun userDao(): UserDao
}