package com.example.mova.data.local

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module // This Hilt module provides dependecies
@InstallIn(SingletonComponent::class) // Scopes bindings to the Mova's singleton component
object DatabaseModule {

    @Provides
    @Singleton // Ensures only 1 instance of the Room database exists-app wide
    fun provideMovaDatabase(@ApplicationContext context: Context): MovaDatabase {
        return Room.databaseBuilder(
            context,
            MovaDatabase::class.java,
            "mova_database" // Name of the SQLite database file on disk
        )
            .fallbackToDestructiveMigration()
            .build()
    }

    @Provides // Exposes UserDao for injection, derived from the database instance
    fun provideUserDao(database: MovaDatabase): UserDao {
        return database.userDao()
    }
}