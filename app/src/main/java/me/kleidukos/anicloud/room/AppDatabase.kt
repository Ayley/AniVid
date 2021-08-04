package me.kleidukos.anicloud.room

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import me.kleidukos.anicloud.room.converter.GenreListConverter
import me.kleidukos.anicloud.room.savedgenres.GenreDao
import me.kleidukos.anicloud.room.savedgenres.RoomGenre
import me.kleidukos.anicloud.room.series.DisplayContainerDao
import me.kleidukos.anicloud.room.series.RoomDisplayStream
import me.kleidukos.anicloud.room.user.User
import me.kleidukos.anicloud.room.user.UserDao
import me.kleidukos.anicloud.room.watchlist.RoomWatchlist
import me.kleidukos.anicloud.room.watchlist.WatchlistDao

@TypeConverters(GenreListConverter::class)
@Database(entities = [User::class, RoomDisplayStream::class, RoomGenre::class, RoomWatchlist::class], version = 11)
abstract class AppDatabase: RoomDatabase() {

    abstract fun userDao(): UserDao

    abstract fun watchlistDao(): WatchlistDao

    abstract fun genresDao(): GenreDao

    abstract fun seriesDao(): DisplayContainerDao
}