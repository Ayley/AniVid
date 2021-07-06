package me.kleidukos.anicloud.room

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [User::class, RoomDisplayStream::class, RoomGenre::class], version = 3)
abstract class AppDatabase: RoomDatabase() {

    abstract fun userDao(): UserDao

    abstract fun watchlistDao(): WatchlistDao

    abstract fun genresDao(): GenreDao
}