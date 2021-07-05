package me.kleidukos.anicloud.room

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = arrayOf(User::class), version = 2)
abstract class AppDatabase: RoomDatabase() {
    abstract fun userDao(): UserDao
}